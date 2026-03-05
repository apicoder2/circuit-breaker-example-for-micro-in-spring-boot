
# 🔄 Circuit Breaker Example for Microservices in Spring Boot

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Resilience4j](https://img.shields.io/badge/Resilience4j-2.2.0-orange.svg)](https://resilience4j.readme.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A practical, production-ready example demonstrating **Resilience4j Circuit Breaker** pattern with **Spring Boot**, **Eureka Discovery Service**, and **Feign Client** in a microservices architecture.

## 📋 Table of Contents
- [Architecture](#-architecture)
- [Technologies Used](#-technologies-used)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Testing the Circuit Breaker](#-testing-the-circuit-breaker)
- [Project Structure](#-project-structure)
- [Key Configuration](#-key-configuration)
- [Understanding Circuit Breaker States](#-understanding-circuit-breaker-states)
- [Important Note](#-important-note)
- [Contributing](#-contributing)
- [License](#-license)

## 🏗️ Architecture

This project consists of three interconnected microservices:

| Service | Port | Description |
|---------|------|-------------|
| **Eureka Server** | `8761` | Service discovery server where all services register |
| **Payment Service** | `8082` | Simulates payment processing with 30% random failures |
| **Product Service** | `8081` | Consumer service with circuit breaker that calls payment service |

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐
│    Eureka   │     │    Payment   │     │    Product   │
│    Server   │<────│   Service    │<────│   Service    │
│   (8761)    │     │   (8082)     │     │   (8081)     │
└─────────────┘     └──────────────┘     └──────────────┘
       │                   │                     │
       └───────────────────┴─────────────────────┘
              All services register with Eureka
```

## 🛠️ Technologies Used

- **Java 21** - Latest LTS version
- **Spring Boot 3.5.11** - Application framework
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Cloud OpenFeign** - Declarative REST client
- **Resilience4j 2.2.0** - Circuit breaker implementation
- **Maven** - Dependency management

## 📋 Prerequisites

Before you begin, ensure you have the following installed:
- ✅ **Java 21** or higher ([Download](https://openjdk.org/projects/jdk/21/))
- ✅ **Maven** 3.8+ ([Download](https://maven.apache.org/download.cgi))
- ✅ **Postman** or any REST client ([Download](https://www.postman.com/downloads/))
- ✅ Your favorite **IDE** (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Getting Started

### Step 1: Clone the Repository
```bash
git clone https://github.com/apicoder2/circuit-breaker-example-for-micro-in-spring-boot.git
cd circuit-breaker-example-for-micro-in-spring-boot
```

### Step 2: Import into Your IDE
Import as an existing Maven project in your preferred IDE:
- **IntelliJ IDEA**: `File > Open > Select the project folder`
- **Eclipse**: `File > Import > Existing Maven Projects`

### Step 3: Run the Services (In Order!)

<details>
<summary><b>📌 Click to expand: Start Eureka Server</b></summary>

```bash
cd eureka-server
mvn spring-boot:run
```
Or run the main class: `com.eureka_server.EurekaServerApplication`
</details>

<details>
<summary><b>📌 Click to expand: Start Payment Service</b></summary>

```bash
cd paymentdemo
mvn spring-boot:run
```
Or run the main class: `com.paymentdemo.PaymentdemoApplication`
</details>

<details>
<summary><b>📌 Click to expand: Start Product Service</b></summary>

```bash
cd productdemo
mvn spring-boot:run
```
Or run the main class: `com.productdemo.ProductdemoApplication`
</details>

### Step 4: Verify Everything is Running
- Open your browser and go to: **http://localhost:8761**
- You should see both `PAYMENT-SERVICE` and `PRODUCT-SERVICE` registered in the Eureka dashboard

---

## 🧪 Testing the Circuit Breaker

You can test all three circuit breaker states (**CLOSED, OPEN, HALF_OPEN**) using just your browser and Postman - no terminal needed!

### 🎯 **Quick Reference URLs**

| URL | Purpose | Expected When Running |
|-----|---------|----------------------|
| http://localhost:8761 | **Eureka Dashboard** | Shows registered services |
| http://localhost:8082/api/payments/status | **Direct Payment Test** | Success or 500 error (30% random) |
| http://localhost:8081/api/orders/check-payment | **Circuit Breaker Test** | Main endpoint to test |
| http://localhost:8081/actuator/circuitbreakers | **Circuit Breaker State** | Shows current state and stats |
| http://localhost:8081/actuator/health | **Health Check** | Overall application health |

---

### **Step 1: Test CLOSED State (Normal Operation)**

| 🟢 **CLOSED** | Normal operation - circuit is healthy |
|---------------|--------------------------------------|

**In Postman:**
1. Create a **GET** request to: `http://localhost:8081/api/orders/check-payment`
2. Click **Send** 10-15 times
3. **Observe the responses** - you'll see a mix of:

| Response Type | Frequency | Example |
|--------------|-----------|---------|
| ✅ **Success** | ~70% | `Payment Successful at: 2026-03-05T10:30:45.123` |
| ⚠️ **Fallback** | ~30% | `Payment Service is DOWN - Circuit OPEN (Feign Fallback)` |

**In Browser:**
- Open: http://localhost:8081/actuator/circuitbreakers
- You'll see stats showing `state: "CLOSED"` with call counts

```json
{
  "circuitBreakers": {
    "PaymentClientgetPaymentStatus": {
      "state": "CLOSED",
      "failureRate": "30.0%",
      "bufferedCalls": 10,
      "failedCalls": 3
    }
  }
}
```

---

### **Step 2: Trigger OPEN State (Service Down)**

| 🔴 **OPEN** | Circuit is open - service is considered down |
|--------------|----------------------------------------------|

1. **Stop the Payment Service**
   - In your IDE, click the **red stop button** for `PaymentdemoApplication`

2. **In Postman**, hit the endpoint **5 times**:
   ```
   http://localhost:8081/api/orders/check-payment
   ```

3. **Watch what happens:**

   | Call # | What You'll See |
   |--------|-----------------|
   | 1-3 | Still trying (may show connection errors) |
   | 4-5 | Circuit opens - all calls return fallback |
   | 6+ | ⚠️ **IMMEDIATE FALLBACK** - no attempt to call |

4. **In Browser**, check the state:
   - Open: http://localhost:8081/actuator/circuitbreakers
   - You'll see:

```json
{
  "circuitBreakers": {
    "PaymentClientgetPaymentStatus": {
      "state": "OPEN",
      "failureRate": "100.0%",
      "bufferedCalls": 5,
      "failedCalls": 5,
      "notPermittedCalls": 2
    }
  }
}
```

---

### **Step 3: Observe HALF_OPEN State**

| 🟡 **HALF_OPEN** | Testing if service recovered - allowing limited calls |
|------------------|------------------------------------------------------|

1. **Wait 10 seconds** (this is your configured `wait-duration-in-open-state`)

2. **In Postman**, hit the endpoint **once**:
   ```
   http://localhost:8081/api/orders/check-payment
   ```

3. **In Browser**, check the state immediately:
   - Open: http://localhost:8081/actuator/circuitbreakers
   - You'll see:

```json
{
  "circuitBreakers": {
    "PaymentClientgetPaymentStatus": {
      "state": "HALF_OPEN",
      "bufferedCalls": 1,
      "failedCalls": 1
    }
  }
}
```

---

### **Step 4: Test Recovery (Back to CLOSED)**

| 🟢 **CLOSED** | Service recovered - back to normal |
|---------------|-----------------------------------|

1. **Restart the Payment Service**
   - Run `PaymentdemoApplication` again from your IDE

2. **In Postman**, hit the endpoint **3 times**:
   ```
   http://localhost:8081/api/orders/check-payment
   ```
   - You should see successful responses again

3. **In Browser**, verify the state:
   - Open: http://localhost:8081/actuator/circuitbreakers
   - It should show `state: "CLOSED"` again

---

## 📊 Sample Responses in Postman

### **When payment service is UP (CLOSED state):**
```
✅ Payment Successful at: 2026-03-05T15:30:45.123
✅ Payment Successful at: 2026-03-05T15:30:46.456
⚠️ Payment Service is DOWN - Circuit OPEN (Feign Fallback)
✅ Payment Successful at: 2026-03-05T15:30:47.789
⚠️ Payment Service is DOWN - Circuit OPEN (Feign Fallback)
```

### **When payment service is DOWN and circuit is OPEN:**
```
⚠️ Payment Service is DOWN - Circuit OPEN (Feign Fallback)
⚠️ Payment Service is DOWN - Circuit OPEN (Feign Fallback)
⚠️ Payment Service is DOWN - Circuit OPEN (Feign Fallback)
```

---

## 📁 Project Structure

```
circuit-breaker-example/
├── 📁 eureka-server/
│   ├── src/main/java/com/eureka_server/
│   │   └── EurekaServerApplication.java
│   └── pom.xml
│
├── 📁 paymentdemo/
│   ├── src/main/java/com/paymentdemo/
│   │   ├── PaymentdemoApplication.java
│   │   └── controller/
│   │       └── PaymentController.java    # Has 30% random failure rate
│   └── pom.xml
│
└── 📁 productdemo/
    ├── src/main/java/com/productdemo/
    │   ├── ProductdemoApplication.java
    │   ├── client/
    │   │   ├── PaymentClient.java        # Feign client
    │   │   └── PaymentClientFallback.java # Fallback implementation
    │   ├── controller/
    │   │   └── OrderController.java      # REST endpoint
    │   └── service/
    │       └── OrderService.java          # Business logic
    └── pom.xml
```

---

## ⚙️ Key Configuration

### `productdemo/application.properties`

```properties
# Enable Feign circuit breaker (THIS IS CRITICAL!)

spring.cloud.openfeign.circuitbreaker.enabled=true

# Circuit breaker settings for Feign client

resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.sliding-window-size=5
resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.minimum-number-of-calls=5
resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.permitted-number-of-calls-in-half-open-state=2
resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.automatic-transition-from-open-to-half-open-enabled=true
resilience4j.circuitbreaker.instances.PaymentClientgetPaymentStatus.sliding-window-type=count_based

```

### `paymentdemo/PaymentController.java`

```java
@GetMapping("/status")
public String getPaymentStatus() {
    // 30% failure rate for testing
    if (random.nextInt(10) < 3) {
        throw new RuntimeException("Random payment failure!");
    }
    return "Payment Successful at: " + LocalDateTime.now();
}
```

---

## 🎯 Understanding the Circuit Breaker States

| State | Icon | What it means | What happens to your calls |
|-------|------|---------------|---------------------------|
| **CLOSED** | 🟢 | Everything is normal | Calls go through normally, failures are counted |
| **OPEN** | 🔴 | Service is down | All calls immediately fail fast (return fallback) |
| **HALF_OPEN** | 🟡 | Testing if service recovered | Allows a few test calls through |

### State Transition Diagram
```
        ┌─────────┐
        │ CLOSED  │
        └────┬────┘
             │ Failure rate > 50%
             ↓
        ┌─────────┐    wait 10s    ┌────────────┐
        │  OPEN   │───────────────→│ HALF_OPEN  │
        └─────────┘                 ──────┬─────┘
                                          │
                              ┌───────────┴───────────┐
                              │                       │
                      Test calls fail           Test calls succeed
                              ↓                       ↓
                        ┌─────────┐             ┌─────────┐
                        │  OPEN   │             │ CLOSED  │
                        └─────────┘             └─────────┘
```

---

## ⚠️ Important Note

> **"Everyone needs to configure their own `application.properties` according to their setup, as my `pom.xml` configuration may be different from theirs."**

This project uses specific versions of Spring Boot and Spring Cloud. If you're using different versions, make sure to check compatibility:
- Spring Boot 3.5.x works with Spring Cloud 2025.0.x
- Adjust versions in your `pom.xml` if needed

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. 🍴 **Fork** the repository
2. 🌿 **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. 💻 **Commit** your changes (`git commit -m 'Add some amazing feature'`)
4. 📤 **Push** to the branch (`git push origin feature/amazing-feature`)
5. 🔍 **Open** a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## ⭐ Show Your Support

If you found this project helpful, please consider giving it a ⭐ on GitHub!

---

**Happy Coding!** 🚀
```
