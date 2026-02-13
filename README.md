# 🧠 Brain Health - AI Medical Article Service

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20Hexagonal-blue.svg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
[![Tests](https://img.shields.io/badge/Tests-179%20passed-success.svg)](RELATORIO_FINAL_TESTES.md)
[![Coverage](https://img.shields.io/badge/Coverage-85%25%2B-brightgreen.svg)](JACOCO_COVERAGE_GUIDE.md)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Microserviço de geração de conteúdo médico com IA, buscando artigos em fontes confiáveis e processando com OpenAI.

> ⚠️ **ATENÇÃO**: Este serviço requer uma **chave válida da OpenAI** para funcionar. Sem ela, o serviço não iniciará.
> 
> 🔑 **Obtenha sua chave gratuita em**: https://platform.openai.com/api-keys

---

## 📋 Índice

- [Sobre](#sobre)
- [⚠️ Configuração OpenAI (OBRIGATÓRIA)](#️-configuração-openai-obrigatória)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Executando](#executando)
- [Integração com Check Health](#-integração-com-check-health-service)
- [Testes](#testes)
- [API](#api)
- [Documentação](#documentação)

---

## 🎯 Sobre

O Brain Health é um microserviço que:

1. **Busca** artigos médicos em fontes confiáveis (CREMESP)
2. **Processa** o conteúdo com IA (OpenAI GPT-4)
3. **Gera** conteúdo estruturado com:
   - Introdução
   - Recomendações práticas
   - Conclusão
   - Quiz educativo

**Versão Atual:** 2.0.0 (Refatorado com Clean Architecture)

---

## ⚠️ Configuração OpenAI (OBRIGATÓRIA)

### 🔑 Passo 1: Obter a Chave API

1. Acesse: https://platform.openai.com/api-keys
2. Faça login ou crie uma conta (gratuita para começar)
3. Clique em **"Create new secret key"**
4. Copie a chave (começa com `sk-proj-...`)

⚠️ **Importante**: Guarde a chave em local seguro. Você não poderá vê-la novamente!

### 📝 Passo 2: Configurar no Projeto

#### Se usar Docker Compose (Recomendado):

Na **raiz do projeto** (onde está o `docker-compose.yaml`), edite o arquivo `.env`:

```env
# Arquivo: .env (raiz do projeto)
OPENAI_API_KEY=sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxx
```

#### Se usar execução local:

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxx"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxx"
```

### ✅ Passo 3: Verificar

Após configurar, inicie o serviço:

```bash
# Docker Compose
docker-compose up -d brain-health-service

# Local
mvn spring-boot:run
```

Verifique se iniciou corretamente:

```bash
curl http://localhost:8082/actuator/health
```

Se ver `"status": "UP"`, está funcionando! 🎉

### 🚨 Erros Comuns

#### Erro: "OpenAI API key must be set"
- ❌ Chave não configurada ou vazia
- ✅ Verifique o arquivo `.env` ou variável de ambiente
- ✅ Reinicie o serviço após configurar

#### Erro: "Unauthorized" (401) da OpenAI
- ❌ Chave inválida ou expirada
- ✅ Gere uma nova chave em https://platform.openai.com/api-keys

#### Serviço não inicia no Docker
- ❌ Arquivo `.env` não está na raiz do projeto
- ✅ Crie o arquivo `.env` ao lado do `docker-compose.yaml`
- ✅ Execute: `docker-compose down && docker-compose up -d --build`

---

## 🏗️ Arquitetura

### Clean Architecture + Hexagonal

```
┌─────────────────────────────────────────────────┐
│              API Layer (Controllers)            │
│  ┌──────────────────────────────────────────┐   │
│  │  POST /api/v1/ai/articles/search         │   │
│  └──────────────────────────────────────────┘   │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│              Domain Layer (Core)                │
│  ┌──────────────────────────────────────────┐   │
│  │  ArticleOrchestrationService             │   │
│  │  AIProcessingService                     │   │
│  │  MedicalArticle (Rich Domain Model)      │   │
│  └──────────────────────────────────────────┘   │
└────────────────┬────────────────────────────────┘
                 │
┌────────────────▼────────────────────────────────┐
│         Infrastructure Layer (Adapters)         │
│  ┌──────────────────────────────────────────┐   │
│  │  CremespArticleAdapter                   │   │
│  │  OpenAI Integration                      │   │
│  └──────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### Princípios Aplicados

✅ **SOLID** - Todos os 5 princípios  
✅ **DDD** - Domain-Driven Design  
✅ **Ports & Adapters** - Hexagonal Architecture  
✅ **Clean Architecture** - Camadas bem definidas  

---

## 📦 Pré-requisitos

### Execução com Docker Compose (Recomendado)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop)
- **OpenAI API Key** - [Get Key](https://platform.openai.com/api-keys) ⚠️ **OBRIGATÓRIO**

### Execução Standalone
- **Java 21+** - [Download](https://adoptium.net/)
- **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- **OpenAI API Key** - [Get Key](https://platform.openai.com/api-keys)
- **PostgreSQL** - Para persistência de dados
- **Kafka** - Para mensageria

---

## 🚀 Instalação

### Opção 1: Docker Compose (Integrado - Recomendado)

O Brain Health agora faz parte da arquitetura de microserviços integrada!

#### 1. Configure a chave OpenAI

Na **raiz do projeto principal** (não na pasta brain-health), crie/edite o arquivo `.env`:

```bash
# No diretório raiz (onde está o docker-compose.yaml)
cd ../
```

Edite o arquivo `.env`:
```env
# OpenAI API Key (OBRIGATÓRIO)
OPENAI_API_KEY=sk-proj-xxxxxxxxxxxxxxxxxxxxxxxxxx

# Outros já estão configurados no docker-compose
POSTGRES_DB=checkhealth
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin123
```

⚠️ **Importante**: Sem a chave OpenAI válida, o serviço **não iniciará**!

#### 2. Inicie todos os serviços

```bash
docker-compose up -d --build
```

Isso iniciará:
- ✅ **Config Service** (porta 8888) - Configuração centralizada
- ✅ **Discovery Service** (porta 8761) - Eureka
- ✅ **API Gateway** (porta 8080) - Gateway de entrada
- ✅ **PostgreSQL** (porta 5432) - Banco compartilhado
- ✅ **Kafka + Zookeeper** (porta 9092) - Mensageria
- ✅ **Check Health Service** (porta 8081) - Gerenciamento de metas
- ✅ **Brain Health Service** (porta 8082) - Este serviço!
- ✅ **Kafka UI** (porta 8090) - Interface do Kafka

#### 3. Verifique o status

```bash
# Ver todos os serviços
docker-compose ps

# Ver logs do brain-health
docker-compose logs -f brain-health-service

# Verificar health
curl http://localhost:8082/actuator/health

# Ver no Eureka Dashboard
# Abra: http://localhost:8761
```

---

### Opção 2: Execução Standalone (Desenvolvimento)

#### 1. Clone o repositório

```bash
git clone https://github.com/your-org/brain-health.git
cd brain-health
```

#### 2. Configure variáveis de ambiente

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-key-here"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/checkhealth"
$env:SPRING_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
```

**Linux/Mac:**
```bash
export OPENAI_API_KEY="sk-your-key-here"
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/checkhealth"
export SPRING_KAFKA_BOOTSTRAP_SERVERS="localhost:9092"
```

#### 3. Build do projeto

```bash
mvn clean install
```

---

## ⚙️ Configuração

### Arquitetura de Configuração

O Brain Health agora usa **Spring Cloud Config** para configuração centralizada:

```
Config Service (8888)
  └── config/brain-health-service.yml (configuração centralizada)
      ├── PostgreSQL (banco compartilhado com check-health)
      ├── Kafka (tópicos: goal.created → goal.progress.updated)
      └── Eureka (descoberta de serviços)

Brain Health (8082)
  └── application.yml (configuração local + OpenAI)
      ├── spring.config.import → busca config do Config Service
      ├── OpenAI API Key (obrigatória)
      └── Eureka Client
```

### application.yml (Serviço Local)

```yaml
spring:
  application:
    name: brain-health
  config:
    import: optional:configserver:http://config-service:8888
  
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # ⚠️ OBRIGATÓRIA
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7

server:
  port: 8080

eureka:
  client:
    service-url:
      defaultZone: http://discovery-service:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
```

### brain-health-service.yml (Config Service)

Configuração centralizada gerenciada pelo Config Service:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/checkhealth
    username: admin
    password: admin123
  
  jpa:
    hibernate:
      ddl-auto: update
  
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: brain-health-consumer-group
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

# Tópicos Kafka
kafka:
  topic:
    consumer: goal.created        # Consome de check-health
    producer: goal.progress.updated  # Produz para check-health
```

### Fluxo de Configuração

1. **Brain Health inicia** → Busca configurações do Config Service (8888)
2. **Config Service** → Retorna configurações de banco, kafka, eureka
3. **Variável local** → OPENAI_API_KEY vem do `.env` ou variável de ambiente
4. **Eureka Registration** → Registra-se no Discovery Service (8761)
5. **Pronto para uso** → API disponível na porta 8082

### Variáveis de Ambiente (Docker)

Configuradas no `docker-compose.yaml`:

```yaml
environment:
  SPRING_PROFILES_ACTIVE: default
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/checkhealth
  SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://discovery-service:8761/eureka/
  OPENAI_API_KEY: ${OPENAI_API_KEY}  # Lê do arquivo .env
```

### Legacy Configuration (Standalone)

Para execução standalone sem Config Service:

```properties
# application.properties (modo standalone)

# Spring Application
spring.application.name=brain-health

# OpenAI Configuration
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.chat.options.temperature=0.7

# External Services - CREMESP
external-services.cremesp.base-url=https://cremesp.org.br/pesquisar.php
external-services.cremesp.max-content-length=8000
external-services.cremesp.timeout-seconds=30

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

---

## 🏃 Executando

### Modo 1: Docker Compose (Produção - Recomendado)

**Inicie toda a stack de microserviços:**

```bash
# Na raiz do projeto (onde está o docker-compose.yaml)
docker-compose up -d --build
```

**Acesse os serviços:**
- 🧠 Brain Health API: http://localhost:8082
- 📋 Swagger UI: http://localhost:8082/swagger-ui.html
- 🏥 Health Check: http://localhost:8082/actuator/health
- 🌐 Eureka Dashboard: http://localhost:8761
- 📊 Kafka UI: http://localhost:8090

**Gerenciar serviços:**

```bash
# Ver status de todos os serviços
docker-compose ps

# Ver logs do brain-health
docker-compose logs -f brain-health-service

# Reiniciar apenas o brain-health
docker-compose restart brain-health-service

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (limpa banco)
docker-compose down -v
```

### Modo 2: Desenvolvimento Local

**Com Config Service e Eureka rodando:**

```bash
# Certifique-se que Config Service (8888) e Eureka (8761) estão rodando
mvn spring-boot:run
```

**Standalone (sem Config Service):**

```bash
# Configure as variáveis de ambiente primeiro
export OPENAI_API_KEY="sk-your-key"
export SPRING_CLOUD_CONFIG_ENABLED=false

# Execute
mvn spring-boot:run -Dspring.profiles.active=standalone
```

### Modo 3: Produção (JAR)

```bash
# Build
mvn clean package -DskipTests

# Execute
java -jar target/brain-health-0.0.1-SNAPSHOT.jar \
  -DOPENAI_API_KEY=sk-your-key \
  -DSPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/checkhealth \
  -DSPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### Modo 4: Docker Standalone

```bash
# Build da imagem
docker build -t brain-health:2.0.0 .

# Execute standalone
docker run -p 8082:8080 \
  -e OPENAI_API_KEY=sk-your-key \
  -e SPRING_CLOUD_CONFIG_ENABLED=false \
  brain-health:2.0.0
```

### Verificar se está funcionando

```bash
# Health check
curl http://localhost:8082/actuator/health

# Listar endpoints disponíveis
curl http://localhost:8082/actuator

# Ver no Swagger
# Abra: http://localhost:8082/swagger-ui.html
```

---

## 🔗 Integração com Check Health Service

O Brain Health integra-se com o Check Health através de **Kafka**:

### Fluxo de Integração

```
Check Health (8081)                Brain Health (8082)
      │                                    │
      │ 1. Cria Meta                      │
      ├──────────────────────────────────►│
      │                                    │
      │ 2. Publica: goal.created          │
      │    ├─ goalId                       │
      │    ├─ userId                       │
      │    ├─ goalType                     │
      │    └─ targetValue                  │
      ├──────────────────────────────────►│
      │                                    │
      │                          3. Processa com IA
      │                             ├─ Busca artigos
      │                             ├─ Gera conteúdo
      │                             └─ Cria quiz
      │                                    │
      │ 4. Publica: goal.progress.updated │
      │    ├─ goalId                       │
      │    ├─ article (título, conteúdo)  │
      │    ├─ recommendations              │
      │    └─ quiz (perguntas)             │
      │◄──────────────────────────────────┤
      │                                    │
      │ 5. Atualiza progresso da meta     │
      │                                    │
```

### Tópicos Kafka

- **Consumidor**: `goal.created` - Recebe novas metas do Check Health
- **Produtor**: `goal.progress.updated` - Envia artigos e questionários processados

### Testar Integração

```bash
# 1. Criar uma meta no Check Health
curl -X POST http://localhost:8081/api/goals \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Melhorar Sono",
    "goalType": "SLEEP_IMPROVEMENT",
    "targetValue": 8.0
  }'

# 2. Verificar no Kafka UI que o evento foi publicado
# Abra: http://localhost:8090
# Tópico: goal.created

# 3. Brain Health processa automaticamente

# 4. Verificar resposta no tópico goal.progress.updated
# Kafka UI → Topics → goal.progress.updated
```

---

## 🐳 Kafka Testing Interfaces

O projeto inclui três interfaces web para gerenciar e testar o Kafka:

#### Iniciar todas as interfaces:

**Windows:**
```bash
.\scripts\start-kafka-ui.bat
```

**Linux/Mac:**
```bash
./scripts/start-kafka-ui.sh
```

#### Ou manualmente:

```bash
docker-compose up -d
```

#### Interfaces disponíveis:

1. **Kafka UI (Provectus)** - http://localhost:8080
   - Interface moderna e completa
   - Criar tópicos, enviar mensagens, monitorar consumers

2. **Kafdrop** - http://localhost:9000
   - Interface leve e rápida
   - Visualizar tópicos e mensagens facilmente

3. **Kafka REST Proxy** - http://localhost:8082
   - API REST para produzir/consumir mensagens
   - Ideal para testes automatizados

📖 **Guia completo:** Consulte [KAFKA_TESTING_GUIDE.md](KAFKA_TESTING_GUIDE.md) para exemplos detalhados e instruções de uso.

---

## 🧪 Testes

### 📊 Estatísticas de Testes

| Tipo | Quantidade | Cobertura |
|------|------------|-----------|
| **Testes Unitários** | 93 | Domain + Application |
| **Testes de Integração** | 52 | Controllers + Full Stack |
| **Testes E2E** | 34 | Fluxos Completos |
| **TOTAL** | **179** | **85%+** |

### 🚀 Executar Testes

#### **Todos os testes:**
```bash
mvn test
```

#### **Apenas Unitários:**
```bash
mvn test -Dtest="*Test,!*IntegrationTest,!*E2ETest"
```

#### **Apenas Integração:**
```bash
mvn test -Dtest="*IntegrationTest"
```

#### **Apenas E2E:**
```bash
mvn test -Dtest="*E2ETest"
```

#### **Teste Específico:**
```bash
# Testes unitários do domain
mvn test -Dtest=MedicalArticleTest

# Testes do serviço de orquestração
mvn test -Dtest=SearchAndGenerateArticleUseCaseTest

# Testes E2E de busca de artigos
mvn test -Dtest=ArticleSearchE2ETest
```

### 📊 Cobertura de Código (JaCoCo)

#### **Gerar relatório de cobertura:**

##### Windows:
```bash
scripts\run-tests-with-coverage.bat
```

##### Linux/Mac:
```bash
chmod +x scripts/run-tests-with-coverage.sh
./scripts/run-tests-with-coverage.sh
```

##### Maven:
```bash
mvn clean test jacoco:report
# Relatório HTML: target/site/jacoco/index.html
# Relatório XML: target/site/jacoco/jacoco.xml
```

#### **Verificar mínimos de cobertura:**
```bash
mvn jacoco:check
# Mínimo configurado: 80% linhas, 70% branches
```

### 📚 Documentação de Testes

- 📖 [**Relatório Completo de Testes**](RELATORIO_FINAL_TESTES.md) - 179 testes implementados
- 📊 [**Guia de Cobertura JaCoCo**](JACOCO_COVERAGE_GUIDE.md) - Como usar e interpretar
- 🧪 [**Testes Unitários**](TESTES_UNITARIOS_RESUMO.md) - 93 testes (Domain + Application)
- 🔗 [**Testes de Integração**](TESTES_INTEGRACAO_RESUMO.md) - 52 testes (Controllers + Full Stack)
- 🌐 [**Testes E2E**](TESTES_E2E_RESUMO.md) - 34 testes (Fluxos Completos)

### 🎯 Estrutura de Testes

```
src/test/java/
├── domain/model/
│   └── MedicalArticleTest.java (24 testes)
├── application/
│   ├── mapper/ArticleResponseMapperTest.java (25 testes)
│   └── usecase/
│       ├── SearchAndGenerateArticleUseCaseTest.java (21 testes)
│       └── ProcessKafkaMessageUseCaseTest.java (23 testes)
├── api/controller/
│   ├── AIArticleControllerIntegrationTest.java (19 testes)
│   └── KafkaControllerIntegrationTest.java (18 testes)
├── integration/
│   └── FullStackIntegrationTest.java (15 testes)
└── e2e/
    ├── ArticleSearchE2ETest.java (17 testes)
    └── KafkaMessagingE2ETest.java (17 testes)
```

---

## 📡 API

### Endpoint Principal (Novo - Recomendado)

#### POST /api/v1/ai/articles/search

Busca artigo e gera conteúdo com IA.

**Request:**
```json
{
  "message": "Quais são os benefícios da meditação para saúde mental?"
}
```

**Response (200 OK):**
```json
{
  "title": "Benefícios da Meditação para Saúde Mental",
  "introduction": "A meditação é uma prática milenar...",
  "recommendations": [
    {
      "category": "Prática Diária",
      "description": "Medite por 10-15 minutos diariamente",
      "tips": [
        "Escolha um horário fixo",
        "Encontre local tranquilo",
        "Use aplicativos guiados"
      ]
    }
  ],
  "conclusion": "A meditação traz benefícios comprovados...",
  "context": "Saúde Mental",
  "quizzes": [
    {
      "question": "Qual o tempo mínimo recomendado de meditação diária?",
      "options": ["5 minutos", "10-15 minutos", "30 minutos", "1 hora"],
      "correctAnswer": "10-15 minutos"
    }
  ],
  "sourceLink": "https://cremesp.org.br/article/123",
  "timestamp": "2026-02-10 14:30:00"
}
```

**Erros:**

- `400 Bad Request` - Request inválido
- `404 Not Found` - Artigo não encontrado
- `429 Too Many Requests` - Quota OpenAI excedida
- `500 Internal Server Error` - Erro no processamento

### Endpoint Legado (Deprecated)

#### POST /api/ai/article ⚠️

> **DEPRECATED:** Use `/api/v1/ai/articles/search` em vez deste.  
> Será removido na versão 3.0.0

---

## 📚 Documentação

### Arquitetura e Migrações

- **[REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md)** - Resumo completo da refatoração
- **[MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)** - Guia de migração detalhado

### Estrutura do Projeto

```
brain-health/
├── src/main/java/com/fiap/brain/health/
│   ├── api/                    # Camada de apresentação
│   │   ├── controller/         # REST Controllers
│   │   ├── dto/                # DTOs de request/response
│   │   └── exception/          # Exception handlers
│   │
│   ├── domain/                 # Lógica de negócio
│   │   ├── model/              # Domain models
│   │   ├── service/            # Business services
│   │   ├── mapper/             # Mappers
│   │   ├── repository/         # Repository interfaces (Ports)
│   │   └── exception/          # Domain exceptions
│   │
│   ├── infrastructure/         # Detalhes de implementação
│   │   └── integration/
│   │       └── external/       # Adapters externos
│   │
│   └── config/                 # Configurações
│
├── src/test/java/              # Testes
├── src/main/resources/         # Recursos
├── pom.xml                     # Maven dependencies
└── README.md                   # Este arquivo
```

---

## 🔧 Tecnologias

- **Java 21** - Linguagem
- **Spring Boot 3.3.5** - Framework
- **Spring AI** - Integração OpenAI
- **OpenAI GPT-4** - Processamento IA
- **JSoup** - Parsing HTML
- **Lombok** - Redução boilerplate
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks em testes

---

## 📊 Qualidade do Código

### Métricas (Versão 2.0)

- ✅ Cobertura de testes: 75%+
- ✅ Complexidade ciclomática: < 10
- ✅ Acoplamento: Baixo (Hexagonal)
- ✅ Coesão: Alta (Single Responsibility)
- ✅ Nomenclatura: Inglês correto

### SonarQube (Target)

- Code Smells: 0
- Bugs: 0
- Vulnerabilities: 0
- Technical Debt: < 5%

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Seguir Clean Code principles
- Manter cobertura de testes > 70%
- Documentar APIs com JavaDoc
- Seguir convenções Java (Google Style Guide)

---

## 📝 Changelog

### [2.0.0] - 2026-02-10

#### ✨ Adicionado
- Nova arquitetura Clean + Hexagonal
- Domain model rico (MedicalArticle)
- Ports & Adapters pattern
- Exception handling estruturado
- Testes unitários completos
- Configurações externalizadas

#### ♻️ Modificado
- Nomenclatura corrigida (Inglês)
- Separação de responsabilidades
- Organização de pacotes

#### ⚠️ Deprecated
- `IntelligenceArtificialController`
- `IntelligenceArtificialService`
- Endpoint `/api/ai/article`

### [1.0.0] - 2026-01-15

- Release inicial

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👥 Autores

- **FIAP Tech Challenge Team** - *Desenvolvimento inicial*
- **GitHub Copilot** - *Refatoração arquitetural 2.0*

---

## 🙏 Agradecimentos

- CREMESP - Fonte de artigos médicos
- OpenAI - Processamento de IA
- Spring Team - Framework excelente
- Clean Architecture Community

---

## 📞 Suporte

- **Issues:** [GitHub Issues](https://github.com/your-org/brain-health/issues)
- **Email:** support@brainhealth.com
- **Docs:** [Documentação Completa](https://docs.brainhealth.com)

---

**Desenvolvido com ❤️ e ☕ pela FIAP Tech Challenge Team**
