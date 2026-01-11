package com.example.bookstore.simulations.chains;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.bookstore.dto.BookType;

import io.gatling.javaapi.core.ChainBuilder;

public class MyAppChains {

    public static Iterator<Map<String, Object>> bookFeeder =
        Stream.generate(() -> {
            BookType[] types = BookType.values();
            int randomIndex = ThreadLocalRandom.current().nextInt(types.length);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                sb.append(ThreadLocalRandom.current().nextInt(10));
            }
            var uuid = sb.toString();
            return Map.<String, Object>of(
                "isbn",  uuid,
                "title", "title-" + uuid,
                "author", "author-" + uuid,
                "basePrice", ThreadLocalRandom.current().nextDouble(5.0, 100.0),
                "bookType", types[randomIndex].name()
            );
        }).iterator();

    public static ChainBuilder register = exec(http("Register")
            .post("/auth/register")
            .body(ElFileBody("bodies/register.json"))
            .check(status().is(201))).pause(1);

    public static ChainBuilder login = exec(http("Login")
            .post("/auth/login")
            .body(ElFileBody("bodies/login.json"))
            .check(status().is(200), jsonPath("$.token").saveAs("authToken"))).pause(1);

    public static ChainBuilder createBooks = repeat(5).on(
                feed(bookFeeder)
                /* If token expiered logic can be added here 
                .doIf(session -> isTokenExpired(session)).then(
                    exec(http("Refresh Token").post("/api/auth/refresh")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200), jsonPath("$.token").saveAs("authToken"))
                    ).pause(1)
                )*/
                .exec(http("Create Books")
                    .post("/books")
                    .header("Authorization", "Bearer #{authToken}")
                    .body(ElFileBody("bodies/create_books.json"))
                    .check(status().is(201))
                ).pause(1)
            );
            
    public static ChainBuilder browseOrderBooksAndCheckPoints = forever().on(
                /* If token expiered logic can be added here 
                .doIf(session -> isTokenExpired(session)).then(
                    exec(http("Refresh Token").post("/api/auth/refresh")
                        .header("Authorization", "Bearer #{authToken}")
                        .check(status().is(200), jsonPath("$.token").saveAs("authToken"))
                    ).pause(1)
                )*/
                exec(http("Get Books")
                    .get("/books")
                    .queryParam("page", "0")
                    .queryParam("size", "10")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200), jsonPath("$.content[*].isbn").findAll().saveAs("allIsbns"))
                )
                .exec(session -> {
                    List<String> allIsbns = session.getList("allIsbns");
                    if (allIsbns == null || allIsbns.isEmpty()) {
                        return session.set("subsetIsbns", "");
                    }
                    
                    // Shuffle and pick a random size
                    List<String> shuffled = new ArrayList<>(allIsbns);
                    Collections.shuffle(shuffled);
                    
                    int randomSize = ThreadLocalRandom.current().nextInt(1, allIsbns.size() + 1);
                    String joinedIsbns = shuffled.stream()
                            .limit(randomSize)
                            .collect(Collectors.joining(","));

                    return session.set("subsetIsbns", joinedIsbns);
                })
                .exec(
                    http("Order books")
                    .post("/orders")
                    .queryParam("isbns", "#{subsetIsbns}")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                )
                .pause(1)
                .exec(
                    http("Check points for ordered books")
                    .get("/orders/points")
                    .header("Authorization", "Bearer #{authToken}")
                    .check(status().is(200))
                )
            );

}
