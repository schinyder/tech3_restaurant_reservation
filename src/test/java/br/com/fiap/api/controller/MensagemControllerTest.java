package br.com.fiap.api.controller;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.service.MensagemService;
import br.com.fiap.api.utils.MensagemHelper;
import br.com.fiap.gerenciadorDeReservas.controllers.cliente.MensagemController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                })
                .build();
    }

    @AfterEach()
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            // Act & Assert
            mockMvc.perform(
                            post("/mensagens")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(mensagem))
                    )
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1)).registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() throws Exception {
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(
                            post("/mensagens")
                                    .contentType(MediaType.APPLICATION_XML)
                                    .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() throws Exception {
            var id = UUID.fromString("75477352-d5fa-48f8-aa41-4d48c7430c30");
            var mensagem = MensagemHelper.gerarMensagem();
            when(mensagemService.buscarMensagem(any(UUID.class))).thenReturn(mensagem);

            mockMvc.perform(get("/mensagens/{id}", id)).andExpect(status().isOk());
            verify(mensagemService, times(1)).buscarMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("cc40bc30-1e06-44cf-ac35-8b1fd85b4650");

            when(mensagemService.buscarMensagem(id))
                    .thenThrow(MensagemNotFoundException.class);

            mockMvc.perform(get("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest());
            verify(mensagemService, times(1)).buscarMensagem(id);
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() throws Exception {
            var id = UUID.fromString("fb384847-a533-4425-a62e-b67fc527877e");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);

            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenAnswer(i -> i.getArgument(1));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    .andExpect(status().isAccepted());
            verify(mensagemService, times(1)).alterarMensagem(id, mensagem);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("d86433b2-c7a5-4b9b-98d4-98e92e4e43f7");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(id);
            var conteudoDaExcecao = "mensagem não encontrada";
            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    //.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() throws Exception {
            var id = UUID.fromString("d86433b2-c7a5-4b9b-98d4-98e92e4e43f7");
            var mensagem = MensagemHelper.gerarMensagem();
            mensagem.setId(UUID.fromString("983e13fd-b822-4510-9354-d0cd342e6333"));
            var conteudoDaExcecao = "mensagem atualizada não apresenta o ID correto";
            when(mensagemService.alterarMensagem(id, mensagem))
                    .thenThrow(new MensagemNotFoundException(conteudoDaExcecao));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagem)))
                    //.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(conteudoDaExcecao));

            verify(mensagemService, times(1))
                    .alterarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_ApresentaPayloadComXML() throws Exception {
            var id = UUID.fromString("fb384847-a533-4425-a62e-b67fc527877e");
            String xmlPayload = "<mensagem><id>" + id.toString() + "</id><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            mockMvc.perform(
                            put("/mensagens/{id}", id)
                                    .contentType(MediaType.APPLICATION_XML)
                                    .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).alterarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() throws Exception {
            var id = UUID.fromString("32b8503c-adb1-4e8b-ac91-2424dca50927");

            when(mensagemService.removerMensagem(id)).thenReturn(true);

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("mensagem removida"));

            verify(mensagemService, times(1)).removerMensagem(id);
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() throws Exception {
            var id = UUID.fromString("06a23741-8ff3-461e-8400-2731bf7fdb3c");
            var mensagemDaExcecao = "Mensagem não encontrada";

            when(mensagemService.removerMensagem(id))
                    .thenThrow(new MensagemNotFoundException(mensagemDaExcecao));

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(mensagemDaExcecao));

            verify(mensagemService, times(1)).removerMensagem(id);
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagem = MensagemHelper.gerarMensagem();
            var page = new PageImpl<>(Collections.singletonList(
                    mensagem
            ));

            when(mensagemService.listarMensagens(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/mensagens")
                            .param("page", "0")
                            .param("size", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", not(empty())))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() throws Exception {
            var mensagem = MensagemHelper.gerarMensagem();
            var page = new PageImpl<>(Collections.singletonList(
                    mensagem
            ));

            when(mensagemService.listarMensagens(any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/mensagens"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", not(empty())))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    public static String asJsonString(final Object object) throws JsonProcessingException {
        //return new ObjectMapper().writeValueAsString(object);
        return new ObjectMapper().writeValueAsString(object);
    }
}
