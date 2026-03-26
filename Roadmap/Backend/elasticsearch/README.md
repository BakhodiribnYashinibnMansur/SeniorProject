# Elasticsearch Roadmap

- Roadmap: https://roadmap.sh/elasticsearch
- PDF: [elasticsearch.pdf](./elasticsearch.pdf)

## 1. Introduction
- 1.1 What is Elasticsearch
- 1.2 Search Engines vs Relational DBs
- 1.3 The ELK Stack
- 1.4 Elasticsearch Usecases

### 1.5 Pre-requisites
- 1.5.1 JSON
- 1.5.2 REST API Basics

### 1.6 Environment Setup
- 1.6.1 Running with Docker
- 1.6.2 Elastic Cloud
- 1.6.3 Kibana Console

## 2. Core Architecture

### 2.1 Logical Concepts
- 2.1.1 Cluster (System)
- 2.1.2 Node (Instance)
- 2.1.3 Index (Database)
- 2.1.4 Document (Row)
- 2.1.5 ID (Primary Key)

### 2.2 Physical Layout
- 2.2.1 Master-Elegible Nodes
- 2.2.2 Data Nodes
- 2.2.3 Coordinating Nodes

### 2.3 Sharding & Scaling
- 2.3.1 Primary Shards
- 2.3.2 Replica Shards
- 2.3.3 The "Split Brain" Problem

## 3. Data Modelling

### 3.1 Mappings
- 3.1.1 Explicit
- 3.1.2 Dynamic
- 3.1.3 Mapping Explosion

### 3.2 Data Types

#### 3.2.1 Code Data Types
- 3.2.1.1 Numeric
- 3.2.1.2 Boolean
- 3.2.1.3 Dates
- 3.2.1.4 Geo Points

#### 3.2.2 Text vs Keyword
- 3.2.2.1 Text
- 3.2.2.2 Keyword

#### 3.2.3 Advanced Types
- 3.2.3.1 Object
- 3.2.3.2 Nested
- 3.2.3.3 Flattened

## 4. Data Ingestion

### 4.1 CRUD Operations
- 4.1.1 Create Index
- 4.1.2 Index Document
- 4.1.3 Delete Index
- 4.1.4 Get Document
- 4.1.5 Update Document
- 4.1.6 Delete Documents

### 4.2 Bulk Operations
- 4.2.1 Bulk index
- 4.2.2 Optimizing Bulk Indexing

### 4.3 Migrations & Repair
- 4.3.1 Bulk index
- 4.3.2 Update by Query
- 4.3.3 Delete by Query

## 5. Search Fundamentals

### 5.1 Query Languages
- 5.1.1 Query DSL
- 5.1.2 ES|QL
- 5.1.3 EQL
- 5.1.4 SQL
- 5.1.5 KQL
- 5.1.6 Lucene

### 5.2 Search Contexts
- 5.2.1 Query
- 5.2.2 Filter

### 5.3 Leaf vs Compound Queries

#### 5.3.1 Leaf Queries
- 5.3.1.1 Match Query
- 5.3.1.2 Term Query
- 5.3.1.3 Range Query
- 5.3.1.4 Exists Query
- 5.3.1.5 ID Query
- 5.3.1.6 Prefix Query
- 5.3.1.7 Wildcard Query

#### 5.3.2 Bool Queries (Compound Queries)
- 5.3.2.1 must
- 5.3.2.2 should
- 5.3.2.3 filter
- 5.3.2.4 must_not

### 5.4 Controlling Search Results
- 5.4.1 Pagination
- 5.4.2 Source Filtering
- 5.4.3 Sorting
- 5.4.4 Highlighting

## 6. How Search Works
- 6.1 The Inverted Index
- 6.2 Doc values
- 6.3 fielddata

## 7. Text Analysis

### 7.1 Search Analyzer
- 7.1.1 The Analyze API
- 7.1.2 Standard Analyzer
- 7.1.3 Custom Analyzers

## 8. Aggregations

### 8.1 Metric Aggregations
- 8.1.1 Value Count
- 8.1.2 Cardinality
- 8.1.3 Avg / Sum / Min / Max
- 8.1.4 Stats / Extended Stats

### 8.2 Bulk Aggregations
- 8.2.1 Terms
- 8.2.2 Range / Date Range
- 8.2.3 Histogram
- 8.2.4 Filter Aggregations

### 8.3 Advanced Aggregations
- 8.3.1 Nested Aggregations
- 8.3.2 Pipeline Aggregations

## 9. Transformations
- 9.1 Transform API
- 9.2 Pivot
- 9.3 Latest

## 10. Relevance & Tuning
- 10.1 Document Scoring
- 10.2 Understanding Similarity
- 10.3 BM25 algorithm
- 10.4 Improve Query Precision
- 10.5 Boosting Queries
- 10.6 Function Score Query
- 10.7 Match Phrase Query
- 10.8 Synonyms Graph

## 11. Production

### 11.1 Cluster Management
- 11.1.1 CAT API
- 11.1.2 Segment Merging
- 11.1.3 Cluster Monitoring
- 11.1.4 Cross-cluster Replication
- 11.1.5 Autoscaling

### 11.2 Data Life Cycle
- 11.2.1 ILM
- 11.2.2 Rollover Policies

### 11.3 Data Safety
- 11.3.1 Data Tiers
- 11.3.2 Snapshots & restore
- 11.3.3 SLM

### 11.4 Security
- 11.4.1 Authentication
- 11.4.2 Roles & Users
- 11.4.3 API Keys

## 12. Advanced Features
- 12.1 AI-Powered Search
- 12.2 Vector Search
- 12.3 Semantic Search
- 12.4 Hybrid Search
