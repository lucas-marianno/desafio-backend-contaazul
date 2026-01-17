# Desafio Desenvolvedor Backend - ContaAzul
Documento original do desafio [aqui](https://drive.google.com/file/d/1DvjRBTvnHwlUOoNBwAsvoRF6aKqYm7pP/view).

## Pré-requisitos

```
● Java 8+
● Utilizar ferramentas de build Maven ou Gradle
● Seguir os padrões REST
● Banco de dados H2
● Escolha a stack de sua preferência: Spring Boot ou Wildfly Swarm
● README.md bem descrito com instruções para rodar o projeto
● Git
```

## Avaliação

O que vamos considerar na avaliação do desafio:
Muito importante, nessa ordem:
- Clean Code, SOLID, DRY
- Implementar 100 % da especificação do desafio abaixo
- Instruções de como rodar o projeto descrito no README.md
Importante, nessa ordem:
- Criação de testes automatizados (teste unitário e teste de integração)
Dicas opcionais, se quiser colocar a cereja no bolo:
- Uso de bibliotecas auxiliares, soluções inovadoras e ferramentas que auxiliem build, deploy
etc, documentação da API.

## Desafio

O objetivo do desafio é construir uma API REST para geração de boletos que será consumido por
um módulo de um sistema de gestão financeira de microempresas.
No final do desafio vamos ter os seguintes endpoints para:
- Criar boleto
- Listar boletos
- Ver detalhes
- Pagar um boleto
- Cancelar um boleto
Siga as especificações das páginas seguintes e boa sorte ;)


## Criar boleto

**Endpoint:** ​ ​POST [http://localhost:8080/rest/bankslips](http://localhost:8080/rest/bankslips)

Esse método deve receber um novo boleto e inseri-lo em um banco de dados para ser consumido
pela própria API. Todos os campos são obrigatórios.

Request:
```
{
​"due_date"​:​"2018-01-01"​,
​"total_in_cents"​:​"100000"​,
​"customer"​:​"Trillian Company"
}
```
Retorno:
```
{
"id"​:​"84e8adbf-1a14-403b-ad73-d78ae19b59bf"​,
​"due_date"​:​"2018-01-01"​,
​"total_in_cents"​:​"100000"​,
​"customer"​:​"Trillian Company"​,
​"status"​:​"PENDING"
}
```
- 201 : Bankslip created
- 400 : Bankslip not provided in the request body
- 422 : Invalid bankslip provided.The possible reasons are:
  - A field of the provided bankslip was null or with invalid values

|Campo | Tipo | Formato |
| --- | --- | ---|
|id | UUID | UUID|
|due_date| date| yyyy-MM-dd|
|total_in_cents | BigDecimal | valor em centavos|
|customer |string | |
|status | string | PENDING, PAID, CANCELED|

## Lista de boletos

**Endpoint:** ​GET ​http://localhost:8080/rest/bankslips/

Esse método da API deve retornar uma lista de boletos em formato JSON.

Ex:
```
[
{
​"id"​:​"84e8adbf-1a14-403b-ad73-d78ae19b59bf"​,
​"due_date"​:​"2018-01-01"​,
​"total_in_cents"​:​"100000"​,
​"customer"​:​"Ford Prefect Company"​,
​"status"​:​"PENDING"
},
{
​"id"​:​"c2dbd236-3fa5-4ccc-9c12-bd0ae1d6dd89"​,
​"due_date"​:​"2018-02-01"​,
​"total_in_cents"​:​"200000"​,
​"customer"​:​"Zaphod Company"​,
​"status"​:​"PAID"
}
]
```

## Ver detalhes de um boleto

**Endpoint:** ​GET [http://localhost:8080/rest/bankslips/{id}](http://localhost:8080/rest/bankslips/{id})

Esse método da API deve retornar um boleto filtrado pelo id, caso o boleto estiver atrasado deve
ser calculado o valor da multa.

Regra para o cálculo da multa aplicada por dia para os ​boletos atrasados:

- Até 10 dias: Multa de 0,5% (Juros Simples)
- Acima de 10 dias: Multa de 1% (Juros Simples)

Retorno:
```
{
​"id"​:​"c2dbd236-3fa5-4ccc-9c12-bd0ae1d6dd89"​,
​"due_date"​:​"2018-05-10"​,
​"payment_date"​:​"2018-05-13"​,
​"total_in_cents"​:​"99000"​,
​"customer"​:​"Ford Prefect Company"​,
​"fine"​:​"1485"​,
​"status"​:​"PAID"
}
```

**Mensagens de resposta**
- 200 : Ok
- 404 : Bankslip not found with the specified id

## Pagar um boleto

Esse método da API deve alterar o status do boleto para PAID.

**Endpoint** ​: POST [http://localhost:8080/rest/bankslips/{id}/payments](http://localhost:8080/rest/bankslips/{id}/payments)

Request:
```
{
​"payment_date"​:​"2018-06-30"
}
```

Campo Tipo Formato
payment_date Date `yyyy-MM-dd`

|Campo| Tipo| Formato|
|---|---|---|
|payment_date| Date| yyyy-MM-dd|

**Mensagens de resposta**
- 204 : No content
- 404 : Bankslip not found with the specified id

## Cancelar um boleto

Esse método da API deve alterar o status do boleto para ​CANCELED​.

**Endpoint:** ​ ​DELETE [http://localhost:8080/rest/bankslips/{id}](http://localhost:8080/rest/bankslips/{id})

**Mensagens de resposta**
- 204 : Bankslip canceled
- 404 : Bankslip not found with the specified id
