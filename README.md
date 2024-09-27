
# API Rest de Agendamentos

API Rest desenvolvida para um sistema de agendamento de serviços de farmácia, permitindo o gerenciamento dos usuários e de seus agendamentos. 

## Documentação

http://localhost:8080/swagger-ui/index.html

## Funcionalidades

- Listagem, cadastro e atualização de usuários
- Listagem, criação e cancelamento de agendamentos de serviços

## Tecnologias utilizadas

- Java JDK 21
- Maven 3.9.6
- Springboot 3.2.0
- Springboot Maven Plugin
- Jackson para manipular os arquivos JSON
- JUnit e Mockito para testes unitários

## Requisitos

- Java JDK 21
- Maven

## Instalação e execução da aplicação

1. Clonar o repositório utilizando o comando:
~~~~
git clone {URL do repositório}"
~~~~
2. Abrir o projeto utilizando uma IDE Java;
3. Executar o comando "mvn spring-boot:run";
4. A aplicação será inicializada em: http://localhost:8080.

## Docker

Para executar a aplicação com Docker:

- Certifique-se de ter o Docker instalado na máquina.

### Passo a Passo:

1. Baixe a imagem mais recente da aplicação:

~~~
docker pull victoriastalino/agendamentos-api:1.0.0
~~~

2. Execute um contêiner com a aplicação:

~~~
docker run -p 8080:8080 victoriastalino/agendamentos-api:1.0.0
~~~

Acesse a API através do navegador: http://localhost:8080 ou utilize ferramentas como o Postman.

## Estrutura do projeto

### Models

Contém as classes que representam os agendamentos e os usuários.

- Agendamentos.
- Usuários.

### Repository

Contém as classes que lidam com a persistência dos dados nos arquivos JSON.

- AgendamentosRepository.
- UsuariosRepository.

### Controllers

Contém os controladores da aplicação.

- AgendamentosController: controlador de Agendamentos.
- UsuariosController: controlador de Usuários.

### Services

Contém as classes de serviços que possuem as lógicas de negócios.

- AgendamentosService: Lógicas relacionadas a Agendamentos.
- UsuariosService: lógicas relacionadas a Usuários




