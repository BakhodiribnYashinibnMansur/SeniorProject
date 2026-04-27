# 🏗️ System Design — "Design X" Problems (Detailed)

[← README](./README.md) · [Sources →](./05-sources.md)

> **Maqsad:** FAANG/highload intervyularda berilgan klassik **"Design X"** masalalari. Har bir masala uchun: **scale**, **functional requirements**, **non-functional requirements**, va **muhokama qilinadigan qiyin qismlar (hard parts)**.
>
> **Format:** Har bir masala intervyu prompt'i sifatida. 30–60 daqiqalik whiteboard sessiya uchun mo'ljallangan.
> **Qoida:** Avval **clarifying savollar** bering, keyin **back-of-the-envelope estimation**, keyin **APIs / data model**, **HLD diagram**, **deep dives**.

---

## 📑 Mundarija (Mavzular bo'yicha)

**Jami: 1000 ta detailed design masalasi + 50 ta OOD masalasi = 1050 ta.**

### Birinchi qism — Klassik domenlar (1–202)

| # | Domen | Masalalar |
|---|---|---|
| 1 | Social Media & Feed | 1–15 |
| 2 | Messaging & Chat | 16–27 |
| 3 | Video & Audio Streaming | 28–39 |
| 4 | E-commerce & Marketplace | 40–54 |
| 5 | Booking & Reservations | 55–64 |
| 6 | Maps & Location | 65–76 |
| 7 | Storage & File Sharing | 77–86 |
| 8 | Search & Discovery | 87–96 |
| 9 | Real-time Collaboration | 97–106 |
| 10 | Payments & Finance | 107–118 |
| 11 | Ads & Marketing | 119–128 |
| 12 | Gaming | 129–138 |
| 13 | IoT & Devices | 139–146 |
| 14 | Analytics & Logging | 147–156 |
| 15 | Infrastructure & DevOps | 157–168 |
| 16 | AI / ML Systems | 169–178 |
| 17 | Security & Identity | 179–186 |
| 18 | Communication Tools | 187–194 |
| 19 | URL / Identifier / Misc | 195–202 |

### Ikkinchi qism — Industry-specific & Specialized (203–1000)

| # | Domen | Masalalar |
|---|---|---|
| 20 | Healthcare & Medical | 203–235 |
| 21 | Logistics, Supply Chain, Shipping | 236–270 |
| 22 | Education / E-Learning | 271–300 |
| 23 | Real Estate | 301–318 |
| 24 | Travel & Hospitality (advanced) | 319–340 |
| 25 | HR & Recruiting | 341–365 |
| 26 | Legal Tech | 366–380 |
| 27 | Government / Civic Tech | 381–400 |
| 28 | News & Media | 401–425 |
| 29 | Sports & Fitness | 426–450 |
| 30 | Music & Audio Tech | 451–470 |
| 31 | Photography & Video Editing | 471–490 |
| 32 | Insurance Tech | 491–510 |
| 33 | Energy & Utilities | 511–525 |
| 34 | Agriculture & Food Tech | 526–540 |
| 35 | Construction & AEC | 541–555 |
| 36 | Manufacturing & Industrial IoT | 556–575 |
| 37 | Retail & POS Systems | 576–600 |
| 38 | Banking & FinTech (advanced) | 601–630 |
| 39 | Crypto & DeFi | 631–660 |
| 40 | Gambling & Sports Betting | 661–680 |
| 41 | Subscription Commerce | 681–700 |
| 42 | Pet & Animal Tech | 701–715 |
| 43 | Sub-systems & Components | 716–770 |
| 44 | AI / LLM / Generative | 771–810 |
| 45 | Internal Dev Tools | 811–840 |
| 46 | VR / AR / Spatial | 841–855 |
| 47 | Robotics & Autonomous | 856–870 |
| 48 | Telecom & Mobile | 871–890 |
| 49 | Smart Cities | 891–905 |
| 50 | Climate / Sustainability | 906–920 |
| 51 | Workflow & Orchestration | 921–945 |
| 52 | Data Engineering & ETL | 946–975 |
| 53 | Compliance, Auditing, Privacy | 976–1000 |

### Bonus — Object-Oriented Design (50 OOD masalasi)

| # | OOD Problem | ID |
|---|---|---|
| OOD | Parking Lot, Elevator, Vending Machine, Library, Tic-Tac-Toe, Chess, Snake & Ladder, Connect Four, Poker, BlackJack | O1–O10 |
| OOD | ATM, Stock Brokerage, LRU Cache, LFU Cache, HashMap, Linked List, Movie Booking, Cab Booking, Hotel, Restaurant | O11–O20 |
| OOD | Pizza Delivery, Carpool, Logger, Rate Limiter, Concurrent HashMap, Producer-Consumer, Thread Pool, Connection Pool, NxN TTT, File System | O21–O30 |
| OOD | In-Mem FS, URL Class, Splitwise, Snake, Auction, Order Matching, Multiplayer TTT, Calendar, Music Streaming, Notification | O31–O40 |
| OOD | ATM Detailed, Coffee Vending, Chess Clock, Card Framework, UNO, StackOverflow, Twitter LLD, Browser History, Text Editor, WhatsApp LLD | O41–O50 |

---

## 🧭 Universal Framework — Har bir masalada ishlatish

```
1️⃣  CLARIFY      → Functional / non-functional / scale (5 daqiqa)
2️⃣  ESTIMATE     → DAU, QPS, storage, bandwidth, memory (5 daqiqa)
3️⃣  API DESIGN   → Endpoints, contracts (5 daqiqa)
4️⃣  DATA MODEL   → Tables / collections / indexes (5 daqiqa)
5️⃣  HLD          → Boxes & arrows: client → LB → service → DB (10 daqiqa)
6️⃣  DEEP DIVE    → Hot parts: caching, sharding, replication, MQ (15 daqiqa)
7️⃣  TRADE-OFFS   → Justify choices, alternatives, scaling next 10x (5 daqiqa)
```

---

# 🌐 1. Social Media & Feed

### 1. Design Twitter (X)
- **Scale:** 500M MAU, 200M DAU, 6K tweets/sec write, 300K timeline reads/sec.
- **Core features:** Post tweet (≤280 chars), follow user, home timeline, user timeline, like, retweet, reply.
- **Hard parts:** Fanout (push vs pull vs hybrid), celebrity problem, timeline ranking, search across all tweets, trending topics.
- **Discuss:** How to deliver tweets to 1M followers? When does a write become 1M writes? How to cache home timelines?

### 2. Design Instagram
- **Scale:** 2B MAU, 500M daily uploads, average photo 2MB.
- **Core features:** Photo/video upload, follow, feed, stories, explore, comments, likes, DM (basic).
- **Hard parts:** Image storage and CDN, feed personalization, stories' 24h TTL, hot creators.
- **Discuss:** Pre-resize variants vs on-the-fly? How to fan out to 100M followers?

### 3. Design Facebook News Feed
- **Scale:** 3B MAU, ranking score per post.
- **Core features:** News feed ranking, friends' posts, ads insertion, reactions.
- **Hard parts:** Real-time ranking (EdgeRank-like), ads injection, graph queries for "friends of friends".
- **Discuss:** Push vs pull vs hybrid; how to inject ads without breaking ranking; edge cases (friend just posted but ranked low).

### 4. Design TikTok / For You Page
- **Scale:** 1B MAU, infinite-scroll video feed, average 30s watched per video.
- **Core features:** Personalized video feed, like/share/comment, follow, upload.
- **Hard parts:** Cold start for new users, ML ranking pipeline, deduplication, content moderation in real-time.
- **Discuss:** How does the FYP serve the next video before user finishes current? How to recommend without explicit follows?

### 5. Design Reddit
- **Scale:** 500M MAU, 100K subreddits, 50K comments/min on hot threads.
- **Core features:** Subreddit feed, post submission, threaded comments, voting, hot/top/new ranking.
- **Hard parts:** Comment tree storage at scale, vote ranking with vote-fuzzing for fairness, brigading detection.
- **Discuss:** How to render a 100K-comment thread? Materialized view per subreddit?

### 6. Design Pinterest
- **Scale:** 500M MAU, billions of pins, board-based discovery.
- **Core features:** Save pin, create board, follow boards, home feed, search by image.
- **Hard parts:** Image deduplication via perceptual hashing, board-level personalization, related pins via embeddings.
- **Discuss:** How to serve "more like this" in <100ms? Vector search architecture?

### 7. Design LinkedIn Feed
- **Scale:** 1B users, 50M DAU, B2B-focused content.
- **Core features:** Connection feed, recommended posts, "people you may know", articles, jobs.
- **Hard parts:** Graph distance computation for PYMK, mixed-content ranking, ad placement.
- **Discuss:** How to compute 2nd-degree connections at scale? Caching strategy for graph queries?

### 8. Design Tumblr / Medium
- **Scale:** 100M DAU, long-form posts, blogs.
- **Core features:** Publish article, follow blogs, recommendations, drafts, claps.
- **Hard parts:** Editor save & autosave, tag-based discovery, SEO-friendly URLs.
- **Discuss:** Markdown rendering pipeline, cache invalidation when author edits.

### 9. Design Tinder / Bumble
- **Scale:** 50M DAU, swipe-based matching, geo-filtered.
- **Core features:** Swipe left/right, match notification, chat after match, geo-radius filter.
- **Hard parts:** Recommendation engine, double-opt-in matching, undo swipe, fairness (ELO-like rating).
- **Discuss:** How to avoid showing same profile twice for 30 days? How to scale geo queries?

### 10. Design Quora / Stack Overflow
- **Scale:** 300M MAU, Q&A platform.
- **Core features:** Ask question, answer, upvote/downvote, follow topics, search.
- **Hard parts:** Answer ranking, duplicate detection, expertise scoring, spam filter.
- **Discuss:** Full-text search with relevance, how to detect duplicate questions?

### 11. Design Trending Topics (Twitter Trends)
- **Scale:** 100K topics tracked per region, real-time count.
- **Core features:** Detect rising hashtags, region-specific trends, time-window ranking.
- **Hard parts:** Heavy hitter detection (Count-Min Sketch), windowing (5min, 1h, 24h), spam/bot filtering.
- **Discuss:** Why approximate algorithms? How to deal with timezone biases?

### 12. Design "People You May Know"
- **Scale:** 1B users in social graph.
- **Core features:** Recommend 50 connections, refresh weekly.
- **Hard parts:** Friend-of-friend computation, graph storage (adjacency, edge list), feature engineering.
- **Discuss:** Precompute vs on-demand? Graph DB vs Hadoop batch job?

### 13. Design Notification Feed (Bell icon)
- **Scale:** 1B users, 10–100 notifications/day each.
- **Core features:** "X liked your post", "Y commented", real-time push, mark read.
- **Hard parts:** Aggregation ("3 people liked"), TTL, read state sync across devices.
- **Discuss:** Push vs pull on bell icon open? Where to aggregate?

### 14. Design Live Comments / Reactions on a Live Stream
- **Scale:** 1M concurrent viewers per stream, 10K reactions/sec.
- **Core features:** Comments scroll, reaction floats, real-time delivery.
- **Hard parts:** Fanout to 1M WebSockets, rate limiting per user, profanity filter.
- **Discuss:** Sampling at extreme scale (do we deliver every comment?), pub/sub architecture.

### 15. Design Stories (Instagram/Snapchat Stories)
- **Scale:** 500M DAU, 24h TTL per story.
- **Core features:** Post photo/video, view stories of friends, "seen by", auto-expire.
- **Hard parts:** TTL-driven storage (cold after 24h), seen-state sync, batched fanout.
- **Discuss:** Why immutable per-user story timelines? Background expiration job vs lazy delete?

---

# 💬 2. Messaging & Chat

### 16. Design WhatsApp / Messenger
- **Scale:** 2B users, 100B messages/day, 100M concurrent connections.
- **Core features:** 1-1 chat, group chat (up to 1024), delivery + read receipts, media, E2E encryption.
- **Hard parts:** WebSocket fanout, offline message queue, E2E key exchange (Signal protocol), multi-device sync.
- **Discuss:** Per-user inbox vs group inbox? How to handle a user with 10 devices?

### 17. Design Slack
- **Scale:** 30M DAU, multi-tenant workspaces, channels, threads.
- **Core features:** Channels, threads, DMs, file uploads, search, mentions.
- **Hard parts:** Workspace-level data isolation, per-channel scrollback, search per workspace.
- **Discuss:** How to store messages? Multi-tenant DB? How to render unread indicators?

### 18. Design Discord
- **Scale:** 200M MAU, voice + text, large guilds (250K members).
- **Core features:** Servers, channels, voice rooms, screen share, bots.
- **Hard parts:** Voice with WebRTC, large-server fanout, role-based permissions.
- **Discuss:** Centralized voice routing vs P2P; how to deliver mention to 250K members?

### 19. Design iMessage
- **Scale:** 1B+ Apple users, E2E.
- **Core features:** SMS fallback, multi-device, reactions, threads, edit/delete.
- **Hard parts:** APNs at scale, key management for new device, sync across devices without server seeing plaintext.
- **Discuss:** End-to-end vs server-side; how to support "send later"?

### 20. Design SMS Gateway (Twilio-like)
- **Scale:** 1B SMS/day, multi-carrier routing.
- **Core features:** Send SMS, receive reply, delivery report, opt-out compliance.
- **Hard parts:** Carrier rate limits, retry policies, regulatory compliance, webhook callbacks.
- **Discuss:** Outbound queue with priorities? Idempotency on retries?

### 21. Design Group Video Conference (Zoom/Meet)
- **Scale:** 500K concurrent meetings, up to 1000 participants.
- **Core features:** Video + audio, screen share, recording, breakout rooms, chat.
- **Hard parts:** Selective forwarding unit (SFU) vs MCU vs P2P; bandwidth adaptation; meeting recording pipeline.
- **Discuss:** How does media routing scale? Recording stored where?

### 22. Design Read Receipts / Typing Indicators
- **Scale:** Same as 16, but specifically the ephemeral signals.
- **Core features:** "typing…" indicator, "delivered/read" markers.
- **Hard parts:** Volume of ephemeral writes, throttling, opt-out users.
- **Discuss:** Why not store every typing event? Pub/sub vs DB writes.

### 23. Design Message Search (across own DM history)
- **Scale:** 10M messages per user, search in <500ms.
- **Core features:** Full-text search, filters by date/contact.
- **Hard parts:** Per-user inverted index, encrypted search (E2E), index size.
- **Discuss:** Server-side search vs on-device search; index update on new message.

### 24. Design Online Presence System
- **Scale:** 1B users, "online/away/offline" status.
- **Core features:** Presence indicator on chat list, typing, last-seen.
- **Hard parts:** Heartbeat at scale, push to interested subscribers (friends), TTL for stale heartbeats.
- **Discuss:** Pull vs push on chat-list open; how big is the heartbeat overhead?

### 25. Design Push Notification Service (FCM/APNs equivalent)
- **Scale:** 100B notifications/day, 1B devices.
- **Core features:** Send notification, device registration, topic subscription, ack.
- **Hard parts:** Device-token management, delivery retries, prioritization.
- **Discuss:** Persistent connection per device (TCP) — how many on one server? Sharding strategy.

### 26. Design Telegram-like Bot Platform
- **Scale:** 5M bots, webhooks vs long-polling.
- **Core features:** Bot API, command handling, inline queries, payments.
- **Hard parts:** Webhook delivery reliability, rate-limit per bot, bot abuse detection.
- **Discuss:** Push vs pull for bot updates; how to handle bots that crash repeatedly.

### 27. Design Comments Service (Reusable, embedded — Disqus)
- **Scale:** 100K sites, 1B comments stored.
- **Core features:** Embed widget, threaded comments, moderation, voting.
- **Hard parts:** Multi-tenant data, spam filter, embed performance (don't slow host page).
- **Discuss:** Loading comments async vs SSR; per-site quotas.

---

# 🎬 3. Video & Audio Streaming

### 28. Design YouTube
- **Scale:** 2B MAU, 500h/min uploads, 1B hours/day watched.
- **Core features:** Upload, transcode, watch, comments, subscriptions, recommendations.
- **Hard parts:** Transcoding pipeline (multiple resolutions/bitrates), CDN distribution, view-count denormalization, copyright detection (Content ID).
- **Discuss:** Adaptive bitrate (HLS/DASH), how to count views accurately at scale, anti-fraud on view counts.

### 29. Design Netflix
- **Scale:** 250M subscribers, 250M concurrent during peak, 15% of global internet traffic.
- **Core features:** Browse catalog, watch, recommendations, profiles, downloads.
- **Hard parts:** Open Connect CDN (ISP-embedded), encoding ladder, personalization, cross-device resume.
- **Discuss:** Why their own CDN? Pre-positioning content based on regional popularity.

### 30. Design Spotify
- **Scale:** 600M MAU, 100M tracks, personalized playlists.
- **Core features:** Stream music, playlists, search, recommendations (Discover Weekly), podcasts, offline.
- **Hard parts:** Audio streaming with low buffer, royalty calculation, collaborative playlists, offline DRM.
- **Discuss:** How to compute Discover Weekly weekly for 600M users? Storage for offline tracks.

### 31. Design Twitch (Live Streaming)
- **Scale:** 30M DAU, peak 5M concurrent viewers, low-latency live.
- **Core features:** Go live, chat, follow, subscriptions, clips.
- **Hard parts:** Sub-second latency (WebRTC vs LL-HLS), real-time chat fanout, transcoding live ingest.
- **Discuss:** Ingest → transcode → distribute pipeline; how to scale chat alongside video.

### 32. Design Audio-Only Live Streaming (Clubhouse-like)
- **Scale:** 1M rooms, 10K listeners per popular room.
- **Core features:** Audio rooms, raise hand, speaker promotion, recording.
- **Hard parts:** Audio mixing on server, latency for back-and-forth, capacity per room.
- **Discuss:** SFU for many speakers vs all-in-one mixing; how to scale viral rooms.

### 33. Design Podcast Platform (Apple Podcasts-like)
- **Scale:** 100M users, 5M shows, RSS-based ingestion.
- **Core features:** Subscribe, episode list, download, sync playback position cross-device.
- **Hard parts:** RSS poller for millions of feeds, dedup of identical episodes, transcript generation.
- **Discuss:** Pull vs push from RSS publishers; episode storage and CDN.

### 34. Design Video Upload & Transcoding Pipeline
- **Scale:** 500h/min input, output 6+ resolutions.
- **Core features:** Resumable upload, queue for transcoding, multi-resolution output, thumbnail generation.
- **Hard parts:** GPU vs CPU transcoding, parallelism per file, dead-letter for failed transcodes.
- **Discuss:** Chunked upload (multipart), priority queue (paid vs free creators).

### 35. Design Adaptive Bitrate Streaming
- **Scale:** Service for 100M concurrent streams.
- **Core features:** Manifest playlist (HLS/DASH), segment download, quality switch.
- **Hard parts:** Encoding ladder per content, manifest CDN cache, ABR algorithm on client.
- **Discuss:** Per-title encoding vs per-genre; CDN cache key strategy.

### 36. Design Video CDN (like Cloudflare Stream / Mux)
- **Scale:** 10K customers, multi-region delivery.
- **Core features:** Ingest API, transcoding, delivery URLs, analytics.
- **Hard parts:** Multi-tenant cost attribution, abuse prevention, custom domains.
- **Discuss:** Origin shielding, per-tenant TLS certs.

### 37. Design Live Video Recording (Cloud DVR)
- **Scale:** 10M users, schedule-based recording.
- **Core features:** Schedule recording, store, playback later.
- **Hard parts:** Storage tiering (recent vs old), per-user vs shared copy (legal), seek inside long recording.
- **Discuss:** Why "shared copy" is legally tricky in some jurisdictions.

### 38. Design Closed Captions / Subtitles Pipeline
- **Scale:** Auto-generate for 500h/min new uploads.
- **Core features:** Auto-transcribe, sync timing, multi-language, manual edit.
- **Hard parts:** Speech-to-text pipeline, timing alignment, profanity masking.
- **Discuss:** Async pipeline with status; how to backfill for old content.

### 39. Design Watch Party / Synced Playback
- **Scale:** Up to 100 participants per party.
- **Core features:** Synced play/pause/seek, group chat overlay.
- **Hard parts:** Drift correction on each client, host vs democratic control, NAT-traversal.
- **Discuss:** Authoritative server time vs peer-clock; how to recover from network blip.

---

# 🛒 4. E-commerce & Marketplace

### 40. Design Amazon (e-commerce)
- **Scale:** 300M users, 200M products, Prime Day 100x spikes.
- **Core features:** Browse, search, cart, checkout, order tracking, reviews.
- **Hard parts:** Inventory consistency, payment, recommendations, fraud, search relevance.
- **Discuss:** Single product page = how many service calls? Cart in Redis vs DB.

### 41. Design Shopify (multi-tenant store platform)
- **Scale:** 2M merchants, custom domains, themes.
- **Core features:** Store builder, product mgmt, checkout, order, payments.
- **Hard parts:** Multi-tenant isolation, custom domain SSL, theme rendering, app marketplace.
- **Discuss:** Per-tenant DB vs shared schema; webhook delivery to merchants.

### 42. Design eBay / Online Auctions
- **Scale:** 150M users, real-time bidding on auctions.
- **Core features:** List item, bid, watch, snipe protection, payment.
- **Hard parts:** Last-second bid race, atomic bid increment, time extensions.
- **Discuss:** Why classical OLTP isn't enough for sniping; counter sharding.

### 43. Design a Shopping Cart Service
- **Scale:** 100M users, anonymous + logged-in carts.
- **Core features:** Add/remove items, persist across devices, expire after 30 days.
- **Hard parts:** Anonymous-to-logged-in cart merge, stock check, price drift.
- **Discuss:** Cart in Redis with periodic flush vs cart in DB; cart-abandonment pipeline.

### 44. Design a Payment / Checkout Flow
- **Scale:** 10K checkouts/sec at peak.
- **Core features:** Address, payment method, tax, shipping, place order.
- **Hard parts:** Idempotency, saga across inventory + payment + shipping + email.
- **Discuss:** Why eventual consistency is dangerous in checkout; how to recover stuck orders.

### 45. Design Inventory Management at Amazon Scale
- **Scale:** 100M SKUs, multiple warehouses, Prime Now.
- **Core features:** Track stock per warehouse, allocate to orders, restock.
- **Hard parts:** Concurrent decrement (oversell), reservation TTL, near-real-time picture.
- **Discuss:** Distributed counter for stock; reservation vs commit pattern.

### 46. Design a Recommendation System (e-commerce)
- **Scale:** 200M products, 300M users.
- **Core features:** "Customers also bought", "based on history".
- **Hard parts:** Item-item similarity matrix, real-time personalization, cold start.
- **Discuss:** Offline batch (Spark) vs online; embedding-based vs collaborative filtering.

### 47. Design Product Reviews & Ratings
- **Scale:** 1B reviews, 5-star ratings, photos.
- **Core features:** Submit review, read reviews, sort by helpful, moderation.
- **Hard parts:** Average rating denormalization, helpful-vote ranking, fake review detection.
- **Discuss:** How to compute "weighted average" considering reviewer credibility.

### 48. Design Order History / Tracking
- **Scale:** 1B orders/year, 90-day-active.
- **Core features:** List orders, status timeline, tracking number, return.
- **Hard parts:** State machine for order, integration with carrier APIs, return reverse logistics.
- **Discuss:** Webhook from carriers vs polling; storage strategy for old orders.

### 49. Design a Coupon / Promo Code Service
- **Scale:** 100K active codes, applied at checkout.
- **Core features:** Generate code, apply, expire, single-use vs multi-use.
- **Hard parts:** Atomic decrement of redemption count, abuse detection, stacking rules.
- **Discuss:** Race condition on last redemption; fraud rings.

### 50. Design Shopping Cart Abandonment Pipeline
- **Scale:** 10M abandoned carts/day.
- **Core features:** Detect abandonment, send email after 1h, after 24h.
- **Hard parts:** Session vs cart vs user, opt-out compliance, A/B testing emails.
- **Discuss:** Event-driven (Kafka) vs cron sweep.

### 51. Design Multi-currency / Multi-region Pricing
- **Scale:** 200 currencies, 50 regions, daily FX updates.
- **Core features:** Show local price, tax, region-specific availability.
- **Hard parts:** Price freezing in cart for X minutes, FX update propagation.
- **Discuss:** Pre-compute all prices vs convert on read.

### 52. Design a Fraud Detection System (e-commerce)
- **Scale:** 100K transactions/sec, decision in <50ms.
- **Core features:** Score transaction, block/allow, manual review queue.
- **Hard parts:** Real-time features, ML model serving, feedback loop.
- **Discuss:** Feature store, online vs offline features, model retraining cadence.

### 53. Design DoorDash / UberEats
- **Scale:** 30M users, 500K restaurants, peak dinner hours.
- **Core features:** Browse, order, real-time dispatch to driver, ETA, tracking.
- **Hard parts:** Driver matching (3-sided marketplace), surge pricing, ETA prediction.
- **Discuss:** Restaurant capacity, batched delivery, geo-indexing for nearby drivers.

### 54. Design Marketplace Search (Amazon/eBay)
- **Scale:** 200M items, search in <200ms.
- **Core features:** Keyword, filter (price, brand), sort, autocomplete.
- **Hard parts:** Relevance ranking, faceted filters, real-time inventory.
- **Discuss:** Elasticsearch vs Solr; how to keep index in sync with primary DB.

---

# 🏨 5. Booking & Reservations

### 55. Design Airbnb
- **Scale:** 150M users, 7M listings, calendar-based.
- **Core features:** Search by location/date, book, host calendar, reviews, messaging.
- **Hard parts:** Geospatial search, calendar consistency (no double-book), pricing engine, trust signals.
- **Discuss:** S2/H3 cells for geo, locking for booking, hot listings.

### 56. Design Booking.com / Hotel Reservations
- **Scale:** 28M listings, multi-supplier (chains, OTAs).
- **Core features:** Search, compare, book, cancel.
- **Hard parts:** Real-time availability across suppliers, channel manager sync, GDS integration.
- **Discuss:** Cache stale availability vs always live; how to avoid double-bookings across channels.

### 57. Design a Movie Ticket Booking System (BookMyShow)
- **Scale:** 50K theaters, 100K concurrent users at release time.
- **Core features:** Browse shows, pick seats, hold seat for 5 min, pay, ticket QR.
- **Hard parts:** Seat-hold race, distributed locking on seat, theater-side sync.
- **Discuss:** Optimistic vs pessimistic locking; graceful unhold on payment failure.

### 58. Design a Flight Booking System
- **Scale:** Multi-airline, multi-leg, GDS integration (Sabre, Amadeus).
- **Core features:** Search by O&D, fare calc, hold, book, e-ticket.
- **Hard parts:** Itinerary combinations, fare rules, hold expiry, currency.
- **Discuss:** Why caching airline inventory is dangerous; on-demand vs pre-fetched.

### 59. Design a Restaurant Reservation System (OpenTable)
- **Scale:** 60K restaurants, table-level inventory.
- **Core features:** Search by cuisine/time, book table, no-show prediction.
- **Hard parts:** Real-time table availability, party-size matching, restaurant POS integration.
- **Discuss:** Restaurant-side push vs polling; show "first available time".

### 60. Design a Doctor / Clinic Appointment System
- **Scale:** 1M doctors, slot-based.
- **Core features:** Search doctor, book slot, reschedule, reminders.
- **Hard parts:** Doctor's time-block availability, double-booking, cross-clinic doctors.
- **Discuss:** Slot vs queue model; SMS reminder pipeline.

### 61. Design Calendly / Meeting Scheduler
- **Scale:** 10M users, calendar integrations.
- **Core features:** Public link with availability, multi-calendar sync, time-zone aware.
- **Hard parts:** Cross-calendar conflict detection, busy/free polling, time-zone DST bugs.
- **Discuss:** Push notifications from Google/Microsoft vs polling; cache invalidation on edit.

### 62. Design a Meeting Room / Workspace Booking
- **Scale:** Enterprise: 10K rooms across 100 offices.
- **Core features:** Find available room, book, recurring, equipment filters.
- **Hard parts:** Recurring meeting expansion, conflict detection, no-show release.
- **Discuss:** Materialize occurrences vs compute on demand; smart-room sensor integration.

### 63. Design a Car-Rental Service (Hertz/Turo)
- **Scale:** 100K cars, location-based.
- **Core features:** Pick location, dates, vehicle class, book.
- **Hard parts:** Inventory across locations, one-way drop-off, dynamic pricing.
- **Discuss:** Vehicle-class vs specific-vehicle; demand-based price adjust.

### 64. Design Event Ticketing (Eventbrite / Ticketmaster)
- **Scale:** Hot drop = 1M concurrent users at sale start.
- **Core features:** Browse events, queue at sale start, pick seat, buy.
- **Hard parts:** Virtual waiting room, anti-bot, fairness, queueing.
- **Discuss:** Queue-based admission, "you're #5,000 in line", session tokens.

---

# 🗺️ 6. Maps & Location

### 65. Design Uber / Lyft (Ride-sharing)
- **Scale:** 100M riders, 5M drivers, 30M rides/day.
- **Core features:** Request ride, match driver, ETA, route, fare, in-app payment.
- **Hard parts:** Geo-index for nearest driver (S2/H3), matching algorithm, surge, driver pings.
- **Discuss:** How does dispatch select 1 driver from 100 nearby? Why grid-based geo > radius search.

### 66. Design Google Maps
- **Scale:** 1B users, real-time traffic, turn-by-turn navigation.
- **Core features:** Map tiles, search, directions, real-time traffic, street view.
- **Hard parts:** Map tile generation pyramid, A* / Contraction Hierarchies for routing, traffic ingestion.
- **Discuss:** Tile pre-rendering vs vector tiles on client; ETA with traffic.

### 67. Design Yelp / Google Places
- **Scale:** 200M users, 5M businesses, location-based search.
- **Core features:** Search nearby, filter, reviews, photos, business profile.
- **Hard parts:** Geospatial search with filters, review moderation, business claim.
- **Discuss:** Geohash vs S2 vs PostGIS; cold-start ranking for new business.

### 68. Design a Geo-fenced Notification System
- **Scale:** 100M devices reporting location, 1M geofences.
- **Core features:** Define geofence, push notification on enter/exit.
- **Hard parts:** Battery efficiency on device, scaling many geofences, dwell-time triggers.
- **Discuss:** R-tree on server, on-device fence sharding, throttling overlapping fences.

### 69. Design a Real-time Location-sharing (Find My Friends)
- **Scale:** 100M users, opt-in friend list.
- **Core features:** See location of friends on map, history.
- **Hard parts:** Frequency of updates vs battery, privacy (whom to share with), encryption.
- **Discuss:** Push to interested subscribers vs poll; pub/sub with location channels.

### 70. Design Find My Device / Anti-theft Tracker
- **Scale:** 1B devices, low-power offline tracking (AirTag).
- **Core features:** Device beacons via Bluetooth, crowd-sourced location, mark as lost.
- **Hard parts:** End-to-end encryption (only owner reads), anti-stalking, dropped packets.
- **Discuss:** Apple's rotating-key approach; threat model and mitigations.

### 71. Design Surge Pricing (Uber Surge)
- **Scale:** Per-region per-minute updates.
- **Core features:** Detect demand vs supply imbalance, raise multiplier, notify riders/drivers.
- **Hard parts:** Real-time signal aggregation, smoothing (no jitter), fair pricing.
- **Discuss:** Hexagon-based zones; ML demand forecast.

### 72. Design a Delivery Dispatch (DoorDash)
- **Scale:** 1M concurrent orders, 1M dashers.
- **Core features:** Match order to dasher, batched deliveries, ETA.
- **Hard parts:** Bin-packing for batched delivery, ETA prediction, dasher acceptance rate.
- **Discuss:** Optimization problem; greedy vs ILP solver.

### 73. Design Live ETA Updates
- **Scale:** 10M concurrent rides each with live ETA.
- **Core features:** Push updated ETA every few seconds.
- **Hard parts:** Many small updates; rate-limiting per ride; battery efficiency.
- **Discuss:** Push to client vs client polls; ETA with traffic re-route.

### 74. Design Location History (Google Timeline)
- **Scale:** 1B users, store decades of location.
- **Core features:** View past trips, places visited, time spent.
- **Hard parts:** Privacy, storage tiering, place-detection (clustering).
- **Discuss:** GeoTime-series DB; how to detect "visited a place" from raw GPS.

### 75. Design a Bike / Scooter Sharing System (Lime/Bird)
- **Scale:** 100K vehicles in 100 cities.
- **Core features:** Find vehicle, unlock, ride, dock/end-trip, payment.
- **Hard parts:** Vehicle-state sync, geofence operating area, end-of-day rebalancing.
- **Discuss:** IoT integration with vehicle; battery monitoring.

### 76. Design Weather Service (OpenWeatherMap)
- **Scale:** 1B requests/day, location-based.
- **Core features:** Current weather, hourly, alerts.
- **Hard parts:** Multi-source data ingestion, geo-grid resolution, push alerts.
- **Discuss:** Cache by geohash; how to scale with global radar data.

---

# 📁 7. Storage & File Sharing

### 77. Design Dropbox / Google Drive
- **Scale:** 1B users, exabyte-scale storage.
- **Core features:** Upload, sync across devices, share, version history, search.
- **Hard parts:** Block-level dedup, delta sync, conflict resolution, multi-device sync.
- **Discuss:** Chunking strategy, presigned URLs, change journal.

### 78. Design Google Photos / iCloud Photos
- **Scale:** 1B users, 10TB per power user.
- **Core features:** Auto-upload, search by face/object, albums, sharing.
- **Hard parts:** ML-based tagging, deduplication of HEIC/JPEG, RAW handling.
- **Discuss:** Free-tier compression vs original quality; on-device ML vs server-side.

### 79. Design a S3-like Object Storage
- **Scale:** Trillions of objects, 11 nines durability.
- **Core features:** PUT/GET/DELETE, versioning, ACLs, lifecycle.
- **Hard parts:** Erasure coding, geo-replication, metadata index, hot-bucket throttling.
- **Discuss:** Why erasure coding > 3x replication at scale.

### 80. Design a Backup Service (Carbonite / Time Machine)
- **Scale:** 10M users, daily incremental backups.
- **Core features:** Continuous/scheduled backup, restore, encryption.
- **Hard parts:** Incremental dedup (rsync, content-addressable), restore speed.
- **Discuss:** Hash-based chunking; ransomware protection.

### 81. Design a CDN (Cloudflare / Akamai)
- **Scale:** Global PoPs, terabits/sec, 30T req/day.
- **Core features:** Edge cache, origin shield, purge API, WAF, DDoS mitigation.
- **Hard parts:** Cache hierarchy, consistent hashing, cache poisoning prevention.
- **Discuss:** Anycast routing, TLS termination at edge, stale-while-revalidate.

### 82. Design a File Sharing Link Service (WeTransfer)
- **Scale:** 50M users, files up to 200GB, 7-day TTL.
- **Core features:** Upload, share link, password protect, expiry.
- **Hard parts:** Resumable upload of huge files, abuse detection, hot links.
- **Discuss:** Direct-to-S3 upload; presigned URL strategy.

### 83. Design a Notes Sync (Apple Notes / Evernote)
- **Scale:** 100M users, multi-device sync.
- **Core features:** Create note, sync across devices, search, attachments.
- **Hard parts:** Conflict resolution (CRDT?), offline edits, encrypted notes.
- **Discuss:** OT vs CRDT; per-user merkle tree for sync.

### 84. Design Code Hosting (GitHub)
- **Scale:** 100M users, 400M repos, billions of files.
- **Core features:** Git push/pull, PRs, issues, actions.
- **Hard parts:** Git protocol at scale, large monorepos, code search.
- **Discuss:** Sharding by repo, pack files, bare-metal performance.

### 85. Design a Container Registry (Docker Hub / ECR)
- **Scale:** Petabytes of layers, billions of pulls/day.
- **Core features:** Push image, pull, tag, sign, scan.
- **Hard parts:** Layer dedup across users, vulnerability scanning, regional caches.
- **Discuss:** Content-addressable layers, GC for unreferenced.

### 86. Design a Content-Addressable Storage (IPFS-like)
- **Scale:** Distributed, peer-to-peer.
- **Core features:** Hash-addressed blocks, DHT, gateway HTTP access.
- **Hard parts:** Pinning incentives, NAT traversal, GC of unpinned.
- **Discuss:** Centralized gateway pragmatism; how to scale DHT lookups.

---

# 🔍 8. Search & Discovery

### 87. Design Google Search
- **Scale:** Web-scale crawl (trillions of pages), <300ms response.
- **Core features:** Crawl, index, rank, query, snippet.
- **Hard parts:** PageRank, freshness, anti-spam, query understanding.
- **Discuss:** Inverted index sharding, cache (cached results), query rewriting.

### 88. Design YouTube Search
- **Scale:** Billions of videos, real-time updates on new uploads.
- **Core features:** Title/desc/transcript search, filter, sort.
- **Hard parts:** Multi-modal ranking, freshness vs relevance, channel boost.
- **Discuss:** Typeahead, query expansion (synonyms).

### 89. Design Twitter Search
- **Scale:** 500M tweets/day, query historical + real-time.
- **Core features:** Search by keyword, hashtag, user, timeframe.
- **Hard parts:** Real-time index (within seconds of tweet), retention policy.
- **Discuss:** Earlybird-style index sharded by time; hot index in memory.

### 90. Design Autocomplete / Typeahead
- **Scale:** 100K QPS, <50ms latency.
- **Core features:** Suggest top-N completions, personalized, multilingual.
- **Hard parts:** Trie size, popularity update, typo tolerance.
- **Discuss:** Pre-built suggestion trie; weighted edges.

### 91. Design "Did You Mean?" / Spell Correction
- **Scale:** Same as search.
- **Core features:** Suggest corrected query, top results.
- **Hard parts:** Edit distance at scale, query log mining.
- **Discuss:** N-gram language model; user click signal as feedback.

### 92. Design a Document Search Engine (Internal — Confluence)
- **Scale:** 1M documents per workspace.
- **Core features:** Full-text + permission-aware results.
- **Hard parts:** ACL filter at index time vs query time.
- **Discuss:** Per-workspace index vs shared with ACL filter.

### 93. Design Code Search (GitHub Code Search)
- **Scale:** 1B+ source files, regex queries.
- **Core features:** Symbol search, regex, language filter.
- **Hard parts:** Trigram index, regex on 1B docs, line-level ranking.
- **Discuss:** Why trigram; sparse-grams; regex acceleration.

### 94. Design a Vector Search Service (Pinecone)
- **Scale:** Billions of vectors, ANN queries.
- **Core features:** Upsert vector, k-NN query, filter by metadata.
- **Hard parts:** ANN index (HNSW, IVF), index rebuilds, hybrid search.
- **Discuss:** Memory vs disk, quantization, multi-tenant isolation.

### 95. Design a Reverse Image Search (Google / TinEye)
- **Scale:** 1B images, <1s response.
- **Core features:** Upload image, find similar.
- **Hard parts:** Perceptual hashing + CNN embeddings, near-duplicate vs semantic similar.
- **Discuss:** pHash for near-dup, vector ANN for semantic.

### 96. Design a Job-Search Engine (LinkedIn Jobs)
- **Scale:** 30M active jobs, location + skill filters.
- **Core features:** Search, filter, alerts, recommendations.
- **Hard parts:** Skill normalization, recency boost, saved-search alerts.
- **Discuss:** Async alert pipeline; ranking with personalization.

---

# ✏️ 9. Real-time Collaboration

### 97. Design Google Docs (Real-time Collaborative Editor)
- **Scale:** 100M users, up to 100 collaborators per doc.
- **Core features:** Concurrent edit, presence, comments, suggestions.
- **Hard parts:** OT vs CRDT, conflict resolution, offline edits.
- **Discuss:** Why OT in Docs; cursor presence pub/sub; auto-save cadence.

### 98. Design Figma (Collaborative Vector Editor)
- **Scale:** 4M+ designers, large files.
- **Core features:** Concurrent shape edits, multi-cursor, comments, plugins.
- **Hard parts:** CRDT for vector ops, large-scene perf, web-based rendering.
- **Discuss:** WebGL canvas; selective sync of viewport.

### 99. Design Notion (Block-based Collaborative Doc)
- **Scale:** 30M users, page tree.
- **Core features:** Blocks (text, image, embed), nested, real-time edit.
- **Hard parts:** Block-tree CRDT, permissions on subtree, search.
- **Discuss:** Block as first-class node; sync engine.

### 100. Design Miro / Whiteboard
- **Scale:** 30M users, shared infinite canvas.
- **Core features:** Sticky notes, draw, multi-user cursors, voice.
- **Hard parts:** Spatial sync (only viewport), CRDT for shapes.
- **Discuss:** Quad-tree partitioning of canvas; LOD rendering.

### 101. Design Real-time Code Editor (VS Code Live Share)
- **Scale:** Pair-programming sessions.
- **Core features:** Shared editor, terminal, language server.
- **Hard parts:** Cursor sync, language-server multiplexing, latency.
- **Discuss:** Operational transforms on text; bandwidth for terminal.

### 102. Design Excel/Sheets Collaborative
- **Scale:** 100M users.
- **Core features:** Concurrent edit, formulas, comments, history.
- **Hard parts:** Formula recompute on collaborator's edit, cycles.
- **Discuss:** Dependency graph; partial recompute.

### 103. Design Comments + Reactions on a Document
- **Scale:** Like Google Docs comments.
- **Core features:** Anchored comments, threads, resolve, mention.
- **Hard parts:** Anchor when text moves, comment migration on big edit.
- **Discuss:** Anchor as character range vs node-id.

### 104. Design Real-time Multiplayer Whiteboarding (Excalidraw)
- **Scale:** 1M concurrent rooms.
- **Core features:** Free-draw, shapes, export.
- **Hard parts:** P2P vs server-relayed, low-latency strokes.
- **Discuss:** WebRTC datachannel; server-authoritative for export.

### 105. Design Version-history / Time Machine for a Doc
- **Scale:** 100M docs, 1000 versions each.
- **Core features:** Browse versions, restore, diff.
- **Hard parts:** Storage compression (delta), retention policy.
- **Discuss:** Snapshot every N edits + delta in between.

### 106. Design @Mention + Notifications in Collaborative Tools
- **Scale:** 1M users, mentions in comments/chats.
- **Core features:** Detect mention, route notification, mark read.
- **Hard parts:** Spam mentions, mute settings.
- **Discuss:** Notification fanout from collab tool.

---

# 💳 10. Payments & Finance

### 107. Design Stripe / Payment Gateway
- **Scale:** 250M API requests/day, 10M merchants.
- **Core features:** Charge card, subscriptions, refunds, payouts, idempotency.
- **Hard parts:** Idempotency keys, double-entry ledger, payment routing, PCI scope.
- **Discuss:** Why ledger > balance update; reconciliation pipelines.

### 108. Design a Digital Wallet (Venmo / Cash App)
- **Scale:** 80M users, P2P transfers.
- **Core features:** Add funds, send/receive, social feed, instant transfer.
- **Hard parts:** ACID across accounts, fraud, AML/KYC.
- **Discuss:** Account-balance vs ledger model; idempotent transfers.

### 109. Design a Banking Core Ledger
- **Scale:** Millions of accounts, ACID transfers.
- **Core features:** Debit, credit, statements, hold, reverse.
- **Hard parts:** Strong consistency, audit trail, regulatory reporting.
- **Discuss:** Why an immutable append-only ledger; reconciliation.

### 110. Design a Trading System (Robinhood)
- **Scale:** 25M users, broker integration.
- **Core features:** Buy/sell stocks/crypto, watchlist, real-time quotes.
- **Hard parts:** Order routing, order book, market data fan-out, halts.
- **Discuss:** Internal matching vs market routing; outage handling.

### 111. Design a Crypto Exchange (Coinbase / Binance)
- **Scale:** 100M users, real-time order matching.
- **Core features:** Order book, deposit/withdraw, market data, OHLC charts.
- **Hard parts:** Single-threaded matching engine, hot/cold wallet, withdrawal review.
- **Discuss:** Why matching is not horizontally sharded by symbol; risk engine.

### 112. Design Stock Market Order-Matching Engine
- **Scale:** Microsecond latency, millions of orders/sec.
- **Core features:** Limit, market, stop orders; cancel; partial fill.
- **Hard parts:** Determinism, FIFO at price-level, fairness, audit.
- **Discuss:** Single-threaded for determinism; multicast feeds.

### 113. Design a Payment Splitting System (Splitwise)
- **Scale:** 10M users, group expenses.
- **Core features:** Add expense, split among friends, settle up.
- **Hard parts:** Multi-currency, owe-graph, payback simplification.
- **Discuss:** Graph algorithms for minimum settlement set.

### 114. Design Subscription Billing (Stripe Billing)
- **Scale:** 1M merchants, recurring charges.
- **Core features:** Plans, trials, proration, dunning, invoices.
- **Hard parts:** Time-zone-aware renewals, failed-charge retry, proration math.
- **Discuss:** Idempotent renewals; jobs queue per customer.

### 115. Design a Loyalty / Points System
- **Scale:** 100M users, multi-merchant.
- **Core features:** Earn, redeem, expiry, tiers.
- **Hard parts:** Anti-double-redeem, cross-merchant settlement.
- **Discuss:** Ledger-based points balance; expiry sweeper.

### 116. Design Tax Calculation Service (Avalara / Stripe Tax)
- **Scale:** Per-checkout, sub-50ms.
- **Core features:** Compute tax by jurisdiction, rate tables, exemptions.
- **Hard parts:** 12K US tax jurisdictions, frequent rate changes, B2B exempt cert.
- **Discuss:** Hot reload of rate tables; address normalization.

### 117. Design a Refund / Dispute Pipeline
- **Scale:** 1% of transactions disputed.
- **Core features:** Initiate refund, partial, evidence collection for chargeback.
- **Hard parts:** Async with bank, time limits, evidence storage.
- **Discuss:** State machine; SLA tracking.

### 118. Design a Real-time Risk Scoring Service
- **Scale:** 100K transactions/sec, <50ms decision.
- **Core features:** Score, decision (approve/review/decline), feedback loop.
- **Hard parts:** Online features (recent velocity), model versioning.
- **Discuss:** Feature store, A/B model serving, drift monitoring.

---

# 📢 11. Ads & Marketing

### 119. Design Google AdWords / Ad Auction
- **Scale:** Billions of auctions/day, <100ms.
- **Core features:** Advertiser bid, keyword targeting, ranking, billing.
- **Hard parts:** Real-time auction (GSP/VCG), quality score, budget pacing.
- **Discuss:** Why second-price auction; pacing across day.

### 120. Design Google AdSense (Publisher-side Ads)
- **Scale:** Millions of publisher sites.
- **Core features:** Ad slot, targeting, click tracking, payout.
- **Hard parts:** Click fraud, viewability, compliance.
- **Discuss:** Bot detection signals; revenue share model.

### 121. Design a Real-time Bidding (RTB) Platform
- **Scale:** 10M QPS bid requests, 50ms timeout.
- **Core features:** Bid request, response, win notice, billing.
- **Hard parts:** Timeout budget, candidate filtering, low-latency network.
- **Discuss:** OpenRTB protocol; geo-distributed edge bidders.

### 122. Design Pixel / Conversion Tracking
- **Scale:** 1B events/day.
- **Core features:** Tracking pixel, deduplicate, attribution.
- **Hard parts:** Cross-device attribution, privacy (cookies, ATT), spam.
- **Discuss:** Server-side conversion API; identity graph.

### 123. Design an Email Marketing Platform (Mailchimp)
- **Scale:** 12M users, 10B emails/month.
- **Core features:** List management, template, send, track opens/clicks, A/B.
- **Hard parts:** Deliverability (SPF/DKIM/DMARC), bounce handling, unsubscribe compliance.
- **Discuss:** Sending rate per IP; warming new IPs.

### 124. Design a Push Marketing Campaign Platform
- **Scale:** Send to 100M users in 1 hour.
- **Core features:** Audience targeting, schedule, A/B variants, deliverability tracking.
- **Hard parts:** Throttle to APNs/FCM limits, time-zone-staggered send.
- **Discuss:** Scheduler + worker model; per-channel rate limits.

### 125. Design Attribution (Multi-touch)
- **Scale:** 100M users, all touchpoints over 30 days.
- **Core features:** Track touch, attribute conversion (last-click, linear, U-shape).
- **Hard parts:** Identity stitching, touch retention, fractional credit.
- **Discuss:** Session vs touchpoint; Markov-chain attribution.

### 126. Design A/B Testing / Experiment Platform
- **Scale:** 10K simultaneous experiments.
- **Core features:** Define experiment, assign user, log exposure, compute metrics, decide winner.
- **Hard parts:** Bucketing consistency, peeking problem, sequential testing.
- **Discuss:** Sticky bucketing via hash; SRM checks.

### 127. Design a Recommendation Email ("Today's deals")
- **Scale:** Daily personalized email to 100M users.
- **Core features:** Generate per-user candidates, render template, send.
- **Hard parts:** Throughput of nightly job, candidate diversity.
- **Discuss:** Offline batch + per-user assembly; opt-out/spam compliance.

### 128. Design a Coupon Distribution System
- **Scale:** 100K codes per campaign, fair distribution.
- **Core features:** Generate batch, assign per claim, expiry.
- **Hard parts:** Race-free claim, fraud (one per user).
- **Discuss:** Pre-allocate vs claim-on-demand.

---

# 🎮 12. Gaming

### 129. Design a Real-time Multiplayer FPS Server (Counter-Strike-like)
- **Scale:** 64-player matches, <50ms latency.
- **Core features:** Authoritative server, tick-based sim, anti-cheat.
- **Hard parts:** Lag compensation, client prediction, anti-cheat.
- **Discuss:** UDP vs TCP; tick rate vs interpolation.

### 130. Design an MMO Server (World of Warcraft)
- **Scale:** Millions of players, persistent world.
- **Core features:** Zones/instances, world chat, inventory, raids.
- **Hard parts:** Zone sharding, cross-zone events, character DB.
- **Discuss:** Player handoff between zones; auction house.

### 131. Design Matchmaking System (Skill-based)
- **Scale:** 1M concurrent players, find a match in <60s.
- **Core features:** Queue, ELO/MMR, party support, region.
- **Hard parts:** Queue time vs match quality, expansion of skill range over time.
- **Discuss:** Trueskill / Glicko; queue priority.

### 132. Design a Game Leaderboard at Global Scale
- **Scale:** 100M players, real-time top-K.
- **Core features:** Submit score, top-K query, my-rank.
- **Hard parts:** Approximate rank for 100Mth user; real-time updates.
- **Discuss:** Redis sorted-set sharding; bucketing for percentile.

### 133. Design an In-Game Chat (Lobby + Team)
- **Scale:** Same as concurrent players.
- **Core features:** Lobby chat, team chat, ban / mute.
- **Hard parts:** Profanity filter, low-latency delivery.
- **Discuss:** Pub/sub per channel; rate limit per user.

### 134. Design a Gaming Asset Distribution (CDN for Game Updates)
- **Scale:** 10M downloads of 50GB patch on launch day.
- **Core features:** Delta patching, parallel CDN, signature verification.
- **Hard parts:** Bandwidth peak, partial-download resumption.
- **Discuss:** Pre-position to PoPs; P2P assist.

### 135. Design an In-game Ad / Sponsorship Platform
- **Scale:** 100M players, dynamic 3D ad slots.
- **Core features:** Serve ads, track impression, brand safety.
- **Hard parts:** 3D-rendered ads, viewability, kid-safe filtering.
- **Discuss:** SDK vs server-side rendering.

### 136. Design a Turn-based Game Server (Chess.com)
- **Scale:** 10M concurrent games.
- **Core features:** Move, validate, save game, replay.
- **Hard parts:** Anti-cheat (engine detection), live spectate.
- **Discuss:** Server validation per move; ELO.

### 137. Design an Online Casino / Slot Game
- **Scale:** 1M concurrent players, regulated.
- **Core features:** RNG, payouts, jurisdiction filtering.
- **Hard parts:** Provably-fair RNG, audit trails, geofencing for regulation.
- **Discuss:** Server-side RNG with hashing; immutable bet history.

### 138. Design a Battle Royale Lobby (100 players per match)
- **Scale:** Millions of concurrent matches.
- **Core features:** Lobby fill, drop into game, post-game.
- **Hard parts:** Server provisioning, region matchmaking.
- **Discuss:** Game-server orchestration via Kubernetes / Agones.

---

# 📡 13. IoT & Devices

### 139. Design a Smart Home Hub (Google Home / Alexa)
- **Scale:** 100M devices, voice + control.
- **Core features:** Voice command, device control, routines.
- **Hard parts:** Local vs cloud control, vendor integrations, latency.
- **Discuss:** Local fallback when offline; security model.

### 140. Design a Connected Car Telemetry Pipeline
- **Scale:** 10M cars, 1Hz telemetry.
- **Core features:** Ingest signals, real-time alerts, OTA updates.
- **Hard parts:** Cell-network unreliability, OTA rollback, fleet-wide queries.
- **Discuss:** MQTT, edge filtering.

### 141. Design a Wearable Health Tracker Backend
- **Scale:** 50M devices, sub-second heart-rate ingest.
- **Core features:** Sync, dashboards, alerts (high HR), HIPAA.
- **Hard parts:** Battery-friendly upload, data retention, HIPAA scope.
- **Discuss:** Batch sync vs streaming; encryption keys.

### 142. Design an IoT Device Provisioning System
- **Scale:** Onboard 1M devices/month.
- **Core features:** Device cert, TLS bootstrap, claim by user.
- **Hard parts:** Hardware secure element, certificate rotation, anti-spoofing.
- **Discuss:** PKI; just-in-time provisioning at edge.

### 143. Design an Industrial Sensor Network (Factory Floor)
- **Scale:** 100K sensors per facility.
- **Core features:** Real-time dashboards, anomaly detect, historian DB.
- **Hard parts:** Sub-second alerts, downsampling for archive.
- **Discuss:** Time-series DB; on-prem ingestion gateway.

### 144. Design a Smart-Lock Backend
- **Scale:** 5M locks, sub-second unlock latency.
- **Core features:** Issue/revoke key, audit log, offline unlock.
- **Hard parts:** Offline-resilient cryptography, anti-relay attacks.
- **Discuss:** Tokens with short expiry; bluetooth attestation.

### 145. Design a Maritime AIS Tracking Backend
- **Scale:** 100K vessels, 1 ping/min.
- **Core features:** Real-time map, route history, port arrival alerts.
- **Hard parts:** Sparse coverage areas, satellite vs terrestrial AIS.
- **Discuss:** Geo-bucketed map tiles; replay query.

### 146. Design an OTA (Over-the-Air) Firmware Update Platform
- **Scale:** 10M devices, staged rollout.
- **Core features:** Build, sign, distribute, percent-rollout, rollback.
- **Hard parts:** Atomic update, A/B partition, signature verification.
- **Discuss:** TUF (The Update Framework); device fleet sharding.

---

# 📊 14. Analytics & Logging

### 147. Design a Web Analytics Service (Google Analytics)
- **Scale:** 50M sites, 100B events/day.
- **Core features:** Pageview tracking, funnels, real-time, retention.
- **Hard parts:** Cardinality of dimensions, privacy (GDPR), real-time aggregates.
- **Discuss:** Sampling for big sites; columnar OLAP.

### 148. Design a Logging / Log-aggregation Platform (Splunk / Datadog Logs)
- **Scale:** 10PB ingest/day.
- **Core features:** Ingest, parse, search, alert.
- **Hard parts:** Hot-cold tiering, query on petabytes, multi-tenant cost.
- **Discuss:** Inverted index per time-bucket; Lucene-based.

### 149. Design a Metrics Platform (Prometheus / Datadog Metrics)
- **Scale:** 100M time-series, sub-second writes.
- **Core features:** Ingest gauge/counter, query (PromQL), alert.
- **Hard parts:** Cardinality explosion, downsampling, long-term storage.
- **Discuss:** Pull vs push (Prom is pull); remote-write.

### 150. Design Distributed Tracing (Jaeger / Honeycomb)
- **Scale:** 1B spans/day, query <2s.
- **Core features:** Span ingest, trace assembly, query by trace-id, latency-tail dashboards.
- **Hard parts:** Sampling strategy, span volume, trace stitching across services.
- **Discuss:** Head vs tail sampling; OpenTelemetry.

### 151. Design a Real-time Dashboard (Grafana-like)
- **Scale:** 10K concurrent dashboards.
- **Core features:** Multi-source query, panels, alerts.
- **Hard parts:** Query backend across data sources, cache freshness.
- **Discuss:** Live mode via streams; query coalescing.

### 152. Design a Click-stream Pipeline (Snowplow)
- **Scale:** 10B events/day.
- **Core features:** Ingest event, validate schema, enrich, sink to warehouse.
- **Hard parts:** Schema evolution, late-arriving data, dedup.
- **Discuss:** Streaming SQL; self-describing JSON.

### 153. Design a Funnel Analysis Engine
- **Scale:** Compute conversion across 7-day funnel.
- **Core features:** Define steps, compute counts at each step.
- **Hard parts:** Joinless multi-step (sessionize), order matters.
- **Discuss:** Bitmap per user; pre-computed funnels.

### 154. Design a Cohort Analysis Engine
- **Scale:** Cohort by signup-week, retention curve.
- **Core features:** Cohort definition, retention chart.
- **Hard parts:** Compute on event store at warehouse scale.
- **Discuss:** Materialized views; OLAP cube.

### 155. Design Real-time Anomaly Detection
- **Scale:** 100K series, alert in seconds.
- **Core features:** Stream, baseline, alert.
- **Hard parts:** Seasonality, bursts, false-positive rate.
- **Discuss:** EWMA, Prophet, isolation forest.

### 156. Design a Centralized Audit Log (SOC2)
- **Scale:** Immutable, 7-year retention.
- **Core features:** Append-only, search, tamper-evident.
- **Hard parts:** Immutability guarantee, fast point-in-time search.
- **Discuss:** Hash-chained log; WORM storage.

---

# ⚙️ 15. Infrastructure & DevOps

### 157. Design a CI/CD Platform (Jenkins / CircleCI)
- **Scale:** 1M jobs/day, 100K concurrent.
- **Core features:** Trigger build, run pipeline, parallel jobs, artifacts.
- **Hard parts:** Worker autoscale, build cache, secret injection.
- **Discuss:** Job queue, build isolation (containers vs VMs).

### 158. Design a Feature-Flag Service (LaunchDarkly)
- **Scale:** 10K customers, sub-50ms flag evaluation.
- **Core features:** Define flag, target rules, percentage rollout, audit.
- **Hard parts:** Edge-evaluation (no roundtrip), instant propagation.
- **Discuss:** SSE / streaming for flag updates; SDK with local cache.

### 159. Design a Configuration Service (Etcd / Consul)
- **Scale:** Strong-consistent KV for cluster.
- **Core features:** Get/Put with watch, leases, distributed lock.
- **Hard parts:** Linearizable reads, lease expiration, leader election.
- **Discuss:** Raft; how watches handle disconnects.

### 160. Design a Container Orchestration (Kubernetes-lite)
- **Scale:** 5K nodes, 100K pods.
- **Core features:** Schedule pods, health-check, autoscale, networking.
- **Hard parts:** Scheduler bin-packing, networking (CNI), service discovery.
- **Discuss:** etcd for state; controller pattern.

### 161. Design a Function-as-a-Service Platform (Lambda)
- **Scale:** Cold start in <100ms.
- **Core features:** Upload code, trigger, scale, billing per ms.
- **Hard parts:** Sandbox (Firecracker), warm-pool, cold-start mitigation.
- **Discuss:** MicroVM vs containers; pre-warming.

### 162. Design a Secret-Management System (Vault)
- **Scale:** 1M secrets, audit.
- **Core features:** Store secret, lease, rotate, audit log.
- **Hard parts:** Encryption at rest with HSM, dynamic secrets, ACLs.
- **Discuss:** Master key sealing; auto-unseal.

### 163. Design a DNS Provider (Route53)
- **Scale:** Anycast, billions of queries/day.
- **Core features:** Zone management, geolocation routing, health checks.
- **Hard parts:** Anycast routing, fast propagation, DDoS resilience.
- **Discuss:** Why edge resolvers + auth servers split; cache TTL.

### 164. Design a Load Balancer (L7)
- **Scale:** 1M RPS per LB instance.
- **Core features:** TLS termination, routing, health checks, sticky sessions.
- **Hard parts:** Connection pool to backends, graceful drain.
- **Discuss:** L4 vs L7 trade-offs; consistent hashing for cache-affinity.

### 165. Design a Service Mesh (Istio / Linkerd)
- **Scale:** 10K services, mTLS everywhere.
- **Core features:** Sidecar proxy, retries, traffic shifting, telemetry.
- **Hard parts:** Sidecar overhead, control-plane scale, certificate rotation.
- **Discuss:** Envoy proxy; xDS API.

### 166. Design a Build-cache / Remote-build Execution (Bazel RBE)
- **Scale:** 1B build actions/day.
- **Core features:** Cache hit lookup, remote execution, deterministic build.
- **Hard parts:** Hash-action correctness, distributed cache, hermetic builds.
- **Discuss:** Action input hashing; cache eviction.

### 167. Design a Package Registry (npm / PyPI)
- **Scale:** 200B downloads/year.
- **Core features:** Publish package, version, dependency, search.
- **Hard parts:** Squatting, supply-chain attacks, mirror.
- **Discuss:** Immutable publishes; signing.

### 168. Design a Multi-tenant SaaS Database
- **Scale:** 10K tenants, isolation + shared cost.
- **Core features:** Per-tenant data, query, backup.
- **Hard parts:** Noisy neighbor, schema migration, per-tenant restore.
- **Discuss:** Shared vs siloed vs hybrid; tenant_id in row.

---

# 🤖 16. AI / ML Systems

### 169. Design an LLM Serving Platform (Like Anthropic API)
- **Scale:** 1M req/sec, multiple model sizes.
- **Core features:** API endpoint, streaming, batching, billing.
- **Hard parts:** GPU scheduling, KV-cache reuse, queueing.
- **Discuss:** Continuous batching; speculative decoding.

### 170. Design an LLM Gateway / Cost-Routing Layer
- **Scale:** Multi-model (OpenAI, Anthropic, OSS).
- **Core features:** Route by cost/latency, cache, fallback.
- **Hard parts:** Cache key for prompts, fallback during outage, cost attribution.
- **Discuss:** Semantic vs exact-match cache; circuit breaker per provider.

### 171. Design a Vector DB-Backed RAG System
- **Scale:** 1B documents indexed.
- **Core features:** Ingest, embed, retrieve, generate.
- **Hard parts:** Chunking, hybrid search, freshness.
- **Discuss:** Re-ranker, latency budget, eval pipeline.

### 172. Design a Recommendation Pipeline (Two-tower Model)
- **Scale:** 1B users, 100M items.
- **Core features:** Train, deploy, serve, online features.
- **Hard parts:** ANN for retrieval, online + offline features, ranker.
- **Discuss:** Candidate gen vs ranker; cold start.

### 173. Design an ML Feature Store (Feast)
- **Scale:** 10K features, online + offline.
- **Core features:** Define feature, materialize, serve online (Redis), offline (warehouse).
- **Hard parts:** Train-serve skew, online TTL, point-in-time correctness.
- **Discuss:** Why two stores; unified read API.

### 174. Design an ML Model Registry + CI/CD for Models
- **Scale:** 1000 models, multi-team.
- **Core features:** Register, version, promote, rollback, A/B.
- **Hard parts:** Reproducibility, lineage, shadow deploy.
- **Discuss:** Why model registry; canarying for ML.

### 175. Design a Federated Learning Platform
- **Scale:** 1B mobile devices.
- **Core features:** Train on-device, aggregate, deploy.
- **Hard parts:** Differential privacy, secure aggregation, client selection.
- **Discuss:** FedAvg; opt-in/out.

### 176. Design a Speech-to-Text Service
- **Scale:** Millions of streams.
- **Core features:** Real-time + batch transcription, multi-language.
- **Hard parts:** Streaming inference, GPU pooling, language detection.
- **Discuss:** Word-by-word streaming; punctuation post-process.

### 177. Design a Content-Moderation Pipeline (CSAM / Toxicity)
- **Scale:** 1B images/day.
- **Core features:** Auto-detect (ML), human review queue, action.
- **Hard parts:** Latency for posting flow, escalation, appeals.
- **Discuss:** Sync block vs async post-publish; reviewer tooling.

### 178. Design an OCR Pipeline (Receipts, IDs)
- **Scale:** 100M docs/day.
- **Core features:** Upload doc, extract structured fields, redact PII.
- **Hard parts:** Multi-format, language support, accuracy thresholds.
- **Discuss:** Two-stage (detect → recognize); confidence-driven human review.

---

# 🔐 17. Security & Identity

### 179. Design SSO / OIDC Identity Provider
- **Scale:** 100M users.
- **Core features:** Login, refresh, logout, MFA, SAML/OIDC.
- **Hard parts:** Token revocation, device trust, session lifetimes.
- **Discuss:** Rotating refresh tokens; risk-based step-up.

### 180. Design a Permission System (Google Zanzibar)
- **Scale:** 10B objects, sub-50ms check.
- **Core features:** Define relation, check access, list permitted users.
- **Hard parts:** Graph traversal latency, consistent reads, write throughput.
- **Discuss:** Zookies for consistency; relation-tuple storage.

### 181. Design a 2FA / OTP Service
- **Scale:** 100M users.
- **Core features:** TOTP, SMS, push, backup codes.
- **Hard parts:** Replay protection, sub-30s validity, lockout.
- **Discuss:** TOTP secret storage; backup-code one-time use.

### 182. Design a Password-Reset Flow
- **Scale:** Common flow with security gotchas.
- **Core features:** Email link, single-use, expire.
- **Hard parts:** Email enumeration, reset-token leakage, race condition.
- **Discuss:** Generic response messages; signed tokens.

### 183. Design a Bot-Detection / CAPTCHA System
- **Scale:** 10M req/sec.
- **Core features:** Score request, challenge if low.
- **Hard parts:** Adversarial, accessibility, mobile.
- **Discuss:** Behavioral signals; risk-engine + challenge.

### 184. Design a Web Application Firewall (WAF)
- **Scale:** 1M RPS.
- **Core features:** Rule engine, allow/block, log, virtual patching.
- **Hard parts:** Performance of regex rules, false positive, rule rollouts.
- **Discuss:** Edge deployment; managed rule sets.

### 185. Design a DDoS Mitigation System
- **Scale:** Tbps attacks.
- **Core features:** Detect, scrub, allow-list legit traffic.
- **Hard parts:** Volumetric vs L7, BGP rerouting, false positive.
- **Discuss:** Scrubbing centers; rate limiting at edge.

### 186. Design a Secret-Scanning Service (GitHub Secret Scanning)
- **Scale:** Scan billions of commits.
- **Core features:** Detect leaked secrets, notify token providers, revoke.
- **Hard parts:** Pattern matching at scale, partner integrations.
- **Discuss:** Streaming scan vs batch; token-provider partnerships.

---

# 🛠️ 18. Communication Tools

### 187. Design Email Service (Gmail-lite)
- **Scale:** 1B users, IMAP/SMTP.
- **Core features:** Send, receive, search, filter, spam.
- **Hard parts:** Spam (Bayesian/ML), search, threading, attachment.
- **Discuss:** Per-user inverted index; conversation grouping.

### 188. Design a Calendar Service (Google Calendar)
- **Scale:** 1B users, recurring events.
- **Core features:** Event CRUD, recurring rules (RRULE), invite, free-busy.
- **Hard parts:** Recurring expansion, time zones with DST, free/busy across calendars.
- **Discuss:** Materialize occurrences vs compute; conflict detection.

### 189. Design a Contact / Address Book Sync
- **Scale:** 1B contacts.
- **Core features:** CRUD, multi-device sync, dedup.
- **Hard parts:** Merge from multiple sources, conflict on edit.
- **Discuss:** vCard standard; sync token.

### 190. Design a To-Do / Task App with Sync (Todoist)
- **Scale:** 30M users, multi-device.
- **Core features:** Tasks, projects, reminders, sync.
- **Hard parts:** Offline edits, conflict on simultaneous edits.
- **Discuss:** CRDT for tasks; reminder scheduler.

### 191. Design a Voicemail Transcription Service
- **Scale:** 100M users.
- **Core features:** Receive call, record, transcribe, deliver.
- **Hard parts:** Speech-to-text accuracy, multi-language, async pipeline.
- **Discuss:** Telephony integration; cost per minute.

### 192. Design Survey / Form Builder (Google Forms / Typeform)
- **Scale:** 10M forms, 1B submissions.
- **Core features:** Form builder, submission, response export, analytics.
- **Hard parts:** Anti-spam, conditional logic, integrations.
- **Discuss:** Form schema; per-form quotas.

### 193. Design a Screen-Sharing System (TeamViewer / RustDesk)
- **Scale:** 1M concurrent sessions.
- **Core features:** Screen capture, remote control, file transfer.
- **Hard parts:** Low-latency video, NAT traversal, security.
- **Discuss:** Direct P2P with relay fallback.

### 194. Design Webinar Platform (Zoom Webinars)
- **Scale:** 10K attendees per webinar.
- **Core features:** Presenter video, attendees view-only, Q&A, polls.
- **Hard parts:** One-to-many fanout, large-scale chat.
- **Discuss:** RTMP ingest + HLS delivery; scaling to 10K.

---

# 🔗 19. URL / Identifier / Misc

### 195. Design a URL Shortener (Bitly)
- **Scale:** 1B shortens, 10B clicks/month, 200:1 read:write.
- **Core features:** Shorten URL, redirect, click tracking, custom alias.
- **Hard parts:** ID generation (base62 of counter / hash), 301 vs 302, click analytics.
- **Discuss:** Counter-based vs random vs hash; cache for hot links.

### 196. Design a Distributed Unique-ID Generator (Snowflake)
- **Scale:** 1M IDs/sec, cluster-wide unique.
- **Core features:** 64-bit ID, sortable.
- **Hard parts:** Clock skew, machine ID assignment.
- **Discuss:** Twitter Snowflake bit layout; ULID alternative.

### 197. Design a Distributed Rate Limiter
- **Scale:** Limit per-user per-API per-minute.
- **Core features:** Allow/deny, per-key bucket.
- **Hard parts:** Cross-region consistency, sliding window vs token bucket.
- **Discuss:** Redis + Lua; sticky vs distributed counter.

### 198. Design a QR-Code Service (Generate + Scan-tracked)
- **Scale:** 100M QR codes.
- **Core features:** Generate, host short URL, track scans.
- **Hard parts:** High-volume redirect, dynamic QR, fraud.
- **Discuss:** CDN for QR images; rate limit per endpoint.

### 199. Design a Pastebin / Code Snippet Sharing
- **Scale:** 100M snippets.
- **Core features:** Upload, expiry, syntax highlight, public/private.
- **Hard parts:** Spam, abuse, malicious payloads.
- **Discuss:** Append-only store; expire job.

### 200. Design a Geocoding / Reverse-Geocoding Service
- **Scale:** 100K QPS.
- **Core features:** Address → lat/long, lat/long → address.
- **Hard parts:** Address normalization, multi-language, autocomplete.
- **Discuss:** Multi-tier cache; geohash bucketing.

### 201. Design a Translation Service (Google Translate)
- **Scale:** 1B requests/day, 100+ languages.
- **Core features:** Translate text/document, language detect, cache.
- **Hard parts:** Cache for repeated phrases, latency, batch large docs.
- **Discuss:** Sentence-level cache; ML inference fleet.

### 202. Design a Webhook Delivery Service (Stripe Webhooks)
- **Scale:** Reliable delivery to customer endpoints.
- **Core features:** Send event, retry on failure, signature.
- **Hard parts:** Slow customer endpoints, retry budget, replay attack protection.
- **Discuss:** Per-customer queue; signed payloads.

---

# 🏥 20. Healthcare & Medical

### 203. Design an Electronic Health Record (EHR) System
- **Scale:** 100M patient records, HIPAA-compliant, 50K hospitals.
- **Core features:** Patient profile, medical history, prescriptions, lab results, multi-provider access.
- **Hard parts:** HL7/FHIR integration, audit trail per access, break-glass emergency access, decades retention.
- **Discuss:** Multi-tenant DB strategy; field-level encryption for sensitive data.

### 204. Design a Telemedicine Video-Visit Platform
- **Scale:** 10M consultations/month, peak times.
- **Core features:** Schedule visit, waiting room, video call, e-prescription, payment.
- **Hard parts:** HIPAA-compliant WebRTC, recording for legal, e-Rx integration with pharmacies.
- **Discuss:** Where TURN servers sit; record-everything vs no-record policy.

### 205. Design a Hospital Patient Admission System
- **Scale:** 5K beds across multi-site hospital network.
- **Core features:** Admission, bed assignment, transfer, discharge, insurance verification.
- **Hard parts:** Bed availability across departments, transfer chain, insurance authorization async.
- **Discuss:** Bed-state machine; race condition on simultaneous admissions.

### 206. Design a Pharmacy Prescription System
- **Scale:** 50M Rx/year, multi-pharmacy.
- **Core features:** Rx receive, fill, refill, controlled-substance tracking, drug-interaction check.
- **Hard parts:** DEA controlled-substance audit, drug-interaction DB, fill-time SLA.
- **Discuss:** Why a real-time interaction engine; supplier integration.

### 207. Design a Medical Imaging (PACS) Storage System
- **Scale:** Petabyte-scale DICOM images.
- **Core features:** Upload imaging study, viewer integration, multi-site replication.
- **Hard parts:** DICOM compliance, on-demand decompression, radiologist viewer SLA.
- **Discuss:** Archive tiering; instant-load thumbnail generation.

### 208. Design a Clinical Trial Data Platform
- **Scale:** Multi-site trials, FDA-compliant.
- **Core features:** Patient enrollment, eCRF (case report forms), adverse event reporting, audit.
- **Hard parts:** 21 CFR Part 11 compliance, data lock for analysis, blind/unblind.
- **Discuss:** Why immutability is key; CDISC standards.

### 209. Design a Pulse-Oximeter / Wearable Health Aggregator
- **Scale:** 10M devices, second-by-second data.
- **Core features:** Ingest signals, anomaly detection, alert clinician.
- **Hard parts:** Battery efficiency, false-positive alerts, HIPAA encryption end-to-end.
- **Discuss:** Edge filter at phone vs full-fidelity stream.

### 210. Design a Mental-Health Therapy Booking Platform (BetterHelp)
- **Scale:** 4M users, therapist matching.
- **Core features:** Onboarding questionnaire, match therapist, chat, video.
- **Hard parts:** Anonymity with safety, escalation for self-harm, therapist licensing per state.
- **Discuss:** PII redaction in transcripts; emergency-contact policy.

### 211. Design a Vaccine Inventory & Cold-Chain Tracker
- **Scale:** Country-scale, 1B doses.
- **Core features:** Track vials, temperature, administer dose, audit.
- **Hard parts:** IoT sensor reliability, gap detection in cold chain, allocation fairness.
- **Discuss:** Edge buffering; central reconciliation.

### 212. Design a Genomic Sequencing Pipeline
- **Scale:** 100K samples/year, ~100GB per genome.
- **Core features:** Upload reads, alignment, variant call, store + query.
- **Hard parts:** Compute-heavy aligners, genomic DB, privacy of DNA data.
- **Discuss:** Why container-based pipeline (Cromwell/Nextflow); sample-level encryption.

### 213. Design an Insurance Claims-Adjudication System (Healthcare)
- **Scale:** 1B claims/year.
- **Core features:** Claim submission, eligibility check, adjudicate, pay/deny.
- **Hard parts:** Coding (ICD-10, CPT), prior-auth, appeals, fraud.
- **Discuss:** Rule-engine vs ML; clearinghouse integration.

### 214. Design a Hospital Radiology Workflow System
- **Scale:** 100K studies/day per hospital network.
- **Core features:** Order imaging, route to radiologist, report, sign-off.
- **Hard parts:** Worklist routing by sub-specialty, urgent vs routine, second-opinion.
- **Discuss:** Priority queue; tele-radiology routing across timezones.

### 215. Design a Personal Health Record (PHR) — Apple Health-like
- **Scale:** 1B users, multi-source aggregation.
- **Core features:** Aggregate from devices, hospitals, manual; trends; share.
- **Hard parts:** Source-of-truth conflicts, FHIR ingest, export to provider.
- **Discuss:** On-device storage vs cloud; HealthKit-like model.

### 216. Design a Clinical Decision Support (CDS) Engine
- **Scale:** Real-time alerts at every chart-open.
- **Core features:** Ingest patient state, run rule sets, surface alerts to clinician.
- **Hard parts:** Alert fatigue, real-time perf, evidence updates.
- **Discuss:** Tiered severity; rule authoring UI.

### 217. Design a Medical Device IoT Platform (Hospital-grade)
- **Scale:** 10K beds × 20 devices each.
- **Core features:** Stream vitals, alarms, archive.
- **Hard parts:** FDA SaMD classification, clock-sync, alarm storm.
- **Discuss:** Edge gateway per ward; alarm de-duplication.

### 218. Design a Dental Practice Management System
- **Scale:** 100K practices.
- **Core features:** Appointment, charting (tooth-by-tooth), imaging, billing.
- **Hard parts:** Tooth-state model, X-ray storage, multi-doctor schedule.
- **Discuss:** Per-tenant isolation; insurance eligibility integration.

### 219. Design a Donor-Match (Blood / Organ) System
- **Scale:** National registry, 100K donors.
- **Core features:** Match recipient to donor by blood type, HLA, geography.
- **Hard parts:** Multi-criteria match, life-or-death SLA, fairness algorithm.
- **Discuss:** Why matching is not horizontally sharded; ranking ethics.

### 220. Design a Health Insurance Provider-Network Search
- **Scale:** 5M providers, in-network filter.
- **Core features:** Search by specialty, location, plan, accepting-patients.
- **Hard parts:** Provider data freshness ("ghost networks"), plan-network mapping.
- **Discuss:** Provider-data ingest pipeline; periodic outreach.

### 221. Design a Patient-Portal Messaging System
- **Scale:** 50M users, HIPAA-secured.
- **Core features:** Patient ↔ care team chat, attachments, response SLA.
- **Hard parts:** Triage routing, auto-acknowledge, encryption.
- **Discuss:** Provider inbox vs patient inbox model.

### 222. Design a Lab-Result Reporting Pipeline
- **Scale:** 100M results/day.
- **Core features:** Lab → EHR delivery, abnormal flagging, patient portal release.
- **Hard parts:** Critical-value escalation, delayed-release for sensitive results.
- **Discuss:** HL7 v2 vs FHIR R4; delivery confirmation.

### 223. Design a Prior-Authorization Service
- **Scale:** 200M annual auth requests.
- **Core features:** Submit auth, attach docs, decision, appeal.
- **Hard parts:** Async insurer response (days), status updates, document OCR.
- **Discuss:** Workflow engine; supplemental info loop.

### 224. Design a Care-Coordination / Case-Management Platform
- **Scale:** Multi-disciplinary team for chronic patients.
- **Core features:** Care plan, tasks, cross-org messaging, outcomes.
- **Hard parts:** Different EHR sources, plan-level vs visit-level consent.
- **Discuss:** Federated identity across organizations.

### 225. Design a Health-Risk Assessment Engine
- **Scale:** Per-member real-time risk score.
- **Core features:** Ingest claims + clinical, score, surface high-risk for outreach.
- **Hard parts:** Feature freshness, claims latency (90 days), explainability.
- **Discuss:** Rolling features; SHAP for explanations.

### 226. Design a Telehealth Triage Bot
- **Scale:** 1M assessments/day.
- **Core features:** Symptom intake, triage to ER/urgent/PCP/self-care.
- **Hard parts:** Clinical safety, multi-language, escalation.
- **Discuss:** Decision tree vs LLM; physician audit.

### 227. Design a Hospital Bed-Management Dashboard
- **Scale:** 10K beds across system.
- **Core features:** Real-time occupancy, predicted discharges, surge plan.
- **Hard parts:** Discharge prediction, ED-to-floor handoff, cleaning sync.
- **Discuss:** Forecasting model; integration with EVS housekeeping.

### 228. Design a Surgical-Scheduling System
- **Scale:** 500 ORs across network.
- **Core features:** Schedule case, OR/team allocation, supply, anesthesia.
- **Hard parts:** Multi-resource constraints, last-minute cancellations, on-call.
- **Discuss:** Constraint solver; turnover minimization.

### 229. Design a Patient-Generated Health Data (PGHD) Pipeline
- **Scale:** Wearables + manual entries → EHR.
- **Core features:** Ingest, transform, surface to clinician dashboard.
- **Hard parts:** Data validation, signal-to-noise, clinician burden.
- **Discuss:** Threshold filtering; physician opt-in by metric.

### 230. Design a Pandemic / Disease-Surveillance System
- **Scale:** Country-wide, multi-source.
- **Core features:** Aggregate cases, geographic clustering, alerts.
- **Hard parts:** Data lag, deduplication across labs, privacy.
- **Discuss:** k-anonymity for public release; case-definition versioning.

### 231. Design a Maternal/Fetal Monitoring System
- **Scale:** Hospital labor & delivery floor.
- **Core features:** Continuous CTG capture, archive, AI analysis.
- **Hard parts:** Sub-second waveform, false-alarm tuning, archive 18 years.
- **Discuss:** Stream + archive duality; AI-assisted dot-plot.

### 232. Design a Fitness-to-Drive Health Check Service (DOT / DMV)
- **Scale:** Truck driver medical certification.
- **Core features:** Schedule exam, certify, expiry tracking, registry submit.
- **Hard parts:** Federal registry sync, expiry alerts, fraud.
- **Discuss:** Certificate signing chain.

### 233. Design a Veterinary EHR
- **Scale:** Pet clinics, 10K practices.
- **Core features:** Patient = pet, owner accounts, vaccinations, surgery, billing.
- **Hard parts:** Multi-pet households, breed-specific norms, prescription drugs.
- **Discuss:** Why species matters in schema.

### 234. Design a Pharmacy Drug-Recall Notification System
- **Scale:** 70K pharmacies, FDA recall feeds.
- **Core features:** Detect recall, identify dispensed-to patients, notify pharmacy + patient.
- **Hard parts:** Lot-level tracking, patient reach, time-bound urgency.
- **Discuss:** NDC + lot number index; recall severity tiers.

### 235. Design a Healthcare Provider-Credentialing Platform
- **Scale:** 1M providers, multi-payor.
- **Core features:** Collect docs, verify license, primary-source verify, expirations.
- **Hard parts:** Multi-source verification, expiration sweep, payer-specific rules.
- **Discuss:** Per-state board APIs; document evidence vault.

# 🚚 21. Logistics, Supply Chain & Shipping

### 236. Design Amazon Warehouse Management System (WMS)
- **Scale:** 200 fulfillment centers, millions of SKUs.
- **Core features:** Receive, stow, pick, pack, ship; bin location tracking.
- **Hard parts:** Robot orchestration, optimal pick path, real-time inventory.
- **Discuss:** Slotting algorithms; pick-to-light vs voice-pick.

### 237. Design a Last-Mile Delivery Routing System
- **Scale:** 1M packages/day, 10K drivers.
- **Core features:** Daily route generation, dynamic re-route, proof of delivery.
- **Hard parts:** VRP at scale, traffic, customer time windows.
- **Discuss:** Heuristic vs solver; driver-acceptance constraints.

### 238. Design FedEx/UPS Package Tracking
- **Scale:** Billions of packages/year, multi-leg routing.
- **Core features:** Scan events, ETA, exception alerts, delivery confirmation.
- **Hard parts:** Hub-and-spoke routing, scan-event ingestion at scale, ETA prediction.
- **Discuss:** Why pre-compute ETA per leg; exception policies.

### 239. Design a Container-Shipping (Maritime) Tracking
- **Scale:** 10M containers in transit.
- **Core features:** Booking, vessel ETA, port handover, customs status.
- **Hard parts:** Vessel sparse data, port system integrations, rolled cargo.
- **Discuss:** AIS + carrier API + EDI 315; ETA recompute cadence.

### 240. Design a Cold-Chain Logistics System
- **Scale:** Vaccines, perishables.
- **Core features:** Temp tracking per pallet, alert on excursion, chain-of-custody.
- **Hard parts:** IoT sensor offline buffering, gap-fill, regulatory audit.
- **Discuss:** Why immutable temp log; spike rules.

### 241. Design Uber Freight (Trucking Marketplace)
- **Scale:** 100K carriers, 1M loads/year.
- **Core features:** Post load, match carrier, dispatch, paperwork.
- **Hard parts:** Lane pricing, ELD integration, detention/demurrage.
- **Discuss:** Why instant-book; insurance overlays.

### 242. Design a Returns Management System
- **Scale:** Reverse logistics for e-commerce.
- **Core features:** Initiate return, label, receive, inspect, refund.
- **Hard parts:** Restock vs liquidation, fraud, ASN to warehouse.
- **Discuss:** State machine; return policy enforcement.

### 243. Design a Demand-Forecasting Engine for Retail
- **Scale:** 10M SKU-store-day predictions.
- **Core features:** Ingest history, generate forecast, promotion-aware.
- **Hard parts:** Seasonality, new-product cold start, promo lift.
- **Discuss:** Hierarchical forecasting; reconciliation across product/store.

### 244. Design a Inventory-Replenishment / Auto-Reorder
- **Scale:** Multi-warehouse, 100M SKUs.
- **Core features:** Stock level monitoring, reorder point, supplier PO.
- **Hard parts:** Lead time variance, MOQ, multi-supplier.
- **Discuss:** Continuous review (s,Q) vs periodic (R,S).

### 245. Design a Multi-Carrier Shipping API (ShipStation)
- **Scale:** 10M small merchants.
- **Core features:** Quote rates, generate label, track, returns.
- **Hard parts:** Each carrier's API quirks, address validation, rate-shopping.
- **Discuss:** Adapter pattern; rate-cache TTL.

### 246. Design a Dock-Scheduling / Yard-Management System
- **Scale:** Distribution center receiving.
- **Core features:** Appointment booking, gate check-in, dock assignment.
- **Hard parts:** Trailer-status sync, dock conflicts, detention billing.
- **Discuss:** Yard-map state; OCR for trailer numbers.

### 247. Design a Customs / Import-Compliance Platform
- **Scale:** Cross-border B2C and B2B.
- **Core features:** HS-code classify, duties calc, declaration.
- **Hard parts:** HS classification ML, country-specific rules, restricted items.
- **Discuss:** Rule engine + ML; broker integration.

### 248. Design Pallet/Tote-level Tracking with RFID
- **Scale:** 100K reads/sec at warehouse.
- **Core features:** RFID scan, location tracking, dwell time.
- **Hard parts:** Phantom reads, real-time aggregation, hardware diversity.
- **Discuss:** Edge filtering; reader topology.

### 249. Design a Manifest System for Airlines (Cargo)
- **Scale:** Major hub airport.
- **Core features:** AWB creation, ULD pack, weight & balance, security screen.
- **Hard parts:** Hazmat rules, dangerous-goods, partial unloads.
- **Discuss:** IATA messaging; deadline-driven workflow.

### 250. Design a Locker-Pickup Network (Amazon Hub)
- **Scale:** 100K lockers.
- **Core features:** Carrier drop, customer pickup with code, expiry.
- **Hard parts:** Locker-state sync, theft, return.
- **Discuss:** IoT lock + per-locker keys; capacity rotation.

### 251. Design a Drone-Delivery Dispatch System
- **Scale:** Suburban delivery, 10K drones.
- **Core features:** Order → drone → route → land → return.
- **Hard parts:** Airspace coordination, weather routing, no-fly zones.
- **Discuss:** UTM integration; battery & swap planning.

### 252. Design a Trucking Telematics Platform (Samsara-like)
- **Scale:** 1M trucks.
- **Core features:** Engine data, GPS, driver behavior, video.
- **Hard parts:** Cell coverage gaps, video upload at scale, ELD compliance.
- **Discuss:** Edge buffer + compress upload.

### 253. Design a Warehouse Robotics Orchestrator
- **Scale:** 1000 robots in one facility.
- **Core features:** Task assignment, path planning, charging schedule.
- **Hard parts:** Collision avoidance, deadlock-free traffic, robot-failure recovery.
- **Discuss:** Centralized vs distributed planner.

### 254. Design a Crowd-Sourced Delivery (Doordash Drive / Roadie)
- **Scale:** Driver acquisition + fleet match.
- **Core features:** Post task, claim by driver, navigate, complete.
- **Hard parts:** Independent contractors, surge pricing, fraud (fake deliveries).
- **Discuss:** Geofence at pickup/drop; signature verification.

### 255. Design a Cross-Dock System
- **Scale:** Inbound trailer → outbound trailer < 24h.
- **Core features:** Receive, sort, stage, ship.
- **Hard parts:** No-storage flow, sorter routing, delay propagation.
- **Discuss:** Wave planning; dynamic dock assignment.

### 256. Design a Procurement (Purchase Order) System
- **Scale:** 100K POs/day at large enterprise.
- **Core features:** Requisition, approval, PO, receiving, 3-way match.
- **Hard parts:** Approval workflow, partial receipts, invoice mismatch.
- **Discuss:** Workflow engine; ERP integration.

### 257. Design a Vendor / Supplier-Risk Management Platform
- **Scale:** 10K vendors.
- **Core features:** Onboarding, risk score (financial, geo, cyber), monitoring.
- **Hard parts:** Continuous data ingest, news monitoring, periodic reassessment.
- **Discuss:** Score weighting; alert routing.

### 258. Design a Lot/Batch Traceability System (Food Safety)
- **Scale:** Global supply chain.
- **Core features:** Track lot from farm → store, recall trace, certificate.
- **Hard parts:** Multi-tier supplier visibility, immutable trace, partial info.
- **Discuss:** GS1 standards; blockchain vs DB.

### 259. Design a Toll-Booth / Open-Road Tolling System
- **Scale:** Highway-wide ANPR.
- **Core features:** ALPR plate read, account match, charge.
- **Hard parts:** Read accuracy, plate-not-recognized escalation, billing.
- **Discuss:** Async OCR; reconciliation with transponder.

### 260. Design a Reverse-Auction Procurement Platform
- **Scale:** Buyers post RFQ, suppliers bid.
- **Core features:** RFQ, sealed/open bid, award.
- **Hard parts:** Bid sniping, blind vs visible, multi-currency.
- **Discuss:** Auction-end extension rules.

### 261. Design a Fuel-Card / Fleet-Card System
- **Scale:** 10M cards.
- **Core features:** Authorize at pump, limit by driver/vehicle, fraud.
- **Hard parts:** Sub-second auth, geo-fraud detection, chargeback.
- **Discuss:** Card-network integration; offline limits.

### 262. Design an EDI Translation / B2B Integration Platform
- **Scale:** Thousands of trading-partner connections.
- **Core features:** Inbound/outbound EDI mapping, ACK, monitoring.
- **Hard parts:** Per-partner schema, AS2, retry/resend.
- **Discuss:** Why event-sourced; partner onboarding workflow.

### 263. Design a Last-Mile Locker-Network for Apartments
- **Scale:** Building-level smart lockers.
- **Core features:** Resident notification, code-based pickup, carrier interface.
- **Hard parts:** Multi-carrier auth, package size mismatch, expiry.
- **Discuss:** API for carrier "drop"; bin-size matching.

### 264. Design an Auto-Delivery / Subscribe & Save System
- **Scale:** Predictable replenishment for 10M households.
- **Core features:** Schedule cadence, skip, swap.
- **Hard parts:** Reschedule cascading, inventory commit, price drift.
- **Discuss:** Forecasting demand; lead-time aware ordering.

### 265. Design a Dispatching Optimizer for Snowplow / City Services
- **Scale:** City-wide fleet.
- **Core features:** Plan routes per snow event, real-time adapt.
- **Hard parts:** Priority streets, equipment match, weather feedback.
- **Discuss:** GIS overlay; replan on accident.

### 266. Design a Container-Yard Stacking System
- **Scale:** Port terminal.
- **Core features:** Where to stack; how to retrieve fastest.
- **Hard parts:** Stacking depth, retrieval shuffles, ship-call schedule.
- **Discuss:** ML for predicted retrieval time.

### 267. Design a Blockchain-based Provenance for Diamonds (Everledger)
- **Scale:** 1M stones.
- **Core features:** Cert-of-origin, ownership chain, transfer.
- **Hard parts:** Off-chain → on-chain bridging, certificate issuer trust.
- **Discuss:** Why permissioned chain; data minimization.

### 268. Design a Customs Bonded-Warehouse Inventory
- **Scale:** Multi-importer goods, duty-deferred.
- **Core features:** Track duty status, withdraw, re-export.
- **Hard parts:** Complex customs reporting, partial withdrawals.
- **Discuss:** Bond status state machine; auditor view.

### 269. Design a Fleet Vehicle-Maintenance Platform
- **Scale:** 100K vehicles.
- **Core features:** PM schedule, repair orders, parts, technician.
- **Hard parts:** Telematics-driven PM, downtime impact, parts ETA.
- **Discuss:** Telematics → fault → work-order automation.

### 270. Design a Supply-Chain Control Tower (Visibility Dashboard)
- **Scale:** Enterprise-wide.
- **Core features:** End-to-end visibility, exception detection, scenario plan.
- **Hard parts:** Heterogeneous data, real-time alerts, what-if simulation.
- **Discuss:** Event hub; graph data model for supply chain.

# 🎓 22. Education / E-Learning

### 271. Design Coursera / Udemy (MOOC Platform)
- **Scale:** 100M learners, video courses.
- **Core features:** Enroll, video, quizzes, certificate.
- **Hard parts:** Video DRM, progress tracking cross-device, multi-language.
- **Discuss:** Quiz autograder; certificate signing.

### 272. Design Khan Academy / Duolingo
- **Scale:** 500M learners, gamified, adaptive.
- **Core features:** Lessons, streaks, leaderboards, mastery.
- **Hard parts:** Adaptive curriculum, A/B tests, retention.
- **Discuss:** Skill graph; spaced repetition.

### 273. Design an Online Coding Practice Platform (LeetCode)
- **Scale:** 10M users, 100K problems, sandboxed runs.
- **Core features:** Submit code, run tests, leaderboard, contests.
- **Hard parts:** Sandboxed execution, time/memory limits, judge consistency.
- **Discuss:** Container per submission; test-case caching.

### 274. Design a Live Tutoring Marketplace (Wyzant / VIPKid)
- **Scale:** 1M tutors, video lessons.
- **Core features:** Search tutor, schedule, video, payment.
- **Hard parts:** Cross-timezone scheduling, no-show handling, payouts.
- **Discuss:** Video room provisioning; rating fraud.

### 275. Design a School LMS (Moodle / Canvas)
- **Scale:** Multi-school district.
- **Core features:** Course content, assignments, grading, gradebook.
- **Hard parts:** SIS integration, parent access, accessibility.
- **Discuss:** Per-school multitenancy; LTI tools.

### 276. Design an Online Proctoring System (ExamSoft)
- **Scale:** Bar exam, 10K simultaneous.
- **Core features:** Lockdown browser, webcam, AI flagging, human review.
- **Hard parts:** Cheating detection, false positives, accessibility.
- **Discuss:** Local capture + upload; chain-of-custody.

### 277. Design Quiz-Based Game (Kahoot!)
- **Scale:** Classroom + viral, 100K simultaneous rooms.
- **Core features:** Host quiz, join code, real-time scoring.
- **Hard parts:** Sub-second scoring at viral scale, anti-cheat.
- **Discuss:** Pub/sub per room; latency normalization.

### 278. Design a Plagiarism-Detection Service (Turnitin)
- **Scale:** 50M papers/year.
- **Core features:** Submit paper, compare to corpus, similarity report.
- **Hard parts:** Big corpus, fingerprint hashing, citations.
- **Discuss:** N-gram fingerprints; cluster lookup.

### 279. Design a Student-Information System (PowerSchool)
- **Scale:** Districts of 100K students.
- **Core features:** Enroll, schedule, attendance, grades, transcripts.
- **Hard parts:** Year rollover, transcript archive, parent portal.
- **Discuss:** Fiscal year vs school year; data retention.

### 280. Design an Adaptive Learning Engine
- **Scale:** Per-student dynamic difficulty.
- **Core features:** Item bank, IRT model, next-item selection.
- **Hard parts:** Real-time scoring, item drift, exposure control.
- **Discuss:** Bayesian Knowledge Tracing.

### 281. Design a School Bus-Route Planning System
- **Scale:** Per-district daily routes.
- **Core features:** Stops, capacity, time windows, special needs.
- **Hard parts:** Constraint solver, last-minute address change, multi-tier schools.
- **Discuss:** Walking-radius rules; transfer routes.

### 282. Design a Homework-Help Q&A Platform (Chegg)
- **Scale:** 100M questions.
- **Core features:** Post Q, expert answers, search archive.
- **Hard parts:** Honor-code policing, auto-suggest similar Qs, cheating risk.
- **Discuss:** Image OCR for problem photos.

### 283. Design a Live-Streaming Lecture System (Zoom for Edu)
- **Scale:** 1M concurrent classrooms.
- **Core features:** Lecture stream, breakout, recording, captions.
- **Hard parts:** Multi-camera (whiteboard + face), low-bandwidth fallback.
- **Discuss:** SVC encoding; transcription pipeline.

### 284. Design a Course-Recommendation Engine
- **Scale:** 100K courses, 100M users.
- **Core features:** "Based on your progress" suggest next.
- **Hard parts:** Cold start, knowledge dependency graph, interest evolution.
- **Discuss:** Embedding-based; skill-gap detection.

### 285. Design a Code-Review Educational Tool (Replit Pair Programming)
- **Scale:** Student groups.
- **Core features:** Shared editor, instructor view, comments.
- **Hard parts:** Real-time collaboration, instructor monitoring.
- **Discuss:** OT for editor; scoped code execution.

### 286. Design a Test-Prep / Practice-Test Platform (Khan SAT)
- **Scale:** Millions of practice tests.
- **Core features:** Timed tests, scoring, review.
- **Hard parts:** Item exposure control, cheating, score reliability.
- **Discuss:** Item bank rotation; timer enforcement.

### 287. Design a Research-Paper Repository (arXiv)
- **Scale:** 2M papers, daily preprints.
- **Core features:** Submit, version, search, alert.
- **Hard parts:** PDF rendering, LaTeX build, citation graph.
- **Discuss:** Build pipeline; DOI assignment.

### 288. Design a Citation-Manager (Zotero)
- **Scale:** 10M users, sync.
- **Core features:** Collect, annotate, generate bibliography.
- **Hard parts:** Browser-extension capture, attachment storage, multi-style output.
- **Discuss:** CSL styles; PDF annotation sync.

### 289. Design a University Course-Registration System
- **Scale:** 50K students, registration window.
- **Core features:** Browse, register, drop, waitlist.
- **Hard parts:** Last-second rush, capacity caps, prereq enforcement.
- **Discuss:** Queue at registration open; race conditions.

### 290. Design an Online Whiteboard Tutoring Tool
- **Scale:** Live tutor & student session.
- **Core features:** Whiteboard, audio, screen share, math input.
- **Hard parts:** Math equation rendering, low-latency drawing.
- **Discuss:** Vector vs raster sync.

### 291. Design an Education Standards (CCSS) Mapping System
- **Scale:** Map content → standards across states.
- **Core features:** Tag content, align lessons, report coverage.
- **Hard parts:** Standards versioning, multi-state mapping.
- **Discuss:** Graph DB; cross-walk algorithm.

### 292. Design a Special-Education IEP System
- **Scale:** District-level, K-12.
- **Core features:** IEP draft, meeting workflow, progress reporting.
- **Hard parts:** Compliance with IDEA, signatures, audit.
- **Discuss:** Document templates; e-sign.

### 293. Design an Educational AI-Tutor (Khanmigo)
- **Scale:** 1M students, LLM-backed.
- **Core features:** Conversational tutor, hint not answer, math.
- **Hard parts:** Avoid giving away answer, math correctness, safety.
- **Discuss:** RAG with curriculum; guardrails.

### 294. Design a College Application Platform (Common App)
- **Scale:** 1.5M applicants.
- **Core features:** Common essay, school-specific supplements, recommend letters.
- **Hard parts:** Submission deadline spike, file uploads, PII.
- **Discuss:** Deadline-day capacity.

### 295. Design an Alumni Network / Career Services Portal
- **Scale:** Per-university, multi-decade alumni.
- **Core features:** Directory, mentorship, events, donations.
- **Hard parts:** Privacy controls, mentor matching, opt-in.
- **Discuss:** Graph of alum-to-student.

### 296. Design a Library Borrowing / E-book System
- **Scale:** Public-library e-book lending.
- **Core features:** Catalog, hold queue, lend (DRM-limited copies).
- **Hard parts:** Concurrent-copy DRM, hold-position transparency.
- **Discuss:** Why "1 copy = 1 lend"; OverDrive-style.

### 297. Design Learning Analytics Dashboard for Teachers
- **Scale:** Per-class, per-student.
- **Core features:** Engagement, mastery, at-risk alerts.
- **Hard parts:** Real-time signal, FERPA privacy, false-positive risk.
- **Discuss:** Aggregate vs individual views.

### 298. Design a STEM Lab-Simulation Platform (PhET / Labster)
- **Scale:** Browser-based simulations at scale.
- **Core features:** Interactive labs, save state, instructor view.
- **Hard parts:** WebGL physics simulation, low-bandwidth, accessibility.
- **Discuss:** Headless simulator for grading.

### 299. Design a School-Communication Platform (Remind / ClassDojo)
- **Scale:** Teacher ↔ parent messaging.
- **Core features:** Group messages, translations, opt-out.
- **Hard parts:** PII safety, multi-language auto-translate, school-day rules.
- **Discuss:** Non-real-time delivery; quiet hours.

### 300. Design a Hackathon / Coding-Competition Platform
- **Scale:** 100K simultaneous participants in finals.
- **Core features:** Register, submit, judge, leaderboard.
- **Hard parts:** Submission spike, fair judging, plagiarism.
- **Discuss:** Container-based judging; queue saturation.

# 🏠 23. Real Estate

### 301. Design Zillow / Realtor.com Listing Search
- **Scale:** 130M homes, 250M MAU.
- **Core features:** Search by location/price/beds, photos, Zestimate, save search.
- **Hard parts:** Geo+facet search, Zestimate ML pipeline, MLS data ingest.
- **Discuss:** Per-MLS feed sync; price-history append.

### 302. Design a Rental Listing Platform (Apartments.com)
- **Scale:** 1M units, 50M searches/day.
- **Core features:** Search, lease application, screening, contact.
- **Hard parts:** Fake listings, screening provider integration, fair-housing compliance.
- **Discuss:** Application data retention; phone-anonymization.

### 303. Design a Property-Management System (Yardi)
- **Scale:** Multi-property landlord, 10M units.
- **Core features:** Tenant CRUD, lease, rent, maintenance, accounting.
- **Hard parts:** Multi-entity accounting, maintenance dispatch, lease renewal.
- **Discuss:** GL integration; per-property isolation.

### 304. Design a Tenant-Screening Service
- **Scale:** Background + credit pulls.
- **Core features:** Credit, eviction, criminal, income verification.
- **Hard parts:** Bureau integration, dispute, FCRA compliance.
- **Discuss:** Soft-pull vs hard-pull; consent capture.

### 305. Design Real-Estate Agent CRM
- **Scale:** 1M agents, leads pipeline.
- **Core features:** Lead capture, drip campaigns, MLS sync, contracts.
- **Hard parts:** MLS feed merge, e-sign, document templates.
- **Discuss:** RESO Web API; document versioning.

### 306. Design a Mortgage-Origination System
- **Scale:** Lender-side, 1M apps/year.
- **Core features:** Application, doc upload, underwrite, e-close.
- **Hard parts:** Compliance (TRID, RESPA), credit pull, appraisal vendor.
- **Discuss:** State machine; document OCR.

### 307. Design a Home-Insurance Platform
- **Scale:** Quote → policy → renewal.
- **Core features:** Quote engine, bind, claims.
- **Hard parts:** Property data sources, peril rating, cancellations.
- **Discuss:** Aerial imagery underwriting.

### 308. Design a Home-Showing Scheduling System (ShowingTime)
- **Scale:** 1M showings/month.
- **Core features:** Request showing, instant approve, lockbox unlock.
- **Hard parts:** Listing-agent approval window, conflicts, lockbox IoT.
- **Discuss:** Time-bounded keys; agent calendar sync.

### 309. Design a 3D Virtual-Tour Platform (Matterport)
- **Scale:** Million homes scanned.
- **Core features:** Capture from 3D camera, view in browser, embed.
- **Hard parts:** Mesh storage, viewer perf, link sharing.
- **Discuss:** Tile-based 3D streaming.

### 310. Design a Real-Estate Investing / iBuyer (Opendoor)
- **Scale:** Auto-offers on homes.
- **Core features:** Offer engine, walk-through, close, list.
- **Hard parts:** Pricing model, repair-cost estimate, hold inventory cost.
- **Discuss:** Fast-decision pipeline.

### 311. Design a Construction-Project Management Platform (Procore)
- **Scale:** Per-project, multi-stakeholder.
- **Core features:** RFI, submittals, daily logs, drawings.
- **Hard parts:** Drawing version control, photo geo-tagging.
- **Discuss:** Mobile-first offline; drawing diffs.

### 312. Design HOA / Condo Management System
- **Scale:** Per-association, dues + voting.
- **Core features:** Dues collection, violation tracking, voting.
- **Hard parts:** Compliance, electronic vote integrity.
- **Discuss:** Audit trail per resident.

### 313. Design a Roommate-Matching Platform
- **Scale:** University + city-wide.
- **Core features:** Profile, match preferences, chat.
- **Hard parts:** Safety, verification, location-aware.
- **Discuss:** Match score algorithm.

### 314. Design a Vacation-Rental Calendar Sync (iCal across Airbnb/VRBO)
- **Scale:** Multi-platform host calendars.
- **Core features:** Sync availability across listing sites.
- **Hard parts:** Polling cadence, two-way conflicts, time-zone DST.
- **Discuss:** ICS feed merging; lock window during sync.

### 315. Design a Smart-Home Real-Estate Walkthrough (IoT)
- **Scale:** Home full of sensors during showing.
- **Core features:** Door access logs, motion, video.
- **Hard parts:** Privacy, consent, retention.
- **Discuss:** Per-showing data isolation.

### 316. Design a Real-Estate Comp / CMA Tool
- **Scale:** Comparable-market analysis on demand.
- **Core features:** Pull comps, adjust, generate report.
- **Hard parts:** Geo + feature similarity, data freshness.
- **Discuss:** Vector search for comps.

### 317. Design an Eviction-Workflow / Court-Filing Platform
- **Scale:** Multi-jurisdiction.
- **Core features:** Notice, file, court-date sync, judgment.
- **Hard parts:** Per-state procedural differences, e-file integration.
- **Discuss:** Workflow per jurisdiction.

### 318. Design a Land-Records / Title-Search Platform
- **Scale:** County recorder data ingestion.
- **Core features:** Search title chain, lien check, plat maps.
- **Hard parts:** Heterogeneous county systems, OCR of historic deeds.
- **Discuss:** Document-image pipeline; chain-of-title model.

# ✈️ 24. Travel & Hospitality (Advanced)

### 319. Design a Global Distribution System (GDS) (Sabre / Amadeus)
- **Scale:** Trillion-dollar industry, microsecond auth on inventory.
- **Core features:** Multi-airline inventory, fare quote, ticket issue.
- **Hard parts:** Schema across airlines, inventory locks, IATA settlement.
- **Discuss:** Cache-stale risk; PNR storage.

### 320. Design a Loyalty / Frequent-Flyer Program
- **Scale:** 100M members, multi-airline alliance.
- **Core features:** Earn, burn, status tiers, partner accrual.
- **Hard parts:** Multi-currency points, expiration sweeps, fraud.
- **Discuss:** Append-only ledger; reciprocity rules.

### 321. Design a Dynamic-Pricing Engine for Hotels (Revenue Mgmt)
- **Scale:** Per-room-night pricing.
- **Core features:** Forecast demand, adjust price, channel-distribute.
- **Hard parts:** Multi-channel rate parity, last-minute spikes.
- **Discuss:** Reinforcement learning; competitor scrape.

### 322. Design a Travel Insurance Platform
- **Scale:** Quote at booking.
- **Core features:** Quote, policy, claim flight-delay payout.
- **Hard parts:** Real-time flight-delay data, fraud, automated payout.
- **Discuss:** Parametric vs traditional claims.

### 323. Design a Tour / Experience Booking (GetYourGuide)
- **Scale:** 50K activities, scheduled departures.
- **Core features:** Search by city/date, capacity, instant confirm.
- **Hard parts:** Supplier inventory sync, multi-language ops.
- **Discuss:** Push vs pull from supplier.

### 324. Design a Travel-Itinerary Planner (TripIt)
- **Scale:** Email-parsing of confirmations.
- **Core features:** Aggregate flights/hotels/cars into itinerary.
- **Hard parts:** Email parsing reliability, ICS export, alerts.
- **Discuss:** ML for confirmation extraction.

### 325. Design a Concierge / Room-Service System (Hotel)
- **Scale:** Per-hotel, in-room ordering.
- **Core features:** Menu, order, dispatch staff, billing to room.
- **Hard parts:** PMS integration, kitchen routing.
- **Discuss:** OPERA integration; tablet-app provisioning.

### 326. Design a Hotel Front-Desk PMS (Property Mgmt System)
- **Scale:** Multi-property, 100K rooms.
- **Core features:** Reservation, check-in, folio, housekeeping.
- **Hard parts:** Channel manager, group blocks, night audit.
- **Discuss:** Night-audit batch; rate plan model.

### 327. Design a Cruise-Booking System
- **Scale:** Multi-ship, multi-cabin.
- **Core features:** Cabin selection, dining, excursions.
- **Hard parts:** Cabin-level inventory, group bookings, port operations.
- **Discuss:** Deck-plan model; itinerary changes.

### 328. Design an Airline Crew-Scheduling System
- **Scale:** 100K flight crew, regulatory.
- **Core features:** Pair flights to crew, FAA rest rules, swap.
- **Hard parts:** FAR 117 compliance, fatigue model, swap board.
- **Discuss:** Constraint solver; bid-line model.

### 329. Design a Visa / e-Visa Application Service
- **Scale:** Country-government scale.
- **Core features:** Apply, biometrics, decision, e-visa.
- **Hard parts:** Cross-government data, fraud, identity verify.
- **Discuss:** Workflow tied to gov system; rate-limit per nationality.

### 330. Design an Airport Self-Service Kiosk Platform
- **Scale:** 1000 kiosks per airport.
- **Core features:** Check-in, bag tag, boarding pass, payment.
- **Hard parts:** Carrier multi-DCS integration, kiosk health, queue analytics.
- **Discuss:** Common-use platform standards (CUPPS).

### 331. Design a Traveler-Disruption / Re-accommodation System
- **Scale:** Mass cancellation, rebook 100K passengers in 1h.
- **Core features:** Identify affected, propose options, auto-rebook.
- **Hard parts:** Inventory race, multi-leg recovery, lodging vouchers.
- **Discuss:** Why pre-computed swap candidates.

### 332. Design a Hotel Loyalty-Free-Night Calendar
- **Scale:** Member booking with points.
- **Core features:** Filter availability, point-cost calc, blackout.
- **Hard parts:** Rate-vs-points fairness, dynamic point cost.
- **Discuss:** Optimizer for point value.

### 333. Design a Travel Expense-Report System (Concur)
- **Scale:** Enterprise, 1M employees.
- **Core features:** Receipts capture, mileage, approval workflow.
- **Hard parts:** Card-feed reconciliation, OCR, policy enforcement.
- **Discuss:** Per-policy rule engine.

### 334. Design an Airline Operations Control Center (OCC)
- **Scale:** Real-time fleet status.
- **Core features:** Track flights, ATC delays, swap aircraft.
- **Hard parts:** Decisions in minutes, multi-system integration, what-if.
- **Discuss:** Event-stream + simulator.

### 335. Design a Boarding-Pass / Mobile-Wallet Pass System
- **Scale:** Apple/Google Wallet integration.
- **Core features:** Issue pass, push update, gate scan.
- **Hard parts:** Real-time updates (gate change), offline scan.
- **Discuss:** Push vs pull update; barcode redundancy.

### 336. Design an On-Demand Helicopter / Charter App (Blade)
- **Scale:** Niche, premium.
- **Core features:** Routes, seats, dynamic pricing.
- **Hard parts:** Aircraft availability, weather cancellation.
- **Discuss:** Weather-API ingestion; auto-rebook.

### 337. Design Restaurant Tip-Pool / Tip-Out System
- **Scale:** Per-restaurant.
- **Core features:** Capture tips by shift, allocate to roles.
- **Hard parts:** Policy customization, payroll integration.
- **Discuss:** Audit trail; dispute handling.

### 338. Design a Loyalty-Card Stamp System (Coffee shop punch)
- **Scale:** Independent merchant base.
- **Core features:** Earn stamp via QR, reward redeem.
- **Hard parts:** Anti-stamp-spam, multi-merchant.
- **Discuss:** Server-issued stamp tokens.

### 339. Design a Resort All-Inclusive Wristband System
- **Scale:** Resort-wide spending tracking.
- **Core features:** RFID wristband, charge to room.
- **Hard parts:** POS integration, lost wristband, family limits.
- **Discuss:** Edge POS w/ central reconciliation.

### 340. Design a Theme-Park Queue Reservation (Disney Genie+)
- **Scale:** Park-wide, virtual queue.
- **Core features:** Reserve return time, walk on, ride.
- **Hard parts:** Capacity allocation, lottery vs FCFS.
- **Discuss:** Lightning-Lane fairness; demand forecasting.

# 👔 25. HR & Recruiting

### 341. Design Workday / HRIS (Human Resources Info System)
- **Scale:** Enterprise, 100K employees.
- **Core features:** Employee record, compensation, time-off, reports.
- **Hard parts:** Effective-dated changes, multi-country payroll, security.
- **Discuss:** Bi-temporal model; org-tree changes.

### 342. Design an Applicant-Tracking System (Greenhouse)
- **Scale:** 10K companies, multi-stage funnels.
- **Core features:** Job posts, application intake, interview kit, offer.
- **Hard parts:** Job-board fanout, interview-team coord, EEOC reporting.
- **Discuss:** Pipeline state machine.

### 343. Design a Resume Parsing / Search Service
- **Scale:** 100M resumes.
- **Core features:** Parse PDF, structured fields, semantic search.
- **Hard parts:** Format diversity, name dedup, skills taxonomy.
- **Discuss:** ML extraction + human-in-loop.

### 344. Design a Background-Check Platform (Checkr)
- **Scale:** Multi-source, county-level.
- **Core features:** SSN trace, county criminal, MVR, education.
- **Hard parts:** County system integration, FCRA dispute, turnaround.
- **Discuss:** Per-source SLAs; dispute workflow.

### 345. Design a Payroll System (Gusto / ADP)
- **Scale:** Multi-state, garnishments.
- **Core features:** Pay run, tax calc, direct deposit, W-2.
- **Hard parts:** State + local tax, garnishments, retro.
- **Discuss:** Tax-engine updates; ACH timing.

### 346. Design a Time-Tracking / Timesheet System
- **Scale:** Hourly workers, biometric clock.
- **Core features:** Clock in/out, breaks, approvals.
- **Hard parts:** Biometric privacy, geofence verify, overtime calc.
- **Discuss:** State labor laws; meal-break compliance.

### 347. Design an Employee Performance-Review System
- **Scale:** Annual + quarterly cycles.
- **Core features:** Self-review, peer-review, manager calibration.
- **Hard parts:** Multi-rater workflow, calibration, comp link.
- **Discuss:** Anonymity; 9-box grid.

### 348. Design an Onboarding-Workflow Engine
- **Scale:** New-hire pipelines.
- **Core features:** Tasks, e-sign, IT provisioning, training.
- **Hard parts:** Cross-system provisioning, role-based templates.
- **Discuss:** SaaS connectors; deprovisioning.

### 349. Design a Benefits-Enrollment Platform
- **Scale:** Open enrollment annual rush.
- **Core features:** Plan compare, elect, dependents, deductions.
- **Hard parts:** Carrier feed, EDI 834, qualifying events.
- **Discuss:** Plan-design model; deduction split.

### 350. Design an Internal Job-Board / Mobility Platform
- **Scale:** Internal candidate matching.
- **Core features:** Skill profile, internal jobs, manager approval.
- **Hard parts:** Skill graph, privacy, manager visibility.
- **Discuss:** Two-sided match algorithm.

### 351. Design an Engineering Levels / Career-Ladder Tool
- **Scale:** Company-wide framework.
- **Core features:** Ladder docs, self-assess, promo packet.
- **Hard parts:** Cross-org calibration, evidence collection.
- **Discuss:** Document templates; promo committee workflow.

### 352. Design a Recruiting-Ad / Job Distribution Platform
- **Scale:** Post job to 100+ boards.
- **Core features:** Sponsor jobs, attribution, budget.
- **Hard parts:** Indeed-style scrape rules, attribution, budget pacing.
- **Discuss:** Bidder per board.

### 353. Design a Skills-Assessment Test Platform (HackerRank)
- **Scale:** Coding tests at hire.
- **Core features:** Test, sandbox run, score, anti-cheat.
- **Hard parts:** Container isolation, plagiarism, IDE features.
- **Discuss:** Submission queue; comparison to peers.

### 354. Design an Interview Scheduling Tool (GoodTime)
- **Scale:** Multi-interviewer panel.
- **Core features:** Find slots across interviewers, candidate timezone.
- **Hard parts:** Calendar integration, load-balancing across interviewers.
- **Discuss:** Calendar polling; debounce holds.

### 355. Design a Reference-Check Platform
- **Scale:** Confidential surveys.
- **Core features:** Send survey to references, collect, summarize.
- **Hard parts:** Anonymity, deliverability.
- **Discuss:** Anti-bias question framework.

### 356. Design an Org-Chart Tool
- **Scale:** Real-time updates.
- **Core features:** Tree view, search, reporting line history.
- **Hard parts:** Effective-dated reporting changes, matrix orgs.
- **Discuss:** Graph DB; bitemporal time travel.

### 357. Design an Employee Surveys & Pulse-Check Platform (Culture Amp)
- **Scale:** Quarterly, 100K employees.
- **Core features:** Send pulse, anonymize, manager dashboards.
- **Hard parts:** Anonymity threshold, sentiment trending.
- **Discuss:** Min-cell suppression.

### 358. Design a Learning Management System for L&D (Workday Learning)
- **Scale:** Required compliance training.
- **Core features:** Assign, track, certify, expire.
- **Hard parts:** Cert renewal, role-based curriculum.
- **Discuss:** Deadline reminders.

### 359. Design a Mentorship-Matching Platform
- **Scale:** Internal mentor program.
- **Core features:** Profile, match, sessions, feedback.
- **Hard parts:** Match algorithm, capacity per mentor.
- **Discuss:** Bandit-based match.

### 360. Design an Employee Ticketing System (HR Helpdesk)
- **Scale:** 100K tickets/year.
- **Core features:** Submit ticket, route, SLA, knowledge base.
- **Hard parts:** Routing rules, escalation, multi-region.
- **Discuss:** Routing engine; KB feedback loop.

### 361. Design a Compensation Planning Tool (Cycle)
- **Scale:** Annual merit cycle.
- **Core features:** Manager allocations, budget, equity refresh.
- **Hard parts:** Budget enforcement, calibration, currency.
- **Discuss:** Lock periods; dual-approval.

### 362. Design an Employee Recognition Platform (Bonusly)
- **Scale:** Peer-to-peer points.
- **Core features:** Send points, monthly budget, redeem.
- **Hard parts:** Budget reset, abuse, redemption catalog.
- **Discuss:** Per-tenant catalog.

### 363. Design an Internal Job-Referral Bonus System
- **Scale:** Refer + bonus payout.
- **Core features:** Refer, track, payout on milestone.
- **Hard parts:** Attribution, tax, eligibility windows.
- **Discuss:** Source-of-truth ATS link.

### 364. Design a Workforce Planning / Headcount Tool
- **Scale:** Quarterly planning.
- **Core features:** Approved positions, hires, attrition.
- **Hard parts:** Position-vs-headcount sync, frozen positions.
- **Discuss:** Approval state machine.

### 365. Design a Corporate Training-Compliance Tracker (OSHA / HIPAA)
- **Scale:** Required annual cert.
- **Core features:** Assign, complete, audit reporting.
- **Hard parts:** Role-mapped courses, expiry, regulatory audit.
- **Discuss:** Audit-export pipeline.

# ⚖️ 26. Legal Tech

### 366. Design an E-Discovery Platform (Relativity)
- **Scale:** Petabyte-scale legal review.
- **Core features:** Ingest emails/files, dedup, tag, produce.
- **Hard parts:** Predictive coding, privilege review, custodian-level chain.
- **Discuss:** Full-text + ML; chain-of-custody.

### 367. Design a Contract-Lifecycle Mgmt (CLM) System (Ironclad)
- **Scale:** Enterprise contracts.
- **Core features:** Draft, redline, approve, sign, repository.
- **Hard parts:** Version diff, clause library, expiration alerts.
- **Discuss:** Word/track-changes integration; OCR'd legacy docs.

### 368. Design a Legal-Research Platform (Westlaw / LexisNexis)
- **Scale:** Cases, statutes, secondary sources.
- **Core features:** Full-text search, citation graph, alerts.
- **Hard parts:** KeyCite-style red flags, paragraph-level perma-links.
- **Discuss:** Citation graph DB; real-time citator updates.

### 369. Design a Court Case-Management System
- **Scale:** State court system.
- **Core features:** Filing, docket, hearings, sealed access.
- **Hard parts:** Public-records compliance, sealed/redacted, e-filing.
- **Discuss:** Per-court rule customization.

### 370. Design an Online Notary / E-Sign Service (DocuSign)
- **Scale:** Billions of envelopes/year.
- **Core features:** Tags, route, sign, audit.
- **Hard parts:** Signer authentication, audit trail integrity, ESIGN/UETA.
- **Discuss:** Hash-chain audit; KBA + biometric.

### 371. Design a Patent-Search & Filing Tool
- **Scale:** USPTO + EPO + WIPO.
- **Core features:** Prior-art search, claim drafting, filing.
- **Hard parts:** Multi-jurisdiction filing, family-tree, IDS submissions.
- **Discuss:** Patent classification ML.

### 372. Design a Regulatory-Compliance Tracker (Thomson Reuters)
- **Scale:** Multi-jurisdiction reg watch.
- **Core features:** Track regs, alert changes, mapping to controls.
- **Hard parts:** Multi-language, classification, change-impact analysis.
- **Discuss:** NLP for regulatory text.

### 373. Design a Time-Tracking / Billing for Lawyers (Clio)
- **Scale:** Per-firm SaaS.
- **Core features:** Timer, invoice, trust account, conflict check.
- **Hard parts:** IOLTA trust, conflict of interest, billing rules.
- **Discuss:** Per-bar-association compliance.

### 374. Design a Whistleblower / Hotline Platform
- **Scale:** Anonymous intake.
- **Core features:** Submit anonymously, follow up via case ID.
- **Hard parts:** Anonymity preservation, EU vs US rules.
- **Discuss:** Tor-friendly intake; encrypted at rest.

### 375. Design a Will / Estate-Planning Platform (Trust & Will)
- **Scale:** Self-service docs.
- **Core features:** Q&A → doc generation, sign, vault.
- **Hard parts:** State-specific templates, witness req, secure storage.
- **Discuss:** Document-generation engine.

### 376. Design a GDPR Subject-Access-Request (SAR) Platform
- **Scale:** Multi-system data discovery.
- **Core features:** Receive request, find data across systems, package.
- **Hard parts:** Connector library, deadline (30 days), redaction.
- **Discuss:** Data-mapping inventory.

### 377. Design an Online Mediation / Arbitration Service
- **Scale:** Small-claims dispute resolution.
- **Core features:** File case, exchange, video hearing, decision.
- **Hard parts:** Asynchronous workflow, evidence storage, enforceability.
- **Discuss:** State-machine per dispute.

### 378. Design a Litigation Hold System
- **Scale:** Enterprise legal hold.
- **Core features:** Issue hold to custodians, pause deletion across systems.
- **Hard parts:** Connector to email/docs, custodian acknowledgement.
- **Discuss:** Universal "do-not-delete" tag.

### 379. Design a Trademark / IP-Watch Service
- **Scale:** Global watch by class.
- **Core features:** Subscribe to mark, alert filings, oppose deadline.
- **Hard parts:** Multi-registry ingest, fuzzy match, multi-language.
- **Discuss:** Phonetic + semantic match.

### 380. Design an Online Court Filing (eFile)
- **Scale:** Per-state, multi-court.
- **Core features:** Submit pleading, fee pay, stamp filing.
- **Hard parts:** Per-court schemas, attachment limits, clerk approval.
- **Discuss:** Adapter per court system; signed receipts.

# 🏛️ 27. Government / Civic Tech

### 381. Design a Voting / Election System
- **Scale:** Country-wide, polling places + mail-in.
- **Core features:** Voter check-in, ballot issue, count, audit.
- **Hard parts:** Voter privacy + auditability, attack surface, paper trail.
- **Discuss:** Why E2E-verifiable; risk-limiting audits.

### 382. Design an Online Tax-Filing Platform (TurboTax)
- **Scale:** 100M filings on April 15.
- **Core features:** Q&A, federal+state, e-file, refund track.
- **Hard parts:** Tax-engine updates, multi-state, audit defense.
- **Discuss:** Tax-form abstraction; April surge capacity.

### 383. Design a DMV Vehicle-Registration System
- **Scale:** Per-state, multi-million records.
- **Core features:** Title transfer, registration, plates, fees.
- **Hard parts:** Inter-state title transfers, lien holders, special plates.
- **Discuss:** AAMVA NMVTIS integration.

### 384. Design a National ID / Aadhaar-style Identity System
- **Scale:** 1B+ identities, biometric.
- **Core features:** Enroll, biometric match, e-KYC API.
- **Hard parts:** Dedup at billion scale, privacy, federation.
- **Discuss:** ABIS architecture; tokenization.

### 385. Design a Public-Records Search (Sunshine Law)
- **Scale:** State/county records portal.
- **Core features:** Search, request copy, redact, deliver.
- **Hard parts:** Redaction at scale, public-records exemptions.
- **Discuss:** ML-assisted PII redaction.

### 386. Design a Census Enumeration Platform
- **Scale:** Once-a-decade national headcount.
- **Core features:** Self-response online, field collection, dedup.
- **Hard parts:** Confidentiality (Title 13), disclosure-avoidance.
- **Discuss:** Differential privacy.

### 387. Design a Government Permit-Issuance System
- **Scale:** Local building/business permits.
- **Core features:** Apply, review, inspect, issue, renew.
- **Hard parts:** Multi-department review, GIS overlay, code references.
- **Discuss:** Workflow per permit type.

### 388. Design a 911 / Emergency Dispatch (CAD)
- **Scale:** PSAP-wide.
- **Core features:** Call intake, location, dispatch units, status.
- **Hard parts:** Sub-second response, multi-agency, redundancy.
- **Discuss:** Why active-active across data centers.

### 389. Design a Mass-Notification / Public-Alert System
- **Scale:** State/national, AMBER/Wireless Emergency Alerts.
- **Core features:** Author alert, target geography, deliver.
- **Hard parts:** Carrier integration, geo-targeting accuracy.
- **Discuss:** WEA gateway, IPAWS protocol.

### 390. Design a Court Date-Reminder / Pretrial-Service Platform
- **Scale:** Reduce FTA via reminders.
- **Core features:** Receive court calendar, send SMS reminders.
- **Hard parts:** Court-data ingest, multi-channel comms, opt-in.
- **Discuss:** Reminder cadence A/B test.

### 391. Design a SNAP / Government-Benefits Disbursement Platform
- **Scale:** State EBT system.
- **Core features:** Eligibility determination, EBT load, transactions.
- **Hard parts:** Recertification, fraud, EBT card network.
- **Discuss:** Caseworker workflow; fraud heuristics.

### 392. Design an Open-Data Portal (data.gov)
- **Scale:** Thousands of datasets.
- **Core features:** Catalog, download, API, schema.
- **Hard parts:** Heterogeneous datasets, versioning, citation.
- **Discuss:** CKAN-style metadata; DOI assignment.

### 393. Design a Driver-License Issuance System
- **Scale:** State-wide.
- **Core features:** Apply, test, photo, issue, renewal.
- **Hard parts:** REAL-ID compliance, biometric capture, fraud.
- **Discuss:** Document chain; appointment system.

### 394. Design a Toll-Road Billing System
- **Scale:** State/region tollway.
- **Core features:** Toll capture, account match, pay-by-plate billing.
- **Hard parts:** ALPR accuracy, dispute resolution, interoperability.
- **Discuss:** Inter-state transponder reciprocity.

### 395. Design a Public-Transit Real-Time Arrival System
- **Scale:** City-wide buses + trains.
- **Core features:** Live vehicle position, ETA, alerts.
- **Hard parts:** GTFS-RT ingest, coverage gaps, congestion.
- **Discuss:** Hybrid ETA models.

### 396. Design a City-Service 311 Reporting Platform (NYC 311)
- **Scale:** Millions of complaints/year.
- **Core features:** Report (photo+geo), route to agency, status.
- **Hard parts:** Dedup, agency routing rules, SLA tracking.
- **Discuss:** Geo-clustering of similar reports.

### 397. Design a Voter-Registration & Roll Maintenance System
- **Scale:** State-level.
- **Core features:** Register, verify, age-out, deceased remove.
- **Hard parts:** Inter-state list-maintenance, address standards, audit.
- **Discuss:** ERIC interstate comparison.

### 398. Design a Customs Border Pre-Clearance System (Global Entry)
- **Scale:** Trusted-traveler enrollment.
- **Core features:** Apply, biometric enroll, kiosk on entry.
- **Hard parts:** Multi-agency vetting, kiosk fleet management.
- **Discuss:** Biometric-on-entry vs biometric-on-exit.

### 399. Design a Court Bail / Pretrial Risk-Assessment Tool
- **Scale:** Per-arrest assessment.
- **Core features:** Compute risk score, recommend bail.
- **Hard parts:** Bias mitigation, transparency, jurisdictional rules.
- **Discuss:** Why explainability mandatory.

### 400. Design a Gov-to-Citizen Notification Service (e.g. tax due, license renewal)
- **Scale:** State residents.
- **Core features:** Multi-channel delivery, opt-in, multi-language.
- **Hard parts:** Address-of-record sync, accessibility.
- **Discuss:** USPS NCOA integration.

# 📰 28. News & Media

### 401. Design The New York Times CMS / Publishing Platform
- **Scale:** Hundreds of stories/day, paywall.
- **Core features:** Edit, embargo, publish, breaking news, push.
- **Hard parts:** Embargo enforcement, multi-channel distribution, comment moderation.
- **Discuss:** Editorial workflow; near-real-time push.

### 402. Design a News Aggregator (Google News / Apple News)
- **Scale:** 100K publishers, billions of articles.
- **Core features:** Crawl, dedup, cluster, personalize.
- **Hard parts:** Story clustering across sources, freshness, paywall handling.
- **Discuss:** TF-IDF clustering vs embeddings.

### 403. Design a Paywall / Subscription Engine for News
- **Scale:** 10M subscribers.
- **Core features:** Metered limits, hard paywall, gift links.
- **Hard parts:** Cookie-based metering vs login, share-link abuse.
- **Discuss:** Edge counter; cohort-based limit.

### 404. Design a Live Election-Night Reporting Platform
- **Scale:** Spike on Tuesday night.
- **Core features:** Precinct results, projections, maps.
- **Hard parts:** Result-feed ingest, multi-state schemas, error correction.
- **Discuss:** Read-heavy CDN strategy.

### 405. Design a Comments-Moderation System for News
- **Scale:** Millions of comments.
- **Core features:** Auto-classify, queue, ban, appeal.
- **Hard parts:** Multi-language, auto-ban evaders, false positives.
- **Discuss:** ML + community flag.

### 406. Design a Newsletter-Publishing Platform (Substack)
- **Scale:** 1M writers, 50M readers.
- **Core features:** Post, paid, email, web archive.
- **Hard parts:** Email deliverability, payouts, abuse.
- **Discuss:** Per-writer subdomain.

### 407. Design a Live Blog / Live Updates Page
- **Scale:** Breaking news, 1M concurrent readers.
- **Core features:** Streaming updates, pin, push.
- **Hard parts:** SSE/WebSocket fanout, CDN caching with live updates.
- **Discuss:** Edge stream vs polling.

### 408. Design a Fact-Check Database
- **Scale:** Cross-publisher.
- **Core features:** Claim CRUD, verdict, citations.
- **Hard parts:** ClaimReview schema, fuzzy claim match.
- **Discuss:** Cross-org dedup.

### 409. Design a Photo-Wire / Press-Photo Distribution (Getty / AP)
- **Scale:** Photographers worldwide → newsrooms.
- **Core features:** Upload, caption, license, search.
- **Hard parts:** IPTC metadata, embargo, watermarking.
- **Discuss:** Per-customer license bundling.

### 410. Design a Real-time Sports-Score Service (ESPN ScoreCenter)
- **Scale:** Live scores, push.
- **Core features:** Game state, scoreboard widgets, push.
- **Hard parts:** Multi-data-feed reliability, latency, clock sync.
- **Discuss:** Authoritative data feed; CDN cache busting.

### 411. Design a Podcast Hosting + Analytics
- **Scale:** Millions of shows.
- **Core features:** Upload, RSS, analytics on downloads.
- **Hard parts:** IAB-certified analytics, prefix tracking, ads insert.
- **Discuss:** Server-side ad insertion.

### 412. Design a Magazine PDF Reader (Zinio)
- **Scale:** Subscription of magazines.
- **Core features:** Download, page-flip viewer, annotations.
- **Hard parts:** DRM, search across PDFs.
- **Discuss:** Page-level DRM keys.

### 413. Design a Book-Publishing Editorial Platform
- **Scale:** Authors → editors → press.
- **Core features:** Manuscript ingest, track changes, ARC distribution.
- **Hard parts:** Versioning, multi-format export (EPUB/MOBI).
- **Discuss:** Pandoc-based pipeline.

### 414. Design a Book Reader (Kindle Cloud)
- **Scale:** Sync library + position across devices.
- **Core features:** Library, sync, highlights, dictionary.
- **Hard parts:** DRM, position sync, offline caching.
- **Discuss:** Per-book license; whisper-sync.

### 415. Design a Translation-Memory / CAT Tool (SDL Trados)
- **Scale:** Global translators.
- **Core features:** Translation memory, glossary, MT assist.
- **Hard parts:** TMX standard, fuzzy match, customer-specific TMs.
- **Discuss:** Per-tenant TM isolation.

### 416. Design an Audio-Book Platform (Audible)
- **Scale:** 1M books, 100M users.
- **Core features:** Download, sync position with text, sleep timer.
- **Hard parts:** Whisper-sync between book + audio, DRM.
- **Discuss:** Multi-narrator chapters; speed-vs-quality encoding.

### 417. Design a Newsroom CMS for TV Broadcast
- **Scale:** Broadcast control room.
- **Core features:** Rundown, prompter, lower-thirds, video clip.
- **Hard parts:** Real-time rundown changes, MOS protocol.
- **Discuss:** Studio integration; redundancy.

### 418. Design a Crowdsourced Wiki (Wikipedia)
- **Scale:** 60M articles, multi-language.
- **Core features:** Edit, version, revert, talk pages.
- **Hard parts:** Vandalism detection, translation parity, bot edits.
- **Discuss:** Diff storage; WikiText rendering.

### 419. Design a Comments Threading Engine (Disqus-tier 2)
- **Scale:** Threaded, vote-sorted.
- **Core features:** Reply, vote, sort by best.
- **Hard parts:** Best-sort across millions, near-real-time score.
- **Discuss:** Confidence-bound (Wilson) vs hot.

### 420. Design a Media-Embargo / Pressroom Platform
- **Scale:** PR companies → journalists.
- **Core features:** Distribute embargoed news, lift at time.
- **Hard parts:** Time-locked release, watermarked drafts.
- **Discuss:** Per-journalist watermarking; auto-lift.

### 421. Design a Magazine Subscription Box (Print + Digital)
- **Scale:** Recurring shipments.
- **Core features:** Auto-renewal, shipping cycles, address mgmt.
- **Hard parts:** Issue-based vs date-based, donations, gift subs.
- **Discuss:** Subscription state machine.

### 422. Design a Live Translation / Captioning for News Streams
- **Scale:** Multi-language broadcast.
- **Core features:** Translate live audio, render captions.
- **Hard parts:** Latency, accuracy, profanity.
- **Discuss:** ML pipeline + human-correction.

### 423. Design a User-Generated Content Submission Pipeline (CNN iReport)
- **Scale:** Photos/video from public.
- **Core features:** Upload, verify, publish.
- **Hard parts:** Authenticity verification, deepfake detection.
- **Discuss:** Provenance tracking (C2PA).

### 424. Design a Ratings / Review Aggregator (Rotten Tomatoes)
- **Scale:** Movie/TV reviews.
- **Core features:** Aggregate critic + audience scores.
- **Hard parts:** Critic-eligibility curation, review parsing.
- **Discuss:** Tomatometer threshold; verified-audience.

### 425. Design a Media Asset Management (MAM)
- **Scale:** Studio video archive.
- **Core features:** Ingest, transcode, metadata, search, retrieve.
- **Hard parts:** LTO archive integration, proxy generation.
- **Discuss:** Hierarchical storage; AI-assisted tagging.

# 🏃 29. Sports & Fitness

### 426. Design Strava (Activity Tracker)
- **Scale:** 100M athletes, GPS uploads.
- **Core features:** Record activity, upload, segments, leaderboards.
- **Hard parts:** GPS smoothing, segment matching, leaderboard scaling.
- **Discuss:** Geo-line matching; segment leaderboard re-rank.

### 427. Design a Fitness-Tracking Wearable Backend (Fitbit)
- **Scale:** 30M devices.
- **Core features:** Sync steps, sleep, HR; trends; challenges.
- **Hard parts:** Sub-second HR ingest, battery, data backfill on offline.
- **Discuss:** Streaming vs batched sync.

### 428. Design a Workout-Plan / Personal Trainer App (Peloton)
- **Scale:** Live classes + on-demand.
- **Core features:** Live class stream, leaderboard, metric capture (cadence/HR).
- **Hard parts:** Live-class concurrent metrics, low-latency stream.
- **Discuss:** Pub/sub per class.

### 429. Design a Real-time Fantasy-Sports Platform (DraftKings)
- **Scale:** Millions of contests.
- **Core features:** Lineup, real-time scoring, payout.
- **Hard parts:** Stat-feed ingest, late-swap rules, regulation per state.
- **Discuss:** Stream processing for scoring.

### 430. Design a Sports-Betting Sportsbook
- **Scale:** Global, multi-market.
- **Core features:** Pre-match + live betting, in-play, settle.
- **Hard parts:** Line management, risk engine, instant settle.
- **Discuss:** Cash-out engine; throttling on hot lines.

### 431. Design a Tournament Bracket Generator (March Madness)
- **Scale:** Office pool style.
- **Core features:** Bracket entry, scoring as games conclude.
- **Hard parts:** Score recompute as upsets happen.
- **Discuss:** Recompute on each game finish.

### 432. Design a Live Sports Score Broadcasting (ESPN-tier 2)
- **Scale:** Concurrent live games globally.
- **Core features:** Push score updates to apps.
- **Hard parts:** Delivery to 50M devices in seconds.
- **Discuss:** SSE/WebSocket vs push notification.

### 433. Design a Sports Streaming Platform (Disney+/ESPN)
- **Scale:** Live + DVR.
- **Core features:** Live stream with auth, multi-camera angles.
- **Hard parts:** Geo-blackout rules, low-latency live, DVR storage.
- **Discuss:** Per-team blackout maps.

### 434. Design a Running-Coach App (Couch to 5K)
- **Scale:** Plan-driven workouts.
- **Core features:** Weekly plan, audio cues, progress.
- **Hard parts:** Plan adaptation, GPS-based run capture.
- **Discuss:** Adaptive coaching algorithm.

### 435. Design a Gym-Membership Management (Mindbody)
- **Scale:** Per-gym SaaS.
- **Core features:** Class booking, check-in, billing, waitlist.
- **Hard parts:** Capacity caps, no-show fees, multi-location.
- **Discuss:** Time-zone handling.

### 436. Design a Live Marathon-Tracker
- **Scale:** 50K runners with chip times.
- **Core features:** Real-time mat timing, push to subscribers.
- **Hard parts:** Mat-event ingest, runner search, ETA at next mat.
- **Discuss:** Event mat → runner → ETA pipeline.

### 437. Design a League-Management Platform (Soccer/Hockey)
- **Scale:** Local leagues.
- **Core features:** Schedule, refs, standings, stats.
- **Hard parts:** Round-robin generator, ref availability.
- **Discuss:** Constraint solver.

### 438. Design a Coaching Video-Analysis Tool (Hudl)
- **Scale:** Game-film with telestrator.
- **Core features:** Upload, tag plays, draw, share.
- **Hard parts:** Frame-accurate seek, tag schema, multi-team perms.
- **Discuss:** Web-based video annotation.

### 439. Design a Live ESports Platform
- **Scale:** Tournaments with millions concurrent.
- **Core features:** Bracket, live stream, chat.
- **Hard parts:** Stream alongside game state, anti-cheat.
- **Discuss:** Game-server feed integration.

### 440. Design a Step-Challenge / Workplace Wellness
- **Scale:** Corporate, 100K employees.
- **Core features:** Aggregate steps, team leaderboards, prizes.
- **Hard parts:** Multi-device source, anti-cheat.
- **Discuss:** HealthKit/GoogleFit ingest.

### 441. Design a Yoga / Meditation App Backend (Calm)
- **Scale:** 5M subscribers.
- **Core features:** Session library, downloads, streaks, sleep stories.
- **Hard parts:** Audio CDN, recommendation, offline.
- **Discuss:** Per-user playlist personalization.

### 442. Design a Sports Ticket Resale (StubHub)
- **Scale:** Secondary market.
- **Core features:** List, bid, transfer, e-ticket.
- **Hard parts:** Fraud, ticket verification, dynamic pricing.
- **Discuss:** Barcode reissue protocols.

### 443. Design a Sports Nutrition / Meal-Plan App
- **Scale:** Per-athlete meal plans.
- **Core features:** Plan, track macros, recipe.
- **Hard parts:** Food DB at scale, photo recognition.
- **Discuss:** USDA + crowdsourced food DB.

### 444. Design a Climbing-Gym Route-Tracker
- **Scale:** Per-gym route library.
- **Core features:** Mark routes done, grade, photos.
- **Hard parts:** Route-set turnover, crowdsourced grades.
- **Discuss:** Per-gym admin tool.

### 445. Design a Group Fitness Class Booking (ClassPass)
- **Scale:** Multi-studio aggregator.
- **Core features:** Search, book, credits, no-show.
- **Hard parts:** Studio-side inventory, late-cancel fees.
- **Discuss:** Studio API standardization.

### 446. Design a Live-Stream Workout (Mirror / Tonal)
- **Scale:** Connected device + mobile.
- **Core features:** Live class, on-device sensors, leaderboard.
- **Hard parts:** Form-correction ML, sensor calibration.
- **Discuss:** On-device CV; cloud aggregation.

### 447. Design a Sport League Player-Tracking (RFID)
- **Scale:** NBA/NFL player tracking.
- **Core features:** Real-time positions, derived stats.
- **Hard parts:** Sub-cm position, multi-camera fusion.
- **Discuss:** Sensor + CV fusion.

### 448. Design a Sports Event Photography Distribution (Marathonfoto)
- **Scale:** Photographers tag bib numbers.
- **Core features:** Bib OCR, runner search, purchase prints.
- **Hard parts:** OCR accuracy, multi-photographer dedup.
- **Discuss:** Bib detection ML.

### 449. Design a Pickleball / Local-League Match-Finder
- **Scale:** Local matchmaking.
- **Core features:** Find partner, courts, ELO.
- **Hard parts:** Pickup-game vs scheduled, no-shows.
- **Discuss:** Geo-radius search; rating ladder.

### 450. Design a Surf-Forecast / Tide Service
- **Scale:** Per-spot forecasts.
- **Core features:** Wave height, wind, tide, alerts.
- **Hard parts:** Weather model ingest, spot-specific local effects.
- **Discuss:** Buoy data + model fusion.

# 🎵 30. Music & Audio Tech

### 451. Design a Music-Recognition Service (Shazam)
- **Scale:** Billions of fingerprints.
- **Core features:** Capture audio, match, return song.
- **Hard parts:** Robust audio fingerprint, sub-second match, noise.
- **Discuss:** Constellation map; hash-based lookup.

### 452. Design a Spotify Discover Weekly Pipeline
- **Scale:** 600M users, weekly playlist.
- **Core features:** Per-user 30 tracks, fresh weekly.
- **Hard parts:** Compute at scale, freshness, exposure fairness.
- **Discuss:** ALS + audio embeddings; offline batch.

### 453. Design a SoundCloud-Style UGC Audio Platform
- **Scale:** 100M tracks, free upload.
- **Core features:** Upload, follow, comments at timestamp, plays.
- **Hard parts:** Copyright detection, pitch-shifted reuploads, CDN.
- **Discuss:** Audio fingerprint at upload.

### 454. Design a Music Royalty / Settlement System
- **Scale:** PRO + label payouts.
- **Core features:** Track plays, calc royalty, distribute.
- **Hard parts:** Splits across writers, mechanical vs performance, ISRC.
- **Discuss:** Append-only ledger; periodic statements.

### 455. Design a Music DAW Cloud Sync (BandLab)
- **Scale:** Multi-track project sync.
- **Core features:** Cloud projects, collaborator invite, mix.
- **Hard parts:** Multi-GB project sync, plugin compatibility.
- **Discuss:** Delta sync.

### 456. Design a Live-Concert Streaming Platform
- **Scale:** Single-event 1M concurrent.
- **Core features:** Pay-per-view, multi-cam, chat.
- **Hard parts:** Spike scaling, geo-blackouts, recording rights.
- **Discuss:** Pre-warm + autoscale.

### 457. Design a Music Karaoke App (Smule)
- **Scale:** Sing along + share.
- **Core features:** Sync lyrics, pitch detection, share recordings.
- **Hard parts:** Latency for duet, real-time pitch overlay.
- **Discuss:** Echo cancellation; pitch ML.

### 458. Design a Beat-Maker / Sample Marketplace (Splice)
- **Scale:** Subscription sample library.
- **Core features:** Browse, download samples, license.
- **Hard parts:** License tracking, audio search.
- **Discuss:** Per-sample license metadata.

### 459. Design a Music Download Store (iTunes Music Store legacy)
- **Scale:** 30M tracks, pay-per-track.
- **Core features:** Buy, download, library re-download.
- **Hard parts:** DRM (or DRM-free), region rights.
- **Discuss:** Region-rights matrix.

### 460. Design a Concert Setlist / Tour Tracker (Setlist.fm)
- **Scale:** Crowdsourced setlists.
- **Core features:** Tour calendar, setlist per show, statistics.
- **Hard parts:** Crowdsource accuracy, song equivalence (covers, intros).
- **Discuss:** Edit-history; community moderation.

### 461. Design a Music Lyrics + Chord Service (Genius / Ultimate Guitar)
- **Scale:** Crowdsourced.
- **Core features:** Crowd-edited lyrics, annotations, tabs.
- **Hard parts:** Copyright, edit conflicts, transposition.
- **Discuss:** License agreements with publishers.

### 462. Design a Music Notation Editor (MuseScore)
- **Scale:** Browser-based score editor.
- **Core features:** Note input, playback, share.
- **Hard parts:** Real-time sync, MIDI import, MusicXML.
- **Discuss:** Score-data model.

### 463. Design a DJ Mix Streaming (Mixcloud)
- **Scale:** DJ uploads + listener streams.
- **Core features:** Continuous mixes, copyright track ID.
- **Hard parts:** ID tracks within mix, proper licensing.
- **Discuss:** ACR (auto content recognition).

### 464. Design a Voice-Memo / Recorder Sync Service
- **Scale:** Apple Voice Memos at scale.
- **Core features:** Record, sync across devices.
- **Hard parts:** Variable file size, transcript on demand.
- **Discuss:** Local-first sync; iCloud-style.

### 465. Design a Live Audio Description (Accessibility) Service
- **Scale:** TV + film accessibility.
- **Core features:** Insert audio description in gaps.
- **Hard parts:** Mixing in real-time, sync to original audio.
- **Discuss:** Live blender + cue-points.

### 466. Design a Music Streaming with Spatial Audio
- **Scale:** Apple Spatial Audio.
- **Core features:** Atmos delivery, head-tracking.
- **Hard parts:** Bandwidth, format support, fallback.
- **Discuss:** ABR with Atmos vs stereo.

### 467. Design a Concert Wristband / RFID Cashless System
- **Scale:** Festival cashless payments.
- **Core features:** Top-up, tap-to-pay, refund.
- **Hard parts:** Offline POS, end-of-event reconciliation.
- **Discuss:** Edge POS sync; offline allowed.

### 468. Design a Radio Streaming Aggregator (TuneIn)
- **Scale:** Thousands of stations.
- **Core features:** Live radio, recordings, podcasts.
- **Hard parts:** Geo-rights, station feed reliability.
- **Discuss:** Stream proxy + caching.

### 469. Design a Music Concert Discovery (Bandsintown)
- **Scale:** Match user listening to nearby concerts.
- **Core features:** Sync library, location, alert.
- **Hard parts:** Artist disambiguation, geo-radius.
- **Discuss:** Push notification cadence.

### 470. Design a Lossless Music Streaming Service (Tidal)
- **Scale:** FLAC + MQA delivery.
- **Core features:** High-bandwidth audio, offline.
- **Hard parts:** Bandwidth, DRM, device support.
- **Discuss:** ABR for audio quality.

# 📷 31. Photography & Video Editing

### 471. Design a Cloud Photo Editor (Pixlr / Photopea)
- **Scale:** Browser-based image editing.
- **Core features:** Filters, layers, save/share.
- **Hard parts:** WebGL/Wasm rendering, large-file handling.
- **Discuss:** On-device vs cloud; export pipeline.

### 472. Design a Stock-Photo Marketplace (Shutterstock)
- **Scale:** 500M assets.
- **Core features:** Upload, license, search, payout.
- **Hard parts:** Reverse-image dedup, model release, watermark on preview.
- **Discuss:** Per-license SKU; royalty splits.

### 473. Design a Photo-Print / Photobook Service (Shutterfly)
- **Scale:** Holiday spike.
- **Core features:** Upload photos, design book, ship.
- **Hard parts:** Print-shop queue, print-quality validation.
- **Discuss:** Vendor routing; preview rendering.

### 474. Design a Wedding Photography Portfolio Platform (Pixieset)
- **Scale:** Per-photographer galleries.
- **Core features:** Client gallery, downloads, watermark, sales.
- **Hard parts:** Right-click protection, sales upsells.
- **Discuss:** Per-client password; print sales.

### 475. Design Adobe Creative Cloud File-Sync
- **Scale:** Multi-GB design files.
- **Core features:** Sync across devices, version, share.
- **Hard parts:** Delta sync, multi-app file types, attached fonts.
- **Discuss:** Block-level dedup; font sync.

### 476. Design a Mobile Photo-Editing App (VSCO / Lightroom Mobile)
- **Scale:** Edit + cloud sync.
- **Core features:** Filters, presets, RAW edit, sync.
- **Hard parts:** Non-destructive edits, multi-device sync.
- **Discuss:** Preset XMP sync; RAW thumbnail.

### 477. Design a Live Photo / Burst Photo Sync
- **Scale:** Apple Live Photo behavior.
- **Core features:** Sync image + 1.5s video.
- **Hard parts:** Pair handling, dedup across devices.
- **Discuss:** Asset pairing IDs.

### 478. Design a Video Editing Cloud (Adobe Premiere Cloud)
- **Scale:** Multi-collaborator video editing.
- **Core features:** Project sync, asset library, render.
- **Hard parts:** Multi-TB project, proxy generation, render farm.
- **Discuss:** Proxy editing; render queue.

### 479. Design a Photo-Recognition / Search by Image
- **Scale:** Within own library, or web-wide.
- **Core features:** "Search photos with cats", reverse search.
- **Hard parts:** Embedding model, ANN index, on-device for privacy.
- **Discuss:** Hybrid metadata + vector search.

### 480. Design a 3D-Render Farm / Cloud Render
- **Scale:** Animation studio rendering.
- **Core features:** Submit job, distribute frames, retrieve.
- **Hard parts:** Frame distribution, retry, asset sync.
- **Discuss:** Coordinator + worker pool.

### 481. Design an AI Photo-Enhance Service (Topaz / Adobe Enhance)
- **Scale:** Upload, ML upscale, return.
- **Core features:** Async pipeline, GPU pool.
- **Hard parts:** GPU sched, cost per inference.
- **Discuss:** Batch vs real-time.

### 482. Design a Real-Time Video Filter (Snapchat lenses)
- **Scale:** On-device AR filters.
- **Core features:** AR filter, server-distributed, capture share.
- **Hard parts:** Real-time on-device ML, filter package distribution.
- **Discuss:** Edge-package pipeline.

### 483. Design a Video Compression Service for User Uploads
- **Scale:** Pre-upload compress on client + server transcode.
- **Core features:** Client compress, multi-resolution server.
- **Hard parts:** Quality vs size, on-device GPU.
- **Discuss:** Two-stage encoding.

### 484. Design a 360°/VR Photo Service (Google Street View Photos)
- **Scale:** Crowdsourced street imagery.
- **Core features:** Upload, stitch, geotag, viewer.
- **Hard parts:** Equirectangular handling, blur faces/plates.
- **Discuss:** Auto-blur ML.

### 485. Design a TikTok-Style Video Editor (CapCut)
- **Scale:** On-device editor.
- **Core features:** Trim, overlay, effects, music.
- **Hard parts:** Real-time preview, asset library.
- **Discuss:** Project-file format.

### 486. Design a Live-Event Photo-Sharing App (Festival photo wall)
- **Scale:** Event-scoped, ephemeral.
- **Core features:** Group photo wall, geo-fenced upload.
- **Hard parts:** Auto-curate, moderation, takedown.
- **Discuss:** Event-scoped DB; auto-expiry.

### 487. Design a Document Scan App (Office Lens / CamScanner)
- **Scale:** Document scan + OCR.
- **Core features:** Detect edges, perspective correct, OCR, PDF.
- **Hard parts:** Edge detection, multi-page binding, OCR accuracy.
- **Discuss:** On-device CV; OCR cloud.

### 488. Design a Stop-Motion / Time-Lapse Pipeline
- **Scale:** Long-running capture.
- **Core features:** Capture, assemble, render.
- **Hard parts:** Storage of frames, render assembly.
- **Discuss:** Frame storage in object store.

### 489. Design a Family-Photo Sharing App (FamilyAlbum / Tinybeans)
- **Scale:** Closed family group.
- **Core features:** Upload, comment, monthly book.
- **Hard parts:** Parental controls, ad-free, offline.
- **Discuss:** Closed-group ACL.

### 490. Design a Long-Term Photo Archive Service (Libraries / Museums)
- **Scale:** Decades of preservation.
- **Core features:** Ingest, metadata, preservation, access.
- **Hard parts:** Format migration, fixity checks, OAIS.
- **Discuss:** Tape archive; metadata schema.

# 🛡️ 32. Insurance Tech

### 491. Design an Auto-Insurance Quote Engine
- **Scale:** Real-time quotes.
- **Core features:** Driver+vehicle inputs, MVR, quote.
- **Hard parts:** External data joins (MVR, CLUE), per-state filings.
- **Discuss:** Rating engine; rate-filing versioning.

### 492. Design a Home-Insurance Claims Platform
- **Scale:** Catastrophe surge (hurricane).
- **Core features:** FNOL, adjuster dispatch, payout.
- **Hard parts:** Catastrophe spike, catastrophe modeling, fraud.
- **Discuss:** Surge capacity for adjusters.

### 493. Design a Telematics Auto-Insurance (Progressive Snapshot)
- **Scale:** Driving-behavior monitor.
- **Core features:** Capture trips, scoring, premium adjust.
- **Hard parts:** Crash detection, privacy, opt-out.
- **Discuss:** Edge ML; data minimization.

### 494. Design a Pet-Insurance Claims App (Trupanion)
- **Scale:** Vet bill submission.
- **Core features:** Submit invoice, OCR, adjudicate, pay vet directly.
- **Hard parts:** Vet integration, pre-existing condition rules.
- **Discuss:** Vet-portal direct integration.

### 495. Design an Embedded-Insurance API (Lemonade)
- **Scale:** Insurance at checkout.
- **Core features:** Quote in-flow, bind, manage.
- **Hard parts:** Underwriting in <1s, partner-merchant SDK.
- **Discuss:** Decision-engine for instant bind.

### 496. Design a Health-Insurance Claims Adjudication
- **Scale:** B2B with providers.
- **Core features:** Claim ingest, COB, adjudicate, EOB.
- **Hard parts:** Rules per plan, prior auth, COB across payers.
- **Discuss:** Pricer + bundler engines.

### 497. Design a Workers-Comp Claims System
- **Scale:** State-regulated.
- **Core features:** Injury intake, medical payments, indemnity.
- **Hard parts:** Multi-state regulations, employer experience-mod.
- **Discuss:** Per-state forms.

### 498. Design a Life-Insurance Underwriting Pipeline
- **Scale:** App → exam → decision.
- **Core features:** App, MIB pull, exam orders, decision.
- **Hard parts:** Multi-vendor data, accelerated UW, mortality model.
- **Discuss:** Decision-engine + manual review.

### 499. Design a Reinsurance Treaty Tracking System
- **Scale:** B2B reinsurer.
- **Core features:** Treaty terms, cession, reporting.
- **Hard parts:** Complex slip terms, non-standard contracts.
- **Discuss:** Domain-specific DSL.

### 500. Design an Insurance Producer (Agent) Platform
- **Scale:** 100K agents.
- **Core features:** Quote, bind, commissions, licensing.
- **Hard parts:** State licensing tracking, commission calc.
- **Discuss:** NIPR sync; carrier integration.

### 501. Design a Catastrophe-Modeling Backend (RMS / AIR)
- **Scale:** Per-hurricane simulation.
- **Core features:** Event set, vulnerability, exposure → loss.
- **Hard parts:** Compute-intensive, simulation determinism.
- **Discuss:** GPU sim grid.

### 502. Design a Health-Insurance Open-Enrollment Marketplace (Healthcare.gov)
- **Scale:** Annual surge.
- **Core features:** Plan compare, enroll, subsidy calc.
- **Hard parts:** Federal/state hub integration, subsidy compute.
- **Discuss:** Eligibility hub orchestration.

### 503. Design an EOB (Explanation of Benefits) Engine
- **Scale:** Per-claim EOB to member.
- **Core features:** Adjudication detail rendering, mail/portal.
- **Hard parts:** Multi-language, accessibility, privacy.
- **Discuss:** Per-claim audit trail.

### 504. Design a Group-Insurance Enrollment Platform
- **Scale:** Employer group coverage.
- **Core features:** EOI, dependents, evidence of insurability.
- **Hard parts:** Group census reconciliation, life events.
- **Discuss:** EDI 834 ingest/output.

### 505. Design an Insurance Fraud Detection System
- **Scale:** SIU caseload.
- **Core features:** Score claims, refer to investigator.
- **Hard parts:** Multi-claim graph, ring detection.
- **Discuss:** Graph DB; SIU workflow.

### 506. Design a Risk-Engineering Visit Scheduler
- **Scale:** Commercial insurance loss-control.
- **Core features:** Schedule visit, report, recommendations.
- **Hard parts:** Engineer routing, doc capture, finding tracking.
- **Discuss:** Mobile app; offline capture.

### 507. Design an Annuity Servicing Platform
- **Scale:** Long-tail policies.
- **Core features:** Premium, withdrawal, surrender, RMD.
- **Hard parts:** Decades of service, regulatory rules.
- **Discuss:** Effective-dated everything.

### 508. Design an Insurance Self-Service Portal
- **Scale:** Member self-care.
- **Core features:** Pay premium, view docs, file claim, change beneficiary.
- **Hard parts:** Identity proof, POA, MFA.
- **Discuss:** Service-now style ticketing.

### 509. Design a Drone-Based Roof Inspection
- **Scale:** Claim adjust via drone.
- **Core features:** Schedule flight, capture, report.
- **Hard parts:** Drone fleet, FAA Part 107, ML for damage detect.
- **Discuss:** Pilot dispatch + AI report.

### 510. Design a Dental-Insurance Plan-Pricing Tool
- **Scale:** Network procedure pricing.
- **Core features:** Procedure code price by network.
- **Hard parts:** Provider contracts, fee schedule updates.
- **Discuss:** Per-network fee table.

# ⚡ 33. Energy & Utilities

### 511. Design a Smart-Meter Data Pipeline
- **Scale:** 50M meters, 15-min reads.
- **Core features:** Ingest, billing, outage detect.
- **Hard parts:** AMI head-end integration, missing reads, theft detect.
- **Discuss:** Time-series at scale; gap-fill.

### 512. Design an Outage-Management System for Utility
- **Scale:** Storm response.
- **Core features:** Customer reports, predictive map, crew dispatch.
- **Hard parts:** Outage prediction from meter loss, ETR calc.
- **Discuss:** Geo-clustering of reports.

### 513. Design an EV Charging Network (ChargePoint)
- **Scale:** 500K stations.
- **Core features:** Find/charge/pay, idle fees, fleet billing.
- **Hard parts:** OCPP, station offline, demand response.
- **Discuss:** Station-state sync.

### 514. Design a Demand-Response Aggregator
- **Scale:** Grid-scale curtailment.
- **Core features:** Enroll devices, dispatch reduction, settle.
- **Hard parts:** Sub-minute dispatch, baseline computation.
- **Discuss:** OpenADR; settlement math.

### 515. Design a Solar-Panel Performance Monitor (Enphase)
- **Scale:** Per-microinverter.
- **Core features:** Production, alerts, monetary value.
- **Hard parts:** Long-tail device support.
- **Discuss:** Edge gateway → cloud.

### 516. Design a Utility Customer-Billing System (CIS)
- **Scale:** Tens of millions of customers.
- **Core features:** Billing cycle, payments, arrears, deposit.
- **Hard parts:** Regulated rate plans, prorations, disconnects.
- **Discuss:** Rate-engine versioning.

### 517. Design a Wholesale Energy-Market Trading
- **Scale:** ISO/RTO market.
- **Core features:** Day-ahead, real-time, ancillary.
- **Hard parts:** 5-min scheduling, transmission constraints.
- **Discuss:** SCED algorithm.

### 518. Design a Smart-Grid SCADA Platform
- **Scale:** Substation telemetry.
- **Core features:** Real-time SCADA, alarms, control.
- **Hard parts:** ICS protocols, latency, NERC CIP.
- **Discuss:** Edge gateway + cyber-segmentation.

### 519. Design a Pipeline Leak-Detection (Oil/Gas)
- **Scale:** Continuous SCADA.
- **Core features:** Pressure/flow analysis, alarm, dispatch.
- **Hard parts:** False positive tuning, regulatory notification.
- **Discuss:** Anomaly detection methods.

### 520. Design a Renewable Forecasting Service (Wind/Solar)
- **Scale:** Per-farm forecast.
- **Core features:** Weather model + on-site, day-ahead curve.
- **Hard parts:** Multi-model ensemble, real-time sensor fusion.
- **Discuss:** Forecast skill metrics.

### 521. Design a Building Management System (BMS) Cloud
- **Scale:** Commercial buildings.
- **Core features:** HVAC, lighting, occupancy, schedules.
- **Hard parts:** BACnet integration, cyber-isolation.
- **Discuss:** Edge gateway; cloud command queue.

### 522. Design an EV Battery-Telemetry Platform
- **Scale:** Connected vehicles.
- **Core features:** Battery state, range estimation, OTA.
- **Hard parts:** Sub-second data, V2G readiness.
- **Discuss:** Chargeport-side communication.

### 523. Design a Net-Metering / Solar Settlement
- **Scale:** Customer-generation.
- **Core features:** Bidirectional kWh, monthly settle.
- **Hard parts:** Tariff complexity, true-up.
- **Discuss:** Per-period netting.

### 524. Design a Water-Utility Leak Detector
- **Scale:** District metering.
- **Core features:** Monitor flow, detect leaks, dispatch.
- **Hard parts:** Sensor noise, district modeling.
- **Discuss:** Bayesian leak detection.

### 525. Design a Smart-Home Energy Optimizer (Sense)
- **Scale:** Home circuit-level energy.
- **Core features:** Disaggregate appliances, savings tips.
- **Hard parts:** Signal disaggregation ML.
- **Discuss:** Edge ML model.

# 🌾 34. Agriculture & Food Tech

### 526. Design a Precision-Agriculture Platform (John Deere Operations)
- **Scale:** Farm-level data.
- **Core features:** Field maps, sensor data, yield, prescriptions.
- **Hard parts:** Equipment integration, satellite data, offline-rural.
- **Discuss:** Edge sync at the farm.

### 527. Design a Drone-Based Crop-Monitoring Service
- **Scale:** Per-field flights.
- **Core features:** NDVI, anomaly detect, prescription map.
- **Hard parts:** Image stitching, ML for crop health.
- **Discuss:** Tile-based map UI.

### 528. Design a Farm-to-Restaurant Marketplace
- **Scale:** Local sourcing.
- **Core features:** Browse, order, route delivery.
- **Hard parts:** Perishable inventory, logistics.
- **Discuss:** Per-region inventory.

### 529. Design a Food-Traceability System (FSMA 204)
- **Scale:** Critical-tracking events.
- **Core features:** Capture KDE/CTE, recall trace.
- **Hard parts:** Multi-tier supplier data sharing.
- **Discuss:** GS1 EPCIS standard.

### 530. Design a Restaurant POS + Kitchen Display
- **Scale:** Per-restaurant ops.
- **Core features:** Order, kitchen ticket, payment, inventory.
- **Hard parts:** Offline operations, multi-location franchise.
- **Discuss:** Edge POS + cloud sync.

### 531. Design a Recipe-Personalization Service
- **Scale:** User dietary preferences.
- **Core features:** Recipe DB, filter by dietary, generate meal plan.
- **Hard parts:** Ingredient substitutions, nutritional calc.
- **Discuss:** USDA food DB; substitution graph.

### 532. Design a Grocery-Delivery (Instacart) Picker App
- **Scale:** Picker side of marketplace.
- **Core features:** Pick list, substitutions, customer chat.
- **Hard parts:** Real-time inventory, sub policy, payment at register.
- **Discuss:** In-store inventory uncertainty.

### 533. Design a Vertical-Farming Control System
- **Scale:** Indoor controlled environment.
- **Core features:** Climate control, lighting, irrigation.
- **Hard parts:** Multi-zone control, recipe optimization.
- **Discuss:** PLC + cloud orchestration.

### 534. Design a Livestock-Tracking System (RFID Tags)
- **Scale:** Cattle traceability.
- **Core features:** Birth → slaughter chain, health events.
- **Hard parts:** Rural connectivity, data ownership.
- **Discuss:** Edge gateway in barn.

### 535. Design a Crop-Insurance Platform (USDA RMA)
- **Scale:** Federal crop insurance.
- **Core features:** Acreage report, yield, indemnity.
- **Hard parts:** Multi-stage reporting deadlines, weather indexing.
- **Discuss:** RMA data exchange.

### 536. Design a Fertilizer-Recommendation Engine
- **Scale:** Per-field prescription.
- **Core features:** Soil samples, yield goal, recommend rate.
- **Hard parts:** Sample-to-prescription ML, equipment compatibility.
- **Discuss:** Variable-rate map export.

### 537. Design a Direct-to-Consumer Meal-Kit Service (Blue Apron)
- **Scale:** Subscription, ingredient pack.
- **Core features:** Choose recipes, weekly ship.
- **Hard parts:** Inventory + perishable, kitting at fulfillment.
- **Discuss:** Demand-driven kitting.

### 538. Design a Food-Waste-Tracking System (Restaurants)
- **Scale:** Per-restaurant.
- **Core features:** Log waste, source by type, reports.
- **Hard parts:** Camera-based waste detection.
- **Discuss:** ML on waste-bin camera.

### 539. Design a Greenhouse Climate-Control Cloud
- **Scale:** Multi-greenhouse operator.
- **Core features:** Climate setpoints, override, alarms.
- **Hard parts:** Failsafe controls, multi-vendor sensors.
- **Discuss:** Edge controller authority.

### 540. Design a Beekeeping Hive-Monitor Service
- **Scale:** Beehive sensors.
- **Core features:** Temp, weight, queen alerts.
- **Hard parts:** Battery life, rural cell.
- **Discuss:** LPWAN + cloud.

# 🏗️ 35. Construction & AEC

### 541. Design a BIM (Building Info Modeling) Cloud Platform
- **Scale:** Multi-GB models.
- **Core features:** Upload, view, federate models, clash detect.
- **Hard parts:** IFC/Revit ingest, cloud rendering.
- **Discuss:** Tile-based 3D streaming.

### 542. Design a Construction-Bid Marketplace
- **Scale:** Public bids.
- **Core features:** Post project, sub-bid, award.
- **Hard parts:** Trade-package routing, document control.
- **Discuss:** Bid-leveling spreadsheet.

### 543. Design a Field-Service Daily-Log App
- **Scale:** Construction site logs.
- **Core features:** Daily log, photos, weather, manpower.
- **Hard parts:** Offline capture, geo-tag, audit.
- **Discuss:** Sync conflict resolution.

### 544. Design a Submittal / RFI Workflow
- **Scale:** Per-project routing.
- **Core features:** Submit, review, approve, distribute.
- **Hard parts:** Multi-party routing, deadlines.
- **Discuss:** Workflow engine; reminder schedule.

### 545. Design a Punch-List Management Tool
- **Scale:** End-of-project items.
- **Core features:** Capture punches, assign, close, photo.
- **Hard parts:** Plan-pin location, assignment routing.
- **Discuss:** Plan-PDF overlay.

### 546. Design a Subcontractor Compliance / COI Tracker
- **Scale:** Insurance certs from subs.
- **Core features:** Track expiration, request renewal.
- **Hard parts:** Document parsing, multi-state requirements.
- **Discuss:** OCR + structured extraction.

### 547. Design a Heavy-Equipment Telematics (Caterpillar)
- **Scale:** Construction fleet.
- **Core features:** Hours, idle, maintenance, location.
- **Hard parts:** Multi-OEM, satellite when no cell.
- **Discuss:** ISO 15143-3.

### 548. Design a Site-Safety Incident-Reporting System
- **Scale:** Daily JHA + incidents.
- **Core features:** Report, OSHA report, training compliance.
- **Hard parts:** OSHA log, lagging vs leading indicators.
- **Discuss:** Mobile-first capture.

### 549. Design a Concrete-Pour Scheduling System
- **Scale:** Multi-site batch plant.
- **Core features:** Order, batch, dispatch trucks, ticket.
- **Hard parts:** Pour-window timing, mix design.
- **Discuss:** Plant-side scheduling.

### 550. Design a Modular-Construction Factory Workflow
- **Scale:** Module assembly line.
- **Core features:** Track modules, station completion, ship.
- **Hard parts:** Assembly-line throughput, QC.
- **Discuss:** MES-style integration.

### 551. Design a Geotech Borehole Data Platform
- **Scale:** Soil samples.
- **Core features:** Capture borehole logs, lab tests, reports.
- **Hard parts:** Standard data formats (AGS).
- **Discuss:** Spatial visualization.

### 552. Design an Architecture Drawing Markup Tool (Bluebeam Revu)
- **Scale:** Multi-drawing review.
- **Core features:** PDF markup, sessions, takeoff.
- **Hard parts:** Real-time multi-user markup.
- **Discuss:** OT-based markup.

### 553. Design a Construction-Loan Draw-Schedule Manager
- **Scale:** Multi-project lender.
- **Core features:** Draw request, lien waivers, inspector verify.
- **Hard parts:** Lien tracking, mechanic's lien laws.
- **Discuss:** Document workflow.

### 554. Design a Smart Hard-Hat Worker Tracker
- **Scale:** Site personnel safety.
- **Core features:** Locate, fall detection, alert.
- **Hard parts:** Battery, indoor positioning.
- **Discuss:** UWB anchors.

### 555. Design an Interior-Design / Mood-Board App (Houzz)
- **Scale:** Design inspiration sharing.
- **Core features:** Save photos, build boards, hire pro.
- **Hard parts:** Image annotation for products.
- **Discuss:** Product-tagging in photos.

# 🏭 36. Manufacturing & Industrial IoT

### 556. Design an MES (Manufacturing Execution System)
- **Scale:** Factory floor.
- **Core features:** Work order, station, traceability.
- **Hard parts:** OEE metrics, real-time, ERP sync.
- **Discuss:** ISA-95 levels.

### 557. Design a Predictive-Maintenance Platform
- **Scale:** Industrial equipment.
- **Core features:** Vibration/temp, RUL, alert.
- **Hard parts:** Edge ML, false-alarm cost, retraining.
- **Discuss:** Edge inference + cloud retrain.

### 558. Design a Digital-Twin Platform
- **Scale:** Asset-level twins.
- **Core features:** Live state, sim, what-if.
- **Hard parts:** Sync with physical, simulation perf.
- **Discuss:** Stream-driven state.

### 559. Design a Quality-Inspection / SPC Platform
- **Scale:** Statistical process control.
- **Core features:** Capture measurements, control charts, alerts.
- **Hard parts:** Real-time SPC, multivariate.
- **Discuss:** Control-chart math.

### 560. Design a Robotic Process Automation (UiPath)
- **Scale:** Bot fleet.
- **Core features:** Build bot, deploy, schedule, monitor.
- **Hard parts:** UI-flake handling, credentials, logs.
- **Discuss:** Orchestrator + worker.

### 561. Design a Production-Scheduling APS System
- **Scale:** Multi-line factory.
- **Core features:** Plan jobs, bottleneck, change-overs.
- **Hard parts:** Constraint solver, demand variability.
- **Discuss:** APS solver patterns.

### 562. Design a Material-Requirements-Planning (MRP)
- **Scale:** BOM explosion.
- **Core features:** Demand, BOM, plan.
- **Hard parts:** Multi-level BOM, lot sizing.
- **Discuss:** MRP runs cadence.

### 563. Design a Plant Energy-Monitoring System
- **Scale:** Energy submetering.
- **Core features:** Aggregate, baseline, savings.
- **Hard parts:** Per-line allocation, EnPI.
- **Discuss:** ISO 50001 alignment.

### 564. Design a Tool-Calibration Tracking System
- **Scale:** Per-tool cal cert.
- **Core features:** Schedule cal, cert capture, due alerts.
- **Hard parts:** Audit trail, traceability.
- **Discuss:** ISO 17025 cert.

### 565. Design a Defect-Image AI Classifier
- **Scale:** Inline camera-based QC.
- **Core features:** Capture, classify, accept/reject.
- **Hard parts:** Edge inference latency, drift retraining.
- **Discuss:** Edge GPU + cloud retrain.

### 566. Design a Plant-Floor Andon System
- **Scale:** Real-time call-for-help.
- **Core features:** Pull cord, escalation, response time.
- **Hard parts:** Real-time push to leads, escalations.
- **Discuss:** Pub/sub by station.

### 567. Design a SCADA / DCS Modernization (Cloud Connect)
- **Scale:** Legacy plant data online.
- **Core features:** OPC-UA bridge, cloud time-series.
- **Hard parts:** ICS cyber-isolation.
- **Discuss:** Air-gap broker pattern.

### 568. Design a Spare-Parts Inventory Optimizer
- **Scale:** Per-MRO warehouse.
- **Core features:** Reorder, criticality, supplier.
- **Hard parts:** Long-tail SKU forecast, criticality scoring.
- **Discuss:** ABC/XYZ analysis.

### 569. Design an OEE Dashboard
- **Scale:** Real-time per-line.
- **Core features:** Avail, perf, quality, drilldown.
- **Hard parts:** Reason-code capture, micro-stoppage.
- **Discuss:** Stop-reason taxonomy.

### 570. Design a Recipe-Management System for Process Industry
- **Scale:** Pharma/Food batch.
- **Core features:** Recipe versioning, eBR, regulator audit.
- **Hard parts:** 21 CFR Part 11.
- **Discuss:** Electronic batch records.

### 571. Design a Plant Floor Asset-Tracking (RFID)
- **Scale:** WIP location.
- **Core features:** Read events, dwell, throughput.
- **Hard parts:** Reader topology, phantom reads.
- **Discuss:** Edge filter rules.

### 572. Design a Worker-Productivity Time-Standard System
- **Scale:** Manufacturing time studies.
- **Core features:** Standard time DB, measure, variance.
- **Hard parts:** MTM library, learning curve.
- **Discuss:** Per-operation rates.

### 573. Design a Plant Dispatch Routing for AGVs
- **Scale:** AGV fleet.
- **Core features:** Task assign, traffic mgmt.
- **Hard parts:** Deadlock-free routing.
- **Discuss:** Centralized planner.

### 574. Design an OT Cybersecurity Monitor
- **Scale:** ICS network monitoring.
- **Core features:** Asset discovery, anomaly, alerts.
- **Hard parts:** Passive monitoring, ICS protocols.
- **Discuss:** SPAN ports + DPI.

### 575. Design a Supply-Chain Track-and-Trace via QR
- **Scale:** Per-item serial.
- **Core features:** Scan events, chain, customer verify.
- **Hard parts:** Anti-counterfeit, scan capacity.
- **Discuss:** Public verify portal.

# 🏪 37. Retail & POS Systems

### 576. Design a Modern Retail POS (Square / Shopify POS)
- **Scale:** Multi-store.
- **Core features:** Sale, returns, inventory, tax, tender.
- **Hard parts:** Offline mode, multi-tax, peripheral support.
- **Discuss:** Edge DB + sync.

### 577. Design a Self-Checkout Kiosk Backend
- **Scale:** Grocery scale.
- **Core features:** Scan, weigh, pay, theft prevention.
- **Hard parts:** Theft detection ML, age verification.
- **Discuss:** Computer-vision-assisted.

### 578. Design a Cashierless Store (Amazon Go)
- **Scale:** Per-store sensor fusion.
- **Core features:** Detect items taken, charge on exit.
- **Hard parts:** CV reliability, customer ID, returns.
- **Discuss:** Multi-camera tracking.

### 579. Design a Loyalty Program Backend (Starbucks Stars)
- **Scale:** 30M loyalty members.
- **Core features:** Earn, redeem, tier, partner integration.
- **Hard parts:** Real-time POS link.
- **Discuss:** Mobile-app integration.

### 580. Design a Retail Promo / Pricing Engine
- **Scale:** Multi-promo stacking.
- **Core features:** Promo eligibility, stacking rules.
- **Hard parts:** Rules engine, conflict resolution, last-mile pricing.
- **Discuss:** Drools-style rules.

### 581. Design a Buy-Online-Pickup-In-Store (BOPIS)
- **Scale:** Multi-store fulfillment.
- **Core features:** Reserve, pick, notify ready.
- **Hard parts:** In-store inventory accuracy, pick SLA.
- **Discuss:** Pick-task allocation.

### 582. Design a Returns-Management for Retail
- **Scale:** Multi-channel returns.
- **Core features:** Initiate, accept, refund, restock.
- **Hard parts:** Channel parity, ASN, fraud.
- **Discuss:** State machine.

### 583. Design a Markdown / Clearance Optimizer
- **Scale:** Seasonal markdowns.
- **Core features:** Recommend markdown timing/depth.
- **Hard parts:** Demand model, forward-looking inventory.
- **Discuss:** Per-SKU model.

### 584. Design a Retail Heatmap from Cameras
- **Scale:** Store traffic analytics.
- **Core features:** Foot traffic, dwell, conversion.
- **Hard parts:** Privacy, fixed-camera analytics.
- **Discuss:** Edge person-detection.

### 585. Design a Retail Inventory-Audit App (Cycle Count)
- **Scale:** Daily cycle counts.
- **Core features:** Scan task, variance, adjust.
- **Hard parts:** Wave planning, variance threshold.
- **Discuss:** Mobile scanner workflow.

### 586. Design a Shelf-Out-of-Stock Detector (Camera)
- **Scale:** Per-aisle camera.
- **Core features:** Detect OOS, alert.
- **Hard parts:** Multi-product taxonomy, occlusion.
- **Discuss:** Edge ML, periodic retrain.

### 587. Design a Smart Shopping-Cart (Caper)
- **Scale:** Cart-with-cameras.
- **Core features:** Auto-detect items dropped in.
- **Hard parts:** CV accuracy, payment in-cart.
- **Discuss:** Edge inference + cloud reconciliation.

### 588. Design an In-Store Wayfinding Map App
- **Scale:** Per-store map.
- **Core features:** Item search → aisle, indoor positioning.
- **Hard parts:** BLE beacons, planogram updates.
- **Discuss:** Per-store map authoring tool.

### 589. Design a Retail Endless-Aisle Kiosk
- **Scale:** In-store online catalog.
- **Core features:** Browse online catalog, ship-to-home.
- **Hard parts:** Sync with online inventory.
- **Discuss:** Order-routing decision.

### 590. Design a Grocery-Inventory Expiration Tracker
- **Scale:** Perishable management.
- **Core features:** Date code capture, FEFO rotation, markdown.
- **Hard parts:** Capture method (label scan).
- **Discuss:** OCR for date codes.

### 591. Design a Restaurant Online-Ordering (Toast / Olo)
- **Scale:** Per-restaurant online.
- **Core features:** Menu, cart, kitchen send, payment.
- **Hard parts:** POS integration, item availability.
- **Discuss:** Menu sync from POS.

### 592. Design a Drive-Thru Order-Capture System
- **Scale:** QSR.
- **Core features:** Speaker-post, screen, AI order taker.
- **Hard parts:** Voice recognition, modifier mapping.
- **Discuss:** ASR + menu mapping.

### 593. Design a Loyalty-Linked Receipt OCR App (Fetch)
- **Scale:** Submit receipt for points.
- **Core features:** Photo, OCR, dedupe, points.
- **Hard parts:** Accuracy, fraud (reused receipts).
- **Discuss:** Hash-chain dedup.

### 594. Design a Vendor Compliance Portal (Retailer)
- **Scale:** Suppliers ship to retailer DCs.
- **Core features:** ASN, routing guide, chargeback.
- **Hard parts:** EDI 856/940, compliance scoring.
- **Discuss:** Chargeback automation.

### 595. Design a Retail Workforce-Management (Kronos)
- **Scale:** Hourly staff scheduling.
- **Core features:** Forecast demand, schedule, swap.
- **Hard parts:** Forecast accuracy, fair-week-week laws.
- **Discuss:** Predictive scheduling laws.

### 596. Design a Retail Mystery-Shopper Platform
- **Scale:** Crowd shoppers.
- **Core features:** Assignment, capture, payout.
- **Hard parts:** Assignment routing, fraud.
- **Discuss:** Geo-verification.

### 597. Design a Convenience Store Fuel-Pump Integration
- **Scale:** Forecourt pumps.
- **Core features:** Authorize, pump, payment.
- **Hard parts:** Pre-auth release, payment terminals.
- **Discuss:** Conexxus standards.

### 598. Design a Curbside Pickup Real-time Coord
- **Scale:** Customer arrival → bring out.
- **Core features:** Geofence arrival, notify staff.
- **Hard parts:** Spot identification, no-show.
- **Discuss:** Push notifications and GPS triggers.

### 599. Design a Retail Theft-Detection / EAS System
- **Scale:** Per-store EAS.
- **Core features:** Tag detection, video link, alert.
- **Hard parts:** Tag-detection false positive.
- **Discuss:** Camera-link automation.

### 600. Design a Retail Personalization Engine (1:1 offers)
- **Scale:** Per-customer dynamic offers.
- **Core features:** Profile, offer pool, deliver via channel.
- **Hard parts:** Eligibility logic, frequency caps.
- **Discuss:** Decision engine.

# 🏦 38. Banking & FinTech (Advanced)

### 601. Design a Real-Time Payments Network (FedNow / RTP)
- **Scale:** Sub-second settlement.
- **Core features:** Send, receive, request-to-pay, 24/7.
- **Hard parts:** Liquidity, fraud, ISO 20022.
- **Discuss:** Liquidity throttling.

### 602. Design a Cross-Border Payment System (Wise/SWIFT)
- **Scale:** Multi-currency, multi-corridor.
- **Core features:** Quote FX, send, settle, recipient.
- **Hard parts:** Compliance per country, FX exposure, settlement risk.
- **Discuss:** Pre-funding model.

### 603. Design a Cards-Issuing Platform (Marqeta-style)
- **Scale:** Virtual + physical card issuance.
- **Core features:** Issue card, JIT funding, transaction auth.
- **Hard parts:** Auth in <100ms, network rules, BIN sponsorship.
- **Discuss:** Real-time JIT funding hooks.

### 604. Design a Cards-Acquiring / Merchant Acquirer
- **Scale:** Process merchant transactions.
- **Core features:** Auth, capture, refund, settlement.
- **Hard parts:** PCI scope, network fees, chargebacks.
- **Discuss:** Tokenization vault.

### 605. Design an ACH Origination Platform
- **Scale:** Bulk debit/credit.
- **Core features:** Submit batch, NACHA file, returns.
- **Hard parts:** Same-day vs next-day, returns processing.
- **Discuss:** NACHA file generation.

### 606. Design an ATM Network
- **Scale:** Bank ATM fleet.
- **Core features:** Cash dispense, deposit, fraud.
- **Hard parts:** Reconciliation, cash forecasting, network outages.
- **Discuss:** Standin processing.

### 607. Design a Loan-Origination System (LOS)
- **Scale:** Auto/personal/student loans.
- **Core features:** App, credit pull, decision, fund.
- **Hard parts:** Decision rules, doc collection, post-fund servicing.
- **Discuss:** Decision engine; e-sign.

### 608. Design a Loan-Servicing Platform
- **Scale:** Long-tail loans.
- **Core features:** Amortize, payments, escrow, payoff.
- **Hard parts:** Default servicing, escrow analysis.
- **Discuss:** Calc engine for amortization.

### 609. Design a Mortgage-Servicing Pipeline
- **Scale:** Long-life loans.
- **Core features:** Payment, escrow, PMI, modification.
- **Hard parts:** Reg compliance, loss mitigation.
- **Discuss:** Workflow engine.

### 610. Design a Wealth-Management / Robo-Advisor (Wealthfront)
- **Scale:** Algorithmic portfolios.
- **Core features:** Risk profile, rebalance, tax-loss harvest.
- **Hard parts:** Trade timing, fractional shares, tax lots.
- **Discuss:** Cash drag minimize.

### 611. Design a Brokerage Account-Opening Platform
- **Scale:** Self-service onboarding.
- **Core features:** KYC, AML, account funding.
- **Hard parts:** OFAC, identity, name screening.
- **Discuss:** OCR + ID verification.

### 612. Design a Margin / Risk Engine for Brokerage
- **Scale:** Real-time margin calc.
- **Core features:** Initial, maintenance, calls.
- **Hard parts:** Real-time NAV, margin call timing.
- **Discuss:** Reg-T calculations.

### 613. Design a 401(k) / Retirement Plan System
- **Scale:** Plan participants.
- **Core features:** Contributions, vesting, loans, distributions.
- **Hard parts:** ERISA compliance, blackout, true-up.
- **Discuss:** Employer match logic.

### 614. Design a Tax-Reporting (1099) Generation
- **Scale:** Year-end massive output.
- **Core features:** Generate 1099s, deliver, IRS file.
- **Hard parts:** Per-form logic, corrections, FATCA.
- **Discuss:** Per-form pipeline.

### 615. Design a Bank-Statement / Statement-Aggregator API (Plaid)
- **Scale:** Account aggregation.
- **Core features:** Connect bank, pull transactions, balance.
- **Hard parts:** OFX vs scraping vs OAuth, freshness, MFA.
- **Discuss:** Open Banking (FDX).

### 616. Design a Personal Finance Manager (Mint)
- **Scale:** Aggregate accounts, categorize.
- **Core features:** Auto-categorize, budgets, alerts.
- **Hard parts:** Category accuracy, dedup.
- **Discuss:** ML categorization.

### 617. Design a Budgeting / Envelope App (YNAB)
- **Scale:** Multi-device sync.
- **Core features:** Categories, monthly assign, rollover.
- **Hard parts:** Conflict resolution, family-shared.
- **Discuss:** CRDT-style sync.

### 618. Design a BNPL (Buy Now, Pay Later) Platform (Affirm)
- **Scale:** At checkout decisions.
- **Core features:** Underwrite, pay over time, merchant.
- **Hard parts:** Real-time underwriting, collections.
- **Discuss:** Decision in <500ms.

### 619. Design a Credit-Score Free-View Service (Credit Karma)
- **Scale:** Consumer-side credit info.
- **Core features:** Free score, recommendations, monitoring.
- **Hard parts:** Bureau pull, partner offers.
- **Discuss:** Soft-pull vs hard.

### 620. Design a Stock-Borrowing / Securities-Lending Platform
- **Scale:** B2B short locate.
- **Core features:** Locate request, lend, recall.
- **Hard parts:** Inventory across desks, daily mark.
- **Discuss:** End-of-day allocation.

### 621. Design a Treasury-Management Platform for Corp
- **Scale:** Multi-bank visibility.
- **Core features:** Cash position, sweep, pay-by-FX.
- **Hard parts:** Multi-bank API integration.
- **Discuss:** SWIFT MT940 ingest.

### 622. Design a Wire-Transfer Fraud-Prevention System
- **Scale:** High-value detection.
- **Core features:** Rules + ML scoring, hold, callback.
- **Hard parts:** Velocity vs friction trade-off.
- **Discuss:** Step-up auth heuristics.

### 623. Design a Sanctions-Screening Platform (OFAC)
- **Scale:** Screen wires + customers.
- **Core features:** Name screening, alert, dispose.
- **Hard parts:** Fuzzy match, low FP, timeliness.
- **Discuss:** Phonetic + edit-distance.

### 624. Design a Fraud Case-Management System
- **Scale:** Investigator workflow.
- **Core features:** Cases, evidence, dispositions.
- **Hard parts:** SLA, regulatory reporting (SAR).
- **Discuss:** Workflow + reporting.

### 625. Design a Statement-Rendering Platform (Bank statements)
- **Scale:** PDF + electronic.
- **Core features:** Generate per cycle, deliver.
- **Hard parts:** Template engine, multi-language.
- **Discuss:** PDF rendering at scale.

### 626. Design a Wire-Routing System (SWIFT MT103)
- **Scale:** Inter-bank routing.
- **Core features:** Validate, route, settlement.
- **Hard parts:** Compliance, BIC routing, currency cutoffs.
- **Discuss:** SWIFT GPI.

### 627. Design a Bank-Branch / Teller App
- **Scale:** Frontline software.
- **Core features:** Lookup, transactions, holds, audit.
- **Hard parts:** Latency to core, role-based perms.
- **Discuss:** Core banking integration.

### 628. Design a Card-Tokenization Service (Apple Pay)
- **Scale:** Network token vault.
- **Core features:** Provision, replace, deprovision.
- **Hard parts:** Network token specs, device-binding.
- **Discuss:** EMV tokenization.

### 629. Design a Disputes / Chargeback Platform
- **Scale:** Card disputes.
- **Core features:** Initiate, evidence, network submit.
- **Hard parts:** Visa/MC reason codes, timeline.
- **Discuss:** State machine per dispute.

### 630. Design a Bank-Account Switching Service
- **Scale:** Switch direct deposit + recurring.
- **Core features:** ID payees, redirect, close old.
- **Hard parts:** Payee discovery, settlement window.
- **Discuss:** ACH redirection.

# ⛓️ 39. Crypto & DeFi

### 631. Design a Crypto Custodial Wallet
- **Scale:** Custody at scale.
- **Core features:** Store keys, sign, withdraw.
- **Hard parts:** HSM design, hot/warm/cold split, MPC.
- **Discuss:** Threshold signature schemes.

### 632. Design a Decentralized Exchange (Uniswap)
- **Scale:** AMM-based.
- **Core features:** Swap, LP, fees, route.
- **Hard parts:** Slippage, MEV, gas optimization.
- **Discuss:** AMM math.

### 633. Design a Centralized Crypto Exchange (Coinbase)
- **Scale:** Spot + derivatives.
- **Core features:** Order book, deposit, withdrawal.
- **Hard parts:** Cold-wallet flows, regulation.
- **Discuss:** Internal matching engine.

### 634. Design a Stablecoin Issuance Platform
- **Scale:** Mint/burn pegged token.
- **Core features:** Mint, redeem, attestation.
- **Hard parts:** Reserves audit, peg defense.
- **Discuss:** Reserve breakdown reporting.

### 635. Design a Crypto Tax-Reporting Tool (CoinTracker)
- **Scale:** Multi-exchange aggregation.
- **Core features:** Import, cost basis, gain/loss.
- **Hard parts:** FIFO/LIFO/specific-id, multi-chain.
- **Discuss:** Lot accounting.

### 636. Design a Blockchain Indexer (The Graph)
- **Scale:** All chain data → queryable.
- **Core features:** Subscribe to contracts, index events.
- **Hard parts:** Reorgs, re-index on schema, scale.
- **Discuss:** Subgraph design.

### 637. Design a Decentralized Storage System (Arweave / Filecoin)
- **Scale:** Storage marketplace.
- **Core features:** Store data, prove storage.
- **Hard parts:** Proof of replication, durability incentives.
- **Discuss:** Sealing pipeline.

### 638. Design an NFT Marketplace (OpenSea)
- **Scale:** Multi-chain trading.
- **Core features:** Mint, list, buy, royalties.
- **Hard parts:** Metadata permanence, indexing across chains.
- **Discuss:** Off-chain order books.

### 639. Design a Crypto On-Ramp / Off-Ramp (MoonPay)
- **Scale:** Fiat ↔ crypto.
- **Core features:** KYC, fund with card/ACH, deliver.
- **Hard parts:** Card fraud, settlement risk.
- **Discuss:** Fraud risk scoring.

### 640. Design a DeFi Lending Protocol Frontend (Aave-like)
- **Scale:** Lend/borrow.
- **Core features:** Supply, borrow, liquidate.
- **Hard parts:** Oracle reliance, liquidation incentives.
- **Discuss:** Oracle design.

### 641. Design a Crypto Block Explorer (Etherscan)
- **Scale:** Index all blocks.
- **Core features:** Search tx/address, contract verify.
- **Hard parts:** Reorgs, source-code verification.
- **Discuss:** Bytecode → source matching.

### 642. Design a Bitcoin Mining Pool Backend
- **Scale:** Coordinate miners.
- **Core features:** Distribute jobs, account shares, payout.
- **Hard parts:** Stratum protocol, share validation.
- **Discuss:** PPLNS payout.

### 643. Design a Crypto Mempool Monitor
- **Scale:** Watch unconfirmed tx.
- **Core features:** Stream mempool, alert.
- **Hard parts:** Multi-node aggregation, eviction.
- **Discuss:** Peer-network monitoring.

### 644. Design an MEV Auction System (Flashbots)
- **Scale:** Block-builder market.
- **Core features:** Bundles, sealed bids, builder.
- **Hard parts:** Fairness, latency, anti-frontrun.
- **Discuss:** PBS architecture.

### 645. Design a Token-Launch / IDO Platform
- **Scale:** Launchpad for tokens.
- **Core features:** Whitelist, vesting, distribute.
- **Hard parts:** Sybil resistance, vesting cliffs.
- **Discuss:** Merkle distributor.

### 646. Design a Wallet-as-a-Service API (Embedded wallets)
- **Scale:** SDK for apps.
- **Core features:** Create wallet, sign, recover.
- **Hard parts:** Custody model, key recovery.
- **Discuss:** MPC vs Shamir.

### 647. Design a Bridge Between Chains (cross-chain)
- **Scale:** Move assets between chains.
- **Core features:** Lock+mint, burn+release.
- **Hard parts:** Bridge security, reorgs, validators.
- **Discuss:** Light-client bridges.

### 648. Design a Decentralized Identity (DID) System
- **Scale:** Self-sovereign identity.
- **Core features:** DID resolution, credentials.
- **Hard parts:** Revocation, privacy, recovery.
- **Discuss:** W3C DID methods.

### 649. Design a DAO Governance Platform (Snapshot)
- **Scale:** Off-chain voting.
- **Core features:** Proposal, vote weight by token, execute.
- **Hard parts:** Snapshot block, signature verification.
- **Discuss:** EIP-712 signing.

### 650. Design a Yield-Aggregator (Yearn-like)
- **Scale:** Auto-yield strategies.
- **Core features:** Strategy switch, perf reporting.
- **Hard parts:** Strategy risk, gas-cost optimization.
- **Discuss:** Strategist whitelist.

### 651. Design a Crypto Payment Processor (BitPay)
- **Scale:** Merchant accept crypto.
- **Core features:** Quote, invoice, settle to fiat.
- **Hard parts:** Volatility hedging, refund.
- **Discuss:** Hedge to USD instantly.

### 652. Design an Oracle Network (Chainlink)
- **Scale:** Off-chain → on-chain.
- **Core features:** Aggregate sources, on-chain delivery.
- **Hard parts:** Source quality, attack resistance.
- **Discuss:** Median + reputation.

### 653. Design a Decentralized Rollup Sequencer
- **Scale:** L2 sequencer.
- **Core features:** Order tx, post to L1, fraud-proof.
- **Hard parts:** Censorship resistance, fee market.
- **Discuss:** Shared sequencer designs.

### 654. Design a Tokenized RWA (Real-World Asset) Platform
- **Scale:** Tokenize bonds/real estate.
- **Core features:** Onboarding, mint, transfer with KYC.
- **Hard parts:** Compliance overlay, allowlists.
- **Discuss:** ERC-3643.

### 655. Design a Non-Custodial Mobile Wallet (MetaMask Mobile)
- **Scale:** End-user wallet.
- **Core features:** Manage keys, dapp browser, swaps.
- **Hard parts:** Secure key storage, phishing.
- **Discuss:** Secure enclave usage.

### 656. Design a CryptoTwitter Streaming Bot Platform
- **Scale:** Track addresses + alerts.
- **Core features:** Watch lists, alerts on tx, post.
- **Hard parts:** Reorg correctness, dedupe.
- **Discuss:** Confirmations vs speed.

### 657. Design a Crypto Insurance Market (Nexus Mutual)
- **Scale:** Cover smart-contract risk.
- **Core features:** Cover purchase, claim assess.
- **Hard parts:** Claim assessment governance.
- **Discuss:** Mutual + governance token model.

### 658. Design a DAO Treasury Mgmt
- **Scale:** Multisig + budgeting.
- **Core features:** Multisig, payment streams, budgets.
- **Hard parts:** Approval workflow on-chain.
- **Discuss:** Safe + Zodiac modules.

### 659. Design a Privacy Mixer / Coin-Join (Privacy considerations)
- **Scale:** Privacy by mixing.
- **Core features:** Pool deposits, mix, withdraw.
- **Hard parts:** Compliance, anonymity-set size.
- **Discuss:** ZK-based mixers.

### 660. Design a Layer-2 ZK-Rollup Prover
- **Scale:** Proof generation.
- **Core features:** Witness, prove, batch.
- **Hard parts:** Prover resource, parallelism.
- **Discuss:** Recursive proofs.

# 🎲 40. Gambling & Sports Betting

### 661. Design an Online Casino Backend
- **Scale:** Many simultaneous players.
- **Core features:** Slot/poker/blackjack engines, RNG, payouts.
- **Hard parts:** Provably fair, regulatory audits.
- **Discuss:** RNG cert; per-jurisdiction.

### 662. Design a Sports-Betting Trading Engine
- **Scale:** Real-time odds management.
- **Core features:** Open lines, accept bets, hedge.
- **Hard parts:** Odds compiler, in-play data feed.
- **Discuss:** Risk-managed line moves.

### 663. Design an In-Play Bet Acceptance System
- **Scale:** Live game betting.
- **Core features:** Sub-second odds + accept.
- **Hard parts:** Latency, market suspend, auto-settle.
- **Discuss:** Pause windows on event.

### 664. Design a Daily Fantasy Sports Engine (DraftKings DFS)
- **Scale:** Contest entry to payout.
- **Core features:** Lineup, salary cap, scoring.
- **Hard parts:** Late swap, scoring lag.
- **Discuss:** Per-game scoring pipeline.

### 665. Design a Tote / Pari-Mutuel System (Horse racing)
- **Scale:** Pool wagering.
- **Core features:** Pool by track, combine pools, dividends.
- **Hard parts:** Hub host integration.
- **Discuss:** Inter-tote pooling.

### 666. Design a Bingo / Lottery Platform
- **Scale:** State lottery scale.
- **Core features:** Tickets, draws, claims.
- **Hard parts:** RNG integrity, claim verification.
- **Discuss:** Sealed-randomness commit/reveal.

### 667. Design a Poker Tournament Engine
- **Scale:** MTT with thousands.
- **Core features:** Seating, blinds up, balance tables.
- **Hard parts:** Player movement, anti-collusion.
- **Discuss:** Table-balance algorithm.

### 668. Design a Cashout / Bet-Edit System
- **Scale:** Live partial cashout.
- **Core features:** Real-time price for cashout.
- **Hard parts:** Risk-managed offer, sub-second.
- **Discuss:** Pricing engine throughput.

### 669. Design a Responsible-Gambling Self-Exclusion
- **Scale:** Multi-operator registries.
- **Core features:** Register, share between operators, enforce.
- **Hard parts:** Cross-operator data, identity matching.
- **Discuss:** State registry integration.

### 670. Design a KYC / Geo-Compliance Engine for Betting
- **Scale:** Per-state compliance.
- **Core features:** Geofence, KYC docs, age check.
- **Hard parts:** Geofence accuracy, VPN detection.
- **Discuss:** Wi-Fi triangulation.

### 671. Design a Slot-Game Math / RTP Engine
- **Scale:** Game RNG + math model.
- **Core features:** Reels, paylines, RTP.
- **Hard parts:** Cert by labs (GLI), seed mgmt.
- **Discuss:** Seed and audit.

### 672. Design a Live-Dealer Casino Platform
- **Scale:** Live blackjack/roulette.
- **Core features:** Video stream, side-bet timing.
- **Hard parts:** Latency, dealer station UI.
- **Discuss:** Bet-window timing.

### 673. Design a Bonus / Wagering-Requirement Engine
- **Scale:** Promotions across products.
- **Core features:** Issue bonus, track wagering.
- **Hard parts:** Game weighting, abuse.
- **Discuss:** Wagering ledger.

### 674. Design a Sports Data-Feed Aggregator
- **Scale:** Multiple data providers.
- **Core features:** Normalize feeds, fill gaps.
- **Hard parts:** Conflicting data, latency.
- **Discuss:** Source rank + arbitration.

### 675. Design a Bet-Surveillance / Suspicious Pattern Detector
- **Scale:** Integrity monitoring.
- **Core features:** Bet pattern anomaly, alert.
- **Hard parts:** Cross-account collusion.
- **Discuss:** Graph DB for accounts.

### 676. Design an Esports-Betting Live Lines
- **Scale:** Game-data based.
- **Core features:** Lines from in-game state.
- **Hard parts:** Game-API access, latency.
- **Discuss:** Direct game-data feeds.

### 677. Design a Loyalty/VIP Program for Casino
- **Scale:** Comp points, tiers.
- **Core features:** Earn, comp, tier.
- **Hard parts:** Player value calc.
- **Discuss:** Theoretical hold.

### 678. Design a Free-to-Play Coin Game (Slotomania)
- **Scale:** F2P scale.
- **Core features:** Coins, IAP, daily bonus.
- **Hard parts:** Anti-cheat, loot-box compliance.
- **Discuss:** F2P monetization.

### 679. Design a Sweepstakes / Promo Sports Platform
- **Scale:** US states with sweepstakes model.
- **Core features:** Coins, prize redemption, AMOE.
- **Hard parts:** Compliance per state.
- **Discuss:** AMOE flow.

### 680. Design a Sports Player-Prop Pricing Engine
- **Scale:** Markets per player.
- **Core features:** Models per stat, lines, hedging.
- **Hard parts:** Model accuracy, in-play update.
- **Discuss:** Model-driven price moves.

# 📦 41. Subscription Commerce

### 681. Design a Subscription Box (Birchbox)
- **Scale:** Curated monthly box.
- **Core features:** Sub plan, curation, ship.
- **Hard parts:** Curation engine, inventory matching.
- **Discuss:** Per-cycle inventory.

### 682. Design a Curated Wine-Subscription
- **Scale:** Age-verified.
- **Core features:** Profile, recommend, ship.
- **Hard parts:** State alcohol law.
- **Discuss:** Age-verify at delivery.

### 683. Design a Diaper Auto-Replenish Service
- **Scale:** Predict timing.
- **Core features:** Recurring ship by predicted use.
- **Hard parts:** Use-rate estimation.
- **Discuss:** Pause + accelerate UI.

### 684. Design a Pet-Food Subscription (Chewy AutoShip)
- **Scale:** Pet-specific cadence.
- **Core features:** Per-pet schedule, dosing.
- **Hard parts:** Switching food, vet Rx.
- **Discuss:** Vet-Rx flow.

### 685. Design a Razor Subscription (Dollar Shave Club)
- **Scale:** Quarterly ship.
- **Core features:** Plan, swap, skip.
- **Hard parts:** SKU-level cadence.
- **Discuss:** Plan flexibility UI.

### 686. Design a Magazine + Newspaper Sub Mgmt
- **Scale:** Print + digital combo.
- **Core features:** Print logistics, digital access.
- **Hard parts:** Issue-based vs date-based.
- **Discuss:** Subscription state machine.

### 687. Design a Subscription Pause / Resume Engine
- **Scale:** Common across services.
- **Core features:** Pause, resume, refund proration.
- **Hard parts:** Proration math.
- **Discuss:** Per-cycle accounting.

### 688. Design a Family Plan / Sharing for Subscriptions
- **Scale:** Apple Family-style.
- **Core features:** Share with family, individual profiles.
- **Hard parts:** Entitlement per-member, privacy.
- **Discuss:** Group entitlements.

### 689. Design a Subscription Analytics for SaaS (ChartMogul)
- **Scale:** MRR, churn, LTV.
- **Core features:** Cohort, churn, expansion.
- **Hard parts:** Multi-currency MRR, accuracy.
- **Discuss:** Movement classification.

### 690. Design an Annual Plan + Monthly Plan Switcher
- **Scale:** Plan upgrade/downgrade.
- **Core features:** Switch with proration.
- **Hard parts:** Refund vs credit logic.
- **Discuss:** Pricing models.

### 691. Design a Free-Trial Conversion System
- **Scale:** Trial → paid.
- **Core features:** Trial limits, reminders, conversion.
- **Hard parts:** Anti-multi-trial fraud.
- **Discuss:** Identity-binding.

### 692. Design a Coupon/Promo for Subscriptions
- **Scale:** Promo on first N months.
- **Core features:** Apply, expire, stacking.
- **Hard parts:** Long-tail promo evaluations.
- **Discuss:** Expire-time logic.

### 693. Design an In-App Subscription Receipt Validator (Apple/Google)
- **Scale:** Server-side validation.
- **Core features:** Verify, hold entitlement.
- **Hard parts:** S2S notifications, refunds.
- **Discuss:** Subscription notifications.

### 694. Design an Add-On / Modular Subscription
- **Scale:** Base + add-on bundles.
- **Core features:** Compose plans.
- **Hard parts:** Add-on dependency rules.
- **Discuss:** Plan-template engine.

### 695. Design a Win-Back / Churn-Save Workflow
- **Scale:** Cancellation funnel.
- **Core features:** Offers, downgrade, save.
- **Hard parts:** Offer eligibility logic.
- **Discuss:** Eligibility rules.

### 696. Design a Subscription Notifications & Reminders Service
- **Scale:** Renewal/expire reminders.
- **Core features:** Multi-channel, opt-out.
- **Hard parts:** Time-zone correctness.
- **Discuss:** Cron-style scheduler.

### 697. Design a Gift-Subscription Flow
- **Scale:** Gift purchase, recipient claim.
- **Core features:** Claim code, redeem.
- **Hard parts:** Tax in destination.
- **Discuss:** Tax-engine swap.

### 698. Design a Recurring Donations Platform
- **Scale:** Nonprofit donations.
- **Core features:** Recurring schedule, tax receipt.
- **Hard parts:** Failed-donation retry.
- **Discuss:** Dunning management.

### 699. Design a Subscription Reseller / Aggregator Platform
- **Scale:** Bundle multiple subs.
- **Core features:** Bundle, billing, portion to providers.
- **Hard parts:** Revenue share, provisioning APIs.
- **Discuss:** Per-provider connectors.

### 700. Design an Auto-Renewal Cancellation Compliance (US/EU laws)
- **Scale:** Cancel-online laws.
- **Core features:** Show cancel, deletion.
- **Hard parts:** State-by-state requirements.
- **Discuss:** Per-jurisdiction UX.

# 🐾 42. Pet & Animal Tech

### 701. Design a Pet-Adoption Platform (Petfinder)
- **Scale:** Multi-shelter.
- **Core features:** Browse pets, application, communication.
- **Hard parts:** Shelter data sync, multi-source.
- **Discuss:** Shelter-API standardization.

### 702. Design a Veterinary Telehealth (Pawp)
- **Scale:** On-demand video.
- **Core features:** Video, e-prescription.
- **Hard parts:** Vet licensing per state.
- **Discuss:** Per-state routing.

### 703. Design a Lost-Pet Alert Network (Nextdoor for pets)
- **Scale:** Geo-based alerts.
- **Core features:** Report lost, notify nearby, scan tag.
- **Hard parts:** Geo-radius alerts, privacy.
- **Discuss:** Alert fanout strategy.

### 704. Design a Pet Microchip Registry
- **Scale:** National registry.
- **Core features:** Register chip, lookup, transfer ownership.
- **Hard parts:** Multi-vendor mapping, ownership transfer.
- **Discuss:** Universal lookup portal.

### 705. Design a Dog-Walker / Pet-Sitter Marketplace (Rover)
- **Scale:** Two-sided market.
- **Core features:** Search, book, GPS walk.
- **Hard parts:** Insurance, background check.
- **Discuss:** Trust + safety.

### 706. Design a Dog-Park Reservation System
- **Scale:** City-wide.
- **Core features:** Reserve slot, check-in.
- **Hard parts:** Capacity, no-show.
- **Discuss:** Slot scheduling.

### 707. Design a Pet GPS-Tracker Service (Fi)
- **Scale:** Connected collar.
- **Core features:** Live track, escape alert, activity.
- **Hard parts:** Battery vs GPS frequency.
- **Discuss:** Location-update cadence.

### 708. Design a Pet-Insurance Claim Mobile App
- **Scale:** Per-member claims.
- **Core features:** Upload vet bill, OCR, pay.
- **Hard parts:** Vet integration.
- **Discuss:** OCR accuracy.

### 709. Design a Veterinary Practice Management SaaS
- **Scale:** Multi-practice.
- **Core features:** Appointments, charts, billing.
- **Hard parts:** Multi-pet households, recall sweeps.
- **Discuss:** Recall scheduling.

### 710. Design a Pet Diet / Nutrition Recommender
- **Scale:** Per-pet plans.
- **Core features:** Profile, meal plan, ship food.
- **Hard parts:** Breed/age/condition profiles.
- **Discuss:** Recommender system.

### 711. Design a Cat / Dog DNA Test Pipeline
- **Scale:** Saliva sample → results.
- **Core features:** Order, lab, results portal.
- **Hard parts:** Lab pipeline, breed reference DB.
- **Discuss:** Lab-results turnaround.

### 712. Design a Wildlife-Tracking Citizen-Science App (iNaturalist)
- **Scale:** Crowdsourced sightings.
- **Core features:** Submit observation, ID, expert verify.
- **Hard parts:** Location obfuscation for endangered species.
- **Discuss:** Community-driven ID.

### 713. Design a Pet Vaccination Reminder Service
- **Scale:** Yearly + boosters.
- **Core features:** Vet sync, remind, book.
- **Hard parts:** Multi-vet histories.
- **Discuss:** Reminder cadence.

### 714. Design a Smart Cat-Feeder / Camera Service
- **Scale:** Connected device.
- **Core features:** Schedule feed, camera, alerts.
- **Hard parts:** Device offline, video CDN.
- **Discuss:** Edge buffer + cloud.

### 715. Design a Service-Animal Certification Registry
- **Scale:** Service-dog registry.
- **Core features:** Cert, public verify, expiry.
- **Hard parts:** Anti-fraud cert, ADA scope.
- **Discuss:** Cert verification API.

# 🧩 43. Sub-systems & Components

### 716. Design a Distributed Job Scheduler (Quartz / Sidekiq)
- **Scale:** Millions of scheduled jobs.
- **Core features:** Cron-style, idempotent retries.
- **Hard parts:** Leader election, time accuracy.
- **Discuss:** Per-shard leader.

### 717. Design a Distributed Cron / Workflow Engine (Airflow / Argo)
- **Scale:** Thousands of DAGs.
- **Core features:** DAG schedule, retry, sensors.
- **Hard parts:** DAG-state mgmt, fan-out.
- **Discuss:** Worker orchestration.

### 718. Design a Distributed Task Queue (Celery)
- **Scale:** Async task processing.
- **Core features:** Enqueue, worker, retry.
- **Hard parts:** Visibility timeout, poison pill.
- **Discuss:** Queue brokers.

### 719. Design a Pub/Sub System (Google Pub/Sub-style)
- **Scale:** 100M msg/s.
- **Core features:** Topic, sub, ack.
- **Hard parts:** Ordering, exactly-once.
- **Discuss:** Per-subscription state.

### 720. Design a Distributed File-System Client (HDFS-style)
- **Scale:** Multi-PB.
- **Core features:** Read/write blocks, replication.
- **Hard parts:** NameNode scale, block placement.
- **Discuss:** Federation.

### 721. Design a Block Storage System (EBS-style)
- **Scale:** Per-instance volumes.
- **Core features:** Attach, snapshot, replicate.
- **Hard parts:** Latency, durability.
- **Discuss:** Chained replication.

### 722. Design a Distributed Lock Service
- **Scale:** Cluster-wide.
- **Core features:** Acquire/release with TTL.
- **Hard parts:** Fencing tokens, lease.
- **Discuss:** Chubby-style.

### 723. Design a Distributed Configuration Service
- **Scale:** Cluster KV.
- **Core features:** Get/Put, watch.
- **Hard parts:** Strong consistency, watch reliability.
- **Discuss:** etcd vs ZK.

### 724. Design a Service Discovery System
- **Scale:** Microservice cluster.
- **Core features:** Register, lookup, health.
- **Hard parts:** Heartbeat scale, eviction.
- **Discuss:** Push vs pull.

### 725. Design a Distributed Counter Service
- **Scale:** Million inc/sec.
- **Core features:** Inc/get with consistency.
- **Hard parts:** Hot keys, sharded counters.
- **Discuss:** CRDT vs sharded.

### 726. Design a Bloom-Filter Service for Existence Checks
- **Scale:** Big-data dedup.
- **Core features:** Add, check.
- **Hard parts:** False-positive rate, scaling.
- **Discuss:** Counting Bloom filter.

### 727. Design a Distributed Bloom Filter for Web Crawler
- **Scale:** Crawl frontier.
- **Core features:** Was URL crawled.
- **Hard parts:** False-positive impact.
- **Discuss:** Scaled BF cluster.

### 728. Design a Distributed Top-K (Heavy Hitter)
- **Scale:** Stream of events.
- **Core features:** Real-time top-K.
- **Hard parts:** Memory, accuracy.
- **Discuss:** Count-Min sketch.

### 729. Design a Distributed Cardinality Estimator (HLL)
- **Scale:** Unique counts at billions.
- **Core features:** Add, estimate.
- **Hard parts:** Memory vs accuracy.
- **Discuss:** HyperLogLog++.

### 730. Design a Distributed Quantile Estimator (t-digest)
- **Scale:** p99/p99.9 latency.
- **Core features:** Add, query quantiles.
- **Hard parts:** Aggregating across nodes.
- **Discuss:** Mergeability.

### 731. Design a Geohash / Quadtree Service
- **Scale:** Spatial indexing.
- **Core features:** Insert location, query nearby.
- **Hard parts:** Skewed density.
- **Discuss:** S2 vs H3 vs Geohash.

### 732. Design a Time-Series Database (InfluxDB-like)
- **Scale:** Billions of points/sec.
- **Core features:** Write/query.
- **Hard parts:** Compression, downsampling.
- **Discuss:** TSI + TSM file format.

### 733. Design a Wide-Column DB (HBase)
- **Scale:** Petabyte rows.
- **Core features:** Region, column family.
- **Hard parts:** Region splits, compactions.
- **Discuss:** LSM details.

### 734. Design an Embedded KV Store (RocksDB)
- **Scale:** SST + memtable.
- **Core features:** Put/Get/Delete, iterator.
- **Hard parts:** Compaction styles, tuning.
- **Discuss:** Level vs universal compaction.

### 735. Design a Search Index (Lucene Internals)
- **Scale:** Inverted index.
- **Core features:** Index, query, scoring.
- **Hard parts:** Segment merging, refresh.
- **Discuss:** Near-real-time index.

### 736. Design a Vector Index (HNSW)
- **Scale:** Billion vectors.
- **Core features:** Insert, k-NN.
- **Hard parts:** Memory, recall vs latency.
- **Discuss:** Quantization.

### 737. Design a Distributed Tracing Span Collector
- **Scale:** Billions of spans.
- **Core features:** Receive, sample, store.
- **Hard parts:** Tail-sampling, span volume.
- **Discuss:** OTel collector.

### 738. Design a Metrics Time-Aggregation Service
- **Scale:** Roll-up at ingest.
- **Core features:** 1m/5m/1h aggs.
- **Hard parts:** Late-arriving, downsampling.
- **Discuss:** Pre-agg vs query-time.

### 739. Design a Log-Streaming Tail Service (kubectl logs)
- **Scale:** Real-time tail.
- **Core features:** Subscribe, follow.
- **Hard parts:** Buffering, multiplex.
- **Discuss:** Server-sent events.

### 740. Design a Heartbeat / Failure Detector
- **Scale:** Cluster fault detection.
- **Core features:** Heartbeat, suspect.
- **Hard parts:** Adaptive timeouts (Phi-detector).
- **Discuss:** Phi accrual.

### 741. Design an SSE / Server-Sent Events Service
- **Scale:** Push live updates.
- **Core features:** Subscribe channel, stream.
- **Hard parts:** Reconnects, ordering.
- **Discuss:** Last-Event-ID.

### 742. Design a WebSocket Gateway at Scale
- **Scale:** 10M+ connections.
- **Core features:** Connect, auth, route.
- **Hard parts:** Per-instance limits, drain.
- **Discuss:** Session affinity.

### 743. Design an HTTP/2 Reverse Proxy
- **Scale:** Edge router.
- **Core features:** Multiplex, route, retry.
- **Hard parts:** Connection reuse, head-of-line.
- **Discuss:** Multiplexing.

### 744. Design an Anycast Routing System
- **Scale:** Edge + DNS.
- **Core features:** Same IP, nearest-point routing.
- **Hard parts:** BGP, failover.
- **Discuss:** PoP design.

### 745. Design a Mutable URL Rewriter at Edge
- **Scale:** Cloudflare Workers.
- **Core features:** Modify request/response.
- **Hard parts:** Cold start, stateful workers.
- **Discuss:** WASM at edge.

### 746. Design an Image-Transform CDN (Cloudinary)
- **Scale:** On-the-fly resize.
- **Core features:** URL params → transform.
- **Hard parts:** Origin cache, transform cache.
- **Discuss:** Variant key strategy.

### 747. Design a Live-Tail Log-Search at Edge
- **Scale:** Real-time grep.
- **Core features:** Query, stream.
- **Hard parts:** Pushdown predicates.
- **Discuss:** Edge-shard architecture.

### 748. Design a Low-Cardinality Sampling for Metrics
- **Scale:** Bound cardinality.
- **Core features:** Drop high-cardinality, alert.
- **Hard parts:** Detection, dropping rules.
- **Discuss:** Adaptive cardinality limit.

### 749. Design a Uniform Token Bucket Service (Stripe-like)
- **Scale:** Rate-limit per key.
- **Core features:** Allow/deny, refill.
- **Hard parts:** Distributed bucket sync.
- **Discuss:** Redis Lua atomic ops.

### 750. Design a Sliding Window Counter Service
- **Scale:** Per-key rate.
- **Core features:** Window-based count.
- **Hard parts:** Sub-window precision.
- **Discuss:** Bucketed sliding window.

### 751. Design a Distributed Lock with Lease + Fence
- **Scale:** Critical-section coordination.
- **Core features:** Acquire with TTL + fence token.
- **Hard parts:** Lease renewal, drift.
- **Discuss:** Fence token semantics.

### 752. Design a Saga Coordinator
- **Scale:** Long-running multi-step txn.
- **Core features:** Steps, compensations.
- **Hard parts:** Idempotency, retries.
- **Discuss:** Choreography vs orchestration.

### 753. Design a Workflow Engine (Temporal/Cadence)
- **Scale:** Stateful long-running.
- **Core features:** Activities, timers, signals.
- **Hard parts:** Determinism, history compaction.
- **Discuss:** Event-sourced workflow.

### 754. Design an Outbox Relay for Microservice Events
- **Scale:** Reliable publish.
- **Core features:** Outbox table, relay to bus.
- **Hard parts:** Transactional consistency.
- **Discuss:** CDC vs polling.

### 755. Design a Database CDC (Debezium-style)
- **Scale:** Stream DB changes.
- **Core features:** Capture WAL, stream.
- **Hard parts:** Schema evolution, snapshot.
- **Discuss:** Per-source connectors.

### 756. Design a Schema Registry
- **Scale:** Centralized schema mgmt.
- **Core features:** Register, evolve, validate.
- **Hard parts:** Compatibility checks.
- **Discuss:** Avro/Protobuf.

### 757. Design a Service-Mesh Control Plane
- **Scale:** xDS configs.
- **Core features:** Distribute config to sidecars.
- **Hard parts:** Push reliability, scale.
- **Discuss:** xDS streaming.

### 758. Design a Sidecar Proxy (Envoy)
- **Scale:** Per-pod proxy.
- **Core features:** Routing, retries, mTLS.
- **Hard parts:** Hot reload, perf.
- **Discuss:** xDS subscription.

### 759. Design a Container Runtime (containerd)
- **Scale:** Run containers.
- **Core features:** Pull, run, isolate.
- **Hard parts:** Image distribution, OCI.
- **Discuss:** Snapshotter.

### 760. Design an Image Signing & Verification (cosign)
- **Scale:** Supply-chain.
- **Core features:** Sign, verify.
- **Hard parts:** Key rotation, sigstore.
- **Discuss:** Sigstore transparency log.

### 761. Design a Software-Bill-of-Materials (SBOM) Service
- **Scale:** Org-wide SBOMs.
- **Core features:** Generate, attest, query vulns.
- **Hard parts:** SBOM correctness, vuln matching.
- **Discuss:** SPDX/CycloneDX.

### 762. Design a Vulnerability-Scanning Service
- **Scale:** Image + code.
- **Core features:** Scan, report, gate.
- **Hard parts:** False-positive triage, fix-tracking.
- **Discuss:** SCA + SAST + DAST.

### 763. Design an Internal Service-Catalog (Backstage)
- **Scale:** Org-wide service inventory.
- **Core features:** Register, owner, dependencies.
- **Hard parts:** Auto-discovery, freshness.
- **Discuss:** Source-of-truth.

### 764. Design a Feature-Flag Eval Engine at Edge
- **Scale:** Sub-50ms eval.
- **Core features:** Targeting rules, percentage rollout.
- **Hard parts:** Real-time updates, offline SDK.
- **Discuss:** Eval offline; SSE updates.

### 765. Design a Secrets Vault Embedded SDK
- **Scale:** Per-app secret access.
- **Core features:** Fetch, cache, rotate.
- **Hard parts:** Auth to vault, leases.
- **Discuss:** Workload identity.

### 766. Design a Distributed Trace-ID Propagation
- **Scale:** Across services.
- **Core features:** Inject/extract trace headers.
- **Hard parts:** Cross-language consistency.
- **Discuss:** W3C Trace Context.

### 767. Design an Async Cancellation / Context Propagation
- **Scale:** Cross-service cancel.
- **Core features:** Cancel signal across services.
- **Hard parts:** RPC support, propagation.
- **Discuss:** gRPC deadline propagation.

### 768. Design an Event-Bus for Microservices
- **Scale:** Service-to-service events.
- **Core features:** Publish/subscribe with delivery guarantees.
- **Hard parts:** Ordering, replay.
- **Discuss:** Kafka vs SNS+SQS.

### 769. Design a Job-Result Cache
- **Scale:** Memoize idempotent jobs.
- **Core features:** Lookup result by input hash.
- **Hard parts:** Cache invalidation, size.
- **Discuss:** TTL + LRU.

### 770. Design a Multi-Tenant API Quota / Throttle Service
- **Scale:** Per-tenant rate.
- **Core features:** Define plans, enforce.
- **Hard parts:** Plan changes, real-time throttle.
- **Discuss:** Token bucket per tenant.

# 🤖 44. AI / LLM / Generative

### 771. Design a Multi-Tenant LLM Inference Cluster
- **Scale:** GPU cluster, 1000s of clients.
- **Core features:** Routing, batching, billing.
- **Hard parts:** GPU cost, hot-pool.
- **Discuss:** Continuous batching.

### 772. Design a RAG (Retrieval-Augmented Gen) System
- **Scale:** 1B documents.
- **Core features:** Ingest, chunk, embed, retrieve, gen.
- **Hard parts:** Chunk quality, recall vs precision.
- **Discuss:** Hybrid search; rerankers.

### 773. Design a Prompt-Management / Prompt Library
- **Scale:** Org-wide prompt versioning.
- **Core features:** CRUD prompt, variables, eval.
- **Hard parts:** Versioning, A/B, eval.
- **Discuss:** Eval harness.

### 774. Design an LLM Eval Harness
- **Scale:** Continuous eval.
- **Core features:** Datasets, run LLM, metrics.
- **Hard parts:** Reference-free metrics, cost.
- **Discuss:** Pairwise eval.

### 775. Design a Fine-Tuning Platform
- **Scale:** Multi-tenant fine-tunes.
- **Core features:** Upload data, train, deploy.
- **Hard parts:** GPU sched, data privacy.
- **Discuss:** LoRA vs full FT.

### 776. Design an Embeddings-as-a-Service
- **Scale:** Embedding API.
- **Core features:** Embed text, batch.
- **Hard parts:** Model versioning.
- **Discuss:** Tokenization caching.

### 777. Design a Vector-Search Service Backend
- **Scale:** Multi-collection.
- **Core features:** Insert, ANN, filter.
- **Hard parts:** Hybrid filter+ANN.
- **Discuss:** Filterable HNSW.

### 778. Design an Image-Generation Service (DALL-E/Midjourney)
- **Scale:** Async generation.
- **Core features:** Prompt → image, gallery.
- **Hard parts:** GPU sched, safety, NSFW.
- **Discuss:** Queue + worker.

### 779. Design an AI Code-Completion Backend (Copilot)
- **Scale:** Editor-side completions.
- **Core features:** Sub-second, contextual.
- **Hard parts:** Latency, multi-language.
- **Discuss:** Edge inference.

### 780. Design a Conversational Agent / Chatbot Platform
- **Scale:** Multi-bot.
- **Core features:** Memory, tools, channel routing.
- **Hard parts:** Memory mgmt, hallucination.
- **Discuss:** Tool-call orchestration.

### 781. Design a Multi-Modal Search (Text + Image)
- **Scale:** CLIP-style.
- **Core features:** Text → image / image → text.
- **Hard parts:** Multimodal embeddings, alignment.
- **Discuss:** Joint embedding space.

### 782. Design a Voice-Bot for Customer Service
- **Scale:** Phone IVR replacement.
- **Core features:** ASR + LLM + TTS.
- **Hard parts:** Latency, barge-in.
- **Discuss:** Streaming pipeline.

### 783. Design a Live Audio Translation Service
- **Scale:** Bilingual conversation.
- **Core features:** Real-time TTS in target lang.
- **Hard parts:** Latency, voice cloning.
- **Discuss:** Streaming ASR + MT + TTS.

### 784. Design a Video-Summarization Service
- **Scale:** Long videos.
- **Core features:** Transcribe, key moments.
- **Hard parts:** Long-context, multi-modal.
- **Discuss:** Chapter detection.

### 785. Design an LLM Safety / Moderation Layer
- **Scale:** Input + output filtering.
- **Core features:** Toxic detect, PII redact.
- **Hard parts:** False-positive on edge cases.
- **Discuss:** Multi-model approach.

### 786. Design an LLM Caching Layer (Semantic Cache)
- **Scale:** Cache by similarity.
- **Core features:** Lookup near-duplicate prompts.
- **Hard parts:** Cache key by embedding.
- **Discuss:** Vector + LRU.

### 787. Design an LLM Routing Layer (RouteLLM)
- **Scale:** Multi-model deployments.
- **Core features:** Pick model by cost/quality.
- **Hard parts:** Decision criterion, fallback.
- **Discuss:** Bandit-driven routing.

### 788. Design an Agentic Workflow Orchestrator
- **Scale:** Multi-step LLM agents.
- **Core features:** Plan, tool use, retries.
- **Hard parts:** Looping, halting, cost cap.
- **Discuss:** Reflection patterns.

### 789. Design a Document AI / OCR + Layout Pipeline
- **Scale:** Forms, invoices, IDs.
- **Core features:** Extract structured data.
- **Hard parts:** Layout-aware models.
- **Discuss:** LayoutLM-style.

### 790. Design an LLM-Driven Search Reformulator
- **Scale:** Query rewriting.
- **Core features:** Rewrite, expand, suggest.
- **Hard parts:** Latency, accuracy.
- **Discuss:** Caching reformulations.

### 791. Design an LLM Application Telemetry / LangFuse
- **Scale:** Trace LLM apps.
- **Core features:** Capture prompts, costs, eval.
- **Hard parts:** Token-cost attribution.
- **Discuss:** Trace export pipeline.

### 792. Design a Personal-AI Memory Service
- **Scale:** Per-user knowledge graph.
- **Core features:** Long-term memory across chats.
- **Hard parts:** Privacy, retrieval over time.
- **Discuss:** Episodic vs semantic memory.

### 793. Design a Multi-Agent Coordination Platform (CrewAI)
- **Scale:** Agents collaborating.
- **Core features:** Roles, message bus.
- **Hard parts:** Termination, role definition.
- **Discuss:** Coordinator patterns.

### 794. Design an AI Model-Marketplace (Hugging Face Hub)
- **Scale:** 1M+ models.
- **Core features:** Upload, host, inference API.
- **Hard parts:** Hosting cost, license tracking.
- **Discuss:** Inference endpoints.

### 795. Design a Synthetic Data-Generation Pipeline
- **Scale:** Generate training data.
- **Core features:** Templates, perturbation.
- **Hard parts:** Quality, distribution.
- **Discuss:** Eval on synthetic.

### 796. Design an LLM Prompt-Injection Defense Layer
- **Scale:** Pre + post filter.
- **Core features:** Detect injection, sandbox tools.
- **Hard parts:** Adversarial bypasses.
- **Discuss:** Tool sandboxing.

### 797. Design a Continuous-Eval / Drift Monitor for ML
- **Scale:** Production models.
- **Core features:** Distribution shift detection, alert.
- **Hard parts:** Label-free drift, ground truth lag.
- **Discuss:** PSI, KL divergence.

### 798. Design a Reinforcement-Learning From Human Feedback Pipeline
- **Scale:** Annotate + train.
- **Core features:** Pairwise compare, reward model.
- **Hard parts:** Label quality, tie-breaking.
- **Discuss:** RLHF/DPO.

### 799. Design a Feature-Importance Explainability Service
- **Scale:** Per-prediction SHAP.
- **Core features:** Explanations, attribution.
- **Hard parts:** Cost of SHAP at scale.
- **Discuss:** Approximate methods.

### 800. Design an AI Code-Review Bot
- **Scale:** PR-time review.
- **Core features:** Read diff, suggest, comment.
- **Hard parts:** Hallucination, scope.
- **Discuss:** Per-file context windows.

### 801. Design an AI Document-Translation Pipeline (Document level)
- **Scale:** Whole-doc translation.
- **Core features:** Preserve formatting.
- **Hard parts:** Layout reflow, terminology.
- **Discuss:** Glossary injection.

### 802. Design an Image Background-Removal API (remove.bg)
- **Scale:** Online API.
- **Core features:** Upload, mask, output.
- **Hard parts:** Hair edges, model size.
- **Discuss:** Edge ML.

### 803. Design a Real-time Speaker-Diarization Service
- **Scale:** Multi-speaker call.
- **Core features:** Identify speakers, label segments.
- **Hard parts:** Latency, overlap.
- **Discuss:** Embedding clustering.

### 804. Design an LLM-Powered Internal Search (Glean)
- **Scale:** Enterprise.
- **Core features:** Search across SaaS, permission-aware.
- **Hard parts:** Permission propagation, freshness.
- **Discuss:** Per-source connectors.

### 805. Design an AI Meeting Notes Assistant (Otter)
- **Scale:** Calendar-integrated.
- **Core features:** Join, transcribe, summarize.
- **Hard parts:** Multi-meeting platform, accuracy.
- **Discuss:** Bot-as-attendee.

### 806. Design an AI Photo-Editing Magic Eraser
- **Scale:** On-device + cloud.
- **Core features:** Mask, inpaint.
- **Hard parts:** Edge inference.
- **Discuss:** Hybrid edge/cloud.

### 807. Design an LLM Agent for Data-Analysis (ChatGPT Code Interpreter)
- **Scale:** Sandboxed code exec.
- **Core features:** Generate code, run, return.
- **Hard parts:** Sandbox safety, file IO.
- **Discuss:** Container per session.

### 808. Design an On-Device LLM Inference Runtime
- **Scale:** Mobile + laptop.
- **Core features:** Quantized models, KV cache.
- **Hard parts:** Battery, memory.
- **Discuss:** GGUF/MLX.

### 809. Design an LLM Streaming-Response Server
- **Scale:** Token streaming.
- **Core features:** SSE/WebSocket stream.
- **Hard parts:** Backpressure, cancellation.
- **Discuss:** SSE vs WebSocket.

### 810. Design an LLM Multi-Region Failover
- **Scale:** Cross-region resilience.
- **Core features:** Region-prefer, fallback.
- **Hard parts:** State, model parity.
- **Discuss:** Active-active inference.

# 🛠️ 45. Internal Dev Tools

### 811. Design a Code-Search at Org Scale (Sourcegraph)
- **Scale:** Billions of LOC.
- **Core features:** Symbol/regex search, code intelligence.
- **Hard parts:** Indexing speed, freshness.
- **Discuss:** Trigram + LSIF.

### 812. Design a Build System (Bazel)
- **Scale:** Hermetic builds.
- **Core features:** Action graph, cache, remote.
- **Hard parts:** Hermeticity, dynamic deps.
- **Discuss:** Action hashing.

### 813. Design a Monorepo-Build Cache Server
- **Scale:** Org-wide cache.
- **Core features:** Cache lookup, RBE.
- **Hard parts:** Hash determinism.
- **Discuss:** Cas + AC.

### 814. Design a Code-Review System (Gerrit/Phabricator)
- **Scale:** Org-wide.
- **Core features:** Patch review, approve, merge.
- **Hard parts:** Stacked diffs.
- **Discuss:** Stacked-diff workflows.

### 815. Design an Internal Slack-Bot Platform
- **Scale:** Org bots.
- **Core features:** Build bots, slash commands.
- **Hard parts:** Permission scopes, rate limits.
- **Discuss:** Bot framework.

### 816. Design an Engineering Metrics (DORA) Tracker
- **Scale:** Per-team metrics.
- **Core features:** Lead time, deploy freq.
- **Hard parts:** Cross-tool data fusion.
- **Discuss:** Definition rigor.

### 817. Design an Incident-Response Tool (PagerDuty)
- **Scale:** On-call rotations.
- **Core features:** Alert, page, escalate, post-mortem.
- **Hard parts:** Phone-tree reliability.
- **Discuss:** Multi-channel alerting.

### 818. Design a Status-Page Service (Statuspage.io)
- **Scale:** Customer-facing status.
- **Core features:** Components, incidents, subscribe.
- **Hard parts:** Updates during incident.
- **Discuss:** SSE for real-time.

### 819. Design a Synthetic-Monitoring Tool (Pingdom)
- **Scale:** Many checks/min.
- **Core features:** HTTP checks, alerts.
- **Hard parts:** Multi-region check, false-positives.
- **Discuss:** Probe locations.

### 820. Design an Error-Tracking Service (Sentry)
- **Scale:** Billions of errors/day.
- **Core features:** Capture, dedupe, alert.
- **Hard parts:** Source-map symbolication, dedup.
- **Discuss:** Fingerprint algorithm.

### 821. Design a Feature-Flag Targeting Engine
- **Scale:** Per-user evaluation.
- **Core features:** Target rules, percentage rollout.
- **Hard parts:** Real-time updates, SDK perf.
- **Discuss:** Rule engine.

### 822. Design an A/B-Testing Bucket Assignment
- **Scale:** Sticky bucketing.
- **Core features:** Assign user to variant, log exposure.
- **Hard parts:** Re-bucketing avoidance.
- **Discuss:** Hash-based bucketing.

### 823. Design a Telemetry SDK (OpenTelemetry)
- **Scale:** Per-app, multi-language.
- **Core features:** Instrument, export.
- **Hard parts:** Cross-language consistency.
- **Discuss:** OTLP protocol.

### 824. Design a Dependency-Vulnerability Tracker (Dependabot)
- **Scale:** Org-wide.
- **Core features:** Detect vuln, suggest update.
- **Hard parts:** Compatibility risk.
- **Discuss:** Automated PR.

### 825. Design a Code-Coverage Aggregation Service (Codecov)
- **Scale:** Per-PR coverage.
- **Core features:** Ingest report, diff coverage.
- **Hard parts:** Multi-language formats.
- **Discuss:** Coverage delta.

### 826. Design an Internal-Tooling Platform (Retool)
- **Scale:** Internal app builder.
- **Core features:** Connect DB, build UI.
- **Hard parts:** Per-tenant data access, perms.
- **Discuss:** SaaS connectors.

### 827. Design a Schema-Migration Tool (Flyway)
- **Scale:** Multi-environment.
- **Core features:** Versioned migrations, rollback.
- **Hard parts:** Failed-mid migration.
- **Discuss:** Forward-only vs reversible.

### 828. Design an Internal Knowledge Graph (Notion-style for engineering)
- **Scale:** Wiki + service catalog.
- **Core features:** Pages, search, perm.
- **Hard parts:** Backlinks, wikilinks.
- **Discuss:** Graph DB.

### 829. Design a Service-Catalog (Backstage Plugin)
- **Scale:** All services + owners.
- **Core features:** Register, owner, dependencies.
- **Hard parts:** Auto-discovery accuracy.
- **Discuss:** YAML in repos.

### 830. Design a Code-Owner Routing Bot (CODEOWNERS)
- **Scale:** Auto-assign reviewers.
- **Core features:** Parse CODEOWNERS, assign.
- **Hard parts:** Cross-repo overrides.
- **Discuss:** Github API integration.

### 831. Design a Pre-Commit Hook Distribution (pre-commit.com)
- **Scale:** Org-wide hooks.
- **Core features:** Distribute, install, run.
- **Hard parts:** Cross-platform, hook updates.
- **Discuss:** Hook registry.

### 832. Design a Test-Flake Tracker
- **Scale:** Detect flaky tests.
- **Core features:** Track pass/fail, quarantine.
- **Hard parts:** Flake detection accuracy.
- **Discuss:** Statistical detection.

### 833. Design a Coverage-Based Test Selection
- **Scale:** Run only relevant tests.
- **Core features:** Map code change → tests.
- **Hard parts:** Coverage map freshness.
- **Discuss:** Predictive test selection.

### 834. Design an Engineering Onboarding Setup Automator
- **Scale:** New-hire dev env.
- **Core features:** Bootstrap dev box.
- **Hard parts:** Dependency hell.
- **Discuss:** Devcontainer.

### 835. Design a Local-Dev Environment Service (Codespaces / Gitpod)
- **Scale:** Cloud dev workspaces.
- **Core features:** Spin up workspace, prebuild.
- **Hard parts:** State persistence, prebuild cache.
- **Discuss:** Layered FS.

### 836. Design a Crash-Reporting for Mobile (Firebase Crashlytics)
- **Scale:** Mobile fleet.
- **Core features:** Capture crash, symbolicate.
- **Hard parts:** Symbol files, batch upload.
- **Discuss:** Symbolication pipeline.

### 837. Design a CDN Purge / Cache-Bust Coordinator
- **Scale:** Multi-CDN purge.
- **Core features:** Purge URL, verify.
- **Hard parts:** Cross-provider differences.
- **Discuss:** Async purge confirms.

### 838. Design a Browser-Test Recording / Replay (Cypress Cloud)
- **Scale:** Test artifacts.
- **Core features:** Capture screenshots/videos, replay.
- **Hard parts:** Storage cost, upload speed.
- **Discuss:** Selective capture.

### 839. Design an Internal Linter / Style-Enforcement Bot
- **Scale:** PR-time gating.
- **Core features:** Multi-language lint, auto-fix.
- **Hard parts:** Language plugins, perf.
- **Discuss:** Server-side lint.

### 840. Design a Dynamic Configuration Hot-Reload System
- **Scale:** Live config update.
- **Core features:** Push config, watch.
- **Hard parts:** Atomic update across instances.
- **Discuss:** Versioned configs.

# 🥽 46. VR / AR / Spatial

### 841. Design a Multi-User VR Meeting Room
- **Scale:** Real-time avatars.
- **Core features:** Voice, hand tracking, shared scene.
- **Hard parts:** Sync 60Hz pose, lip sync.
- **Discuss:** Spatial audio.

### 842. Design AR Cloud Anchors (Google Cloud Anchors)
- **Scale:** Persistent AR.
- **Core features:** Save anchor, retrieve.
- **Hard parts:** SLAM matching, cross-device.
- **Discuss:** Anchor identity.

### 843. Design a VR-Workout Platform (Supernatural)
- **Scale:** Subscription content.
- **Core features:** Coach, music, scoring.
- **Hard parts:** 90Hz video, calorie estimate.
- **Discuss:** Music sync.

### 844. Design an AR Furniture Preview (IKEA Place)
- **Scale:** Catalog AR.
- **Core features:** Place 3D model.
- **Hard parts:** Plane detection, scale.
- **Discuss:** USDZ/glTF pipeline.

### 845. Design a Spatial Scanning Service (Polycam)
- **Scale:** 3D scans.
- **Core features:** Scan, mesh, share.
- **Hard parts:** Photogrammetry, on-device.
- **Discuss:** LiDAR vs photogrammetry.

### 846. Design a VR Game-Server with Avatar Sync
- **Scale:** Multi-player VR game.
- **Core features:** Pose sync, voice.
- **Hard parts:** Latency, voice mixing.
- **Discuss:** Authoritative server.

### 847. Design an AR Indoor Navigation
- **Scale:** Mall/airport.
- **Core features:** AR arrows, POI.
- **Hard parts:** Indoor pos accuracy.
- **Discuss:** Visual positioning.

### 848. Design a 3D Asset Marketplace (Sketchfab)
- **Scale:** Million 3D models.
- **Core features:** Upload, view in browser, license.
- **Hard parts:** Multi-format, viewer perf.
- **Discuss:** glTF.

### 849. Design a Volumetric Capture Pipeline
- **Scale:** Multi-camera capture.
- **Core features:** Capture, mesh, stream.
- **Hard parts:** Compute, bandwidth.
- **Discuss:** Per-frame mesh compression.

### 850. Design a Mixed-Reality Telepresence (Holoportation)
- **Scale:** Real-time 3D person.
- **Core features:** Capture, transmit, render.
- **Hard parts:** Bandwidth, latency.
- **Discuss:** Compression for 3D.

### 851. Design an AR Try-On (Glasses, Makeup)
- **Scale:** E-commerce AR.
- **Core features:** Face tracking, virtual try.
- **Hard parts:** Real-time face mesh, light match.
- **Discuss:** WebAR.

### 852. Design a Spatial-Audio Service for VR Concerts
- **Scale:** 3D audio mix.
- **Core features:** HRTF, head tracking.
- **Hard parts:** Latency, head tracking.
- **Discuss:** Spatial codecs.

### 853. Design a VR Education Lab Platform
- **Scale:** Schools.
- **Core features:** Interactive labs, assessment.
- **Hard parts:** Multi-headset compatibility.
- **Discuss:** Cross-platform deploy.

### 854. Design a Smart-Glasses Notification Bridge
- **Scale:** Phone → glasses pipe.
- **Core features:** Filter notifications, render minimal.
- **Hard parts:** Battery, prioritization.
- **Discuss:** BLE bandwidth budget.

### 855. Design a Drone-FPV Streaming for Racing
- **Scale:** Low-latency video.
- **Core features:** Live FPV, telemetry.
- **Hard parts:** Sub-50ms video.
- **Discuss:** Analog vs digital FPV.

# 🤖 47. Robotics & Autonomous

### 856. Design a Self-Driving Car Fleet Platform
- **Scale:** AV fleet ops.
- **Core features:** Mission, monitoring, takeover.
- **Hard parts:** Real-time safety driver, edge inference.
- **Discuss:** Tele-op fallback.

### 857. Design an Autonomous-Truck Routing System
- **Scale:** Long-haul AV.
- **Core features:** Route, hub-to-hub, driver swap.
- **Hard parts:** ODD constraints, weather rerouting.
- **Discuss:** Hub-and-spoke AV.

### 858. Design a Robotaxi Dispatching Platform
- **Scale:** Waymo-style.
- **Core features:** Match rider, dispatch AV, monitoring.
- **Hard parts:** ODD-aware match, rider safety.
- **Discuss:** Geo-fenced ODD.

### 859. Design an HD-Map Update Pipeline for AVs
- **Scale:** Continuous map update.
- **Core features:** Crowdsource, validate, deploy.
- **Hard parts:** Map freshness, validation.
- **Discuss:** Drive log → map updates.

### 860. Design a Tele-Operation Backend for Robots
- **Scale:** Remote operator.
- **Core features:** Video, control, latency monitor.
- **Hard parts:** Latency, fallback safety.
- **Discuss:** Edge POPs.

### 861. Design a Warehouse-Robot Simulation Service
- **Scale:** Pre-deploy sim.
- **Core features:** Build environment, run sim.
- **Hard parts:** Determinism, scale of agents.
- **Discuss:** Headless sim runners.

### 862. Design a Robot Software Update (OTA)
- **Scale:** Robot fleet.
- **Core features:** Versioned releases, A/B.
- **Hard parts:** Rollback safety.
- **Discuss:** Atomic update.

### 863. Design a Drone-Delivery Air-Traffic-Mgmt
- **Scale:** UTM.
- **Core features:** Reserve airspace, conflict-free routing.
- **Hard parts:** Real-time deconflicting.
- **Discuss:** USS APIs.

### 864. Design an Industrial Robot Programming Cloud
- **Scale:** Multi-vendor (Fanuc, ABB).
- **Core features:** Programs, version, simulate.
- **Hard parts:** Vendor formats.
- **Discuss:** Universal robot DSL.

### 865. Design a Last-Mile Sidewalk Delivery Robot Platform (Starship)
- **Scale:** Campus deliveries.
- **Core features:** Order, route, unlock.
- **Hard parts:** Sidewalk navigation, regulation.
- **Discuss:** Edge perception.

### 866. Design a Robotic Arm Pick-Place Coordination
- **Scale:** Warehouse automation.
- **Core features:** Vision, grasp planning, place.
- **Hard parts:** Grasp success, item variety.
- **Discuss:** ML grasp.

### 867. Design a Multi-Robot Coordination Layer (Multi-Agent Path Finding)
- **Scale:** 1000s of robots.
- **Core features:** Path plan without conflicts.
- **Hard parts:** Real-time MAPF.
- **Discuss:** CBS algorithm.

### 868. Design an Autonomous Boat / USV Fleet Mgmt
- **Scale:** Maritime.
- **Core features:** Mission, comms, weather.
- **Hard parts:** SatCom intermittent, COLREGS.
- **Discuss:** Iridium link.

### 869. Design a Lawn-Mower Robot Fleet
- **Scale:** Consumer robots.
- **Core features:** Schedule, geofence, fault.
- **Hard parts:** GPS-RTK accuracy, yard map.
- **Discuss:** RTK base station.

### 870. Design an Inspection-Drone Pipeline (Solar farms, towers)
- **Scale:** Routine inspection.
- **Core features:** Schedule, fly, AI defect detect.
- **Hard parts:** Stitching imagery, defect ML.
- **Discuss:** GIS overlay.

# 📞 48. Telecom & Mobile

### 871. Design an MVNO (Mobile Virtual Network) Backend
- **Scale:** Mobile carrier reseller.
- **Core features:** SIM provisioning, billing.
- **Hard parts:** HLR/HSS integration, roaming.
- **Discuss:** eSIM provisioning.

### 872. Design an eSIM Provisioning Platform
- **Scale:** Remote SIM mgmt.
- **Core features:** Generate, deliver to device.
- **Hard parts:** GSMA SM-DP+ standards.
- **Discuss:** RSP architecture.

### 873. Design a Cell-Tower Base Station Mgmt
- **Scale:** Telco RAN.
- **Core features:** Health, configuration, alarms.
- **Hard parts:** Vendor-specific, scale.
- **Discuss:** O-RAN.

### 874. Design a 5G Core Slicing Orchestrator
- **Scale:** Network slices.
- **Core features:** Define slice, allocate resources.
- **Hard parts:** Multi-tenant SLA.
- **Discuss:** NSSF.

### 875. Design a Telco CDR (Call Detail Record) Pipeline
- **Scale:** Billions of records.
- **Core features:** Ingest, rate, bill.
- **Hard parts:** Real-time charging.
- **Discuss:** Online vs offline charging.

### 876. Design a SMS Aggregator (Twilio competitor)
- **Scale:** Send to multiple carriers.
- **Core features:** Route by destination.
- **Hard parts:** Carrier route table.
- **Discuss:** SMPP.

### 877. Design a Voice-Call Routing (SIP Trunk)
- **Scale:** Enterprise PBX.
- **Core features:** Call route, recording.
- **Hard parts:** Latency, codec negotiation.
- **Discuss:** SIP signaling.

### 878. Design a Mobile-Number Portability System
- **Scale:** Country-wide.
- **Core features:** Port number, carrier swap.
- **Hard parts:** SLA, central NPC db.
- **Discuss:** NPAC architecture.

### 879. Design a Roaming-Settlement System (TAP)
- **Scale:** Inter-carrier roaming.
- **Core features:** Exchange records, settle.
- **Hard parts:** TAP3.12 format.
- **Discuss:** DataClearing.

### 880. Design a Mobile-Network Outage Detection
- **Scale:** Per-cell outage.
- **Core features:** Alarms, customer impact, dispatch.
- **Hard parts:** Root-cause inference.
- **Discuss:** Alarm correlation.

### 881. Design a 911 Wireless Caller-Location Service
- **Scale:** E911 Phase II.
- **Core features:** Locate caller, dispatch.
- **Hard parts:** Indoor positioning, accuracy.
- **Discuss:** RapidSOS.

### 882. Design a Wi-Fi Hotspot Federation
- **Scale:** Public Wi-Fi roaming.
- **Core features:** Auth across networks.
- **Hard parts:** Hotspot 2.0, Passpoint.
- **Discuss:** OpenRoaming.

### 883. Design a VoIP Server with Echo-Cancellation
- **Scale:** Per-call DSP.
- **Core features:** Call setup, audio mixing.
- **Hard parts:** Latency, echo, jitter.
- **Discuss:** WebRTC stack.

### 884. Design a Video-Calling Quality Tracker (NQM)
- **Scale:** Call analytics.
- **Core features:** MOS scores, bitrate, packet loss.
- **Hard parts:** SDK telemetry, dashboards.
- **Discuss:** Per-call dashboards.

### 885. Design a Conference-Bridge Service
- **Scale:** Audio conferencing.
- **Core features:** Dial-in, mix, recording.
- **Hard parts:** Audio mixing capacity.
- **Discuss:** SFU vs MCU.

### 886. Design a Mobile Push-Token Lifecycle Manager
- **Scale:** Per-device tokens.
- **Core features:** Register, refresh, deprovision.
- **Hard parts:** Stale tokens, fanout.
- **Discuss:** Token rotation.

### 887. Design a Mobile-Network Throttling / Fair Use System
- **Scale:** Carrier throttling.
- **Core features:** Detect heavy users, throttle.
- **Hard parts:** Fairness, neutrality.
- **Discuss:** PCRF rules.

### 888. Design a SIP-Trunk Fraud Detection
- **Scale:** Detect toll fraud.
- **Core features:** Pattern detect, block.
- **Hard parts:** False-positive risk.
- **Discuss:** Velocity rules.

### 889. Design a Mobile App Distribution / OTA Updates
- **Scale:** B2B app distribution.
- **Core features:** App catalog, install.
- **Hard parts:** MDM integration.
- **Discuss:** Apple Business Manager.

### 890. Design an MMS Gateway System
- **Scale:** Multimedia message delivery.
- **Core features:** Receive, send, transcode.
- **Hard parts:** Carrier transcoding.
- **Discuss:** RCS migration.

# 🌆 49. Smart Cities

### 891. Design a Smart-Parking System
- **Scale:** City-wide.
- **Core features:** Available spots, reservation, payment.
- **Hard parts:** Sensor reliability, dispute.
- **Discuss:** Sensor-fusion.

### 892. Design a Traffic-Signal-Optimization Backend
- **Scale:** City-wide signals.
- **Core features:** Adaptive timing, emergency preempt.
- **Hard parts:** Real-time control safety.
- **Discuss:** ATSPM data.

### 893. Design a Connected-Crosswalk / Pedestrian Signal Service
- **Scale:** Per-intersection.
- **Core features:** Detect pedestrian, alert vehicles.
- **Hard parts:** V2X comms.
- **Discuss:** DSRC vs C-V2X.

### 894. Design a Smart-Streetlight Mgmt
- **Scale:** City-wide.
- **Core features:** Dim schedules, fault detect.
- **Hard parts:** Mesh network reliability.
- **Discuss:** LPWAN.

### 895. Design a Public-Transit Card System (Metro Card)
- **Scale:** City transit.
- **Core features:** Tap, fare deduct, top-up.
- **Hard parts:** Offline gates, settlement.
- **Discuss:** MIFARE/EMV.

### 896. Design a Bike-Lane Hazard Reporting App
- **Scale:** Crowdsource.
- **Core features:** Photo+location, route to dept.
- **Hard parts:** Geo-clustering similar reports.
- **Discuss:** 311-style routing.

### 897. Design a Smart-Waste Collection
- **Scale:** Sensor-equipped bins.
- **Core features:** Fill level, route trucks.
- **Hard parts:** Sensor accuracy.
- **Discuss:** Optimization model.

### 898. Design a City Air-Quality Sensor Grid
- **Scale:** 1000s of sensors.
- **Core features:** Ingest, viz, alert.
- **Hard parts:** Sensor calibration.
- **Discuss:** Crowd vs reference sensors.

### 899. Design a Citywide Flood Sensor Network
- **Scale:** Storm-drain monitoring.
- **Core features:** Real-time levels, alerts.
- **Hard parts:** Power, comms.
- **Discuss:** Mesh + LoRa.

### 900. Design a Public Safety Camera Surveillance Backend
- **Scale:** City CCTV.
- **Core features:** Live view, retention, search.
- **Hard parts:** Privacy, retention rules.
- **Discuss:** Per-camera retention.

### 901. Design a Smart-Hydrant / Water-Leak Monitor
- **Scale:** Underground water network.
- **Core features:** Pressure/flow, leak detect.
- **Hard parts:** Sensor placement.
- **Discuss:** Leak-modeling math.

### 902. Design a Snow-Plow Telemetry Map
- **Scale:** Public-facing tracker.
- **Core features:** Live plow position, status.
- **Hard parts:** Privacy of routes vs transparency.
- **Discuss:** Public-facing UI.

### 903. Design a Smart Building Occupancy / Cleaning Trigger
- **Scale:** Office building.
- **Core features:** Detect occupancy, schedule cleaning.
- **Hard parts:** Sensor + booking integration.
- **Discuss:** Privacy.

### 904. Design a Citywide EV-Charger Map / Mgmt
- **Scale:** Multi-vendor chargers.
- **Core features:** Find, reserve, pay.
- **Hard parts:** Cross-vendor APIs.
- **Discuss:** OCPI standards.

### 905. Design a Smart-City Open-Data Hub
- **Scale:** Multi-dept datasets.
- **Core features:** Catalog, API, viz.
- **Hard parts:** Data quality, sharing.
- **Discuss:** Schema standards.

# 🌱 50. Climate / Sustainability

### 906. Design a Carbon-Tracking Platform for Companies
- **Scale:** Scope 1/2/3.
- **Core features:** Track emissions, reports.
- **Hard parts:** Multi-source, factor updates.
- **Discuss:** GHG protocol mapping.

### 907. Design a Renewable-Energy Certificate (REC) Tracking
- **Scale:** Cert issuance + retirement.
- **Core features:** Issue, transfer, retire.
- **Hard parts:** Double-counting prevention.
- **Discuss:** Registry coordination.

### 908. Design a Carbon-Offset Marketplace
- **Scale:** Project + buyer.
- **Core features:** Browse projects, purchase, retire.
- **Hard parts:** Verifiability, double sale.
- **Discuss:** Registry ledger.

### 909. Design a Wildfire-Detection Satellite Pipeline
- **Scale:** Real-time hot-spot detect.
- **Core features:** Ingest sat data, alert.
- **Hard parts:** False positives, latency.
- **Discuss:** GOES-R, MODIS.

### 910. Design an Electric-Bus Fleet Charging Optimizer
- **Scale:** Public transit fleet.
- **Core features:** Charge schedule, route compatible.
- **Hard parts:** Grid demand limits.
- **Discuss:** Smart-charge optimization.

### 911. Design a Carbon Footprint Calculator API
- **Scale:** Per-transaction footprint.
- **Core features:** Lookup category factor.
- **Hard parts:** Factor freshness, accuracy.
- **Discuss:** Climatiq-style API.

### 912. Design a Recycling-Drop-off Tracking App
- **Scale:** Material drop tracking.
- **Core features:** Photo, weigh, points.
- **Hard parts:** Anti-fraud.
- **Discuss:** Computer vision.

### 913. Design a Microplastic Sampling Citizen-Science Backend
- **Scale:** Crowdsourced.
- **Core features:** Submit sample, ID.
- **Hard parts:** Quality control.
- **Discuss:** Lab pipeline.

### 914. Design a Water-Usage Smart Meter for Households
- **Scale:** Residential.
- **Core features:** Real-time, leak alerts.
- **Hard parts:** Sensor accuracy, battery.
- **Discuss:** Edge processing.

### 915. Design a Solar-Power Forecasting for Grids
- **Scale:** Utility-scale.
- **Core features:** Forecast 24h, sub-hour.
- **Hard parts:** Cloud-cover modeling.
- **Discuss:** ML + numeric weather.

### 916. Design a Carbon-Aware Compute Scheduler
- **Scale:** Global compute.
- **Core features:** Run jobs when grid is greenest.
- **Hard parts:** Region prediction.
- **Discuss:** WattTime API.

### 917. Design a Fashion Garment Lifecycle Tracker
- **Scale:** Supply chain CO2.
- **Core features:** Capture stages.
- **Hard parts:** Multi-tier visibility.
- **Discuss:** GS1 + sustainability.

### 918. Design a Reusable-Container Deposit System
- **Scale:** Beverage deposits.
- **Core features:** Deposit, return, refund.
- **Hard parts:** RVM (reverse vending machine).
- **Discuss:** Tag-based tracking.

### 919. Design a Climate-Risk Disclosure Platform
- **Scale:** Corp climate disclosure.
- **Core features:** Capture, reports (TCFD/SASB).
- **Hard parts:** Standards versioning.
- **Discuss:** Auditor workflow.

### 920. Design a Tree-Planting Verification Service
- **Scale:** Reforestation projects.
- **Core features:** Geo-tagged photos, satellite verify.
- **Hard parts:** Long-term survival monitoring.
- **Discuss:** Multi-modal monitoring.

# ⚙️ 51. Workflow & Orchestration

### 921. Design a Generic Workflow-as-a-Service Engine
- **Scale:** Multi-tenant workflows.
- **Core features:** Define, execute, retry.
- **Hard parts:** Idempotent steps, durability.
- **Discuss:** Event-sourced.

### 922. Design an Approval-Chain Workflow System
- **Scale:** Enterprise approvals.
- **Core features:** Multi-level approval, delegation.
- **Hard parts:** Out-of-office routing.
- **Discuss:** Approval-rule engine.

### 923. Design a No-Code Form Builder + Workflow
- **Scale:** Citizen developers.
- **Core features:** Drag-drop, route, integrate.
- **Hard parts:** Versioning, schema migrate.
- **Discuss:** Workflow JSON.

### 924. Design a Document Approval & E-Sign Workflow
- **Scale:** Multi-signer.
- **Core features:** Route, sign, archive.
- **Hard parts:** Signer auth.
- **Discuss:** Audit trail.

### 925. Design a Customer-Service Ticket Routing
- **Scale:** Multi-skill agents.
- **Core features:** Route by skill/SLA.
- **Hard parts:** Real-time routing.
- **Discuss:** Skills-based routing.

### 926. Design a Marketing-Campaign Automation (Marketo)
- **Scale:** Multi-step nurture.
- **Core features:** Trigger, branch, send.
- **Hard parts:** Audience suppression.
- **Discuss:** State machine.

### 927. Design an RPA Bot Orchestrator
- **Scale:** Bot fleet.
- **Core features:** Schedule, run, reports.
- **Hard parts:** Bot health, screen-flake.
- **Discuss:** Coordinator + worker.

### 928. Design a Long-Running Saga Orchestrator
- **Scale:** Multi-service.
- **Core features:** Steps, compensations.
- **Hard parts:** Recovery from crash.
- **Discuss:** Replay + idempotency.

### 929. Design a Job-Retry Engine with Backoff/Jitter
- **Scale:** Generic retry.
- **Core features:** Configurable retry.
- **Hard parts:** Poison detection.
- **Discuss:** Exponential w/ jitter.

### 930. Design a Pipeline Scheduler for Daily Reports
- **Scale:** Nightly batch jobs.
- **Core features:** Cron, dependency, alert.
- **Hard parts:** Late ingress.
- **Discuss:** Backfill strategy.

### 931. Design a Periodic Workflow Engine for IoT
- **Scale:** Per-device cadence.
- **Core features:** Schedule rules per device.
- **Hard parts:** Time-zone, drift.
- **Discuss:** Device-time vs server.

### 932. Design a Cross-Service Long-Lived Workflow with Signals
- **Scale:** Days-long process.
- **Core features:** Signals, timers, queries.
- **Hard parts:** Determinism replay.
- **Discuss:** Temporal-style.

### 933. Design a Multi-Step Form Workflow Engine
- **Scale:** Insurance app.
- **Core features:** Save partial, resume.
- **Hard parts:** Schema evolution mid-flow.
- **Discuss:** Versioned forms.

### 934. Design a Slack-Approval Bot
- **Scale:** Org bot.
- **Core features:** Trigger from app, approve in Slack.
- **Hard parts:** Authn binding.
- **Discuss:** Webhook payloads.

### 935. Design a Notification-Preferences Engine
- **Scale:** Multi-channel preferences.
- **Core features:** Per-user channel/freq.
- **Hard parts:** Quiet hours, digest.
- **Discuss:** Preference store.

### 936. Design a Cron-Replacement Distributed Scheduler
- **Scale:** Hundreds of jobs.
- **Core features:** UI, run history.
- **Hard parts:** Single-fire across nodes.
- **Discuss:** Leader election.

### 937. Design a Bulk-Job Splitter / Map-Reduce Coordinator
- **Scale:** Large input split → chunks.
- **Core features:** Split, run, aggregate.
- **Hard parts:** Skewed partitions.
- **Discuss:** Speculative execution.

### 938. Design a Data Pipeline Lineage Tracker
- **Scale:** Org-wide datasets.
- **Core features:** Capture lineage, query.
- **Hard parts:** Coverage, freshness.
- **Discuss:** OpenLineage.

### 939. Design an Async Job Result Notifier (callback URL)
- **Scale:** Long jobs.
- **Core features:** Submit, get callback.
- **Hard parts:** Webhook reliability.
- **Discuss:** Retry + signing.

### 940. Design a Distributed Idempotency Key Store
- **Scale:** Cross-service idempotency.
- **Core features:** Store result by key, TTL.
- **Hard parts:** Race on first request.
- **Discuss:** Lock + result.

### 941. Design a Multi-Party Coordination Service (Joint approval)
- **Scale:** Multi-tenant signing.
- **Core features:** Threshold approve, finalize.
- **Hard parts:** Deadlock, abandon.
- **Discuss:** State machine per case.

### 942. Design an Event Bus for HR Events (Hire, Fire, Promote)
- **Scale:** Org-wide.
- **Core features:** Publish events, downstream consumers.
- **Hard parts:** PII filtering.
- **Discuss:** Schema + access control.

### 943. Design a Real-time Notification Inbox Sync
- **Scale:** Per-user inbox.
- **Core features:** Read state across devices.
- **Hard parts:** Ordering, delivery once.
- **Discuss:** CRDT counters.

### 944. Design a Webhook Receiver with Replay
- **Scale:** Receive 100M webhooks/day.
- **Core features:** Receive, verify, idempotency, replay.
- **Hard parts:** Slow consumers.
- **Discuss:** Inbound queue.

### 945. Design a Cron-Like Email-Reminder Engine
- **Scale:** Recurring user reminders.
- **Core features:** Per-user schedule, cancel.
- **Hard parts:** Time-zone correctness.
- **Discuss:** Sharded scheduler.

# 🧮 52. Data Engineering & ETL

### 946. Design an ETL Orchestration Tool (Airflow-style)
- **Scale:** Org-wide DAGs.
- **Core features:** DAG, retries, sensors.
- **Hard parts:** Scaling executors, scheduler scale.
- **Discuss:** Backfill semantics.

### 947. Design a Data Catalog (DataHub / Amundsen)
- **Scale:** Org-wide.
- **Core features:** Catalog, lineage, ownership.
- **Hard parts:** Auto-discovery completeness.
- **Discuss:** Crawler + manual.

### 948. Design a Data-Quality Tool (Great Expectations)
- **Scale:** Per-table assertions.
- **Core features:** Define, run, alert.
- **Hard parts:** Cost on huge tables.
- **Discuss:** Sampling.

### 949. Design a Reverse-ETL System (Census, Hightouch)
- **Scale:** Warehouse → SaaS.
- **Core features:** Sync model to destinations.
- **Hard parts:** Incremental sync.
- **Discuss:** Diff detection.

### 950. Design a Data-Contract System
- **Scale:** Producer/consumer contract.
- **Core features:** Schema, SLA, validation.
- **Hard parts:** Enforcement, compatibility.
- **Discuss:** CI/CD enforcement.

### 951. Design a Schema-Evolution Manager
- **Scale:** Avro/Protobuf.
- **Core features:** Compatibility check, version.
- **Hard parts:** Backward/forward.
- **Discuss:** Reader/writer schema.

### 952. Design a Streaming-SQL Layer (ksqlDB)
- **Scale:** Real-time SQL on Kafka.
- **Core features:** Continuous queries.
- **Hard parts:** State stores, joins.
- **Discuss:** Materialized views.

### 953. Design a Lakehouse Catalog (Unity / Glue)
- **Scale:** Multi-engine catalog.
- **Core features:** Tables, perms, lineage.
- **Hard parts:** Engine plugin spec.
- **Discuss:** Iceberg REST catalog.

### 954. Design a Petabyte Data-Warehouse Query Engine (Trino)
- **Scale:** Multi-source queries.
- **Core features:** Federate sources, SQL.
- **Hard parts:** Spill to disk, scheduler.
- **Discuss:** MPP scheduler.

### 955. Design a Materialized-View Refresh Engine
- **Scale:** Continuous + scheduled.
- **Core features:** Build, refresh, swap.
- **Hard parts:** Incremental refresh.
- **Discuss:** Watermark-based.

### 956. Design a Data-Lake Compaction Job
- **Scale:** Hadoop-style.
- **Core features:** Small-file → big.
- **Hard parts:** Concurrent writers.
- **Discuss:** Iceberg snapshots.

### 957. Design a CDC Sink to Lakehouse
- **Scale:** Insert-update-delete to Iceberg.
- **Core features:** Apply CDC, MERGE.
- **Hard parts:** Compaction frequency.
- **Discuss:** Hudi vs Iceberg.

### 958. Design a Data-Anonymization / Tokenization Pipeline
- **Scale:** PII data flows.
- **Core features:** Mask, tokenize, format-preserve.
- **Hard parts:** Reversibility, key mgmt.
- **Discuss:** FPE algorithms.

### 959. Design a Streaming-Joins Engine
- **Scale:** Join two Kafka topics.
- **Core features:** Window, key.
- **Hard parts:** State size.
- **Discuss:** RocksDB-backed.

### 960. Design a Late-Arriving-Data Handler
- **Scale:** Stream processor.
- **Core features:** Watermark, side outputs.
- **Hard parts:** Allowed lateness.
- **Discuss:** Allowed-lateness windows.

### 961. Design a Data-Replay / Time-Travel Query Service
- **Scale:** Lakehouse time travel.
- **Core features:** Query as-of.
- **Hard parts:** Snapshot retention.
- **Discuss:** Iceberg metadata.

### 962. Design a Cross-Region Data Replication Service
- **Scale:** Multi-region warehouse.
- **Core features:** Replicate datasets.
- **Hard parts:** Bandwidth, drift.
- **Discuss:** Diff + ship.

### 963. Design a Pipeline Cost-Attribution Service
- **Scale:** Show team-level cost.
- **Core features:** Tag jobs, attribute.
- **Hard parts:** Shared resources.
- **Discuss:** Tagging discipline.

### 964. Design a Data Observability Platform (Monte Carlo)
- **Scale:** Detect data downtime.
- **Core features:** Freshness, volume, schema, distribution.
- **Hard parts:** Per-table baselines.
- **Discuss:** Anomaly detection.

### 965. Design a Privacy-Aware Data Sharing (Snowflake Data Share)
- **Scale:** Cross-org sharing.
- **Core features:** Share dataset, policies.
- **Hard parts:** Row/column access.
- **Discuss:** Secure views.

### 966. Design a Data-Migration Tool (DB → DB)
- **Scale:** Multi-TB migration.
- **Core features:** Snapshot + CDC + cutover.
- **Hard parts:** Zero-downtime, validation.
- **Discuss:** Dual-writes.

### 967. Design a Real-Time Aggregation Service
- **Scale:** Per-key aggregates.
- **Core features:** Sum/count/avg over time window.
- **Hard parts:** Hot keys.
- **Discuss:** Pre-agg vs query-time.

### 968. Design a Backfill / Recompute Engine
- **Scale:** Recompute history.
- **Core features:** Retro-compute over old data.
- **Hard parts:** Cost, side-effects.
- **Discuss:** Idempotent jobs.

### 969. Design a Federated SQL on Multiple Sources
- **Scale:** Across Mongo, MySQL, S3.
- **Core features:** Distributed query.
- **Hard parts:** Predicate pushdown.
- **Discuss:** Trino connectors.

### 970. Design a Personal Data Inventory Service (DSAR-ready)
- **Scale:** Org-wide PII map.
- **Core features:** Where is user X data.
- **Hard parts:** Coverage, freshness.
- **Discuss:** Per-system tag.

### 971. Design a Data-Pipeline DAG Visualizer
- **Scale:** Org-wide.
- **Core features:** Lineage graph view.
- **Hard parts:** Performance on big graphs.
- **Discuss:** Layered layout.

### 972. Design a Streaming-Window Aggregator
- **Scale:** Tumbling/sliding.
- **Core features:** Window functions.
- **Hard parts:** Out-of-order.
- **Discuss:** Watermarks.

### 973. Design an Approximate-Aggregation OLAP (Druid)
- **Scale:** Fast slice & dice.
- **Core features:** Pre-agg cubes.
- **Hard parts:** Real-time + batch unite.
- **Discuss:** Lambda-merge.

### 974. Design a Lakehouse Time-Travel Audit Service
- **Scale:** Compliance.
- **Core features:** "Show me data as of T".
- **Hard parts:** Snapshot retention budgets.
- **Discuss:** Iceberg snapshot mgmt.

### 975. Design a Cost-Aware Query Optimizer (BigQuery slot)
- **Scale:** Query priority.
- **Core features:** Quotas, fair-share.
- **Hard parts:** Tail-latency.
- **Discuss:** Slot-based pricing.

# 🛡️ 53. Compliance, Auditing, Privacy

### 976. Design a GDPR Right-to-be-Forgotten Pipeline
- **Scale:** Org-wide.
- **Core features:** Receive request, delete across systems.
- **Hard parts:** Backup deletion, derived data.
- **Discuss:** Tombstones + crypto-shred.

### 977. Design a SOC 2 Continuous-Compliance Tool
- **Scale:** Auditor-ready.
- **Core features:** Evidence collection, controls.
- **Hard parts:** Multi-system evidence.
- **Discuss:** Vanta-style.

### 978. Design a HIPAA Audit-Trail System
- **Scale:** Healthcare access logs.
- **Core features:** Capture access, alert anomalies.
- **Hard parts:** Tamper-resistance.
- **Discuss:** Append-only WORM.

### 979. Design a PCI Tokenization Vault
- **Scale:** Card data tokenization.
- **Core features:** Tokenize, detokenize, audit.
- **Hard parts:** Scope reduction.
- **Discuss:** Network token.

### 980. Design a Data Retention Lifecycle Manager
- **Scale:** Auto-delete after N days.
- **Core features:** Per-dataset policy.
- **Hard parts:** Legal hold overrides.
- **Discuss:** Policy engine.

### 981. Design a Privacy-Preserving Computation Service
- **Scale:** Multi-party.
- **Core features:** MPC / secure aggregation.
- **Hard parts:** Performance, primitives.
- **Discuss:** SMPC libs.

### 982. Design an Audit-Log Centralized Service
- **Scale:** All system events.
- **Core features:** Capture, search, retain.
- **Hard parts:** Tamper-evident.
- **Discuss:** Hash-chain.

### 983. Design a Consent-Management Platform (CMP)
- **Scale:** GDPR consent.
- **Core features:** Capture consent, distribute.
- **Hard parts:** Cross-tool propagation.
- **Discuss:** TCF v2.2.

### 984. Design a Data-Subject-Rights Request Handler
- **Scale:** Multiple regions.
- **Core features:** Intake, fulfill, audit.
- **Hard parts:** Identity verification.
- **Discuss:** Verification flow.

### 985. Design a Continuous Penetration Testing Platform
- **Scale:** Automated re-test.
- **Core features:** Schedule scans, replay attacks.
- **Hard parts:** False-positive triage.
- **Discuss:** Authenticated scans.

### 986. Design a SIEM (Security Info & Event Mgmt)
- **Scale:** TB/day logs.
- **Core features:** Ingest, correlate, alert.
- **Hard parts:** Detection rules at scale.
- **Discuss:** Sigma rules.

### 987. Design a User-Behavior Analytics (UBA) Tool
- **Scale:** Insider threat.
- **Core features:** Per-user baseline, anomaly.
- **Hard parts:** Privacy + accuracy.
- **Discuss:** Anomaly metrics.

### 988. Design a Document-Classification System (DLP)
- **Scale:** Org-wide content.
- **Core features:** Classify, tag, restrict.
- **Hard parts:** Multi-format support.
- **Discuss:** Pre-built classifiers.

### 989. Design a CMDB / Asset-Inventory System
- **Scale:** Enterprise IT.
- **Core features:** Discover, catalog, relate.
- **Hard parts:** Auto-discovery accuracy.
- **Discuss:** Multi-source dedup.

### 990. Design an Identity-Governance & Admin (IGA) Platform
- **Scale:** Org-wide access reviews.
- **Core features:** Provision, review, recert.
- **Hard parts:** Multi-app provisioning.
- **Discuss:** SCIM connectors.

### 991. Design a Privileged-Access Mgmt (PAM) Service
- **Scale:** Sensitive credentials.
- **Core features:** Vault, JIT access, session record.
- **Hard parts:** Session brokering.
- **Discuss:** Bastion + recording.

### 992. Design an Insider-Threat Detection Platform
- **Scale:** User-activity.
- **Core features:** Detect data exfil patterns.
- **Hard parts:** Privacy + detection balance.
- **Discuss:** ML on email/file events.

### 993. Design a Compliance Evidence Collection Bot
- **Scale:** Continuous controls.
- **Core features:** Capture screenshot/state, archive.
- **Hard parts:** Multi-tool integration.
- **Discuss:** Connector library.

### 994. Design a Privacy Budget Tracker (Differential Privacy)
- **Scale:** Per-dataset budget.
- **Core features:** Track epsilon spend.
- **Hard parts:** Accountant correctness.
- **Discuss:** Renyi DP.

### 995. Design a Cookie-Consent Banner SDK
- **Scale:** Embed on websites.
- **Core features:** Show banner, store consent.
- **Hard parts:** Geo-aware behavior.
- **Discuss:** GPC support.

### 996. Design an SBOM-Based Vuln-Risk Scoring
- **Scale:** Enterprise SBOM.
- **Core features:** Match SBOM → CVE → severity.
- **Hard parts:** EPSS / KEV signals.
- **Discuss:** Prioritization.

### 997. Design a Threat-Intel Sharing Platform (STIX/TAXII)
- **Scale:** Inter-org TI.
- **Core features:** Ingest, normalize, share.
- **Hard parts:** Anonymization, provenance.
- **Discuss:** TAXII protocol.

### 998. Design an Anti-Phishing / Email Security Gateway
- **Scale:** Org email.
- **Core features:** Detect phish, sandbox links.
- **Hard parts:** False positives, evasion.
- **Discuss:** Time-of-click rewrites.

### 999. Design a Cryptographic Key-Mgmt Service (KMS)
- **Scale:** Org-wide keys.
- **Core features:** Create, rotate, destroy.
- **Hard parts:** HSM-backed, audit.
- **Discuss:** Envelope encryption.

### 1000. Design an Audit-Trail Tamper-Evident Log
- **Scale:** Years of retention.
- **Core features:** Append, hash-chain, prove.
- **Hard parts:** Performance + provability.
- **Discuss:** Merkle log + transparency.

---

# 🧱 OOD — Object-Oriented Design Problems

> **Fokus:** Low-Level Design (LLD) / Object-Oriented Design intervyularda klassik 50 ta OOD masalasi. Class diagram, design pattern, SOLID, va concurrency-aware kod yozish.
>
> **Format:** Har bir masala 30–45 daqiqa whiteboard / IDE sessiyasi uchun.
> **Talab:** Class diagram, key methods, design patterns, edge cases, thread-safety.

### O1. Design Parking Lot
- **Class:** ParkingLot, Floor, Slot, Vehicle (Car/Bike/Truck), Ticket, Payment.
- **Patterns:** Strategy (pricing), Factory (vehicle), Singleton (lot).
- **Hard parts:** Concurrent slot allocation, lost-ticket flow, multi-floor.

### O2. Design Elevator System
- **Class:** ElevatorSystem, Elevator, Request, Direction, Scheduler.
- **Patterns:** Strategy (scheduling — SCAN/LOOK), State, Observer.
- **Hard parts:** Multi-elevator dispatch, priority, fault.

### O3. Design Vending Machine
- **Class:** VendingMachine, Inventory, Product, Coin, State.
- **Patterns:** State (Idle/Selecting/Paid/Dispensing).
- **Hard parts:** Refund on out-of-stock, change-making.

### O4. Design Library Management System
- **Class:** Library, Book, Member, Loan, Reservation, Fine.
- **Patterns:** Repository, Observer (overdue).
- **Hard parts:** Concurrent reservations, fine calc.

### O5. Design Tic-Tac-Toe
- **Class:** Game, Board, Player, Move, WinChecker.
- **Patterns:** State, Strategy (AI).
- **Hard parts:** Move validation, win-detection efficiency.

### O6. Design Chess
- **Class:** Game, Board, Piece (8 types), Move, Player.
- **Patterns:** Strategy (per-piece move), Command (move history).
- **Hard parts:** Castling, en-passant, check detection.

### O7. Design Snake & Ladder
- **Class:** Game, Board, Player, Dice, Snake, Ladder.
- **Patterns:** Strategy (dice), Chain-of-Responsibility (turn).
- **Hard parts:** Multi-die, snake-on-snake.

### O8. Design Connect Four
- **Class:** Game, Grid, Move, Player, WinChecker.
- **Patterns:** Strategy (AI), State.
- **Hard parts:** Win-pattern detection in 4 directions.

### O9. Design Poker (Texas Hold'em)
- **Class:** Game, Table, Player, Card, Hand, Pot, BettingRound.
- **Patterns:** State (rounds), Strategy (hand-ranking).
- **Hard parts:** Side-pots, all-in, hand evaluator.

### O10. Design BlackJack
- **Class:** Game, Dealer, Player, Hand, Card, Shoe.
- **Patterns:** State, Strategy (dealer rules).
- **Hard parts:** Splits, doubles, insurance.

### O11. Design ATM Machine
- **Class:** ATM, Card, Account, Transaction, CashDispenser.
- **Patterns:** State, Strategy (transaction type), Chain (cash-bin).
- **Hard parts:** Cash bin denominations, daily limits.

### O12. Design Online Stock Brokerage
- **Class:** Account, Order, Trade, Portfolio, Quote.
- **Patterns:** Observer (quote), Command (order).
- **Hard parts:** Order types, cancellation race.

### O13. Design LRU Cache
- **Class:** LRUCache (HashMap + DoublyLinkedList).
- **Patterns:** Composition.
- **Hard parts:** O(1) get/put, thread-safety.

### O14. Design LFU Cache
- **Class:** LFUCache, FreqList, Node.
- **Patterns:** Composition.
- **Hard parts:** Frequency tie-breaking, eviction.

### O15. Design HashMap (from scratch)
- **Class:** HashMap, Entry, Bucket.
- **Patterns:** Iterator.
- **Hard parts:** Resize, collision (chaining vs probing), thread-safety.

### O16. Design Linked List
- **Class:** SinglyList, Node, Iterator.
- **Patterns:** Iterator.
- **Hard parts:** Doubly vs singly, reverse, cycle detection.

### O17. Design Movie Ticket Booking System
- **Class:** Movie, Theater, Show, Seat, Booking, Payment.
- **Patterns:** Singleton (theater catalog), State (booking).
- **Hard parts:** Seat-hold race, concurrent booking.

### O18. Design Cab Booking System (Uber LLD)
- **Class:** Rider, Driver, Trip, Location, FareCalc.
- **Patterns:** Strategy (matching), Observer (location).
- **Hard parts:** Driver matching, surge.

### O19. Design Hotel Management System
- **Class:** Hotel, Room (types), Reservation, Guest, Service.
- **Patterns:** Factory (room), State (reservation).
- **Hard parts:** Overlapping bookings, walk-ins.

### O20. Design Restaurant Management System
- **Class:** Restaurant, Table, Order, MenuItem, Bill.
- **Patterns:** Command (order), Observer (kitchen).
- **Hard parts:** Concurrent table state, course timing.

### O21. Design Pizza Delivery System
- **Class:** Pizza, Order, Topping, Driver, Customer.
- **Patterns:** Builder (pizza), Decorator (topping).
- **Hard parts:** Custom builds, time-window.

### O22. Design Ride-Share Carpool
- **Class:** Trip, Rider, Driver, Match.
- **Patterns:** Strategy (match algo).
- **Hard parts:** Multi-rider trip, route insertion.

### O23. Design Logger / Logging Framework
- **Class:** Logger, Appender, Layout, LogLevel.
- **Patterns:** Chain-of-Responsibility, Strategy, Singleton.
- **Hard parts:** Async log, level routing.

### O24. Design Rate Limiter (LLD)
- **Class:** RateLimiter, TokenBucket, Strategy.
- **Patterns:** Strategy (algo), Decorator.
- **Hard parts:** Distributed sync, sliding window.

### O25. Design Concurrent HashMap
- **Class:** Segment, Bucket.
- **Patterns:** Lock-striping.
- **Hard parts:** Resize during concurrent access.

### O26. Design Producer-Consumer Queue
- **Class:** BoundedQueue.
- **Patterns:** Monitor.
- **Hard parts:** Wake-ups, fairness.

### O27. Design Thread Pool
- **Class:** ThreadPool, Worker, TaskQueue.
- **Patterns:** Worker, Command.
- **Hard parts:** Graceful shutdown, rejection policy.

### O28. Design Connection Pool
- **Class:** ConnectionPool, ConnectionFactory.
- **Patterns:** Object Pool.
- **Hard parts:** Stale connection eviction, leak detection.

### O29. Design Tic-Tac-Toe with N×N Board
- **Class:** Game, Board, WinChecker.
- **Patterns:** Strategy.
- **Hard parts:** Efficient win check via row/col/diag counters.

### O30. Design File System (Unix-like)
- **Class:** FileSystem, INode, Directory, File.
- **Patterns:** Composite (tree).
- **Hard parts:** Path resolution, perms.

### O31. Design In-Memory File System
- **Class:** Node (file/dir), Path resolver.
- **Patterns:** Composite, Visitor.
- **Hard parts:** ls, mkdir, addContent.

### O32. Design URL Class
- **Class:** URL, Scheme, Host, Path, Query.
- **Patterns:** Builder.
- **Hard parts:** Encoding, parsing.

### O33. Design Splitwise (Bill Split)
- **Class:** User, Group, Expense, Balance.
- **Patterns:** Strategy (split type), Observer.
- **Hard parts:** Multi-currency, settlement minimization.

### O34. Design Snake Game
- **Class:** Game, Snake, Food, Board.
- **Patterns:** State.
- **Hard parts:** Self-collision, growing tail.

### O35. Design Online Auction System
- **Class:** Auction, Item, Bid, Bidder.
- **Patterns:** Observer (bid), Strategy (auction type).
- **Hard parts:** Anti-snipe extension, concurrent bids.

### O36. Design Stock Exchange Order Matching
- **Class:** OrderBook, Order, Trade.
- **Patterns:** Singleton (book per symbol), Strategy.
- **Hard parts:** Price-time priority, partial fill.

### O37. Design Tic-Tac-Toe Online Multiplayer
- **Class:** Game, Player, Network, GameState.
- **Patterns:** Observer.
- **Hard parts:** State sync across sockets.

### O38. Design Calendar / Scheduler
- **Class:** Calendar, Event, RecurrenceRule.
- **Patterns:** Iterator (occurrences).
- **Hard parts:** RRULE parsing, conflict detect.

### O39. Design Music Streaming Service (LLD)
- **Class:** Song, Album, Playlist, User, Subscription.
- **Patterns:** Decorator (subscription), Iterator.
- **Hard parts:** Offline downloads, queue.

### O40. Design Notification Service (LLD)
- **Class:** Notification, Channel (Email/SMS/Push), User, Preferences.
- **Patterns:** Strategy (channel), Observer.
- **Hard parts:** Quiet hours, fanout.

### O41. Design Bank ATM (Detailed LLD)
- **Class:** ATM, Card, Account, Bank, CashBin.
- **Patterns:** State, Chain (auth steps).
- **Hard parts:** Concurrency on shared bin, denomination.

### O42. Design Coffee Vending Machine (with multiple drinks)
- **Class:** Machine, DrinkRecipe, Ingredient, Inventory.
- **Patterns:** Builder (drink), Decorator (extras).
- **Hard parts:** Concurrent purchases, ingredient depletion.

### O43. Design Chess Clock
- **Class:** ChessClock, Player, Mode (Bullet/Blitz/Increment).
- **Patterns:** Strategy.
- **Hard parts:** Increment vs delay modes.

### O44. Design CardGame Framework
- **Class:** Card, Deck, Player, Game.
- **Patterns:** Template (Game).
- **Hard parts:** Reusable for poker/bridge/uno.

### O45. Design UNO Game
- **Class:** UnoGame, Player, Card (color/value), Deck.
- **Patterns:** State.
- **Hard parts:** Special cards (skip/reverse/wild).

### O46. Design Stack Overflow (LLD)
- **Class:** Question, Answer, Comment, Tag, User, Vote.
- **Patterns:** Observer (notif), Composite.
- **Hard parts:** Reputation calc, accepted answer.

### O47. Design Twitter (LLD-only)
- **Class:** User, Tweet, Timeline, Follow.
- **Patterns:** Observer (follow), Strategy (timeline).
- **Hard parts:** In-memory feed for top-N.

### O48. Design Browser History
- **Class:** History, Tab, Stack, ForwardStack.
- **Patterns:** Memento, Stack.
- **Hard parts:** Multi-tab, time-travel.

### O49. Design Text Editor (Undo/Redo)
- **Class:** Editor, Buffer, Command, History.
- **Patterns:** Command, Memento.
- **Hard parts:** Multi-cursor, autosave.

### O50. Design Whatsapp / Messenger (LLD)
- **Class:** User, Chat, GroupChat, Message, Status.
- **Patterns:** Observer, Composite (group).
- **Hard parts:** Read receipts, group admin perms.

---

## 🎯 Mock Interview o'tkazganda

1. **Random pick:** har domendan 1 ta tanlang.
2. **Vaqt:** 45–60 daqiqa, framework bo'yicha (yuqorida).
3. **Yozib oling** va keyin tahlil qiling.
4. **Doim trade-off** justify qiling — "bunday qildim, chunki..."
5. **Boshqa variantni** ham aytib o'ting — "alternativa: X, lekin Y sababdan ushbusini tanladim."

## 📚 Qo'shimcha o'qish

Bu masalalarni chuqurroq tushunish uchun:
- *System Design Interview* Vol. 1 & 2 — Alex Xu
- *Designing Data-Intensive Applications* — Martin Kleppmann
- ByteByteGo, Hello Interview, System Design School (yuqoridagi sources)

---

[← README](./README.md) · [Sources →](./05-sources.md)
