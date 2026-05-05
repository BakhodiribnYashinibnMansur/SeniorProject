# Functions — Senior Level

## Table of Contents

1. [Introduction](#introduction)
2. [Function Design for Large Systems](#function-design-for-large-systems)
3. [Dependency Injection Through Functions](#dependency-injection-through-functions)
4. [Middleware Pattern](#middleware-pattern)
5. [Memoization as a Function Wrapper](#memoization-as-a-function-wrapper)
6. [Functional Programming Patterns in Imperative Languages](#functional-programming-patterns-in-imperative-languages)
7. [Concurrent Function Execution Patterns](#concurrent-function-execution-patterns)
8. [Code Examples](#code-examples)
9. [Summary](#summary)

---

## Introduction

> Focus: "How to design functions for real-world, production systems?" and "What patterns make code maintainable at scale?"

At the senior level, functions become architectural tools. You think about function boundaries, side effects, testability, and how functions compose into larger systems. You use patterns like dependency injection, middleware, and memoization not as textbook exercises but as daily tools.

---

## Function Design for Large Systems

### Single Responsibility Principle (SRP)

A function should have **one reason to change**. If your function does two things, split it.

#### Bad — does too much

```go
// BAD: fetches, validates, transforms, and saves
func processOrder(orderID string) error {
    resp, err := http.Get("https://api.example.com/orders/" + orderID)
    if err != nil { return err }
    defer resp.Body.Close()

    var order Order
    json.NewDecoder(resp.Body).Decode(&order)

    if order.Total <= 0 { return errors.New("invalid total") }
    if order.Items == nil { return errors.New("no items") }

    order.Tax = order.Total * 0.08
    order.Status = "processed"

    db.Save(&order)
    sendEmail(order.CustomerEmail, "Order processed")
    return nil
}
```

#### Good — each function does one thing

```go
func fetchOrder(id string) (Order, error) { /* ... */ }
func validateOrder(o Order) error { /* ... */ }
func calculateTax(o *Order) { o.Tax = o.Total * 0.08 }
func saveOrder(o Order) error { /* ... */ }
func notifyCustomer(email, msg string) error { /* ... */ }

func processOrder(id string) error {
    order, err := fetchOrder(id)
    if err != nil { return fmt.Errorf("fetch: %w", err) }

    if err := validateOrder(order); err != nil {
        return fmt.Errorf("validate: %w", err)
    }

    calculateTax(&order)
    order.Status = "processed"

    if err := saveOrder(order); err != nil {
        return fmt.Errorf("save: %w", err)
    }

    return notifyCustomer(order.CustomerEmail, "Order processed")
}
```

### Pure Functions vs Side Effects

A **pure function** always returns the same output for the same input and has no side effects (no I/O, no mutation of external state).

```go
// Pure — easy to test, easy to reason about
func calculateDiscount(price float64, percentage float64) float64 {
    return price * (percentage / 100.0)
}

// Impure — reads time, writes to DB
func applyDiscount(orderID string) error {
    now := time.Now()
    if now.Weekday() == time.Sunday {
        db.UpdateDiscount(orderID, 20)
    }
    return nil
}

// Better — extract pure logic, inject dependencies
func discountPercentage(day time.Weekday) float64 {
    if day == time.Sunday { return 20 }
    return 0
}
```

### Function Contracts

Document what a function expects (preconditions) and guarantees (postconditions):

```python
def binary_search(arr, target):
    """
    Find target in a sorted array.

    Preconditions:
        - arr must be sorted in ascending order
        - arr must not be None

    Postconditions:
        - Returns index i where arr[i] == target
        - Returns -1 if target not found

    Time: O(log n), Space: O(1)
    """
    lo, hi = 0, len(arr) - 1
    while lo <= hi:
        mid = lo + (hi - lo) // 2
        if arr[mid] == target:
            return mid
        elif arr[mid] < target:
            lo = mid + 1
        else:
            hi = mid - 1
    return -1
```

---

## Dependency Injection Through Functions

Instead of hardcoding dependencies, pass them as function parameters. This makes code testable and flexible.

### Go — inject via function parameters

```go
// HARD TO TEST: depends on real HTTP and real DB
func getUser(id string) (User, error) {
    resp, _ := http.Get("https://api.example.com/users/" + id)
    // parse response...
    db.Save(user)
    return user, nil
}

// EASY TO TEST: dependencies injected as functions
type UserFetcher func(id string) (User, error)
type UserSaver func(user User) error

func getUser(id string, fetch UserFetcher, save UserSaver) (User, error) {
    user, err := fetch(id)
    if err != nil {
        return User{}, fmt.Errorf("fetch user: %w", err)
    }
    if err := save(user); err != nil {
        return User{}, fmt.Errorf("save user: %w", err)
    }
    return user, nil
}

// Production
user, err := getUser("123", apiFetcher, dbSaver)

// Test
user, err := getUser("123",
    func(id string) (User, error) { return User{Name: "Mock"}, nil },
    func(u User) error { return nil },
)
```

### Java — inject via functional interfaces

```java
@FunctionalInterface
interface UserFetcher {
    User fetch(String id) throws Exception;
}

@FunctionalInterface
interface UserSaver {
    void save(User user) throws Exception;
}

public User getUser(String id, UserFetcher fetcher, UserSaver saver) throws Exception {
    User user = fetcher.fetch(id);
    saver.save(user);
    return user;
}

// Production
User user = getUser("123", apiClient::fetchUser, repository::save);

// Test
User user = getUser("123",
    id -> new User("Mock", "mock@test.com"),
    u -> {}  // no-op saver
);
```

### Python — inject via callable parameters

```python
def get_user(user_id, fetcher, saver):
    """
    fetcher: Callable[[str], User]
    saver: Callable[[User], None]
    """
    user = fetcher(user_id)
    saver(user)
    return user

# Production
user = get_user("123", api_client.fetch, db.save)

# Test
user = get_user("123",
    fetcher=lambda id: User(name="Mock"),
    saver=lambda u: None
)
```

---

## Middleware Pattern

Middleware wraps a function to add behavior (logging, auth, timing) without modifying the original function.

### Go — HTTP Middleware

```go
// Middleware type: takes a handler, returns a handler
type Middleware func(http.HandlerFunc) http.HandlerFunc

// Logging middleware
func logging(next http.HandlerFunc) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        log.Printf("→ %s %s", r.Method, r.URL.Path)
        next(w, r)
        log.Printf("← %s %s [%v]", r.Method, r.URL.Path, time.Since(start))
    }
}

// Auth middleware
func requireAuth(next http.HandlerFunc) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        token := r.Header.Get("Authorization")
        if token == "" {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }
        // validate token...
        next(w, r)
    }
}

// Rate limiting middleware
func rateLimit(maxPerSecond int) Middleware {
    limiter := rate.NewLimiter(rate.Limit(maxPerSecond), maxPerSecond)
    return func(next http.HandlerFunc) http.HandlerFunc {
        return func(w http.ResponseWriter, r *http.Request) {
            if !limiter.Allow() {
                http.Error(w, "Too Many Requests", http.StatusTooManyRequests)
                return
            }
            next(w, r)
        }
    }
}

// Chain middleware
func chain(handler http.HandlerFunc, middlewares ...Middleware) http.HandlerFunc {
    for i := len(middlewares) - 1; i >= 0; i-- {
        handler = middlewares[i](handler)
    }
    return handler
}

// Usage
http.HandleFunc("/api/data", chain(
    handleGetData,
    logging,
    requireAuth,
    rateLimit(100),
))
```

### Java — Servlet Filters / Function Wrapping

```java
@FunctionalInterface
interface Handler {
    String handle(Map<String, String> request);
}

@FunctionalInterface
interface Middleware {
    Handler apply(Handler next);
}

// Logging middleware
static Middleware logging() {
    return next -> request -> {
        long start = System.nanoTime();
        System.out.println("→ " + request.get("path"));
        String response = next.handle(request);
        long elapsed = (System.nanoTime() - start) / 1_000_000;
        System.out.println("← " + request.get("path") + " [" + elapsed + "ms]");
        return response;
    };
}

// Auth middleware
static Middleware requireAuth() {
    return next -> request -> {
        if (!request.containsKey("token")) {
            return "401 Unauthorized";
        }
        return next.handle(request);
    };
}

// Chain middleware
static Handler chain(Handler handler, Middleware... middlewares) {
    for (int i = middlewares.length - 1; i >= 0; i--) {
        handler = middlewares[i].apply(handler);
    }
    return handler;
}

// Usage
Handler api = chain(
    req -> "data: " + req.get("path"),
    logging(),
    requireAuth()
);
```

### Python — Decorators as Middleware

```python
import time
import functools

# Logging decorator
def logging(func):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        start = time.time()
        print(f"→ Calling {func.__name__}")
        result = func(*args, **kwargs)
        elapsed = time.time() - start
        print(f"← {func.__name__} [{elapsed:.3f}s]")
        return result
    return wrapper

# Auth decorator
def require_auth(func):
    @functools.wraps(func)
    def wrapper(request, *args, **kwargs):
        if "token" not in request:
            return "401 Unauthorized"
        return func(request, *args, **kwargs)
    return wrapper

# Rate limit decorator with parameters
def rate_limit(max_calls, period=60):
    calls = []
    def decorator(func):
        @functools.wraps(func)
        def wrapper(*args, **kwargs):
            now = time.time()
            calls[:] = [t for t in calls if now - t < period]
            if len(calls) >= max_calls:
                return "429 Too Many Requests"
            calls.append(now)
            return func(*args, **kwargs)
        return wrapper
    return decorator

# Usage — decorators stack like middleware
@logging
@require_auth
@rate_limit(max_calls=100, period=60)
def get_data(request):
    return f"data for {request['path']}"

# Equivalent to: get_data = logging(require_auth(rate_limit(100)(get_data)))
```

---

## Memoization as a Function Wrapper

**Memoization** caches a function's results for previously seen inputs, trading memory for speed.

### Go

```go
func memoize(fn func(int) int) func(int) int {
    cache := make(map[int]int)
    return func(n int) int {
        if val, ok := cache[n]; ok {
            return val
        }
        result := fn(n)
        cache[n] = result
        return result
    }
}

// Thread-safe version
func memoizeSafe(fn func(int) int) func(int) int {
    cache := sync.Map{}
    return func(n int) int {
        if val, ok := cache.Load(n); ok {
            return val.(int)
        }
        result := fn(n)
        cache.Store(n, result)
        return result
    }
}

// Usage — slow recursive Fibonacci becomes fast
var fib func(int) int
fib = memoize(func(n int) int {
    if n <= 1 { return n }
    return fib(n-1) + fib(n-2)
})

fmt.Println(fib(50)) // instant — without memo, takes forever
```

### Java

```java
public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
    Map<T, R> cache = new HashMap<>();
    return input -> cache.computeIfAbsent(input, fn);
}

// Thread-safe version
public static <T, R> Function<T, R> memoizeSafe(Function<T, R> fn) {
    Map<T, R> cache = new ConcurrentHashMap<>();
    return input -> cache.computeIfAbsent(input, fn);
}

// Usage
Function<Integer, Long> fib = memoize(new Function<>() {
    Function<Integer, Long> self = this;
    public Long apply(Integer n) {
        if (n <= 1) return (long) n;
        return self.apply(n - 1) + self.apply(n - 2);
    }
});

// Simpler approach with a wrapper class
public class Memo {
    private Map<Integer, Long> cache = new HashMap<>();

    public long fib(int n) {
        if (cache.containsKey(n)) return cache.get(n);
        long result = (n <= 1) ? n : fib(n - 1) + fib(n - 2);
        cache.put(n, result);
        return result;
    }
}
```

### Python

```python
from functools import lru_cache

# Built-in memoization decorator
@lru_cache(maxsize=None)
def fib(n):
    if n <= 1:
        return n
    return fib(n - 1) + fib(n - 2)

print(fib(50))  # instant

# Manual implementation
def memoize(fn):
    cache = {}
    @functools.wraps(fn)
    def wrapper(*args):
        if args not in cache:
            cache[args] = fn(*args)
        return cache[args]
    wrapper.cache = cache         # expose cache for inspection
    wrapper.clear = cache.clear   # allow cache clearing
    return wrapper

@memoize
def expensive_computation(n):
    time.sleep(1)  # simulate slow work
    return n ** 2

print(expensive_computation(5))  # slow first time
print(expensive_computation(5))  # instant from cache

# TTL-based memoization (cache expires)
def memoize_ttl(ttl_seconds):
    def decorator(fn):
        cache = {}
        @functools.wraps(fn)
        def wrapper(*args):
            now = time.time()
            if args in cache:
                result, timestamp = cache[args]
                if now - timestamp < ttl_seconds:
                    return result
            result = fn(*args)
            cache[args] = (result, now)
            return result
        return wrapper
    return decorator

@memoize_ttl(ttl_seconds=300)  # cache for 5 minutes
def fetch_config(key):
    return db.query(f"SELECT value FROM config WHERE key = '{key}'")
```

---

## Functional Programming Patterns in Imperative Languages

### Option/Maybe Pattern — avoiding nil/null

#### Go

```go
type Option[T any] struct {
    value T
    valid bool
}

func Some[T any](v T) Option[T] { return Option[T]{value: v, valid: true} }
func None[T any]() Option[T]    { return Option[T]{} }

func (o Option[T]) Map(fn func(T) T) Option[T] {
    if !o.valid { return None[T]() }
    return Some(fn(o.value))
}

func (o Option[T]) FlatMap(fn func(T) Option[T]) Option[T] {
    if !o.valid { return None[T]() }
    return fn(o.value)
}

func (o Option[T]) GetOrElse(fallback T) T {
    if !o.valid { return fallback }
    return o.value
}

// Usage
result := Some(42).Map(func(x int) int { return x * 2 }).GetOrElse(0)
fmt.Println(result) // 84
```

#### Java

```java
// Java has Optional built in
Optional<String> name = Optional.of("Alice");

String upper = name
    .map(String::toUpperCase)
    .filter(s -> s.length() > 3)
    .orElse("Unknown");

System.out.println(upper); // ALICE

// Chaining with flatMap
Optional<String> city = getUser("123")
    .flatMap(User::getAddress)
    .flatMap(Address::getCity);
```

#### Python

```python
from typing import TypeVar, Generic, Callable, Optional

T = TypeVar("T")
U = TypeVar("U")

class Maybe(Generic[T]):
    def __init__(self, value: Optional[T]):
        self._value = value

    @staticmethod
    def of(value: T) -> "Maybe[T]":
        return Maybe(value)

    @staticmethod
    def empty() -> "Maybe[T]":
        return Maybe(None)

    def map(self, fn: Callable[[T], U]) -> "Maybe[U]":
        if self._value is None:
            return Maybe.empty()
        return Maybe.of(fn(self._value))

    def flat_map(self, fn: Callable[[T], "Maybe[U]"]) -> "Maybe[U]":
        if self._value is None:
            return Maybe.empty()
        return fn(self._value)

    def get_or_else(self, default: T) -> T:
        return self._value if self._value is not None else default

# Usage
result = Maybe.of(42).map(lambda x: x * 2).get_or_else(0)
print(result)  # 84
```

### Result/Either Pattern — typed error handling

#### Go

```go
// Go already uses (value, error) pattern natively
func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

// Chain with helper
func then(val float64, err error, fn func(float64) (float64, error)) (float64, error) {
    if err != nil { return 0, err }
    return fn(val)
}

result, err := then(divide(10, 2), func(v float64) (float64, error) {
    return divide(v, 0) // will fail
})
```

#### Java

```java
// Using Either pattern (from libraries like Vavr, or custom)
sealed interface Result<T> {
    record Ok<T>(T value) implements Result<T> {}
    record Err<T>(String error) implements Result<T> {}

    default <U> Result<U> map(Function<T, U> fn) {
        return switch (this) {
            case Ok<T> ok -> new Ok<>(fn.apply(ok.value()));
            case Err<T> err -> new Err<>(err.error());
        };
    }

    default T getOrElse(T fallback) {
        return switch (this) {
            case Ok<T> ok -> ok.value();
            case Err<T> err -> fallback;
        };
    }
}

Result<Integer> result = new Result.Ok<>(42);
int doubled = result.map(x -> x * 2).getOrElse(0); // 84
```

#### Python

```python
from dataclasses import dataclass
from typing import Union, Callable

@dataclass
class Ok:
    value: any

@dataclass
class Err:
    error: str

Result = Union[Ok, Err]

def map_result(result: Result, fn: Callable) -> Result:
    if isinstance(result, Err):
        return result
    try:
        return Ok(fn(result.value))
    except Exception as e:
        return Err(str(e))

def chain(*fns):
    def run(value):
        result = Ok(value)
        for fn in fns:
            result = map_result(result, fn)
        return result
    return run

# Usage
pipeline = chain(
    lambda x: x * 2,
    lambda x: x + 10,
    lambda x: x / 0,  # will produce Err
)

print(pipeline(5))  # Err(error='division by zero')
```

---

## Concurrent Function Execution Patterns

### Go — Goroutines and Channels

```go
// Fan-out: run multiple functions concurrently
func fanOut(tasks ...func() (string, error)) []string {
    results := make([]string, len(tasks))
    var wg sync.WaitGroup

    for i, task := range tasks {
        wg.Add(1)
        go func(idx int, fn func() (string, error)) {
            defer wg.Done()
            result, err := fn()
            if err != nil {
                results[idx] = "error: " + err.Error()
            } else {
                results[idx] = result
            }
        }(i, task)
    }

    wg.Wait()
    return results
}

// Worker pool pattern
func workerPool(jobs <-chan int, results chan<- int, workers int) {
    var wg sync.WaitGroup
    for w := 0; w < workers; w++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for job := range jobs {
                results <- process(job)
            }
        }()
    }
    wg.Wait()
    close(results)
}

// Timeout wrapper
func withTimeout(fn func() (string, error), timeout time.Duration) (string, error) {
    ch := make(chan struct {
        result string
        err    error
    }, 1)

    go func() {
        r, e := fn()
        ch <- struct{ result string; err error }{r, e}
    }()

    select {
    case res := <-ch:
        return res.result, res.err
    case <-time.After(timeout):
        return "", errors.New("timeout")
    }
}
```

### Java — CompletableFuture

```java
// Run multiple functions concurrently
CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> fetchUser("123"));
CompletableFuture<String> orderFuture = CompletableFuture.supplyAsync(() -> fetchOrders("123"));
CompletableFuture<String> prefsFuture = CompletableFuture.supplyAsync(() -> fetchPrefs("123"));

// Wait for all
CompletableFuture.allOf(userFuture, orderFuture, prefsFuture).join();

String user = userFuture.get();
String orders = orderFuture.get();
String prefs = prefsFuture.get();

// Chain async operations
CompletableFuture<String> result = CompletableFuture
    .supplyAsync(() -> fetchUser("123"))
    .thenApply(user -> user.toUpperCase())
    .thenCompose(name -> CompletableFuture.supplyAsync(() -> fetchOrders(name)))
    .exceptionally(ex -> "Error: " + ex.getMessage());

// Timeout
CompletableFuture<String> withTimeout = CompletableFuture
    .supplyAsync(() -> slowOperation())
    .orTimeout(5, TimeUnit.SECONDS)
    .exceptionally(ex -> "Timed out");
```

### Python — asyncio and concurrent.futures

```python
import asyncio
from concurrent.futures import ThreadPoolExecutor, as_completed

# Async/await approach
async def fetch_all(user_id):
    user, orders, prefs = await asyncio.gather(
        fetch_user(user_id),
        fetch_orders(user_id),
        fetch_prefs(user_id),
    )
    return {"user": user, "orders": orders, "prefs": prefs}

async def with_timeout(coro, seconds):
    try:
        return await asyncio.wait_for(coro, timeout=seconds)
    except asyncio.TimeoutError:
        return None

# Thread pool for CPU-bound or blocking I/O
def parallel_map(fn, items, max_workers=4):
    results = []
    with ThreadPoolExecutor(max_workers=max_workers) as executor:
        futures = {executor.submit(fn, item): item for item in items}
        for future in as_completed(futures):
            try:
                results.append(future.result())
            except Exception as e:
                results.append(f"Error: {e}")
    return results

# Usage
urls = ["https://api.example.com/1", "https://api.example.com/2"]
responses = parallel_map(requests.get, urls, max_workers=10)
```

---

## Code Examples

### Example: Retry with Exponential Backoff

#### Go

```go
func retry(fn func() error, maxAttempts int, baseDelay time.Duration) error {
    var lastErr error
    for attempt := 0; attempt < maxAttempts; attempt++ {
        if err := fn(); err != nil {
            lastErr = err
            delay := baseDelay * time.Duration(1<<uint(attempt))
            log.Printf("Attempt %d failed: %v. Retrying in %v...", attempt+1, err, delay)
            time.Sleep(delay)
            continue
        }
        return nil
    }
    return fmt.Errorf("all %d attempts failed, last error: %w", maxAttempts, lastErr)
}

// Usage
err := retry(func() error {
    resp, err := http.Get("https://flaky-api.example.com/data")
    if err != nil { return err }
    if resp.StatusCode >= 500 { return fmt.Errorf("server error: %d", resp.StatusCode) }
    return nil
}, 5, time.Second)
```

#### Java

```java
public static <T> T retry(Callable<T> fn, int maxAttempts, long baseDelayMs)
        throws Exception {
    Exception lastException = null;
    for (int attempt = 0; attempt < maxAttempts; attempt++) {
        try {
            return fn.call();
        } catch (Exception e) {
            lastException = e;
            long delay = baseDelayMs * (1L << attempt);
            System.out.printf("Attempt %d failed: %s. Retrying in %dms...%n",
                attempt + 1, e.getMessage(), delay);
            Thread.sleep(delay);
        }
    }
    throw new RuntimeException("All " + maxAttempts + " attempts failed", lastException);
}

// Usage
String result = retry(() -> {
    HttpResponse<String> resp = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder(URI.create("https://api.example.com")).build(),
              HttpResponse.BodyHandlers.ofString());
    if (resp.statusCode() >= 500) throw new RuntimeException("Server error");
    return resp.body();
}, 5, 1000);
```

#### Python

```python
import time

def retry(fn, max_attempts=5, base_delay=1.0):
    last_error = None
    for attempt in range(max_attempts):
        try:
            return fn()
        except Exception as e:
            last_error = e
            delay = base_delay * (2 ** attempt)
            print(f"Attempt {attempt + 1} failed: {e}. Retrying in {delay}s...")
            time.sleep(delay)
    raise RuntimeError(f"All {max_attempts} attempts failed") from last_error

# As a decorator
def with_retry(max_attempts=5, base_delay=1.0):
    def decorator(fn):
        @functools.wraps(fn)
        def wrapper(*args, **kwargs):
            return retry(lambda: fn(*args, **kwargs), max_attempts, base_delay)
        return wrapper
    return decorator

@with_retry(max_attempts=3, base_delay=0.5)
def fetch_data(url):
    response = requests.get(url)
    response.raise_for_status()
    return response.json()
```

---

## Summary

- **SRP**: one function, one responsibility — split large functions into pipelines
- **Dependency injection**: pass functions as parameters for testability
- **Middleware**: wrap functions to add cross-cutting concerns (logging, auth, rate limiting)
- **Memoization**: cache function results to avoid redundant computation
- **Functional patterns**: Option/Result types give typed error handling without exceptions
- **Concurrency**: fan-out, worker pools, timeouts — all built on function composition
- Senior-level function design is about **boundaries**: what goes in, what comes out, what side effects are allowed
