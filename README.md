#  DeliveryTech API

API RESTful desenvolvida com Spring Boot 3 e Java 21 para gerenciar um sistema de delivery completo. Este projeto simula as funcionalidades principais de plataformas como iFood e Uber Eats, incluindo autenticação JWT, cache, monitoramento, CI/CD e muito mais.

## 1. Como instalar e executar o projeto

### Pré-requisitos

- Java JDK 21
- Maven 3.8+
- Docker e Docker Compose (opcional, para execução containerizada)
- Redis (para cache)
- Git

### Instalação

1. Clone o repositório:
```bash
git clone https://github.com/BrunaCelestino/deliverytech.git
cd deliverytech
```

2. Instale as dependências:
```bash
mvn clean install
```

### Execução

Você pode executar o projeto de duas maneiras:

#### Usando Maven:
```bash
mvn spring-boot:run
```

#### Usando Docker:
```bash
docker-compose up
```

A API estará disponível em: `http://localhost:8080`
A documentação Swagger/OpenAPI estará disponível em: `http://localhost:8080/swagger-ui.html`

---

## Funcionalidades

- Cadastro e login de usuários com JWT
- Controle de acesso por perfis (CLIENTE, RESTAURANTE, ADMIN, ENTREGADOR)
- Cadastro de clientes, restaurantes, produtos e pedidos
- Listagem de produtos por restaurante
- Criação de pedidos com itens e cálculo do total
- Atualização de status de pedido
- Cache com Spring Cache
- Testes automatizados com JUnit e Mockito
- Documentação com Swagger/OpenAPI
- Banco de dados em memória com H2
- Containerização com Docker e orquestração com Docker Compose
- Pipeline CI/CD com GitHub Actions (sugestão)

---

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.x
- Spring Data JPA
- Spring Security + JWT
- Spring Validation
- H2 Database
- SpringDoc OpenAPI (Swagger)
- Docker + Docker Compose
- JUnit 5 + Mockito

---

## 2. Estrutura de pastas e organização

O projeto segue uma arquitetura em camadas, organizada da seguinte forma:

```
src/main/java/com/deliverytech/
├── config/              # Configurações do Spring e OpenAPI
├── controller/          # Controladores REST
├── dto/                 # Objetos de Transferência de Dados
│   ├── request/        # DTOs de entrada
│   └── response/       # DTOs de saída
├── exception/           # Tratamento de exceções
├── model/              # Entidades JPA
├── repository/         # Repositórios Spring Data JPA
├── security/           # Configurações de segurança e JWT
└── service/            # Lógica de negócio
    └── impl/          # Implementações dos serviços
```

## 3. Bibliotecas e dependências utilizadas

### Spring Boot Stack (v3.2.5)
- **spring-boot-starter-web**: Desenvolvimento de API REST
- **spring-boot-starter-data-jpa**: Persistência de dados com JPA
- **spring-boot-starter-security**: Segurança e autenticação
- **spring-boot-starter-validation**: Validação de dados
- **spring-boot-starter-actuator**: Monitoramento da aplicação
- **spring-boot-starter-cache**: Suporte a cache

### Banco de Dados e Cache
- **H2 Database**: Banco de dados em memória para desenvolvimento
- **Redis**: Sistema de cache distribuído para otimização

### Segurança e Documentação
- **jjwt**: Implementação de JSON Web Tokens (JWT)
- **springdoc-openapi**: Documentação automática da API com Swagger/OpenAPI

### Utilitários e Monitoramento
- **Lombok**: Redução de boilerplate code
- **Micrometer**: Monitoramento e observabilidade

## 4. Fluxos principais e arquitetura

### Principais fluxos:

1. **Autenticação e Autorização**
   - Registro de usuários (clientes/restaurantes)
   - Login com geração de JWT
   - Autorização baseada em roles (CLIENTE, RESTAURANTE, ADMIN)

2. **Fluxo de Pedidos**
   - Cliente faz pedido
   - Restaurante recebe e processa pedido
   - Atualização de status
   - Entrega ao cliente

3. **Gestão de Restaurante**
   - Cadastro de produtos
   - Gerenciamento de cardápio
   - Controle de pedidos

### Diagrama de Arquitetura
```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Cliente    │     │     API      │     │    Redis     │
│   Browser    │────>│  Spring Boot │────>│    Cache     │
└──────────────┘     └──────────────┘     └──────────────┘
                           │
                           │
                     ┌──────────────┐
                     │     H2       │
                     │  Database    │
                     └──────────────┘
```

## Documentação da API

A documentação completa da API está disponível via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```
