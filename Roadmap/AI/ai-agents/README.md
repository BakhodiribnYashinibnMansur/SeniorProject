# AI Agents Roadmap

- Roadmap: https://roadmap.sh/ai-agents

## 1. Learn the Pre-requisites
- 1.1 Basic Backend Development
- 1.2 Git and Terminal Usage
- 1.3 REST API Knowledge
- 1.4 Backend Beginner Roadmap
- 1.5 Git and GitHub Roadmap
- 1.6 API Design Roadmap

## 2. LLM Fundamentals

### 2.1 Understand the Basics
- 2.1.1 Streamed vs Unstreamed Responses
- 2.1.2 Reasoning vs Standard Models
- 2.1.3 Fine-tuning vs Prompt Engineering
- 2.1.4 Embeddings and Vector Search
- 2.1.5 Understand the Basics of RAG
- 2.1.6 Pricing of Common Models

### 2.2 Model Families and Licences
- 2.2.1 Open Weight Models
- 2.2.2 Closed Weight Models

### 2.3 Transformer Models and LLMs
- 2.3.1 Model Mechanics
- 2.3.2 Tokenization
- 2.3.3 Context Windows
- 2.3.4 Token Based Pricing

### 2.4 Generation Controls
- 2.4.1 Temperature
- 2.4.2 Top-p
- 2.4.3 Frequency Penalty
- 2.4.4 Presence Penalty
- 2.4.5 Stopping Criteria
- 2.4.6 Max Length

## 3. AI Agents 101

### 3.1 What are AI Agents?

### 3.2 What are Tools?

### 3.3 Agent Loop
- 3.3.1 Perception / User Input
- 3.3.2 Reason and Plan
- 3.3.3 Acting / Tool Invocation
- 3.3.4 Observation & Reflection

### 3.4 Example Usecases
- 3.4.1 Personal assistant
- 3.4.2 Code generation
- 3.4.3 Data analysis
- 3.4.4 Web Scraping / Crawling
- 3.4.5 NPC / Game AI

## 4. Prompt Engineering

### 4.1 What is Prompt Engineering

### 4.2 Writing Good Prompts
- 4.2.1 Be specific in what you want
- 4.2.2 Provide additional context
- 4.2.3 Use relevant technical terms
- 4.2.4 Use Examples in your Prompt
- 4.2.5 Iterate and Test your Prompts
- 4.2.6 Specify Length, format etc
- 4.2.7 Prompt Engineering Roadmap

## 5. Tools / Actions

### 5.1 Tool Definition
- 5.1.1 Name and Description
- 5.1.2 Input / Output Schema
- 5.1.3 Error Handling
- 5.1.4 Usage Examples

### 5.2 Examples of Tools
- 5.2.1 Web Search
- 5.2.2 Code Execution / REPL
- 5.2.3 Database Queries
- 5.2.4 API Requests
- 5.2.5 Email / Slack / SMS
- 5.2.6 File System Access

## 6. Model Context Protocol (MCP)

### 6.1 Core Components
- 6.1.1 MCP Hosts
- 6.1.2 MCP Client
- 6.1.3 MCP Servers

### 6.2 Creating MCP Servers

### 6.3 Deployment Modes
- 6.3.1 Local Desktop
- 6.3.2 Remote / Cloud

## 7. Agent Memory

### 7.1 What is Agent Memory?

### 7.2 Episodic vs Semantic Memory

### 7.3 Short Term Memory
- 7.3.1 Within Prompt

### 7.4 Long Term Memory
- 7.4.1 Vector DB / SQL / Custom

### 7.5 Maintaining Memory
- 7.5.1 RAG and Vector Databases
- 7.5.2 User Profile Storage
- 7.5.3 Summarization / Compression
- 7.5.4 Forgetting / Aging Strategies

## 8. Agent Architectures

### 8.1 Common Architectures
- 8.1.1 RAG Agent
- 8.1.2 ReAct (Reason + Act)
- 8.1.3 Chain of Thought (CoT)

### 8.2 Other Architecture Patterns
- 8.2.1 Planner Executor
- 8.2.2 DAG Agents
- 8.2.3 Tree-of-Thought

## 9. Building Agents

### 9.1 Manual (from scratch)
- 9.1.1 Direct LLM API calls
- 9.1.2 Implementing the agent loop
- 9.1.3 Parsing model output
- 9.1.4 Error & Rate-limit handling

### 9.2 LLM Native "Function Calling"
- 9.2.1 OpenAI Functions Calling
- 9.2.2 OpenAI Assistant API
- 9.2.3 Gemini Function Calling
- 9.2.4 Anthropic Tool Use

### 9.3 Building Using Frameworks
- 9.3.1 Langchain
- 9.3.2 LlamaIndex
- 9.3.3 Haystack
- 9.3.4 AutoGen
- 9.3.5 CrewAI
- 9.3.6 Smol Depot

## 10. Evaluation and Testing

### 10.1 Metrics to Track

### 10.2 Unit Testing for Individual Tools

### 10.3 Integration Testing for Flows

### 10.4 Human in the Loop Evaluation

### 10.5 Frameworks
- 10.5.1 LangSmith
- 10.5.2 DeepEval
- 10.5.3 Ragas

## 11. Debugging and Monitoring

### 11.1 Structured logging & tracing

### 11.2 Observability Tools
- 11.2.1 LangSmith
- 11.2.2 Helicone
- 11.2.3 LangFuse
- 11.2.4 openllmetry

## 12. Security & Ethics
- 12.1 Prompt Injection / Jailbreaks
- 12.2 Tool sandboxing / Permissioning
- 12.3 Data Privacy + PII Redaction
- 12.4 Bias & Toxicity Guardrails
- 12.5 Safety + Red Team Testing
