package br.com.fiap.api.service;

import br.com.fiap.api.exception.MensagemNotFoundException;
import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.repository.MensagemRepository;
import br.com.fiap.api.utils.MensagemHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MensagemServiceTest {

    private MensagemService mensagemService;

    @Mock
    private MensagemRepository mensagemRepository;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this); // Todas as variáveis definidas com a anotação @Mock sejam carregadas
        mensagemService = new MensagemServiceImpl(mensagemRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = MensagemHelper.gerarMensagem();
        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));
        // Act
        var mensagemRegistrada = mensagemService
                .registrarMensagem(mensagem);
        // Assert
        assertThat(mensagemRegistrada)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRegistrada.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRegistrada.getUsuario()).isEqualTo(mensagem.getUsuario());
        assertThat(mensagem.getId()).isNotNull();
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("80cc28a9-6551-4f53-bae1-84f8fa5b7bc1");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagem));
        // Act
        var mensagemObtida = mensagemService.buscarMensagem(id);

        // Assert
        assertThat(mensagemObtida).isEqualTo(mensagem);
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
        var id = UUID.fromString("0d2850aa-a42d-41ff-8c18-50b07945d23f");
        when(mensagemRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> mensagemService.buscarMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    void devePermitirAlterarMensagem() {
        // Arrange
        var id = UUID.fromString("6b363558-d6e5-48ad-a59d-360dc8ccb1a3");

        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = new Mensagem();
        mensagemNova.setId(mensagemAntiga.getId());
        mensagemNova.setUsuario(mensagemAntiga.getUsuario());
        mensagemNova.setConteudo("ABCD 12345");

        when(mensagemRepository.findById(id))
                .thenReturn(Optional.of(mensagemAntiga));

        when(mensagemRepository.save(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act
        var mensagemObtida = mensagemService.alterarMensagem(id, mensagemNova);

        // Assert

        assertThat(mensagemObtida).isInstanceOf(Mensagem.class).isNotNull();
        assertThat(mensagemObtida.getId()).isEqualTo(mensagemNova.getId());
        assertThat(mensagemObtida.getUsuario()).isEqualTo(mensagemNova.getUsuario());
        assertThat(mensagemObtida.getConteudo()).isEqualTo(mensagemNova.getConteudo());
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.fromString("c09fab79-1794-415d-a43b-4ee755327439");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagem))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
        // Arrange
        var id = UUID.fromString("0d1c8800-c2d2-4e85-b6ae-bc38b71087cb");
        var mensagemAntiga = MensagemHelper.gerarMensagem();
        mensagemAntiga.setId(id);

        var mensagemNova = MensagemHelper.gerarMensagem();
        mensagemNova.setId(UUID.fromString("6f1841d3-bff5-419d-981e-4d687cbda718"));
        mensagemNova.setConteudo("ABCD 123");

        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagemAntiga));
        // Act & Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(id, mensagemNova))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("mensagem atualizada não apresenta o ID correto");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).save(any(Mensagem.class));
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("e1d1fede-b693-4f8d-b954-809e4e3b9060");
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);
        when(mensagemRepository.findById(id)).thenReturn(Optional.of(mensagem));
        doNothing().when(mensagemRepository).deleteById(id);

        // Act
        var mensagemFoiRemovida = mensagemService.removerMensagem(id);

        // Assert
        assertThat(mensagemFoiRemovida).isTrue();
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, times(1)).deleteById(any(UUID.class));
    }

    @Test
    void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
        // Arrange
        var id = UUID.fromString("744578ee-d2d0-4d58-b873-147d18788ed2");
        when(mensagemRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy((() -> mensagemService.removerMensagem(id)))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(any(UUID.class));
        verify(mensagemRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    void devePermitirListarMensagens() {
        // Arrange
        Page<Mensagem> listaDeMensagens = new PageImpl<>(Arrays.asList(
                MensagemHelper.gerarMensagem(),
                MensagemHelper.gerarMensagem()
        ));
        when(mensagemRepository.listarMensagens(any(Pageable.class)))
                .thenReturn(listaDeMensagens);

        // Act
        var resultadoObtido = mensagemService.listarMensagens(Pageable.unpaged());
        // Assert
        assertThat(resultadoObtido).hasSize(2);
        assertThat(resultadoObtido.getContent())
                .asList()
                .allSatisfy(mensagem -> {
                    assertThat(mensagem)
                            .isNotNull()
                            .isInstanceOf(Mensagem.class);
                });
        verify(mensagemRepository, times(1)).listarMensagens(any(Pageable.class));
    }

}
