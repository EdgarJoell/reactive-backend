package com.example.reactive_backend.repository;

import com.example.reactive_backend.exception.CouldNotInsertException;
import com.example.reactive_backend.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<Task> getAllTasks() {
        return mongoTemplate.findAll(Task.class)
                .doOnSubscribe(sub -> log.info("Attempting to retrieve all tasks from Collection"))
                .doOnComplete(() -> log.info("Successfully retrieved all Tasks from Collection"))
                .onErrorMap(err -> new RuntimeException("An error occurred: ", err));
    }

    public Mono<Task> createTask(Task newTask) {
        return mongoTemplate.insert(newTask)
                .doOnSubscribe(sub -> log.info("Creating new Document in 'Tasks' Collection"))
                .doOnSuccess(suc -> log.info("Successfully inserted task with into Collection."))
                .doOnError(err -> log.error("Could not insert Document into 'Tasks' Collection", err))
                .onErrorMap(err -> new CouldNotInsertException("Could not insert Document into 'Tasks' Collection", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
