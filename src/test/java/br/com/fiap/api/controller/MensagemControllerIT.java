package br.com.fiap.api.controller;

import br.com.fiap.api.model.Mensagem;
import br.com.fiap.api.utils.MensagemHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class MensagemControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = MensagemHelper.gerarMensagem();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    //.log().all()
            .when()
                    .post("/mensagens")
            .then()
                    //.log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadXML() {
            String xmlPayload = "<mensagem><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
                    //.log().all()
            .when()
                    .post("/mensagens")
            .then()
                    //.log().all()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens"));
        }
    }

    @Nested
    class BuscarMensagem {

        @Test
        void devePermitirBuscarMensagem() {
            var id = "6774c100-c79e-45ed-8fad-1d4d39653952";
            when()
                    .get("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExiste() {
            var id = "6774c100-c79e-45ed-8fad-1d4d3965395";
            when()
                    .get("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class AlterarMensagem {
        @Test
        void devePermitirAlterarMensagem() {
            var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50b");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Eva")
                    .conteudo("Conteudo da mensagem")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoExiste() {
            var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50");
            var mensagem = Mensagem.builder()
                    .id(id)
                    .usuario("Eva")
                    .conteudo("Conteudo da mensagem")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdDaMensagemNovaApresentaValorDiferente() {
            var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50b");
            var mensagem = Mensagem.builder()
                    .id(UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50"))
                    .usuario("Eva")
                    .conteudo("Conteudo da mensagem")
                    .build();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("mensagem atualizada não apresenta o ID correto"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_ApresentaPayloadComXML() {

            var id = UUID.fromString("a22c22b0-2197-440a-aff2-3a53bdc4f50b");
            String xmlPayload = "<mensagem><id>a22c22b0-2197-440a-aff2-3a53bdc4f50b</id><usuario>Ana</usuario><conteudo>Mensagem do Conteudo</conteudo></mensagem>";


            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(xmlPayload)
            .when()
                    .put("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"))
                    .body("error", equalTo("Bad Request"))
                    .body("path", equalTo("/mensagens/a22c22b0-2197-440a-aff2-3a53bdc4f50b"))
                    .body("path", containsString("/mensagens"));
        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("2e5afb42-6388-4e9e-bf93-9d5bf128def5");

            when()
                    .delete("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo("mensagem removida"));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExiste() {
            var id = UUID.fromString("2e5afb42-6388-4e9e-bf93-9d5bf128def");

            when()
                    .delete("/mensagens/{id}", id)
            .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("Mensagem não encontrada"));
        }
    }

    @Nested
    class ListarMensagens {

        @Test
        void devePermitirListarMensagens() {
            given()
                    .queryParam("page", "0")
                    .queryParam("size", "10")
            .when()
                    .get("/mensagens")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoInformadoPaginacao() {
            when()
                    .get("/mensagens")
            .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/mensagem.page.schema.json"));
        }
    }
}
