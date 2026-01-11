package com.example.bookstore.simulations;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import com.example.bookstore.simulations.chains.MyAppChains;

public class AppSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/vnd.com.example.bookstore.v1+json")
        .contentTypeHeader("application/json");
    
    FeederBuilder.Batchable<String> adminFeeder = csv("data/users_admin.csv").batch();
    // constant users    
    //FeederBuilder.Batchable<String> customerFeeder = csv("data/users_customer.csv").batch();
    // dynamic users per seconds they keep coming
    Iterator<Map<String, Object>> dynamicUserFeeder = Stream.generate(() -> {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    return Map.<String, Object>of(
        "username", "user_" + uniqueId,
        "password", "pass_" + uniqueId,
        "role", "CUSTOMER"
    );
    }).iterator();
    
    
    ScenarioBuilder adminScn = scenario("Admin Scenario: register -> login -> create books")
        .feed(adminFeeder)
        .exec(MyAppChains.register)
        .exec(MyAppChains.login)
        .exec(MyAppChains.createBooks);
    
    ScenarioBuilder customerScn = scenario("Customer Scenario: register -> login -> browse books -> purchase book")
        //.feed(customerFeeder)
        .feed(dynamicUserFeeder)
        .exec(MyAppChains.register)
        .exec(MyAppChains.login)
        .exec(MyAppChains.browseOrderBooksAndCheckPoints);

    {
        setUp(
            adminScn.injectOpen(atOnceUsers(2)).andThen(
                customerScn.injectOpen(
                    nothingFor(Duration.ofSeconds(5)),
                    // Wave 1 go from 5 user per sec to 20 users per sec in 1 min, keep 20 users per sec for 1 min
                    rampUsersPerSec(5).to(20).during(Duration.ofMinutes(1)),
                    constantUsersPerSec(20).during(Duration.ofMinutes(1)),
                    // Wave 2
                    rampUsersPerSec(20).to(50).during(Duration.ofMinutes(1)),
                    constantUsersPerSec(50).during(Duration.ofMinutes(1)),
                    // Cool down
                    rampUsersPerSec(50).to(0).during(Duration.ofMinutes(1))
                )
                /* only 100 total users with closed model
                customerScn.injectClosed(
                    // Start with 10 users looping, ramp up to 50 looping
                    rampConcurrentUsers(10).to(50).during(Duration.ofMinutes(1)),
                    // Keep exactly 50 users looping for 1 minute
                    constantConcurrentUsers(50).during(Duration.ofMinutes(1)),
                    // Ramp up to 100 users looping
                    rampConcurrentUsers(50).to(100).during(Duration.ofMinutes(1)),
                    // Keep exactly 100 users looping for 1 minute
                    constantConcurrentUsers(100).during(Duration.ofMinutes(1))
                )
                 */
            )
            
        )
        .protocols(httpProtocol)
        .maxDuration(Duration.ofMinutes(5))
        .assertions(
            global().responseTime().mean().lt(100), // Mean response time < 100ms
            global().successfulRequests().percent().gt(95.0) // > 95% success rate
        );
        /* controls rps no matter the users (there is also peace() method instead of throttle)
             scn.injectOpen(constantUsersPerSec(100).during(Duration.ofMinutes(10))))
            .protocols(httpProtocol)
            .throttle(
                reachRps(50).in(Duration.ofSeconds(20)), // Reach 50 req/s in 20s
                holdFor(Duration.ofMinutes(2)),           // Stay at 50 req/s for 2m
                jumpToRps(100),                           // Suddenly spike to 100 req/s
                holdFor(Duration.ofMinutes(1))            // Stay at 100 for 1m
            )
            .maxDuration(Duration.ofMinutes(5));
            ) 
        */
    }
}
