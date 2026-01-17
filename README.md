# Gerador de Boletos - API REST

Este projeto é a minha solução para o desafio de backend proposto pela empresa Conta Azul. 

Desafio desponível [aqui](https://drive.google.com/file/d/1DvjRBTvnHwlUOoNBwAsvoRF6aKqYm7pP/view). (Também compilei as instruções para o arquivo `INSTRUCOES-DESAFIO.md` para facilitar referência no ambiente de desenvolvimento).

## Tecnologias Utilizadas
- Java 25
- Maven: Ferramenta de build e gerenciador de dependencias.
- Spring Boot 4.0.1: Framework que facilita a criação de applicações web e rest.
- Spring Data JPA: Abstração para persistência de dados, permite a troca fácil de H2 para postgres por exemplo.
- H2 Database: Banco de dados em memória, exigido pelos requisitos do desafio.
- Flyway Migration: Facilita a migração de bancos.
- Lombok: Redução de boilerplate através de anotações.

## Como Executar a Aplicação
É possível executar o projeto de três modos:

### 1. Docker (Recomendado)
Você precisará ter instalado `docker` e `docker-compose` em sua máquina.

- Baixe este repositório e execute na pasta raiz do projeto:
  - `docker-compose up --build`

### 2. Executável JAR (Releases)
Você precisará ter instalado `JRE 25` em sua máquina.

- Acesse o release mais recente desta aplicação clicando [aqui](https://github.com/lucas-marianno/desafio-backend-contaazul/releases);
- Baixe o arquivo `.jar` e salve na pasta de destino desejada;
- Abra o terminal na raiz do projeto e execute:
  `java -jar nome-do-arquivo-baixado.jar`

### 3. Compilação via Código Fonte
Você precisará ter instalado `JDK 25` em sua máquina. O Maven Wrapper já está incluso no projeto.

- Baixe este repositório e execute na pasta raiz do projeto:
  `./mvnw spring-boot:run`

## Executar testes unitários e de integração
Você precisará ter instalado `JDK 25` em sua máquina. O Maven Wrapper já está incluso no projeto.

- Baixe este repositório e execute na pasta raiz do projeto:
  `./mvnw test`

Para executar e analizar os testes individualmente, abra o projeto na IDE ou editor de texto de sua preferência e execute como de costume

# Documentação da API

Após executar o projeto, a API estará disponível em http://localhost:8080/.

## Criar Boleto
Endpoint: `POST` http://localhost:8080/rest/bankslips

Cria um novo boleto no sistema com status inicial PENDING.

### Requisição

- `due_date`: Data de vencimento (yyyy-MM-dd).
- `total_in_cents`: Valor total em centavos (ex: 10000 para R$ 100,00).
- `customer`: Nome do cliente.

Exemplo de requisição:
```json
{
  "due_date":"2025-05-10",
  "total_in_cents":"99000",
  "customer":"Ford Prefect Company"
}
```

### Respostas

- status: `201 - CREATED`
Em caso de sucesso

exemplo:
```json
{
  "id": "e24b19a6-4783-4c21-a07c-7b2f727a7908",
  "due_date": "2018-05-10",
  "total_in_cents": 99000,
  "customer": "Ford Prefect Company",
  "status": "PENDING"
}
```

- status: `400 - BAD_REQUEST`
Em caso de corpo vazio

- status: `422 - UNPROCESSABLE_CONTENT`
Em caso de o corpo `json` estar mal formado (items faltando, mal escritos ou em formato inválido)

## Listar Boletos
Endpoint: `GET` http://localhost:8080/rest/bankslips

Retorna uma lista simplificada de todos os boletos armazenados.

### Requisição

Parâmetros: Nenhum.

### Respostas
- status: `200 - OK`

Exemplo de resposta:
```json
[
  {
    "id": "b6dd5324-c2ec-4fcc-bbf3-87ef0ed36b54",
    "due_date": "2018-05-10",
    "total_in_cents": 99000,
    "customer": "Ford Prefect Company",
    "status": "PENDING"
  },
  {
    "id": "575888f2-fc1f-44ea-b16a-8e459f9eb8e4",
    "due_date": "2018-05-10",
    "total_in_cents": 99000,
    "customer": "Ford Prefect Company",
    "status": "PENDING"
  },
  {
    "id": "f38e2fc4-8778-400a-8580-e38e91e370cc",
    "due_date": "2018-05-10",
    "total_in_cents": 99000,
    "customer": "Ford Prefect Company",
    "status": "PENDING"
  }
]
```

## Ver Detalhes
Endpoint: `GET` http://localhost:8080/rest/bankslips/{id}

Retorna todos os dados de um boleto específico. Se o boleto estiver atrasado, o campo fine (multa) será calculado e exibido.

**Regra de Multa:**
- Até 10 dias de atraso: 0,5% de juros simples por dia.
- Acima de 10 dias de atraso: 1% de juros simples por dia.

#### Requisição:
- Variável: `id` (`UUID` do boleto).
- Corpo: vazio.

#### Resposta:
- status: `200 - OK`
  - exemplo de resposta:
```json
{
  "id": "e24b19a6-4783-4c21-a07c-7b2f727a7908",
  "due_date": "2018-05-10",
  "total_in_cents": 99000,
  "customer": "Ford Prefect Company",
  "status": "PENDING",
  "fine": 990
}
```
- status: `400 - BAD_REQUEST`
Em caso de `UUID` inválida ou mal construída.

- status: `404 - NOT_FOUND`
Em caso de não encontrar nenhum boleto com id correspondente à `UUID` fornecida.

## Pagar um Boleto

Endpoint: `POST` http://localhost:8080/rest/bankslips/{id}/payments

Altera o status de um boleto de `PENDING` para `PAID`.

#### Requisição
- Variável: `id` (`UUID` do boleto).
- Corpo:
  - `payment_date`: Data em que o pagamento foi realizado (yyyy-MM-dd).

Exemplo de requisição
```json
{
  "payment_date":"2025-12-30" 
}
```
#### Resposta
- status: `204 - NO_CONTENT` em caso de sucesso.
- status: `400 - BAD_REQUEST` Em caso de:
  - Corpo vazio;
  - `UUID` inválida ou mal construída;
  - Data no corpo estar no formato errado;
- status: `404 - NOT_FOUND` Em caso de:
  - Não encontrar nenhum boleto com id correspondente à `UUID` fornecida.
- status: `422 - UNPROCESSABLE_CONTENT` Em caso de:
  - Corpo `json` estar mal formado (items faltando, mal escritos)
  - Boleto estar em status diferente de `PENDING`

## Cancelar um Boleto
Endpoint: `DELETE` http://localhost:8080/rest/bankslips/{id}

Altera o status do boleto para CANCELED.
Observação: Apenas boletos com status PENDING podem ser cancelados.

#### Requisição
- Variável: `id` (`UUID` do boleto).
- Corpo: Vazio.

#### Resposta
- status: `204 - NO_CONTENT` em caso de sucesso.
- status: `400 - BAD_REQUEST` Em caso de:
  - `UUID` inválida ou mal construída;
- status: `404 - NOT_FOUND` Em caso de:
  - Não encontrar nenhum boleto com id correspondente à `UUID` fornecida.
- status: `422 - UNPROCESSABLE_CONTENT` Em caso de:
  - Boleto estar em status diferente de `PENDING`

# Observações pessoais

Foi um projeto interessante, pois apesar de simples o executei com a maior atenção possível focando em seguir os princípios de código limpo e os paragigmas OOP. Além disso, foi uma excelente oportunidade de por em prática a abordagem TDD.

Dito isso, acredito que o desafio precisa de algumas revisões. Como por exemplo:
 - No método de criação de boletos, a documentação do desafio diz que o campo `total_in_cents` deve ser um `BigDecimal`, o que é de fato no mínimo curioso visto que o valor já está em centavos. Acredito que o correto seria utilizar `BigInteger`
 - No método de detalhes do boleto, o desafio não estabelece o tipo que deve ser retornado ao usuário. Assim sendo, para fins de coerência, utilizei a mesma lógica de `total_in_cents` e o valor retornado da multa está em centávos. 
 - Ainda no mesmo método, não é informado se devemos arredondar o valor, ou simplesmente devolver frações de centavos. Portanto, tomei a liberdade de incluir o arrendondamento `RoundingMode.HALF_EVEN` que é o padrão bancário pois diminui erros cumulativos.

Para concluir, embora não fosse solicitado pelo desafio, como fiz uso da versão LTS do Java mais recente (jdk-25) achei melhor realizar a portagem para `docker` para facilitar a distribuição e avaliação sem que fosse necessário que o avaliador tivesse que instalar mais uma versão java em sua máquina.