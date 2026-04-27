# 🟢 Junior Level (1–250)

[← README](./README.md) · [Middle →](./02-middle-level.md)

> **Fokus:** Asoslar — HTTP, DNS, REST, oddiy DB, cache, monolith vs microservices, oddiy designs (URL shortener, parking lot). Interviewer **chuqurlikni emas**, mantiqiy fikrlash va asosiy komponentlarni tushunishni kutadi.
>
> **Kim uchun:** 0–2 yil tajriba, bootcamp/junior level.
> **Vaqt/savol:** 2–5 daqiqa.

---

## 🌐 Networking & Web Basics (1–30)

1. What is HTTP and how does it differ from HTTPS?
2. Explain the difference between TCP and UDP.
3. What happens when you type a URL in the browser and press Enter?
4. What is DNS and how does DNS resolution work?
5. What is a TCP three-way handshake?
6. Define IP address — what is the difference between IPv4 and IPv6?
7. What is the OSI model and its 7 layers?
8. Explain HTTP status codes: what do 2xx, 3xx, 4xx, and 5xx mean?
9. What is the difference between HTTP/1.1, HTTP/2, and HTTP/3?
10. What is a cookie and how is it different from session storage?
11. What is CORS and why does it exist?
12. Explain the request/response cycle of a web application.
13. What is SSL/TLS and what problem does it solve?
14. What is a proxy server?
15. What is the difference between a forward proxy and a reverse proxy?
16. What is a CDN and what is its primary role?
17. What is latency vs throughput?
18. What is bandwidth?
19. What does keep-alive mean in HTTP?
20. What is a webhook and how does it differ from polling?
21. Explain long polling vs short polling vs WebSockets.
22. What is a port number and which common ports do you know?
23. How does WebSocket handshake work?
24. What is a public IP vs a private IP?
25. What is NAT (Network Address Translation)?
26. What does "stateless" mean for HTTP?
27. What is a session and how is it managed?
28. What are the most common HTTP methods?
29. What is the difference between PUT and PATCH?
30. What is the role of a load balancer at the highest level?

## 🗄️ Database Basics (31–60)

31. What is the difference between SQL and NoSQL databases?
32. What is a primary key vs a foreign key?
33. What is normalization and why do we use it?
34. What is denormalization and when is it appropriate?
35. Explain the ACID properties.
36. What is an index and why does it speed up queries?
37. What is a JOIN and what types of JOINs exist?
38. What is a transaction in a database?
39. What is the difference between a clustered and a non-clustered index?
40. What is a database schema?
41. Define 1NF, 2NF, and 3NF in simple terms.
42. What is a stored procedure?
43. What is a trigger?
44. What is a view in SQL?
45. What is the difference between DELETE, TRUNCATE, and DROP?
46. What is a key-value store and when would you use one?
47. What is a document database (like MongoDB)?
48. What is a graph database and what problem does it solve?
49. What is a column-family store (like Cassandra)?
50. What is the difference between OLTP and OLAP?
51. What is data integrity?
52. What is a deadlock?
53. What is database replication in a single sentence?
54. What is read-only replica?
55. Why do we use connection pooling?
56. What is an ORM and what are its pros/cons?
57. What is a UUID and when would you use it instead of auto-increment IDs?
58. What does "eventually consistent" mean in plain English?
59. What is a composite key?
60. When would you choose Postgres vs MySQL?

## 💾 Caching Basics (61–80)

61. What is caching and why do we use it?
62. What are the layers where caching can happen (browser, CDN, app, DB)?
63. What is a cache hit vs cache miss?
64. What is TTL (time to live)?
65. What is cache eviction?
66. Explain LRU, LFU, and FIFO eviction policies.
67. What is the difference between Redis and Memcached?
68. What is local cache vs distributed cache?
69. What is a write-through cache?
70. What is a write-back (write-behind) cache?
71. What is a cache-aside (lazy-loading) pattern?
72. What is a stale cache?
73. What problem does cache invalidation solve?
74. What is a thundering herd problem?
75. Why is "there are only two hard things in CS: cache invalidation and naming things" famous?
76. What is the role of caching in a URL shortener?
77. How does browser caching work?
78. What is HTTP cache-control header?
79. What is ETag?
80. What is the typical hit ratio you should target for a cache?

## 🧱 Architecture Basics (81–110)

81. What is a monolithic architecture?
82. What is microservice architecture?
83. What is the difference between monolith and microservices?
84. What is service-oriented architecture (SOA)?
85. What is a 3-tier architecture?
86. What is client–server architecture?
87. What is peer-to-peer architecture?
88. What is event-driven architecture in simple terms?
89. What is a serverless architecture?
90. What are pros and cons of monolith for a small startup?
91. What is vertical scaling (scale up)?
92. What is horizontal scaling (scale out)?
93. Why is horizontal scaling usually preferred for web services?
94. What is high availability?
95. What is fault tolerance?
96. What is single point of failure (SPOF)?
97. What is redundancy in system design?
98. What is failover?
99. What is a stateless service?
100. What is a stateful service?
101. What is idempotency?
102. Why is idempotency important for retries?
103. What is the role of message queues at a basic level?
104. What is a publish–subscribe pattern?
105. What is decoupling and why does it matter?
106. What is loose coupling vs tight coupling?
107. What is cohesion?
108. Why is "single responsibility" important in service design?
109. What is the role of an API gateway?
110. What does "12-factor app" mean?

## ⚖️ Load Balancing Basics (111–125)

111. What is a load balancer?
112. What is round-robin load balancing?
113. What is least-connections load balancing?
114. What is IP-hash load balancing?
115. What is the difference between L4 and L7 load balancers?
116. Why might you put a load balancer in front of a database?
117. What is a sticky session?
118. When should you avoid sticky sessions?
119. What is health-checking on a load balancer?
120. What is the difference between hardware and software load balancers?
121. What is HAProxy vs nginx vs ELB at a basic level?
122. What is DNS-based load balancing?
123. What is Anycast routing?
124. Why do load balancers improve availability?
125. What is SSL termination at the load balancer?

## 🔒 Security Basics (126–145)

126. What is authentication vs authorization?
127. What is a JWT token?
128. What is OAuth 2.0 in one sentence?
129. What is HTTPS and why is it important?
130. What is a man-in-the-middle attack?
131. What is SQL injection?
132. What is XSS?
133. What is CSRF?
134. What is hashing vs encryption?
135. What is salt in password hashing?
136. Why should you never store passwords in plain text?
137. What is rate limiting and why is it useful?
138. What is two-factor authentication?
139. What is an API key?
140. What is the principle of least privilege?
141. What is a firewall?
142. What is a DMZ in network architecture?
143. What is a brute-force attack?
144. Why do we use HTTPS for login pages?
145. What is data-at-rest vs data-in-transit encryption?

## 🛠️ API Basics (146–165)

146. What is a REST API?
147. What are REST principles (statelessness, uniform interface, etc.)?
148. What is the difference between REST and SOAP?
149. What is GraphQL and how does it differ from REST?
150. What is gRPC?
151. What is an HTTP method-resource mapping?
152. How would you design endpoints for a blog (posts, comments)?
153. What is API versioning and why is it needed?
154. What is pagination and how do you implement it?
155. What is cursor-based vs offset-based pagination?
156. What is rate limiting at an API level?
157. What is throttling?
158. What is API documentation, and why is OpenAPI/Swagger useful?
159. What is content negotiation?
160. What is HATEOAS?
161. What is an idempotent endpoint? Give an example.
162. What is the difference between 401 and 403?
163. What is the difference between 200, 201, and 204?
164. What is bulk endpoint vs single endpoint?
165. When would you use webhooks instead of polling?

## 📦 Storage & Files Basics (166–180)

166. What is object storage and how does it differ from block storage?
167. What is S3 in simple terms?
168. What is a CDN edge node?
169. What is the difference between SSD and HDD in system design?
170. What is RAID and which levels do you know?
171. What is file system journaling?
172. What is a blob?
173. What is metadata for a file?
174. How would you store user-uploaded avatars?
175. Why don't you store large files in a relational DB?
176. What is a presigned URL?
177. What is multipart upload?
178. What is data redundancy in storage?
179. Why is S3 considered "11 nines" durable?
180. What is data tiering (hot/warm/cold)?

## 📐 Common Junior-Level Designs (181–215)

181. Design a URL shortener like Bitly (high-level).
182. Design a TinyURL — what data store would you pick?
183. Design a basic in-memory key-value store.
184. Design a simple to-do list backend.
185. Design a basic blog platform.
186. Design a simple chat between two users.
187. Design a parking lot system (object-oriented).
188. Design an elevator system.
189. Design a vending machine.
190. Design a tic-tac-toe game.
191. Design a coffee shop ordering system.
192. Design a library book lending system.
193. Design an ATM machine.
194. Design a simple e-commerce product page.
195. Design a hotel reservation system (basic).
196. Design a calendar / meeting room booking.
197. Design a simple feed of recent posts.
198. Design a basic job scheduler.
199. Design a polling/voting system.
200. Design a movie ticket booking (simplified).
201. Design a bookstore inventory.
202. Design a music playlist app.
203. Design a basic notification system (email only).
204. Design a flashcard study app.
205. Design a basic file uploader.
206. Design a simple photo gallery.
207. Design a contact list / address book.
208. Design a simple chess game.
209. Design a step counter app backend.
210. Design a leaderboard for a small game.
211. Design a simple weather API consumer.
212. Design a basic e-mail inbox view.
213. Design a "split the bill" calculator API.
214. Design a basic survey / form builder.
215. Design a simple expense tracker.

## 🧪 Concepts & Terminology (216–235)

216. What is throughput vs latency vs response time?
217. What does "p99 latency" mean?
218. What is QPS (queries per second)?
219. What is back-of-the-envelope estimation?
220. How would you estimate storage needed for 1M users with 1KB profile each?
221. What is a synchronous vs asynchronous system?
222. What is non-blocking I/O?
223. What is concurrency vs parallelism?
224. What is a thread pool?
225. What is event loop (e.g., in Node.js)?
226. What is a process vs a thread?
227. What is a race condition?
228. What is a mutex?
229. What is optimistic vs pessimistic locking?
230. What is a critical section?
231. What is the difference between SDK and API?
232. What is observability (logs, metrics, traces) at a basic level?
233. What does "graceful degradation" mean?
234. What is feature flag?
235. What is blue-green deployment in one sentence?

## 🚀 DevOps & Deployment Basics (236–250)

236. What is CI vs CD?
237. What is Docker and why is it used?
238. What is a container vs a virtual machine?
239. What is Kubernetes in one sentence?
240. What is a pod in Kubernetes?
241. What is a deployment vs a service in Kubernetes?
242. What is rolling deployment?
243. What is a canary release?
244. What is Infrastructure as Code (IaC)?
245. What is a container registry?
246. Why is environment parity important (dev/staging/prod)?
247. What is monitoring vs alerting?
248. What is logging level (DEBUG/INFO/WARN/ERROR)?
249. What is the role of staging environment?
250. What is a runbook?

---

[← README](./README.md) · [Middle Level →](./02-middle-level.md)
