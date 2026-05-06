# PostgreSQL DBA Roadmap

- Roadmap: https://roadmap.sh/postgresql-dba

## 1. Introduction
- 1.1 What are Relational Databases?
- 1.2 RDBMS Benefits and Limitations
- 1.3 PostgreSQL vs Other RDBMS
- 1.4 PostgreSQL vs NoSQL Databases

### 1.5 Object Model
- 1.5.1 Queries
- 1.5.2 Data Types
- 1.5.3 Rows
- 1.5.4 Columns
- 1.5.5 Tables
- 1.5.6 Schemas
- 1.5.7 Databases

### 1.6 Basic RDBMS Concepts

#### 1.6.1 Relational Model
- 1.6.1.1 Domains
- 1.6.1.2 Attributes
- 1.6.1.3 Tuples
- 1.6.1.4 Relations
- 1.6.1.5 Constraints
- 1.6.1.6 NULL

#### 1.6.2 High Level Database Concepts
- 1.6.2.1 ACID
- 1.6.2.2 MVCC
- 1.6.2.3 Transactions
- 1.6.2.4 Write-ahead Log
- 1.6.2.5 Query Processing

## 2. Installation and Setup
- 2.1 Using Docker
- 2.2 Package Managers
- 2.3 Connect using `psql`
- 2.4 Deployment in Cloud

### 2.5 Managing Postgres
- 2.5.1 Using `systemd`
- 2.5.2 Using `pg_ctl`
- 2.5.3 Using `pg_ctlcluster`

## 3. Learn SQL

### 3.1 DDL Queries
- 3.1.1 For Schemas
- 3.1.2 For Tables
- 3.1.3 Data Types

### 3.2 DML Queries
- 3.2.1 Querying Data
- 3.2.2 Filtering Data
- 3.2.3 Modifying Data
- 3.2.4 Joining Tables

### 3.3 Import / Export Using `COPY`

### 3.4 Advanced Topics
- 3.4.1 Transactions
- 3.4.2 Subqueries
- 3.4.3 CTE
- 3.4.4 Lateral Join
- 3.4.5 Grouping
- 3.4.6 Set Operations

## 4. Configuring
- 4.1 Resource Usage
- 4.2 Write-ahead Log
- 4.3 Vacuums
- 4.4 Replication
- 4.5 Query Planner
- 4.6 Checkpoints / Background Writer
- 4.7 Reporting Logging & Statistics
- 4.8 Adding Extra Extensions

## 5. Security

### 5.1 Object Privileges
- 5.1.1 Grant / Revoke
- 5.1.2 Default Privileges

### 5.2 Advanced Topics
- 5.2.1 Row-Level Security
- 5.2.2 SELinux

### 5.3 Authentication Models
- 5.3.1 Roles
- 5.3.2 pg_hba.conf
- 5.3.3 SSL Settings

## 6. Infrastructure Skills

### 6.1 Anonymization
- 6.1.1 PostgreSQL Anonymizer

### 6.2 Upgrade Procedures
- 6.2.1 Using `pg_upgrade`
- 6.2.2 Using Logical Replication

### 6.3 Learn to Automate
- 6.3.1 Shell Scripts
- 6.3.2 Any Programming Language

#### 6.3.3 Configuration Management
- 6.3.3.1 Ansible
- 6.3.3.2 Salt
- 6.3.3.3 Puppet
- 6.3.3.4 Chef

### 6.4 Cluster Management
- 6.4.1 Patroni
- 6.4.2 Patroni Alternatives

### 6.5 Kubernetes Deployment
- 6.5.1 Simple Stateful Setup
- 6.5.2 Helm
- 6.5.3 Operators

### 6.6 Replication
- 6.6.1 Logical Replication
- 6.6.2 Streaming Replication

### 6.7 Connection Pooling
- 6.7.1 PgBouncer
- 6.7.2 PgBouncer Alternatives

### 6.8 Monitoring
- 6.8.1 Prometheus
- 6.8.2 Zabbix
- 6.8.3 check_pgactivity
- 6.8.4 temBoard
- 6.8.5 check_pgbackrest

### 6.9 Load Balancing / Discovery
- 6.9.1 HAProxy
- 6.9.2 Consul
- 6.9.3 KeepAlived
- 6.9.4 Etcd

### 6.10 Backup & Recovery Tools

#### 6.10.1 3rd Party
- 6.10.1.1 barman
- 6.10.1.2 WAL-G
- 6.10.1.3 pgbackrest
- 6.10.1.4 pg_probackup

#### 6.10.2 Builtin Tools
- 6.10.2.1 pg_dump
- 6.10.2.2 pg_dumpall
- 6.10.2.3 pg_restore
- 6.10.2.4 pg_basebackup

#### 6.10.3 Backup Validation Procedures

### 6.11 Resource Usage / Provisioning / Capacity Planning

## 7. Application Skills

### 7.1 Migrations
- 7.1.1 Practical Patterns / Antipatterns
- 7.1.2 Migration Related Tools

### 7.2 Data and Processing
- 7.2.1 Bulk Loading / Processing Data
- 7.2.2 Data Partitioning
- 7.2.3 Sharding Patterns
- 7.2.4 Normalization / Normal Forms

### 7.3 Queues
- 7.3.1 Patterns / Antipatterns
- 7.3.2 PgQ

### 7.4 Advanced Topics

#### 7.4.1 Low Level Internals
- 7.4.1.1 Processes & Memory Architecture
- 7.4.1.2 Vacuum Processing
- 7.4.1.3 Buffer Management
- 7.4.1.4 Lock Management
- 7.4.1.5 Physical Storage and File Layout
- 7.4.1.6 System Catalog

## 8. Fine-grained Tuning
- 8.1 Per-User, Per-Database Setting
- 8.2 Storage Parameters
- 8.3 Workload-Dependant Tuning
  - 8.3.1 OLTP
  - 8.3.2 OLAP
  - 8.3.3 HTAP

## 9. Advanced SQL
- 9.1 PL/pgSQL
- 9.2 Procedures and Functions
- 9.3 Triggers
- 9.4 Recursive CTE
- 9.5 Aggregate and Window functions

## 10. Troubleshooting Techniques

### 10.1 Operating System Tools
- 10.1.1 top
- 10.1.2 sysstat
- 10.1.3 iotop

### 10.2 Profiling Tools
- 10.2.1 gdb
- 10.2.2 strace
- 10.2.3 ebpf
- 10.2.4 perf-tools
- 10.2.5 Core Dumps

### 10.3 Log Analysis
- 10.3.1 pgBadger
- 10.3.2 pgCluu
- 10.3.3 awk
- 10.3.4 grep
- 10.3.5 sed

### 10.4 Postgres System Views
- 10.4.1 pg_stat_activity
- 10.4.2 pg_stat_statements

### 10.5 Postgres Tools
- 10.5.1 pgcenter

### 10.6 Query Analysis
- 10.6.1 EXPLAIN
- 10.6.2 Depesz
- 10.6.3 PEV2
- 10.6.4 Tensor
- 10.6.5 explain.dalibo.com

### 10.7 Techniques
- 10.7.1 USE
- 10.7.2 RED
- 10.7.3 Golden Signals

## 11. SQL Optimization Techniques
- 11.1 SQL Query Patterns / Anti-patterns
- 11.2 Schema Design Patterns / Anti-patterns

### 11.3 Indexes and their Usecases
- 11.3.1 B-Tree
- 11.3.2 BRIN
- 11.3.3 GiST
- 11.3.4 Hash
- 11.3.5 SP-GiST
- 11.3.6 GIN

## 12. Get Involved in Development
- 12.1 Mailing Lists
- 12.2 Reviewing Patches
- 12.3 Writing Patches
