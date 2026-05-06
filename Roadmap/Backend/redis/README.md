# Redis Roadmap

- Roadmap: https://roadmap.sh/redis

## 1. Overview of Redis / What is it?
- 1.1 In-memory Data Structure Store
- 1.2 Key-value Database
- 1.3 Cache
- 1.4 Message Broker

### 1.5 Core Use Cases
- 1.5.1 Caching
- 1.5.2 Real-time Analytics
- 1.5.3 Session Management
- 1.5.4 Pub/Sub Messaging
- 1.5.5 Leaderboards and Counters

### 1.6 Key Features
- 1.6.1 Data Persistence Options
- 1.6.2 Rich Data Structures
- 1.6.3 High Performance and Scalability

### 1.7 Redis vs Other Databases
- 1.7.1 Redis vs SQL/NoSQL DBs
- 1.7.2 When to choose Redis?

## 2. Getting Started with Redis

### 2.1 Installing Redis Locally
- 2.1.1 Using Package Managers
- 2.1.2 Pre-compiled Binaries
- 2.1.3 Using Docker

### 2.2 Running Redis
- 2.2.1 Starting the Server
- 2.2.2 Connecting using Redis CLI
- 2.2.3 Basic Commands / SET, GET

## 3. First Steps

### 3.1 Basic Data Operations
- 3.1.1 and Getting Keys
- 3.1.2 DEL
- 3.1.3 expire
- 3.1.4 TTL

### 3.2 Overview of Data Types
- 3.2.1 Strings, Lists, Sets, Hashes, Sorted Sets

## 4. Core Data Structures

### 4.1 Strings
- 4.1.1 Common Commands: SET, GET, INCR, DECR, APPEND, STRLEN
- 4.1.2 More Commands
- 4.1.3 Usecases

### 4.2 Lists
- 4.2.1 Common Commands: LPUSH, RPUSH, LPOP, RPOP, LRANGE, LINDEX, LLEN, LMOVE
- 4.2.2 More Commands
- 4.2.3 Usecases

### 4.3 Sets
- 4.3.1 Common Commands: SADD, SMEMBERS, SREM, SISMEMBER, SINTER, SCARD, SUNION, SDIFF
- 4.3.2 More Commands
- 4.3.3 Usecases

### 4.4 Hashes
- 4.4.1 Common Commands: HSET, HGET, HGETALL, HDEL, HEXISTS
- 4.4.2 More Commands
- 4.4.3 Usecases

### 4.5 Sorted Sets
- 4.5.1 Common Commands: ZADD, ZRANGE, ZRANGEBYSCORE, ZREM, ZINCRBY, ZRANK, ZCOUNT
- 4.5.2 More Commands
- 4.5.3 Usecases

## 5. Working with Redis

### 5.1 Key Management
- 5.1.1 Naming Conventions
- 5.1.2 Retrieval by Pattern
- 5.1.3 Expiration

### 5.2 Atomicity in Redis

### 5.3 Batch Operations
- 5.3.1 Pipelining
- 5.3.2 MSET / MGET

## 6. Advanced Data Structures

### 6.1 Bitmaps
- 6.1.1 Common Commands: SETBIT, GETBIT, BITCOUNT, BITOP, BITPOS
- 6.1.2 Usecases

### 6.2 HyperLogLog
- 6.2.1 Common Commands: PFADD, PFCOUNT, PFMERGE
- 6.2.2 Usecases

### 6.3 Streams
- 6.3.1 Common Commands: XADD, XREAD, XRANGE, XLEN
- 6.3.2 More Commands
- 6.3.3 Usecases

### 6.4 Geospatial Indexes
- 6.4.1 Common Commands: GEOADD, GEOSEARCH
- 6.4.2 More Commands
- 6.4.3 Usecases

## 7. Pub/Sub
- 7.1 Common Commands: SUBSCRIBE, UNSUBSCRIBE, PUBLISH
- 7.2 More Commands
- 7.3 Usecases

## 8. Transactions
- 8.1 Common Commands: WATCH, EXEC, MULTI
- 8.2 Optimistic Locking

## 9. Lua Scripting
- 9.1 Common Commands: EVAL, EVALSHA
- 9.2 Usecases

## 10. Persistence Options

### 10.1 Snapshotting (RDB)
- 10.1.1 How RDB Works?
- 10.1.2 Configuring Save Interval
- 10.1.3 Usecases / Best Practices

### 10.2 Append-Only File (AOF)
- 10.2.1 How AOF Works?
- 10.2.2 AOF rewrite & compaction
- 10.2.3 Truncation / Corruption
- 10.2.4 Usecases

### 10.3 No Persistence Option

### 10.4 RDB vs AOF Tradeoffs

### 10.5 Hybrid Persistence

### 10.6 Choosing Right Strategy

## 11. Replication / HA
- 11.1 Replication Basics
- 11.2 Redis Sentinel
- 11.3 Clustering

## 12. Security
- 12.1 Authentication
- 12.2 Network Security
- 12.3 SSL/TLS Encryption

## 13. Monitoring / Optimization

### 13.1 Performance Optimization
- 13.1.1 Max Memory Policy
- 13.1.2 Memory Management
- 13.1.3 Slow Log Analysis
- 13.1.4 redis-benchmark

### 13.2 Monitoring
- 13.2.1 Built-in Tools: INFO, MONITOR
- 13.2.2 3rd Party Tools: RedisInsight, RedisCommander

## 14. Redis Modules
- 14.1 RedisJSON
- 14.2 Search
- 14.3 RedisTimeSeries
- 14.4 RedisBloom

## 15. Managing Redis in Production
- 15.1 redis.conf
- 15.2 Important Configurations

### 15.3 Backup and Recovery
- 15.3.1 RDB and AOF Files

### 15.4 Upgrading Redis
- 15.4.1 Minimizing Downtimes

### 15.5 Disaster Recovery

## 16. Redis Enterprise
- 16.1 Enterprise Features
- 16.2 Active-Active geo Distribution
- 16.3 Redis on Flash
- 16.4 Security and Compliance
- 16.5 When to consider enterprise?
