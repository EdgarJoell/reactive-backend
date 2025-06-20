package com.example.reactive_backend.repository;

import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class TaskRepository {
    private MongoClient mongoClient;

    public TaskRepository(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Flux<String> getAllTasks() {
        return Flux.just("Hello", "World", "Coming", "From", "The", "Zambrana", "Household");
    }
}
