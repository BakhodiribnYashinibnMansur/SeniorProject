# Prompt Engineering Roadmap

- Roadmap: https://roadmap.sh/prompt-engineering

## 1. Introduction

- 1.1 LLMs and how they work?
- 1.2 What is a Prompt?
- 1.3 What is Prompt Engineering?
- 1.4 Models offered by ____
  - 1.4.1 OpenAI
  - 1.4.2 Google
  - 1.4.3 Anthropic
  - 1.4.4 Meta
  - 1.4.5 xAI

## 2. Common Terminology

- 2.1 LLM
- 2.2 Tokens
- 2.3 Context Window
- 2.4 Hallucination
- 2.5 Agents
- 2.6 Prompt Injection
- 2.7 Model Weights / Parameters
- 2.8 Fine-Tuning vs Prompt Engg.
- 2.9 AI vs AGI
- 2.10 RAG

## 3. LLM Configuration

### 3.1 Sampling Parameters

- 3.1.1 Temperature
- 3.1.2 Top-K
- 3.1.3 Top-P

### 3.2 Output Control

- 3.2.1 Max Tokens
- 3.2.2 Stop Sequences

### 3.3 Repetition Penalties

- 3.3.1 Frequency Penalty
- 3.3.2 Presence Penalty

## 4. Prompting Techniques

- 4.1 Structured Outputs
- 4.2 Zero-Shot Prompting
- 4.3 One-Shot / Few-Shot Prompting
- 4.4 System / Role / Contextual
  - 4.4.1 System Prompting
  - 4.4.2 Role Prompting
  - 4.4.3 Contextual Prompting
- 4.5 Step-back Prompting
- 4.6 Chain of Thought (CoT) Prompting
- 4.7 Self-Consistency Prompting
- 4.8 Tree of Thoughts (ToT) Prompting
- 4.9 ReAct Prompting
- 4.10 Automatic Prompt Engineering
  - 4.10.1 Use LLM to generate Prompts

## 5. AI Red Teaming

- 5.1 AI Red Teaming Roadmap

## 6. Prompting Best Practices

- 6.1 Provide few-shot examples for structure or output style you need
- 6.2 Keep your prompts short and concise
- 6.3 Ask for structured output if it helps e.g. JSON, XML, Markdown, CSV etc
- 6.4 Use variables / placeholders in your prompts for easier configuration
- 6.5 Prioritize giving clearer instructions over adding constraints
- 6.6 Control the maximum output length
- 6.7 Experiment with input formats and writing styles
- 6.8 Tune sampling (temperature, top-k, top-p) for determinism vs creativity
- 6.9 Guard against prompt injection; sanitize user text
- 6.10 Automate evaluation; integrate unit tests for outputs
- 6.11 Document and track prompt versions
- 6.12 Optimize for latency & cost in production pipelines
- 6.13 Document decisions, failures, and learnings for future devs
- 6.14 Delimit different sections with triple backticks or XML tags

## 7. Improving Reliability

- 7.1 Prompt Debiasing
- 7.2 Prompt Ensembling
- 7.3 LLM Self Evaluation
- 7.4 Calibrating LLMs
