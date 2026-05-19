# Processor API 🧠

Serviço de Inteligência Artificial responsável por analisar diagramas de arquitetura de software e gerar relatórios estruturados contendo componentes identificados, riscos e recomendações. 

Este projeto faz parte da arquitetura orientada a eventos do Hackaton e atua como o "cérebro" do sistema, utilizando o **Google Gemini** através do framework **LangChain4j**.

## 🚀 Arquitetura e Fluxo

1. **Consumo:** Escuta a fila SQS `diagram-process` aguardando novos diagramas em formato Base64.
2. **Processamento:** Envia a imagem e um *System Prompt* rigoroso para o Google Gemini.
3. **Estruturação:** Converte a resposta textual/markdown da IA em um objeto JSON estrito (DTO).
4. **Publicação:** Envia o resultado da análise (ou mensagem de erro) para a fila SQS `diagram-status-update`.

## 🛠️ Stack Tecnológica

- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.4.2
- **IA/LLM:** LangChain4j + Google Gemini (Modelo: `gemini-3.1-flash-lite`)
- **Mensageria:** Spring Cloud AWS (SQS)
- **Testes:** JUnit 5, Mockito, JaCoCo (Cobertura > 90%), PiTest (Testes de Mutação)
- **Containerização:** Docker (Multi-stage build com Eclipse Temurin JRE)

## ⚙️ Configuração (Variáveis de Ambiente)

Para rodar o projeto, configure as seguintes variáveis no seu ambiente ou `.env`:

| Variável | Descrição | Exemplo |
|----------|-------------|---------|
| `GEMINI_API_KEY` | Chave de API do Google AI Studio | `AIzaSy...` |
| `AWS_REGION` | Região da AWS | `us-east-1` |
| `AWS_ACCESS_KEY_ID` | Chave de acesso AWS | `AKIA...` |
| `AWS_SECRET_ACCESS_KEY` | Secret da AWS | `...` |
| `AWS_SESSION_TOKEN` | Token de Sessão (Para AWS Academy) | `...` |
| `SQS_PROCESS_URL` | URL da fila de consumo | `https://sqs.../diagram-process` |
| `SQS_STATUS_URL` | URL da fila de publicação | `https://sqs.../diagram-status-update` |

## 💻 Como Executar Localmente

### Via Maven
Certifique-se de estar na pasta raiz do projeto (`hackaton_processor_api`) e de ter o Java 21 instalado.

```bash
# Exportar variáveis (exemplo no Linux/Mac)
export GEMINI_API_KEY="sua-chave"
export AWS_REGION="us-east-1"
# ... exporte as demais chaves AWS ...

# Rodar a aplicação
mvn spring-boot:run
```

### Via Docker
O projeto possui um Dockerfile otimizado com *Multi-stage Build*.

```bash
# Build da imagem
docker build -t processor-api .

# Execução do container
docker run -p 8080:8080 \
  -e GEMINI_API_KEY="sua-chave" \
  -e AWS_REGION="us-east-1" \
  -e AWS_ACCESS_KEY_ID="sua-chave-aws" \
  -e AWS_SECRET_ACCESS_KEY="seu-secret-aws" \
  -e SQS_PROCESS_URL="url-fila-process" \
  -e SQS_STATUS_URL="url-fila-status" \
  processor-api
```

## 🧪 Testes e Qualidade

O projeto possui rigorosa validação de qualidade seguindo o padrão AAA.

Para executar os testes unitários e gerar os relatórios de cobertura (JaCoCo) e mutação (PiTest):

```bash
mvn clean test pitest:mutationCoverage
```

- **Relatório JaCoCo:** `target/site/jacoco/index.html`
- **Relatório PiTest:** `target/pit-reports/index.html`
