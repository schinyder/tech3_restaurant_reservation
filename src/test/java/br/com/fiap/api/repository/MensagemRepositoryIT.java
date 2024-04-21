package br.com.fiap.api.repository;

import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.utils.MensagemHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
class MensagemRepositoryIT {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirCriarTabela() {
        var totalDeRegistros = mensagemRepository.count();
        assertThat(totalDeRegistros).isPositive();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem();
        mensagem.setId(id);

        // Act
        var mensagemRecebida = mensagemRepository.save(mensagem);

        // Assert
        assertThat(mensagemRecebida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRecebida.getId()).isEqualTo(id);
        assertThat(mensagemRecebida.getConteudo()).isEqualTo(mensagem.getConteudo());
        assertThat(mensagemRecebida.getUsuario()).isEqualTo(mensagem.getUsuario());
    }

    @Test
    void devePermitirBuscarMensagem() {
        // Arrange
        var id = UUID.fromString("6774c100-c79e-45ed-8fad-1d4d39653952");

        // Act
        var mensagemRecebidaOptional = mensagemRepository.findById(id);

        // Assert
        assertThat(mensagemRecebidaOptional).isPresent();

        mensagemRecebidaOptional.ifPresent(mensagemRecebida -> {
            assertThat(mensagemRecebida.getId()).isEqualTo(id);
        });
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50b");
        // Act
        mensagemRepository.deleteById(id);
        var mensagemRecebidaOptional = mensagemRepository.findById(id);
        // Assert
        assertThat(mensagemRecebidaOptional).isEmpty();

    }

    @Test
    void devePermitirListarMensagens() {
        // Act
        var resultadosObtidos = mensagemRepository.findAll();
        // Assert
        assertThat(resultadosObtidos).hasSizeGreaterThan(0);
    }

}
