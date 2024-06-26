# fiap-qualidade-software

Lições das aulas de Qualidade de Software da [Pós Tech em Software Architecture da FIAP](https://postech.fiap.com.br/curso/software-architecture/).

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

## Objetivos

Desenvolver um  Sistema de Reserva e Avaliação de Restaurantes permitindo os seguintes pontos:

- Cadastro de Restaurantes
- Reserva de Mesas
- Avaliações e Comentários
- Busca de Restaurantes
- Gerenciamento de Reservas

## Executar a aplicação

1. Execute o comando `mvn spring-boot:run` para iniciar a aplicação;

### Frameworks de teste utilizados
- [JUnit 5](https://junit.org/junit5/)
- [Mockito](https://site.mockito.org/)
- [AssertJ](https://joel-costigliola.github.io/assertj/)
- [Rest Assured](https://rest-assured.io/)
- [Cucumber](https://cucumber.io/)

### Banco de dados 
- [H2 Database Engine](https://www.h2database.com/)

## APIs 
## Documentação da API 

#### Realiza o Cadastro de Restaurante

```http
  POST /restaurantes/criar-restaurante
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `nome ` | `string` |**Obrigatório**. Deve receber o Nome do Restaurante. Aceita caracteres numéricos e especiais.|
| `endereco ` | `object` | **Obrigatório**. Deve receber o Endereço do Restaurante, contendo os seguintes campos:|
| ↳`cep ` | `string` | **Obrigatório**. Deve receber o CEP do Restaurante respeitando o seguinte modelo: xxxxxx-xxx.|
| ↳`logradouro ` | `string` | **Obrigatório**. Deve receber o Logradouro do Restaurante. |
| ↳`numero ` | `string` | **Obrigatório**. Deve receber o Número do Endereço do Restaurante.|
| ↳`complemento ` | `string` | Deve receber o Complemento do Endereço do Restaurante.|
| ↳`bairro ` | `string` | **Obrigatório**.Deve receber o Bairro do Restaurante.|
| ↳`cidade ` | `string` | **Obrigatório**.Deve receber a Cidade do Restaurante.|
| ↳`uf ` | `string` | **Obrigatório**.Deve receber o UF do Restaurante.|
| `tipoCulinaria ` | `string` | **Obrigatório**.Deve receber o Tipo da Culinária do Restaurante. É um Enum que deve receber algum dos segintes valores: PORTUGUESA, BRASILEIRA, CHINESA, MEXICANA, ESPANHOLA, TAILANDESA, ITALIANA, FRANCESA, JAPONESA.|
| `horarioDeAbertura ` | `string` | **Obrigatório**.Deve receber o Horário de Abertura do Restaurante, respeitando o seguinte modelo: HH:MM ou HH:MM:SS.|
| `horarioDeFechamento ` | `string` | **Obrigatório**.Deve receber o Horário de Fechamento do Restaurante, respeitando o seguinte modelo: HH:MM ou HH:MM:SS.|
| `capacidade ` | `integer` | **Obrigatório**.Deve receber a Capacidade do Restaurante. Aceita apenas valores numéricos.|


#### Realiza a Busca de Restaurante Cadastrado Por Nome

```http
  GET /restaurantes
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `nomeRestaurante` | `string` | Deve receber o nome do Restaurante. Aceita caracteres numéricos e especiais. Não é Case Sensitive. Caso nenhum valor seja informado retorna todos os restaurantes cadastrados.|


#### Realiza a Busca de Restaurante Cadastrado Pela Cidade

```http
  GET /restaurantes/cidade
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `cidadeRestaurante`      | `string` | Deve receber a cidade do Restaurante. Aceita caracteres numéricos e especiais. Não é Case Sensitive. Caso nenhum valor seja informado retorna todos os restaurantes cadastrados.|


#### Realiza a Busca de Restaurante Cadastrado Pela Culinária

```http
  GET /restaurantes/culinaria
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `culinariaRestaurante`      | `string` |Deve receber a culinária do Restaurante. Aceita caracteres numéricos e especiais. Não é Case Sensitive. Caso nenhum valor seja informado retorna todos os restaurantes cadastrados.|


#### Realiza o Cadastro de Cliente

```http
  POST /cliente/criar-cliente
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `nome  ` | `string` |**Obrigatório**. Deve receber o Nome do Cliente. Aceita caracteres numéricos e especiais.|
| `cpf  ` | `string` |**Obrigatório**. Deve receber o CPF do Cliente. Utilizar apenas valores numéricos.|
| `email  ` | `string` |**Obrigatório**. Deve receber o E-mail do Cliente, respeitando o seguinte modelo: xxxxxx@xxxxx.|
| `endereco ` | `object` | **Obrigatório**. Deve receber o endereço do Cliente. contendo os campos:|
| ↳`cep ` | `string` | **Obrigatório**. Deve receber o CEP do Cliente respeitando o seguinte modelo: xxxxxx-xxx.|
| ↳`logradouro ` | `string` | **Obrigatório**. Deve receber o Logradouro do Cliente. |
| ↳`numero ` | `string` | **Obrigatório**. Deve receber o Número da Residência do Cliente.|
| ↳`complemento ` | `string` | Deve receber o Complemento do Endereço do Cliente.|
| ↳`bairro ` | `string` | **Obrigatório**. Deve receber o Bairro do Cliente.|
| ↳`cidade ` | `string` | **Obrigatório**. Deve receber a Cidade do Cliente.|
| ↳`uf ` | `string` | **Obrigatório**. Deve receber o UF do Cliente.|
| `dadosCriacaoTelefoneDTO ` | `object` | Deve receber o Telefone do Cliente, contendo os seguintes campos:|
| ↳`ddi ` | `number` | Deve receber o Código Internacional do Número de Telefone do Cliente.|
| ↳`ddd ` | `number` | Deve receber o Código de Área do Telefone do Cliente.|
| ↳`telefone ` | `number` | Deve receber o Telefone do Cliente.|


#### Realiza a Criação de Reserva

```http
  POST /reservas/criar-reserva
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `cliente_id  ` | `integer` |**Obrigatório**. Deve receber o ID do Cliente. Aceita apenas valores numéricos.|
| `dataReserva  ` | `string` |**Obrigatório**. Deve receber a Data e Hora da Reserva, respeitando o seguinte modelo "aaaa-mm-ddThh:mm:ss".|
| `restaurante_id  ` | `integer` |**Obrigatório**. Deve receber o ID do Restaurante. Aceita apenas valores numéricos.|


#### Lista as Reservas de acordo com o ID

```http
  GET /reservas/listar-reservas/{reserva_id}
```

#### Realiza o Encerramento da Reserva

```http
  PUT /reservas/encerrar-reserva/{reserva_id}
```

#### Cria Avaliações

```http
  POST /avaliacoes/criar-avaliacao
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `cliente_id  ` | `integer` |**Obrigatório**. Deve receber o ID do Cliente. Aceita apenas valores numéricos.|
| `comentario  ` | `string` |Deve receber o comentário. Aceita caracteres numéricos e especiais.|
| `nota  ` | `integer` |**Obrigatório**. Deve receber a Data e Hora da Reserva, respeitando o seguinte modelo "aaaa-mm-ddThh:mm:ss".|
| `restaurante_id  ` | `integer` |**Obrigatório**. Deve receber o ID do Restaurante. Aceita apenas valores numéricos.|



O Dockerfile contém a aplicação da api e o Dockerfile-Jacoco Utiliza o Jacoco para gerar o HTML para visualizar os testes.



## Code Coverage
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
## Requisitos

*JDK 17-18*\
*Maven 3.9.6*