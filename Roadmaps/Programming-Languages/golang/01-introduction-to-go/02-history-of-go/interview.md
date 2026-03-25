# History of Go — Interview Questions

## Table of Contents

1. [Junior Level](#1-junior-level)
2. [Middle Level](#2-middle-level)
3. [Senior Level](#3-senior-level)
4. [Scenario-Based Questions](#4-scenario-based-questions)
5. [FAQ](#5-faq)

---

## 1. Junior Level

### Savol 1: Go tili qachon va kim tomonidan yaratilgan?

<details>
<summary>Javob</summary>

Go 2007-yilda **Google** ichida loyihalashtirilgan va 2009-yil 10-noyabrda ochiq manba sifatida e'lon qilingan. Yaratuvchilar:

- **Rob Pike** — til dizayni, concurrency modeli (UTF-8, Plan 9 yaratuvchisi)
- **Ken Thompson** — kompilyator, runtime (Unix, C tili yaratuvchisi, Turing Award laureati)
- **Robert Griesemer** — til spetsifikatsiyasi, parser (V8 JavaScript engine, Java HotSpot)

Go 1.0 birinchi barqaror versiya 2012-yil mart oyida chiqarildi.
</details>

### Savol 2: Go nima uchun yaratildi? Qanday muammo hal qilindi?

<details>
<summary>Javob</summary>

Google muhandislari quyidagi muammolarga duch kelgan:

1. **C++ kompilyatsiya vaqti juda uzoq** — katta loyihalar soatlab kompilyatsiya qilinar edi
2. **Dependency boshqaruvi murakkab** — C/C++ da header fayllari va linking muammolari
3. **Ko'p yadroli protsessorlardan foydalanish qiyin** — thread boshqaruvi murakkab va xatoga moyil
4. **Dasturchilar samaradorligi past** — murakkab tillar o'rganishga ko'p vaqt talab qilardi

Go bu muammolarni hal qildi: tez kompilyatsiya, sodda dependency tizimi, built-in concurrency (goroutine + channel), va minimalist sintaksis.
</details>

### Savol 3: Go 1 Compatibility Promise nima?

<details>
<summary>Javob</summary>

Go 1 Compatibility Promise — Go 1.0 (2012) bilan birga e'lon qilingan kafolat. U quyidagini anglatadi:

**Go 1.x versiyasida to'g'ri ishlaydigan dastur Go 1.y (y > x) versiyasida ham to'g'ri ishlaydi.**

Bu kafolat quyidagilarga tegishli:
- Til sintaksisi va semantikasi
- Standart kutubxona API'lari
- `go` buyrug'i va build tizimi

Tegishli emas:
- `unsafe` paketi orqali ishlaydigan kod
- Undocumented behavior (masalan, map iteration tartibi)
- Bug'lar (tuzatilishi mumkin)
</details>

### Savol 4: Go'ning qaysi versiyasida Generics qo'shildi va nima uchun shuncha kech?

<details>
<summary>Javob</summary>

Generics **Go 1.18** da 2022-yil mart oyida qo'shildi — Go ochiq manba bo'lganidan 13 yil keyin.

**Nima uchun kech qo'shildi:**
1. Go jamoasi soddalik falsafasini buzmaslik uchun ehtiyotkor bo'ldi
2. Noto'g'ri generics dizayni tilni murakkablashtirishi mumkin edi (Java type erasure muammolari)
3. Ko'p use case'lar `interface{}` bilan hal qilinardi
4. Bir nechta prototip sinab ko'rildi (GJ, contracts, type parameters)
5. Oxirida "type parameters with constraints" dizayni tanlandi
</details>

### Savol 5: Go Modules nima va u nimani almashtirdi?

<details>
<summary>Javob</summary>

**Go Modules** — Go 1.11 (2018) da joriy etilgan dependency boshqaruv tizimi. U **GOPATH** tizimini almashtirdi.

**GOPATH muammolari:**
- Barcha kodlar `$GOPATH/src` ichida bo'lishi kerak edi
- Versioning tizimi yo'q edi — faqat oxirgi commit
- Bir vaqtda bitta loyiha uchun bitta dependency versiya

**Go Modules afzalliklari:**
- Istalgan katalogda loyiha yaratish mumkin
- `go.mod` — aniq versiyalar ro'yxati
- `go.sum` — integrity checksums
- Reproducible builds
- Module proxy va checksum database
</details>

### Savol 6: Go'ning concurrency modeli qaysi nazariyaga asoslangan?

<details>
<summary>Javob</summary>

Go'ning concurrency modeli **CSP (Communicating Sequential Processes)** nazariyasiga asoslangan. Bu nazariya 1978-yilda **Tony Hoare** tomonidan taklif qilingan.

CSP asosiy g'oyasi: "Don't communicate by sharing memory; share memory by communicating."

Go'da bu goroutine'lar va channel'lar orqali amalga oshiriladi:
- **Goroutine** — yengil thread (2-8 KB stack)
- **Channel** — goroutine'lar orasida xavfsiz ma'lumot almashish

Rob Pike oldin CSP asosida Newsqueak va Limbo tillarini yaratgan — Go'ning concurrency modeli ana shu tajribaning davomi.
</details>

### Savol 7: Go kompilyatori qaysi versiyada o'zi Go'da qayta yozildi?

<details>
<summary>Javob</summary>

**Go 1.5** (2015) da kompilyator to'liq Go'da qayta yozildi. Bu "self-hosting" deyiladi.

**Jarayon:**
1. Go 1.0-1.4: kompilyator C da yozilgan edi
2. Go 1.4: `c2go` tool yordamida C kodi avtomatik Go'ga tarjima qilindi
3. Go 1.5: butunlay Go'da yozilgan kompilyator ishga tushdi

Bu muhim milestone — til o'zini o'zi kompilyatsiya qila boshladi. Hozir Go'ni build qilish uchun oldingi Go versiyasi kerak (bootstrap).
</details>

---

## 2. Middle Level

### Savol 1: Go module tizimining Minimal Version Selection (MVS) algoritmi qanday ishlaydi va u npm/pip dan qanday farq qiladi?

<details>
<summary>Javob</summary>

**MVS (Minimal Version Selection)** — Go modules'ning dependency resolution algoritmi:

- MVS barcha `go.mod` fayllarini o'qiydi va har bir dependency uchun **eng kichik versiyani** tanlaydi, bu barcha talablarni qondiadi
- Masalan: A `go.mod` da `require X v1.2.0`, B `go.mod` da `require X v1.3.0` — MVS **v1.3.0** tanlaydi (har ikkala talabni qondiradi)

**npm/pip dan farqi:**
- npm/pip eng **yangi** mos versiyani tanlaydi — bu kutilmagan yangiliklarga olib kelishi mumkin
- MVS **deterministik** — bir xil `go.mod` fayllardan har doim bir xil natija
- MVS tezroq — solver muammolari yo'q (NP-hard emas)
- MVS reproducible — `go.sum` bilan birga 100% takrorlanadigan build

Bu Russ Cox tomonidan dizayn qilingan va Go'ning barqarorlik falsafasiga mos.
</details>

### Savol 2: GODEBUG muhit o'zgaruvchisi Go evolyutsiyasida qanday rol o'ynaydi?

<details>
<summary>Javob</summary>

**GODEBUG** — Go 1.21 dan boshlab Go'ning asosiy evolyutsiya mexanizmiga aylangan muhit o'zgaruvchisi.

**Qanday ishlaydi:**
1. Yangi Go versiya behavior o'zgarishi joriy etadi
2. `go.mod` dagi `go` directive asosida yangi/eski behavior tanlanadi
3. GODEBUG individual o'zgarishlarni override qilish imkonini beradi

**Misol:**
```
# Go 1.22 da loop variable semantikasi o'zgardi
# Eski behavior qaytarish:
GODEBUG=loopvar=1 go run main.go

# Go 1.22 da ServeMux pattern matching o'zgardi
GODEBUG=httpmuxgo121=1 go run main.go
```

**Strategic ahamiyati:** GODEBUG Go jamoasiga Go 1 compatibility promise'ni saqlagan holda tilni evolyutsiya qilish imkonini beradi.
</details>

### Savol 3: Go 1.17 da register-based ABI qanday ta'sir ko'rsatdi?

<details>
<summary>Javob</summary>

Go 1.17 da calling convention **stack-based** dan **register-based** ga o'tdi:

**Oldin (stack-based):**
- Funksiya argumentlari va natijalar stack'ga yozilardi
- Har bir funksiya chaqiruvi memory read/write talab qilardi

**Keyin (register-based):**
- Argumentlar CPU registr'larida uzatiladi (AX, BX, CX, DI, SI, R8-R11)
- Natijalar ham registr'larda qaytariladi

**Ta'siri:**
- ~5% umumiy performance yaxshilanishi
- Stack memory usage kamayishi
- Function call overhead kamayishi

**Backward compatibility:** Eski assembly kod uchun ABI0 (eski) va ABIInternal (yangi) ikki ABI bir vaqtda mavjud. Wrapper funksiyalar ular orasida ko'chiradi.
</details>

### Savol 4: Go 1.22 dagi loop variable semantics o'zgarishi nimani hal qildi?

<details>
<summary>Javob</summary>

**Muammo (Go 1.21 va oldingi):**
```go
for _, v := range values {
    go func() {
        fmt.Println(v) // XATO: barcha goroutine'lar oxirgi v ni chop etadi
    }()
}
```
Sabab: `v` loop davomida bitta o'zgaruvchi — barcha closure'lar bir xil `v` ga murojaat qiladi.

**Yechim (Go 1.22+):**
Har bir iteratsiyada `v` **yangi o'zgaruvchi** sifatida yaratiladi. Har bir closure o'z `v` nusxasiga ega.

**Qanday implement qilingan:**
- Faqat `go.mod` da `go 1.22`+ ko'rsatilgan modullar uchun
- `GODEBUG=loopvar=1` bilan eski behavior'ga qaytish mumkin
- Bu Go'ning yangi "per-module evolution" yondashuvi
</details>

### Savol 5: `go.mod` dagi `go` directive va `toolchain` directive farqi nima?

<details>
<summary>Javob</summary>

**`go` directive:**
```
go 1.23
```
- Modulning **minimal Go versiyasini** belgilaydi
- Til xususiyatlari va GODEBUG default'larini aniqlaydi
- Go 1.21+ da **enforced** — agar Go versiyasi past bo'lsa, build qilmaydi

**`toolchain` directive (Go 1.21+):**
```
toolchain go1.23.4
```
- Aniq **kompilyator versiyasini** belgilaydi
- `GOTOOLCHAIN=auto` bilan birga ishlaydi
- Agar kerakli versiya o'rnatilmagan bo'lsa, avtomatik yuklab olinadi

**Farq:** `go 1.23` "bu modul Go 1.23+ talab qiladi" degani. `toolchain go1.23.4` "bu modulni build qilish uchun aniq go1.23.4 ishlatilsin" degani.
</details>

### Savol 6: Go module proxy va checksum database qanday xavfsizlikni ta'minlaydi?

<details>
<summary>Javob</summary>

**Module proxy (`proxy.golang.org`):**
- Barcha ochiq modullarni cache'laydi
- Tez yuklab olish imkonini beradi
- Manba repo o'chirilsa ham modul mavjud qoladi

**Checksum Database (`sum.golang.org`):**
- Global Merkle tree — barcha modullarning checksum'lari
- Har bir `go get` chaqiruvida checksum tekshiriladi
- `go.sum` fayldagi checksum bilan global DB solishtiriladi

**Ximoya:**
1. **Tampering** — agar biror kishi modul kodini o'zgartirsa, checksum mos kelmaydi
2. **Supply chain attack** — checksum DB transparent va audit qilinadi
3. **Deletion** — proxy'da cache, repo o'chirilsa ham ishlaydi

**Sozlash:**
```bash
GONOSUMCHECK=*.internal.com  # Private modullar uchun
GONOSUMDB=*.internal.com     # Private modullar sum DB dan tashqari
GOPROXY=direct               # XAVFLI — proxy bypass
```
</details>

---

## 3. Senior Level

### Savol 1: Go 1 Compatibility Promise orqaga mos bo'lmagan o'zgarishlarni qanday boshqaradi? GODEBUG, go directive, va GOEXPERIMENT'ning o'zaro aloqasini tushuntiring.

<details>
<summary>Javob</summary>

Go jamoasi 3 qatlamli strategiya ishlatadi:

**1. `go` directive (`go.mod`):**
- Modul qaysi Go versiyaga mo'ljallangan ekanligini bildiradi
- Go 1.21+ da yangi behavior faqat `go 1.N+` ko'rsatilgan modullarda yoqiladi
- Misol: loop variable semantics faqat `go 1.22+` modullarda o'zgaradi

**2. GODEBUG:**
- Individual behavior o'zgarishlarini override qilish
- `GODEBUG=loopvar=1` — eski loop behavior'ni qaytarish
- Vaqtincha ishlatish uchun — migratsiya davri
- 2 major versiyadan keyin eski behavior o'chirilishi mumkin

**3. GOEXPERIMENT:**
- Hali rasmiy bo'lmagan xususiyatlarni sinash
- `GOEXPERIMENT=rangefunc` — Go 1.22 da range over func sinash
- Production'da ISHLATMASLIK kerak — Go 1 promise qamrab olmaydi

**O'zaro aloqasi:**
```
GOEXPERIMENT (sinov) -> go directive (rasmiy) -> GODEBUG (override)
```

Bu strategiya Go jamoasiga tilni evolyutsiya qilish imkonini beradi — har bir o'zgarish bosqichma-bosqich joriy etiladi.
</details>

### Savol 2: Go generics'ning GC Shape Stenciling implementation'ini Rust monomorphization va Java type erasure bilan solishtirib tushuntiring.

<details>
<summary>Javob</summary>

| Jihat | Go (GC Shape Stenciling) | Rust (Monomorphization) | Java (Type Erasure) |
|-------|-------------------------|------------------------|---------------------|
| **Mexanizm** | Shape bo'yicha guruhlanish | Har bir tur uchun alohida kod | Runtime'da tur ma'lumoti o'chiriladi |
| **Binary hajmi** | O'rtacha | Katta (code bloat) | Kichik |
| **Performance** | ~95% optimal | Maximal | Past (boxing, casting) |
| **Kompilyatsiya** | Tez | Sekin | Tez |
| **Pointer turlari** | Bitta shared funksiya | Alohida funksiyalar | N/A |
| **Dictionary** | Runtime dictionary passing | Yo'q (compile-time) | Yo'q (erasure) |

**Go'ning yondashuvi:**
- Pointer turlari (8 byte) bitta "GC shape" ulashadi — bitta funksiya + dictionary
- Value turlari (int, float64, string) alohida shape — alohida funksiyalar
- Dictionary: type descriptors va method tables — ko'p hollarda kompilyator inline qiladi

**Trade-off:** Go kompilyatsiya tezligini va o'rtacha binary hajmini saqlab qoldi, performance deyarli monomorphization darajasida.
</details>

### Savol 3: Katta kompaniyada Go versiyasini yangilash strategiyasini qanday rejalashtirasiz?

<details>
<summary>Javob</summary>

**Strategik yondashuv:**

**1. Baholash (1-2 hafta):**
- Release notes o'qish
- GODEBUG o'zgarishlar ro'yxatini tuzish
- Deprecated API'larni aniqlash
- `govulncheck` yangi vulnerability'lar tekshirish

**2. Sinov (2-4 hafta):**
- CI/CD da yangi Go versiya bilan testlar ishga tushirish
- Benchmark'lar — performance regression tekshirish
- GODEBUG flag'larni aniqlash — eski behavior kerak bo'lgan joylar
- Edge case'larni sinash

**3. Staging (1-2 hafta):**
- Staging muhitda deploy
- `go.mod` da `toolchain` directive yangilash
- Monitoring — latency, error rate, memory usage
- GODEBUG override'larni qo'llash

**4. Production (phased rollout):**
- Canary deploy — 1-5% traffic
- Asta-sekin kengaytirish
- Rollback plan tayyor
- 1-2 hafta kuzatish

**5. Tozalash (1-2 hafta):**
- GODEBUG override'larni olib tashlash
- Deprecated API'larni yangilash
- `go.mod` da `go` directive yangilash
- Hujjatlarni yangilash

**Vaqt:** Jami ~6-10 hafta. Go'ning 6 oylik release cycle'iga mos keladi.
</details>

### Savol 4: Go'ni 5 yillik strategik loyiha uchun tanlash to'g'ri qarormi? Qanday hollarda Go tanlamasligingiz mumkin?

<details>
<summary>Javob</summary>

**Go ni tanlash TO'G'RI bo'lgan holatlar:**
- Backend/API microservices — Go'ning asosiy kuchi
- DevOps/Infrastructure tooling — Docker, K8s, Terraform misoli
- CLI tools — cross-compile, single binary, tez startup
- High-concurrency tizimlar — goroutine'lar, channel'lar
- Cloud-native dasturlar — container-friendly, kichik image

**Go 1 compatibility promise** 5 yillik investitsiya uchun juda muhim — kod hamon ishlaydi.

**Go ni TANLMASLIK kerak bo'lgan holatlar:**
- **ML/AI** — Python ekotizimi ancha kuchliroq (TensorFlow, PyTorch)
- **GUI desktop** — Go'da GUI framework'lar zaif
- **Real-time tizimlar** — GC pause'lari (Rust/C afzal)
- **Mobile** — Swift (iOS) va Kotlin (Android) native
- **Data Science** — Python/R ekotizimi boyroq
- **Game Development** — C++/Rust/C# (Unity) afzal

**Red flags:**
- Jamoa Go bilmaydi va o'rganishga vaqt yo'q
- Domain Go'ning kuchli tomonlariga mos emas
- Mavjud ekotizim boshqa tilda (masalan, Java Spring katta enterprise'da)
</details>

### Savol 5: Go'ning GC evolyutsiyasi production tizimlarga qanday ta'sir ko'rsatdi? GOGC va GOMEMLIMIT ni qachon va qanday ishlatish kerak?

<details>
<summary>Javob</summary>

**GC evolyutsiya ta'siri:**
- **Go 1.0-1.4**: 10-300ms pause — web server'lar uchun muammo
- **Go 1.5**: Concurrent GC — <10ms, katta breakthrough
- **Go 1.8**: Hybrid write barrier — <1ms, production-ready
- **Go 1.19**: GOMEMLIMIT — memory-efficient GC tuning

**GOGC (default=100):**
```
Keyingi GC = HeapLive * (1 + GOGC/100)
GOGC=100: heap 2x o'sganda GC
GOGC=200: heap 3x o'sganda GC (kamroq GC, ko'proq memory)
GOGC=50: heap 1.5x o'sganda GC (ko'proq GC, kamroq memory)
GOGC=off: GC o'chirilgan (XAVFLI)
```

**GOMEMLIMIT (Go 1.19+):**
```
GOMEMLIMIT=1GiB: Go runtime 1 GiB dan oshmasligi kerak
GC target = min(GOGC target, GOMEMLIMIT)
```

**Qachon ishlatish:**
- **GOGC oshirish**: CPU-bound dastur, memory ko'p, GC CPU sarflayotgan bo'lsa
- **GOGC kamaytirish**: Memory-bound, container memory limit yaqin
- **GOMEMLIMIT**: Container muhitda (K8s pod memory limit - 10-20% buffer)
- **GOMEMLIMIT + GOGC=off**: Memory-bound dastur, GC faqat limit yaqinlashganda

**Best practice:** GOMEMLIMIT = container_memory_limit * 0.8 (20% OS/runtime uchun qoldirish)
</details>

### Savol 6: Go'ning Swiss table map (Go 1.24) implementation qanday performance yaxshilanish beradi?

<details>
<summary>Javob</summary>

**Swiss table vs eski map:**

**Eski map (Go 1.0-1.23):**
- Bucket-based hash table
- Har bir bucket 8 key-value pair
- Linear probing bucket ichida
- Overflow bucket'lar linked list

**Swiss table (Go 1.24+):**
- Group-based design (16 slot per group)
- Metadata bytes: har bir slot uchun 1 byte (hash ning yuqori 7 bit + empty/deleted flag)
- SIMD-friendly: bir instruction bilan 16 metadata tekshirish
- Better cache locality: metadata va data alohida

**Performance ta'siri:**
- Lookup: ~15-30% tezroq
- Insert: ~10-20% tezroq
- Delete: yaxshiroq (tombstone o'rniga group metadata)
- Memory: ~10% kam (yaxshiroq fill ratio)

**Go 1 compatibility:** External behavior bir xil qoladi. Faqat `unsafe` orqali internal struct'larga murojaat qilgan kod buziladi (bu Go 1 promise tashqarisida).
</details>

---

## 4. Scenario-Based Questions

### Scenario 1: Go versiyasini yangilash paytida production muammo

**Savol:** Sizning jamoangiz Go 1.21 dan Go 1.22 ga o'tdi. Production'da ba'zi HTTP handler'lar ishlamay qoldi. Sababini qanday aniqlaysiz va qanday hal qilasiz?

<details>
<summary>Javob</summary>

**Ehtimoliy sabab:** Go 1.22 da `net/http` ServeMux pattern matching o'zgardi. Yangi pattern syntax eski route'lar bilan conflict qilishi mumkin.

**Diagnostika:**
1. Error log'larni tekshirish — "pattern conflict" xabarlari
2. `GODEBUG=httpmuxgo121=1` o'rnatib, eski behavior'ga qaytish
3. Agar muammo hal bo'lsa — sabab ServeMux o'zgarishi

**Hal qilish:**
1. **Qisqa muddatli:** `GODEBUG=httpmuxgo121=1` o'rnatish
2. **Uzoq muddatli:** Route'larni Go 1.22 yangi pattern formatiga moslashtirish:
   - `"/api/users/"` -> `"GET /api/users/{id}"`
   - Eski wildcard pattern'larni yangi syntax ga o'zgartirish
3. **Testing:** Barcha route'lar uchun integration test yozish
4. **Monitoring:** GODEBUG counter'larni kuzatish — `godebug/non-default-behavior/httpmuxgo121:events`
</details>

### Scenario 2: Dependency vulnerability

**Savol:** `govulncheck` sizning loyihangizda critical vulnerability topdi — lekin bu dependency to'g'ridan-to'g'ri ishlatilmaydi, faqat transitiv. Qanday harakat qilasiz?

<details>
<summary>Javob</summary>

**Qadamlar:**

1. **Tahlil:**
   ```bash
   govulncheck ./...          # Qaysi kod ta'sirlangan
   go mod why <vulnerable-pkg> # Nima uchun bu dependency bor
   go mod graph | grep <pkg>   # Kim bu dependency'ni talab qilmoqda
   ```

2. **Baholash:**
   - Vulnerability haqiqatan ta'sir qiladimi? (`govulncheck` faqat chaqirilgan funksiyalarni ko'rsatadi)
   - Agar chaqirilmasa — xavf past, lekin yangilash tavsiya etiladi

3. **Hal qilish:**
   ```bash
   # Direct dependency'ni yangilash
   go get <direct-dependency>@latest
   go mod tidy

   # Agar direct dependency yangilanmagan bo'lsa:
   # replace directive (vaqtincha)
   go mod edit -replace <vulnerable>=<fixed-version>
   ```

4. **Profilaktika:**
   - CI/CD pipeline'ga `govulncheck` qo'shish
   - Dependabot yoki Renovate bot o'rnatish
   - Minimal dependency siyosati
</details>

### Scenario 3: Generics migration

**Savol:** Sizning jamoangiz 500K+ Go kodli loyihada `interface{}` dan generics'ga migratsiya qilmoqchi. Strategiyangiz qanday?

<details>
<summary>Javob</summary>

**Bosqichma-bosqich strategiya:**

**1-bosqich: Tahlil (1 hafta)**
- `interface{}` ishlatilgan joylarni topish: `grep -r "interface{}" --include="*.go"`
- `any` ga o'tkazish mumkin bo'lgan joylarni aniqlash (backward compatible)
- Generic funksiya/tur kerak bo'lgan joylarni prioritetlash

**2-bosqich: `any` migration (1-2 hafta)**
```go
// OLDIN:
func Process(data interface{}) interface{} { ... }
// KEYIN (backward compatible):
func Process(data any) any { ... }
```
- Bu 100% backward compatible — `any = interface{}`
- `gofmt` bilan avtomatik

**3-bosqich: Generic kutubxonalar (2-4 hafta)**
```go
// Yangi generic utility'lar
func Map[T, U any](slice []T, fn func(T) U) []U { ... }
func Filter[T any](slice []T, fn func(T) bool) []T { ... }
```
- Yangi paket yaratish (v2 yoki internal)
- Eski funksiyalarni deprecated qilish

**4-bosqich: Asta-sekin migratsiya (ongoing)**
- Har bir PR da 1-2 funksiyani migratsiya qilish
- Code review checklist yangilash
- Yangi kod faqat generics bilan

**QOIDALAR:**
- Bir vaqtda HAMMA NARSANI o'zgartirmang
- Generics overuse'dan saqlaning — concrete types osonroq debug qilinadi
- `go vet` va testlar har qadamda ishlatilsin
</details>

### Scenario 4: Go vs Rust tanlash

**Savol:** Sizning kompaniyangiz yangi network proxy yozmoqchi. CTO Go va Rust o'rtasida ikkilanmoqda. Go'ning tarixiy kontekstida qanday argument keltirasiz?

<details>
<summary>Javob</summary>

**Go TARAFDOR argumentlar:**

1. **Tarixiy muvaffaqiyat:** Envoy proxy (Lyft), Traefik, Caddy — barchasi Go'da. Cloudflare global edge proxy'sini Go'da yozgan
2. **Developer productivity:** Go o'rganish 2-4 hafta, Rust 3-6 oy. Hiring osonroq
3. **Go 1 compatibility:** 5+ yillik loyiha — barqarorlik kafolatlanadi
4. **Ecosystem:** net/http, gRPC, TLS — barcha network primitive'lar standart kutubxonada
5. **GC:** Go 1.8+ <1ms pause — network proxy uchun yetarli
6. **Goroutine'lar:** 100K+ concurrent connections oson boshqarish

**Rust TARAFDOR argumentlar:**
1. **Nol-cost abstraksiya:** Har bir nanosecond muhim bo'lsa
2. **Memory xavfsizligi GC siz:** Predictable latency
3. **WASM:** Edge computing uchun kichik binary
4. **Tokio ecosystem:** Async I/O juda yaxshi

**Tavsiyam:**
- **Go** — agar jamoa kichik (5-15 kishi), deadline qisqa, 99.9% latency yetarli
- **Rust** — agar jamoa tajribali, har bir microsecond muhim, real-time talablar

Network proxy uchun Go ko'p hollarda to'g'ri tanlov — Cloudflare, Traefik, Caddy buning isboti.
</details>

### Scenario 5: Legacy Go loyihani modernizatsiya

**Savol:** 2015-yilda yozilgan Go loyiha (`go 1.5`, GOPATH, `interface{}` ko'p, vendor/ katalog) ni zamonaviy Go (1.23+) ga ko'chirish kerak. Rejangiz?

<details>
<summary>Javob</summary>

**Bosqichlar:**

**1. Go Modules'ga o'tish (1-2 kun):**
```bash
go mod init github.com/company/project
go mod tidy
# vendor/ katalogni saqlash (ixtiyoriy):
go mod vendor
```

**2. go.mod versiyani bosqichma-bosqich oshirish:**
```
go 1.13  # modules default
go 1.16  # io/ioutil deprecated
go 1.18  # generics, any alias
go 1.21  # toolchain, min/max
go 1.22  # loopvar fix
go 1.23  # range over func
```
Har bir bosqichda test ishga tushirish!

**3. Deprecated API'larni yangilash (1-2 hafta):**
```go
// io/ioutil -> os, io
// interface{} -> any
// golang.org/x/net/context -> context
// sort.Slice -> slices.Sort (Go 1.21+)
```

**4. Yangi xususiyatlardan foydalanish (ongoing):**
```go
// Generics uchun mos joylar
// Range over int/func
// Enhanced ServeMux patterns
// errors.Is/As
```

**5. CI/CD yangilash:**
- Go versiya pinning (`go-version-file: 'go.mod'`)
- `govulncheck` qo'shish
- `staticcheck` qo'shish

**Muhim:** Har bir o'zgarish alohida PR, testlar bilan. Bir vaqtda hammani o'zgartirmang!
</details>

---

## 5. FAQ

### Savol 1: "Go nima uchun generics yo'q?" — bu savol hali berilishi mumkinmi?

<details>
<summary>Javob</summary>

**Hozirgi holat:** Go 1.18 (2022) dan beri generics MAVJUD. Lekin bu savol hamon interview'da berilishi mumkin — intervyuer Go'ning dizayn falsafasini tushunishingizni tekshirmoqda.

**Intervyuer nimani qidiradi:**
1. Generics 2022-da qo'shilganini bilish
2. Nima uchun 13 yil kutilganini tushuntira olish (soddalik falsafasi)
3. GC Shape Stenciling vs monomorphization vs type erasure farqini bilish
4. Generics'ni qachon ishlatish va qachon ishlatMASLIK kerakligini bilish

**Ideal javob:** "Go 1.18 da generics qo'shildi. 13 yil kutilgani — Go jamoasi soddalikni buzmaslik uchun to'g'ri dizayn topishi kerak edi. Hozir generics bor, lekin Go falsafasi saqlanib qoldi — har qanday joyda generics ishlatmaslik kerak, faqat haqiqiy code duplication bo'lganda."
</details>

### Savol 2: "Go'ni o'rganishga arzidimi?" — buni qanday javob berasiz?

<details>
<summary>Javob</summary>

**Intervyuer nimani qidiradi:**
1. Go'ning bozordagi o'rnini bilish
2. Ob'ektiv baholash qobiliyati
3. Go'ning kuchli va zaif tomonlarini bilish

**Ideal javob structure:**
1. **Bozor:** Cloud-native, DevOps, backend — Go talab yuqori va o'sib bormoqda
2. **O'rganish tezligi:** 2-4 hafta — eng tez o'rganiladigan tillardan biri
3. **Maosh:** Go developer'lar maoshi yuqori (ayniqsa DevOps/SRE)
4. **Ekotizim:** Docker, K8s, Terraform — Go bilgan kishi bu tool'larni ham chuqurroq tushunadi
5. **Cheklovlar:** ML/AI, GUI, mobile — Go mos emas

**Raqamlar:** Stack Overflow Survey'da Go eng ko'p "wanted" tillardan biri. TIOBE index'da top 10.
</details>

### Savol 3: "Go 2.0 chiqadimi?" — qanday javob berasiz?

<details>
<summary>Javob</summary>

**Intervyuer nimani qidiradi:**
1. Go evolyutsiya strategiyasini tushunishingizni
2. Go 1 compatibility promise'ni bilishingizni
3. GODEBUG/go directive mexanizmini tushunishingizni

**Ideal javob:** "Go 2.0 rasmiy ravishda chiqmaydi. Go jamoasi 'incremental changes' strategiyasini tanladi. Go 2 g'oyalari (generics, error handling yaxshilash) Go 1.x versiyalariga bosqichma-bosqich qo'shilmoqda. Buning sababi — Go 1 compatibility promise'ni saqlash va ekotizimni buzmASlIK. Yangi mexanizmlar (GODEBUG, go directive) orqaga mos bo'lmagan o'zgarishlarni ham bosqichma-bosqich joriy etish imkonini beradi."
</details>

### Savol 4: "Go va Rust — qaysi biri yaxshiroq?" — qanday javob berasiz?

<details>
<summary>Javob</summary>

**Intervyuer nimani qidiradi:**
1. Ob'ektiv taqqoslash qobiliyati (fanboy bo'lmaslik)
2. Trade-off tushunish
3. Use case asosida tanlash qobiliyati

**Ideal javob structure:**

"Bu savolning javob yo'q, chunki turli maqsadlar uchun turli tillar. Go va Rust turli muammolarni hal qiladi:"

| Jihat | Go tanlang | Rust tanlang |
|-------|-----------|--------------|
| Maqsad | Soddalik, tezkor ishlab chiqish | Maximal performance, xavfsizlik |
| Jamoa | Kichik-o'rta, backend | O'rta-katta, systems |
| Memory | GC (avtomatik) | Ownership (manual, safe) |
| Latency | <1ms GC pause (yetarli) | 0 GC, predictable |
| Learning | 2-4 hafta | 3-6 oy |
| Use case | Microservices, CLI, API | OS, embedded, game engine |

"Ikkisi ham ajoyib tillar. Men loyiha talablariga qarab tanlayman."
</details>

### Savol 5: "Go'ning kelajagi qanday?" — qanday javob berasiz?

<details>
<summary>Javob</summary>

**Intervyuer nimani qidiradi:**
1. Go ekotizimini kuzatib borishingizni
2. Texnologiya trendlarini tushunishingizni
3. Strategic fikrlash qobiliyatini

**Ideal javob:**

"Go'ning kelajagi yorqin, bir nechta yo'nalishda:"

1. **Generics yetuklanishi** — generic standard library (slices, maps, cmp), yangi pattern'lar
2. **Iterator pattern** — range over func (Go 1.23) bilan funksional dasturlash pattern'lari
3. **Performance** — PGO yetuklanishi, Swiss table (Go 1.24), GC yaxshilanishi
4. **Tooling** — govulncheck, telemetry, go vet yaxshilanishi
5. **AI/ML integration** — Go'da inference server'lar (lekin training Python'da qoladi)
6. **WASM** — WebAssembly support yaxshilanmoqda
7. **Cloud-native** — Go CNCF ekotizimining tili bo'lib qoladi

"Go 1 compatibility promise tufayli bugungi investitsiya 10+ yil davomida qaytadi."
</details>
