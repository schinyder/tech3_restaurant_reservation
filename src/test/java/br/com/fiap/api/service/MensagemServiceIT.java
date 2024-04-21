package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import br.com.fiap.api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MensagemServiceIT {

    @Autowired // Injeta o repositório
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = MensagemHelper.gerarMensagem();

            var resultadoObtido = mensagemService.registrarMensagem(mensagem);

            assertThat(resultadoObtido)
                    .isNotNull()
                    .isInstanceOf(Mensagem.class);
            assertThat(resultadoObtido.getId()).isNotNull();
            assertThat(resultadoObtido.getDataCriacao()).isNotNull();
            assertThat(resultadoObtido.getGostei()).isZero();
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() {
            var id = UUID.fromString("6774c100-c79e-45ed-8fad-1d4d39653952");

            var resultadoObtido = mensagemService.buscarMensagem(id);

            assertThat(resultadoObtido)
                    .isNotNull()
                    .isInstanceOf(Mensagem.class);
            assertThat(resultadoObtido.getId())
                    .isNotNull()
                    .isEqualTo(id);
            assertThat(resultadoObtido.getUsuario())
                    .isNotNull()
                    .isEqualTo("Adam");
            assertThat(resultadoObtido.getConteudo())
                    .isNotNull()
                    .isEqualTo("Conteudo da mensagem 01");
            assertThat(resultadoObtido.getDataCriacao()).isNotNull();
            assertThat(resultadoObtido.getGostei()).isZero();
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = UUID.fromString("cba5b5ee-1abc-4a53-9e55-128a8d146774");

            assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50b");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            var resultadoObtido = mensagemService.alterarMensagem(id, mensagemAtualizada);

            assertThat(resultadoObtido.getId()).isEqualTo(id);
            assertThat(resultadoObtido.getConteudo()).isEqualTo(mensagemAtualizada.getConteudo());

            assertThat(resultadoObtido.getUsuario()).isNotEqualTo(mensagemAtualizada.getUsuario());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("3130533b-02a6-4c72-bd29-4e6018d25b69");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(id);

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50b");
            var mensagemAtualizada = MensagemHelper.gerarMensagem();
            mensagemAtualizada.setId(UUID.fromString("75477352-d5fa-48f8-aa41-4d48c7430c30"));

            assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemAtualizada))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("mensagem atualizada não apresenta o ID correto");
        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("2e5afb42-6388-4e9e-bf93-9d5bf128def5");

            var resultadoObtido = mensagemService.removerMensagem(id);

            assertThat(resultadoObtido).isTrue();
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.fromString("85a72875-55d8-41c8-a5ef-5c1606b52736");

            assertThatThrownBy(() -> mensagemService.removerMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("Mensagem não encontrada");
        }
    }

    @Nested
    class ListarMensagens {
        @Test
        void devePermitirListarMensagens() {
            Page<Mensagem> listaDeMensagensObtida = mensagemService.listarMensagens(Pageable.unpaged());

            assertThat(listaDeMensagensObtida).hasSize(3);
            assertThat(listaDeMensagensObtida.getContent())
                    .asList()
                    .allSatisfy(mensagemObtida -> {
                        assertThat(mensagemObtida).isNotNull();
                    });
        }
    }
}
