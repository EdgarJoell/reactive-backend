package com.example.reactive_backend.repository;

import com.example.reactive_backend.errorhandling.exception.CouldNotDeleteException;
import com.example.reactive_backend.errorhandling.exception.CouldNotInsertException;
import com.example.reactive_backend.errorhandling.exception.CouldNotUpdateException;
import com.example.reactive_backend.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TaskRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<Task> getOneTask(ObjectId id) {
        return mongoTemplate.findById(id, Task.class)
                .doOnSubscribe(sub -> log.info("Searching for a Task with id: %s".formatted(id)))
                .doOnSuccess(suc -> log.info("Found Task with id: %s".formatted(id)))
                .doOnError(err -> log.error("Could not find task with id: %s".formatted(id)))
                .onErrorMap(err -> new RuntimeException("An unexpected error occurred."));
    }

    public Flux<Task> getAllTasks() {
        return mongoTemplate.findAll(Task.class)
                .doOnSubscribe(sub -> log.info("Attempting to retrieve all tasks from Collection"))
                .doOnComplete(() -> log.info("Successfully retrieved all Tasks from Collection"))
                .onErrorMap(err -> new RuntimeException("An error occurred: ", err));
    }

    public Flux<Task> getAllTasksForOneUserAccount(ObjectId id) {
        Query query = new Query(Criteria.where("userId").is(id));
        return mongoTemplate.find(query, Task.class)
                .doOnSubscribe(sub -> log.info("Attempting to retrieve all tasks from Collection with userId: %s".formatted(id)))
                .doOnComplete(() -> log.info("Successfully retrieved all Tasks from Collection with userId: %s".formatted(id)))
                .onErrorMap(err -> new RuntimeException("An error occurred: ", err));
    }

    public Mono<Task> createOneTask(Task newTask) {
        return mongoTemplate.insert(newTask)
                .doOnSubscribe(sub -> log.info("Creating new Document in 'Tasks' Collection"))
                .doOnSuccess(suc -> log.info("Successfully inserted task with into Collection."))
                .doOnError(err -> log.error("Could not insert Document into 'Tasks' Collection", err))
                .onErrorMap(err -> new CouldNotInsertException("Could not insert Document into 'Tasks' Collection"));
    }

    public Flux<Task> createTasks(ArrayList<Task> tasks) {
        return mongoTemplate.insertAll(tasks)
                .doOnSubscribe(sub -> log.info("Attempting to insert group of Documents into 'Tasks' Collection."))
                .doOnComplete(() -> log.info("Successfully inserted group of Documents into 'Tasks' Collection"))
                .doOnError(err -> log.error("Could not insert Documents into 'Tasks' Collection."))
                .onErrorMap(err -> new CouldNotInsertException("Could not insert Documents into 'Tasks' Collection."));
    }

    public Mono<Task> updateOneTask(ObjectId id, Task task) {
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("description", task.getDescription())
                .set("title", task.getTitle())
                .set("completed", task.isCompleted())
                .set("userId", task.getUserId());

        return mongoTemplate.findAndModify(query, update, options, Task.class)
                .doOnSubscribe(sub -> log.info("Attempting to update Document with id: %s".formatted(id)))
                .flatMap(res -> {
                    if(res == null) return Mono.empty();

                    return Mono.just(res);
                })
                .doOnSuccess(suc -> log.info("Successfully updated Document with id: %s".formatted(id)))
                .doOnError(err -> log.error("An error occurred with this transaction. Document id: %s".formatted(id)))
                .onErrorMap(err -> new CouldNotUpdateException("Could not update Document with id: %s".formatted(id)));
    }

    public Mono<Task> deleteOneTask(ObjectId id) {
        Query query = new Query(Criteria.where("_id").is(id));

        return mongoTemplate.findAndRemove(query, Task.class)
                .doOnSubscribe(sub -> log.info("Attempting to delete Document with id: %s".formatted(id)))
                .doOnSuccess(suc -> log.info("Successfully deleted Document with id: %s".formatted(id)))
                .doOnError(err -> log.error("An error occurred with this transaction. Document id: %s".formatted(id)))
                .onErrorMap(err -> new CouldNotDeleteException("Could not delete Document with id: %s".formatted(id)));
    }
}
