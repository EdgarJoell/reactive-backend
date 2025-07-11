package com.example.reactive_backend.repository;

import com.example.reactive_backend.errorhandling.exception.CouldNotDeleteException;
import com.example.reactive_backend.errorhandling.exception.CouldNotInsertException;
import com.example.reactive_backend.errorhandling.exception.CouldNotUpdateException;
import com.example.reactive_backend.model.Task;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskRepositoryTest {
    @Mock
    private ReactiveMongoTemplate db;

    @InjectMocks
    private TaskRepository repository;

    @Test
    @Description("Test getAllTasks() in the repository layer with data being returned.")
    void testGetAllTasksWithMongoHappyPathWithData() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Title Two").description("The testing description for test Title Two").completed(true).build();

        Flux<Task> taskFlux = Flux.just(task1, task2);

        when(db.findAll(Task.class)).thenReturn(taskFlux);

        Flux<Task> res = repository.getAllTasks();

        StepVerifier.create(res)
                .expectSubscription()
                .expectNext(task1)
                .expectNext(task2)
                .expectComplete()
                .verify();
    }

    @Test
    @Description("Test getAllTasks() in the repository layer with empty Flux being returned.")
    void testGetAllTasksWithMongoHappyPathWithEmptyData() {
        when(db.findAll(Task.class)).thenReturn(Flux.empty());

        Flux<Task> res = repository.getAllTasks();

        StepVerifier.create(res)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    @Description("Test getAllTasks() in the repository layer with an unexpected error occurring.")
    void testGetAllTasksWithMongoUnhappyPathWithData() {
        when(db.findAll(Task.class)).thenReturn(Flux.error(new RuntimeException("An error occurred: ")));

        Flux<Task> res = repository.getAllTasks();

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @Description("Test getOneTask() in the repository layer with data being returned.")
    void testGetOneTaskEndpointHappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");
        Task task = Task.builder().id(id).title("Get One Task Title").description("The test description so that we can test getOneTask endpoint functionality").completed(true).build();

        when(db.findById(id, Task.class)).thenReturn(Mono.just(task));

        Mono<Task> res = repository.getOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .consumeNextWith(actual -> {
                    assertThat(actual.getId().toString()).isEqualTo("685724022e21a9baae11f00c");
                    assertThat(actual.getTitle()).isEqualTo("Get One Task Title");
                    assertThat(actual.getDescription()).isEqualTo("The test description so that we can test getOneTask endpoint functionality");
                    assertThat(actual.isCompleted()).isEqualTo(true);
                })
                .verifyComplete();
    }

    @Test
    @Description("Test getOneTask() in the repository layer with empty Mono being returned.")
    void testGetOneTaskEndpointUnhappyPathWithEmptyMonoBeingReturned() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        when(db.findById(id, Task.class)).thenReturn(Mono.empty());

        Mono<Task> res = repository.getOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectComplete();
    }

    @Test
    @Description("Test getOneTask() in the repository layer with an unexpected error occurring.")
    void testGetOneTaskEndpointUnhappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        when(db.findById(id, Task.class)).thenReturn(Mono.error(new RuntimeException("An error occurred: ")));

        Mono<Task> res = repository.getOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @Description("Test createOneTask() in the repository layer with passing workflow.")
    void testCreateOneTaskWithMongoHappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        Mono<Task> taskMono = Mono.just(task1);

        when(db.insert(task1)).thenReturn(taskMono);

        Mono<Task> res = repository.createOneTask(task1);

        StepVerifier.create(res)
                .expectSubscription()
                .consumeNextWith(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getTitle()).isEqualTo("Test Title One");
                    assertThat(response.getDescription()).isEqualTo("The testing description for test Title One");
                    assertThat(response.isCompleted()).isEqualTo(false);
                })
                .verifyComplete();
    }

    @Test
    @Description("Test createOneTask() in the repository layer with unexpected error occurring.")
    void testCreateNewTaskUnhappyPath() {
        Task task = Task.builder().title("Test Title One").description("The testing description for test Title One").completed(false).build();

        when(db.insert(task)).thenReturn(Mono.error(new CouldNotInsertException("Could not insert Document into 'Tasks' Collection")));

        Mono<Task> res = repository.createOneTask(task);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotInsertException.class)
                .verify();
    }

    @Test
    @Description("Test createTasks() in the repository layer with data being returned.")
    void testCreateTasksWithMongoHappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Group Insert Title One").description("The testing description for Test Group Insert Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Group Insert Title One").description("The testing description for Test Group Insert Title One").completed(false).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));
        Flux<Task> taskFlux = Flux.just(task1, task2);

        when(db.insertAll(taskList)).thenReturn(taskFlux);

        Flux<Task> res = repository.createTasks(taskList);

        StepVerifier.create(res)
                .expectSubscription()
                .consumeNextWith(actual -> {
                    assertThat(actual.getId()).isEqualTo(task1.getId());
                    assertThat(actual.getTitle()).isEqualTo(task1.getTitle());
                    assertThat(actual.getDescription()).isEqualTo(task1.getDescription());
                    assertThat(actual.isCompleted()).isEqualTo(task1.isCompleted());
                })
                .consumeNextWith(actual -> {
                    assertThat(actual.getId()).isEqualTo(task2.getId());
                    assertThat(actual.getTitle()).isEqualTo(task2.getTitle());
                    assertThat(actual.getDescription()).isEqualTo(task2.getDescription());
                    assertThat(actual.isCompleted()).isEqualTo(task2.isCompleted());
                })
                .expectComplete();
    }

    @Test
    @Description("Test createTasks() in the repository layer with unexpected error occurring.")
    void testCreateTasksWithMongoUnhappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Group Insert Title One").description("The testing description for Test Group Insert Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Group Insert Title One").description("The testing description for Test Group Insert Title One").completed(false).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));

        when(db.insertAll(taskList)).thenReturn(Flux.error(new CouldNotInsertException("Could not insert Document into 'Tasks' Collection")));

        Flux<Task> res = repository.createTasks(taskList);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotInsertException.class)
                .verify();
    }

    @Test
    @Description("Tests a 200 response for the updateOneTask() endpoint workflow and returns data.")
    void testUpdateTaskHappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");
        Task task = Task.builder().id(id).title("Test Update Title One").description("The testing description for updating test Title One").completed(false).build();

        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("description", task.getDescription())
                .set("title", task.getTitle())
                .set("completed", task.isCompleted());

        Mono<Task> taskMono = Mono.just(task);

        when(db.findAndModify(eq(query), eq(update), any(FindAndModifyOptions.class), eq(Task.class))).thenReturn(taskMono);

        Mono<Task> res = repository.updateOneTask(id, task);

        StepVerifier.create(res)
                .expectSubscription()
                .consumeNextWith(actual -> {
                    assertThat(actual.getId()).isEqualTo(task.getId());
                    assertThat(actual.getTitle()).isEqualTo(task.getTitle());
                    assertThat(actual.getDescription()).isEqualTo(task.getDescription());
                    assertThat(actual.isCompleted()).isEqualTo(task.isCompleted());
                })
                .verifyComplete();
    }

    @Test
    @Description("Tests a 500 response for the updateOneTask() endpoint workflow and returns CouldNotUpdateException class.")
    void testUpdateTaskUnhappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");
        Task task = Task.builder().id(id).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update()
                .set("description", task.getDescription())
                .set("title", task.getTitle())
                .set("completed", task.isCompleted());

        when(db.findAndModify(eq(query), eq(update), any(FindAndModifyOptions.class), eq(Task.class))).thenReturn(Mono.error(new CouldNotUpdateException("Could not insert Document into 'Tasks' Collection")));

        Mono<Task> res = repository.updateOneTask(id, task);

        StepVerifier.create(res)
                .expectError(CouldNotUpdateException.class)
                .verify();
    }

    @Test
    @Description("Test the happy path to delete a Task object from the DB.")
    void testDeleteOneTaskHappyPath() {
        ObjectId id = new ObjectId("6857579a7b4c57437855095b");
        Task task = Task.builder().id(id).title("Test Title One").description("The testing description for test Title One").completed(false).build();
        Query query = new Query(Criteria.where("_id").is(id));

        when(db.findAndRemove(eq(query), eq(Task.class))).thenReturn(Mono.just(task));

        Mono<Task> res = repository.deleteOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    @Description("Test the happy path to delete a Task object from the DB but with empty response.")
    void testDeleteOneTaskHappyPathWithEmptyResponse() {
        ObjectId id = new ObjectId("6857579a7b4c57437855095b");
        Query query = new Query(Criteria.where("_id").is(id));

        when(db.findAndRemove(eq(query), eq(Task.class))).thenReturn(Mono.empty());

        Mono<Task> res = repository.deleteOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectNext()
                .verifyComplete();
    }

    @Test
    @Description("Test the unhappy path to delete a Task object from the DB.")
    void testDeleteOneTaskUnhappyPath() {
        ObjectId id = new ObjectId("6857579a7b4c57437855095b");
        Query query = new Query(Criteria.where("_id").is(id));

        when(db.findAndRemove(eq(query), eq(Task.class))).thenReturn(Mono.error(new CouldNotDeleteException("Couldn't delete the record from the DB.")));

        Mono<Task> res = repository.deleteOneTask(id);

        StepVerifier.create(res)
                .expectError(CouldNotDeleteException.class)
                .verify();
    }
}
