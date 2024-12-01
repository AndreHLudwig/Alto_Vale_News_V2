# Alto Vale News V2

Este projeto consiste em um portal de notícias com backend em Spring Boot e frontend em React.

## Estrutura do Projeto

- `altoValeNewsBackend/`: Servidor REST API desenvolvido em Spring Boot
- `avn-client/`: Interface do usuário desenvolvida em React

## Backend (Spring Boot)

### Pré-requisitos

- Java JDK
- Maven
- Apache Tomcat (Opcional)
- PostgreSQL

### Configuração do Backend

1. **Clone o repositório:**

   ```bash
   git clone https://github.com/AndreHLudwig/Alto_Vale_News_V2.git
   cd Alto_Vale_News_V2/altoValeNewsBackend
   ```

2. **Configure o banco de dados:**

   Edite as propriedades do banco de dados PostgreSQL (`spring.datasource`) em:
   `altoValeNewsBackend/src/main/resources/application.properties`

2.1 **Scripts de definição no banco de dados:**
   
   Execute os scripts SQL localizados em `altoValeNewsBackend/src/main/sql/` no banco de dados escolhido.

3. **Build do projeto:**

   ```bash
   mvn clean install
   ```

4. **Execute o backend (escolha uma das opções):**

   **Opção 1 - Deploy no Tomcat:**
   ```bash
   mvn package
   ```
   - Deploy o arquivo `altoValeNewsBackend-0.0.1-SNAPSHOT.war` (localizado em `altoValeNewsBackend/target`) no seu servidor Tomcat

   **Opção 2 - Execução direta pelo Spring:**
   ```bash
   mvn spring-boot:run
   ```

O backend estará disponível em `http://localhost:8080`

## Frontend (React)

### Pré-requisitos

- npm ou yarn

### Configuração do Frontend

1. **Acesse o diretório do frontend:**
   ```bash
   cd avn-client
   ```

2. **Instale as dependências:**
   ```bash
   npm install
   # ou
   yarn
   ```

3. **Execute o projeto em modo de desenvolvimento:**
   ```bash
   npm run dev
   # ou
   yarn dev
   ```

O frontend estará disponível em `http://localhost:5173` (ou a porta indicada no console)

### Estrutura do Frontend

- `src/components/`: Componentes React reutilizáveis
- `src/pages/`: Páginas da aplicação
- `src/auth/`: Contexto e utilitários de autenticação
- `src/services/`: Serviços e chamadas à API
- `src/styles/`: Arquivos de estilo

## Funcionalidades

- Sistema de autenticação de usuários
- Gerenciamento de publicações
- Sistema de comentários
- Curtidas em publicações e comentários
- Painel administrativo
- Painel do editor
- Gerenciamento de perfil de usuário

## Documentação da API

A documentação da API está disponível através do Swagger UI em:
```
http://localhost:8080/swagger-ui.html
```
(A aplicação backend deve estar em execução para acessar a documentação)

## Usuários do Sistema

O sistema possui quatro níveis de usuário:
- Usuário comum (0)
- Usuário VIP (1)
- Editor (2)
- Administrador (3)