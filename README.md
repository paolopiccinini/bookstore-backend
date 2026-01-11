# bookstore-backend

bookstore-backend is a spring boot rest API to solve your challenge

## Installation Maven
```bash
./mvnw spring-boot:run
```
## Installation Docker
```bash
docker build -t bookstore-backend .
docker run -p 8080:8080 bookstore-backend
```
Sorry I've not tested the docker I'm working on a pc without it, I suspect my company pc is under control

## Usage

the swagger is

```bash
http://localhost:8080/swagger-ui/index.html
```

the database is here
```bash
http://localhost:8080/h2-console/
jdbc url jdbc:h2:mem:testdb
username sa
```

## Workflow
U need to `/register` a user there are two roles ADMIN and CONSUMER, with admin u can update/create/delete books, all the other endpoints are usable by logged user except /login outside auth.

After u register u need to `/login` take the token and put in the Authorization button top right.

Then u can start play with the endpoints. U can only make orders for the current logged user. I can switch but for the moment I think it's ok like this.

I used media type versioning, I did some unit test, for the moment I left integration test out, I left some comments here and there in the code. It is a bit of code already to review.

I added logs, a new header in the request/response to track the flow X-Request-UUID, added actuator, modified the response for `/orders` ina a more meaningful one, added more integration and unit test, added the type in the book response for the UI

---

# 🚀 Spring Boot & Gatling: Professional Load Testing Suite

This repository contains a high-performance load testing suite designed for Spring Boot REST APIs using **Gatling (Java SDK)**. It is architected for scalability, allowing for millions of virtual users and real-time infrastructure monitoring.

---

## 📋 1. Project Setup

### Maven Dependencies (`pom.xml`)
Add the Gatling SDK and the Maven plugin. This setup allows you to run tests via `mvn gatling:test`.

```xml
<dependencies>
    <dependency>
        <groupId>io.gatling.highcharts</groupId>
        <artifactId>gatling-charts-highcharts</artifactId>
        <version>3.13.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>io.gatling</groupId>
            <artifactId>gatling-maven-plugin</artifactId>
            <version>4.12.0</version>
            <configuration>
                </configuration>
        </plugin>
    </plugins>
</build>
```
gatling-tests/
├── pom.xml
├── src/test/java/com/example/simulations/
│   └── ApiSimulation.java      # Main logic
├── src/test/resources/
│   ├── bodies/                 # JSON templates with #{var} placeholders
│   ├── data/                   # CSV Feeders
│   ├── gatling.conf            # Advanced Engine Config
│   └── logback.xml             # Logging & Debugging Config

📈 2. Workload Models: Open vs. Closed
Understanding the difference is critical for testing Spring Boot's ability to scale.

Open Model: You control the arrival rate (users/sec). Use this for public-facing APIs where users don't wait for each other.

Closed Model: You control concurrency (total active users). Use this for internal systems with a fixed number of workers.

---

## 🚦 4. Advanced Logic: doIf and doSwitch

In Gatling, you cannot use standard Java `if` blocks to control user flow because the scenario is a pre-compiled "blueprint." Instead, use Gatling's DSL methods.

### Conditional Branching (Role-Based)
Use `doSwitch` when you have multiple user roles (e.g., ADMIN vs CUSTOMER) that perform different API calls.

```java
ScenarioBuilder roleScenario = scenario("Role-Based Branching")
    .feed(csv("data/users.csv")) // Contains a 'role' column
    .doSwitch("#{role}").on(
        Choice.withKey("ADMIN", exec(http("Admin Stats").get("/api/admin/stats"))),
        Choice.withKey("CUSTOMER", exec(http("View Shop").get("/api/shop")))
    );
```
---
Virtual Users (VUs) are isolated. To pass data between them, you must use Global Shared State.Atomic Synchronization (Waiting for an Event)If a Customer user must wait for an Admin user to call a specific API before proceeding, use a thread-safe flag.Javaprivate static final AtomicBoolean isAdminReady = new AtomicBoolean(false);
```java
// Admin Scenario
ScenarioBuilder adminScn = scenario("Admin")
    .exec(http("Init System").post("/api/admin/init"))
    .exec(session -> { isAdminReady.set(true); return session; });

// Customer Scenario
ScenarioBuilder customerScn = scenario("Customer")
    .asLongAs(session -> !isAdminReady.get()).on(
        pause(1) // Check every second
    )
    .exec(http("Consumer Action").get("/api/resource"));
Sequential Chaining (andThen)Use .andThen() to ensure Scenario A finishes entirely (e.g., DB setup) before Scenario B starts.JavasetUp(
    setupScn.injectOpen(atOnceUsers(1))
        .andThen(loadScn.injectOpen(rampUsers(100).during(60)))
).protocols(httpProtocol);
```
⚖️ 6. Traffic Precision: Pacing vs. ThrottlingTo find the "Crash Point" of your Spring Boot app, you must control the Request Per Second (RPS).Featurepace(seconds)throttle(reachRps(n))MechanicControls the time of a loop iteration.Acts as a gatekeeper to cap total traffic.LogicTotal RPS = (Total Users / Pace).You set a hard RPS limit (e.g., 500 RPS).Pacing Multiple ExecsIf you have multiple requests inside a loop, pace covers the entire sequence.
```Java
forever().on(
    pace(10) // Entire loop takes 10s
    .exec(http("Req 1").get("/1")) // Takes 1s
    .exec(http("Req 2").get("/2")) // Takes 1s
    // Gatling waits 8s here automatically
)
```
✅ 7. Assertions (SLA Validation)Assertions allow you to fail the build automatically if performance targets are not met.
```Java
setUp(scn.injectOpen(atOnceUsers(100)))
    .assertions(
        global().responseTime().percentile(95).lt(500),   // 95% of reqs < 500ms
        global().successfulRequests().percent().gt(99.0), // Error rate < 1%
        details("Login").failedRequests().count().is(0L)  // No login errors allowed
    );
```
---

## 🏗 8. Infrastructure Mocking (Testcontainers)

To test your Spring Boot API in isolation, use **Testcontainers** to spin up real instances of Postgres, Kafka, or WireMock within the test lifecycle.

### Mocking External APIs with WireMock
If your app calls a third-party gateway, use a WireMock container to simulate that service and its latency.

```java
public class MockSimulation extends Simulation {
    // 1. Define the Container
    static WireMockContainer wiremock = new WireMockContainer("wiremock/wiremock:3.3.1");

    static {
        wiremock.start();
        
        // 2. Define a Stub with Latency
        wiremock.stubFor(get(urlEqualTo("/api/external"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(200))); // Simulate 200ms lag

        // 3. Inject the Mock URL into Spring Properties
        System.setProperty("external.api.url", wiremock.getBaseUrl());
    }
}
```
☸️ 9. Kubernetes Scaling Strategy
When testing for millions of users, do not run Gatling on your local machine or as a sidecar.

Deployment as a K8s Job
Run Gatling as a standalone Kubernetes Job. This ensures the load generator has its own dedicated CPU/Memory and doesn't interfere with the application under test.

Example Job Snippet:

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: gatling-load-test
spec:
  parallelism: 5 # Run 5 pods to distribute a "million user" load
  template:
    spec:
      containers:
      - name: gatling
        image: your-registry/gatling-load-test:latest
        resources:
          requests:
            cpu: "2"
            memory: "4Gi"
      restartPolicy: Never
```
📊 10. Real-Time Monitoring Configuration
Gatling can push metrics to InfluxDB via the Graphite protocol. This allows you to watch the test live in Grafana.

src/test/resources/gatling.conf
Create this file to override default engine settings.

Code snippet
```Java
gatling {
  data {
    # Enable the graphite writer
    writers = [console, file, graphite]
  }
  graphite {
    host = "localhost"   # InfluxDB/Graphite address
    port = 2003          # Standard Graphite port
    protocol = "tcp"
    rootPathPrefix = "gatling" # Prefix for Grafana dashboard queries
    writePeriod = 1      # Push metrics every second
  }
}
```

src/test/resources/logback.xml
Configure logging to debug your HTTP requests or monitoring connection.

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder><pattern>%d{HH:mm:ss.SSS} [%-5level] %logger{15} - %msg%n</pattern></encoder>
    </appender>

    <logger name="io.gatling.http.engine.response" level="WARN" />
    <logger name="io.gatling.graphite" level="DEBUG" />

    <root level="WARN">
        <appender-ref ref="CONSOLE" />
    </root>
</configuration>
```
🚀 How to Execute
Local Execution
```Bash
mvn gatling:test
```
Run a Specific Simulation
```Bash
mvn gatling:test -Dgatling.simulationClass=com.example.simulations.ApiSimulation
```
Advanced: Environment Overrides
You can pass system properties to change the target URL or user count dynamically:

```Bash
mvn gatling:test -DbaseUrl=[http://staging-api.com](http://staging-api.com) -Dusers=500
```
(Note: You must use System.getProperty("baseUrl") in your Java code to read these values).

After the test finishes, Gatling generates a highly detailed HTML report located in: target/gatling/<simulation-name>-<timestamp>/index.html
