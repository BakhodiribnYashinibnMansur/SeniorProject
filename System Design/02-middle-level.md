# 🟡 Middle Level (251–500)

[← Junior](./01-junior-level.md) · [README](./README.md) · [Senior →](./03-senior-level.md)

> **Fokus:** Real-world system designs (Twitter feed, Instagram, Bitly, Yelp), trade-offs, sharding, replication, MQ patterns, caching strategies, microservices communication, basic distributed concepts.
>
> **Kim uchun:** 2–5 yil tajriba, mid-level engineer.
> **Vaqt/savol:** 30–45 daqiqa, whiteboard sketch + komponentlar bilan.

---

## 🧠 Core Distributed Concepts (251–280)

251. Explain the CAP theorem and the three trade-offs.
252. Explain the PACELC theorem.
253. What is BASE in contrast with ACID?
254. Explain strong consistency vs eventual consistency with examples.
255. What is read-your-writes consistency?
256. What is monotonic read consistency?
257. What is causal consistency?
258. What is linearizability?
259. What is serializability?
260. What is the difference between linearizability and serializability?
261. Explain quorum (R + W > N) in distributed databases.
262. What is consistent hashing and why is it useful?
263. How do virtual nodes (vnodes) help in consistent hashing?
264. What is a hot shard and how do you mitigate it?
265. What is split-brain in distributed systems?
266. What are the trade-offs of strong vs eventual consistency in a chat app?
267. What is "exactly-once" semantics and is it achievable?
268. Explain at-most-once vs at-least-once vs exactly-once.
269. What is back-pressure?
270. What is fan-out (read vs write)?
271. What is the difference between push vs pull models?
272. What is the difference between sync replication and async replication?
273. What is data locality and why is it important?
274. What is leader–follower replication?
275. What is multi-master replication and what are its conflicts?
276. What is replication lag?
277. What is geo-replication?
278. What is horizontal vs vertical partitioning?
279. What is sharding by range vs hash vs directory?
280. What is a "shard key" and how do you pick one?

## 🛢️ Database & Sharding (281–320)

281. When would you choose Cassandra over Postgres?
282. When would you choose MongoDB over MySQL?
283. When would you use DynamoDB?
284. When would you use Redis as a primary store vs cache?
285. What are secondary indexes and what do they cost?
286. What is a covering index?
287. What is an index merge?
288. What is the cost of too many indexes?
289. What is a write-ahead log (WAL)?
290. What is MVCC (multi-version concurrency control)?
291. What are the four standard isolation levels?
292. What is a phantom read?
293. What is a non-repeatable read?
294. What is dirty read?
295. How would you migrate a schema with zero downtime?
296. How does double-write deal with cache–DB consistency?
297. How do you handle failed cache writes?
298. What is read-through cache?
299. What is a cache stampede and how to avoid it?
300. What is request coalescing?
301. What is a denormalized read model?
302. What is materialized view?
303. What is CDC (change data capture)?
304. How does CDC help with cache invalidation?
305. What is an outbox pattern?
306. What is a saga pattern?
307. What is two-phase commit and why is it problematic?
308. What is three-phase commit?
309. What is eventual consistency through compensating transactions?
310. How would you shard a "users" table by user_id?
311. How would you shard a "messages" table for a chat app?
312. What is co-location of related shards?
313. What is the "hot key" problem in sharded systems?
314. How would you re-shard a live database?
315. What is a global secondary index?
316. What is a local secondary index?
317. What is the n+1 query problem and how to fix it?
318. What is connection pooling at the proxy level (PgBouncer)?
319. What is read-replica lag and how to handle it for user-facing reads?
320. What is the difference between OLTP and OLAP DBs at the architecture level?

## 📨 Messaging & Streaming (321–350)

321. What is Apache Kafka at a high level?
322. How do partitions work in Kafka?
323. What is a Kafka consumer group?
324. What is offset in Kafka and how is it managed?
325. How does Kafka guarantee ordering?
326. What is RabbitMQ and how does it differ from Kafka?
327. What is an exchange in RabbitMQ (direct, topic, fanout, headers)?
328. What is dead-letter queue?
329. When do you use SQS vs SNS?
330. When do you use Kafka vs Kinesis?
331. What is the difference between queue and topic?
332. What is a consumer lag and how do you monitor it?
333. How do you achieve at-least-once delivery?
334. How do you make a consumer idempotent?
335. What is the outbox + relay pattern?
336. How do you design retries with exponential backoff and jitter?
337. What is poison pill in queue processing?
338. How would you handle ordering across partitions?
339. What is a compacted topic in Kafka?
340. What is exactly-once semantics in Kafka?
341. What is mirror-maker?
342. What is a stream-table duality?
343. What is windowing in stream processing?
344. What is a watermark in stream processing?
345. What is event time vs processing time?
346. When do you choose Flink vs Spark Streaming?
347. What is back-pressure handling in a streaming pipeline?
348. What is a side-car proxy in messaging context?
349. How do you handle schema evolution (Avro, Protobuf)?
350. What is the difference between push and pull queue consumers?

## 🌐 Microservices & APIs (351–380)

351. What are pros and cons of microservices vs modular monolith?
352. How do you decide service boundaries (DDD)?
353. What is a bounded context?
354. What is an aggregate in DDD?
355. What is service discovery and why is it needed?
356. What is client-side vs server-side service discovery?
357. What is the role of an API gateway in microservices?
358. What is a service mesh and why use it?
359. What is sidecar pattern (Envoy/Istio)?
360. How do services authenticate to each other (mTLS, JWT)?
361. What is a circuit breaker and how does it work?
362. What is a bulkhead pattern?
363. What is retry storm and how to avoid it?
364. What is a backend-for-frontend (BFF)?
365. What is a strangler fig pattern in migrations?
366. How do you handle distributed transactions across services?
367. How would you implement a saga for "order → payment → shipping"?
368. What is the choreography vs orchestration in saga?
369. How would you handle versioning in microservice APIs?
370. What is contract testing?
371. What is consumer-driven contracts (Pact)?
372. How would you design a search service in front of multiple data stores?
373. What is the read-model / write-model split (CQRS basics)?
374. What is BFF cache vs origin cache?
375. How would you migrate from monolith to microservices step by step?
376. What is event-carried state transfer?
377. What is the "shared database anti-pattern"?
378. What is pipelined gRPC?
379. What is server streaming vs client streaming vs bi-di in gRPC?
380. What is REST vs gRPC vs GraphQL trade-off?

## 🌍 Real-World Designs (381–430)

381. Design Twitter (timeline, tweets, follow).
382. Design Instagram (photo upload, feed).
383. Design Facebook News Feed (basic).
384. Design YouTube (video upload, playback).
385. Design Netflix (catalog + streaming).
386. Design Spotify (music streaming).
387. Design WhatsApp / Messenger (chat).
388. Design Slack (channels, threads).
389. Design Discord (servers, voice channels).
390. Design Zoom (video conferencing).
391. Design Google Drive / Dropbox.
392. Design Google Docs (real-time collaborative editing).
393. Design Pinterest (boards + pins).
394. Design Reddit (subreddits, voting).
395. Design Quora / StackOverflow (Q&A platform).
396. Design Yelp (location-based reviews).
397. Design Airbnb (booking, search by location).
398. Design Uber (ride matching, geospatial).
399. Design DoorDash / UberEats (food delivery).
400. Design Amazon product page + cart.
401. Design Shopify-like multi-tenant store.
402. Design eBay / online auctions.
403. Design Tinder (matching, swipes).
404. Design LinkedIn (connections, feed).
405. Design Medium (publishing platform).
406. Design GitHub (repos, PRs, issues).
407. Design Jira (issue tracker, boards).
408. Design Trello (kanban boards).
409. Design Notion (blocks-based docs).
410. Design Figma (collaborative canvas).
411. Design Twitch (live streaming + chat).
412. Design TikTok (short-video feed).
413. Design SoundCloud (audio uploads).
414. Design a typeahead / autocomplete service.
415. Design a real-time multiplayer chess server.
416. Design a basic e-mail service (Gmail-lite).
417. Design a calendar service with reminders.
418. Design a video conferencing whiteboard.
419. Design a real-time stock ticker dashboard.
420. Design a flight booking system.
421. Design a hotel booking system.
422. Design a movie ticket booking system at scale.
423. Design a parking-lot reservation system at city scale.
424. Design a ride-share carpool matcher.
425. Design a coupon / promo code service.
426. Design a recommendation engine for an e-commerce site.
427. Design a "people you may know" service.
428. Design a "who viewed your profile" service.
429. Design a hashtag trending service.
430. Design a real-time leaderboard for a global game.

## 🏗️ Reliability & Performance Patterns (431–470)

431. What is graceful degradation? Give an example.
432. What is fail-fast vs fail-safe?
433. What is retry with backoff and jitter?
434. What is circuit breaker open/half-open/closed states?
435. What is timeout cascading and how to prevent it?
436. What is hedged request?
437. What is request collapsing?
438. What is shadow traffic / dark launching?
439. What is feature flag rollout?
440. What is canary deployment with metric guardrails?
441. What is blue-green deployment trade-off?
442. What is rolling deployment?
443. What is in-place vs immutable deployment?
444. What is the role of synthetic monitoring?
445. What is real-user monitoring (RUM)?
446. What is APM and what tools provide it?
447. What is the four golden signals (latency, traffic, errors, saturation)?
448. What is the USE method (Utilization, Saturation, Errors)?
449. What is the RED method (Rate, Errors, Duration)?
450. How do you set SLO, SLA, SLI?
451. What is error budget?
452. What is the role of chaos engineering?
453. What is fault injection?
454. What is request tracing and why is it valuable?
455. What is OpenTelemetry?
456. What is span vs trace vs context propagation?
457. What is structured logging?
458. What is log aggregation (e.g., ELK, Loki)?
459. What is metric cardinality and why is it dangerous?
460. What is alert fatigue and how do you avoid it?
461. What is on-call rotation design?
462. What is post-mortem (blameless)?
463. What is "graceful shutdown" of a service?
464. What is connection draining on a load balancer?
465. What is keep-alive vs idle timeout in connection pools?
466. What is HTTP connection coalescing?
467. What is pre-warming a cache?
468. What is the "thundering herd" on cold cache and fix?
469. What is request hedging?
470. What is bulkhead with thread-pool isolation?

## 🔧 Caching Strategies (471–490)

471. What is write-through, write-back, write-around caching?
472. What is the cache-aside pattern in detail?
473. How do you choose TTL for a session cache vs profile cache?
474. What is negative caching and when to use it?
475. What is cache stampede protection (mutex / lease)?
476. How does Redis cluster sharding work?
477. What is Redis pub/sub and what are its limitations?
478. What is Redis Streams?
479. What is Redis sentinel vs cluster?
480. What is consistent hashing with virtual nodes in Memcached?
481. How do you cache GraphQL responses?
482. How do you cache user-specific data efficiently?
483. What is edge caching and how does CDN cache personalized content?
484. What is fragment caching (Russian doll caching)?
485. What is HTTP cache header (Cache-Control, ETag, Last-Modified) interplay?
486. What is stale-while-revalidate?
487. What is private vs shared cache?
488. What is varnish HTTP cache and where does it fit?
489. What is multi-tier caching (L1 in-process + L2 Redis)?
490. What is Redis vs ElastiCache vs MemoryDB?

## 🔍 Search & Indexing (491–500)

491. How would you build a basic search index for blog posts?
492. What is an inverted index?
493. What is TF-IDF and where is it used?
494. What is BM25?
495. What is Elasticsearch and how does it shard data?
496. What is the difference between text and keyword fields in ES?
497. How do you handle typo tolerance / fuzzy search?
498. How do you implement autocomplete/typeahead at scale?
499. How do you sync your primary DB with Elasticsearch?
500. What are the trade-offs of using Elasticsearch as a primary data store?

---

[← Junior](./01-junior-level.md) · [README](./README.md) · [Senior Level →](./03-senior-level.md)
