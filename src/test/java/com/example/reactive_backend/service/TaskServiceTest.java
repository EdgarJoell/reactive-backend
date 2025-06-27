package com.example.reactive_backend.service;

import com.example.reactive_backend.errorhandling.exception.CouldNotDeleteException;
import com.example.reactive_backend.errorhandling.exception.CouldNotInsertException;
import com.example.reactive_backend.errorhandling.exception.CouldNotUpdateException;
import com.example.reactive_backend.errorhandling.exception.NotFoundException;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.repository.TaskRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Test
    void testGetOneTaskWithReturnedData() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");
        Task task = Task.builder().id(id).title("Get One Task Title").description("The test description so that we can test getOneTask endpoint functionality").completed(true).build();

        when(repository.getOneTask(id)).thenReturn(Mono.just(task));

        Mono<Task> res = service.getOneTask(id);

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
    void testGetOneTaskWithReturnedEmptyFromRepository() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        when(repository.getOneTask(id)).thenReturn(Mono.empty());

        Mono<Task> res = service.getOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testGetOneTaskWithReturnedEmpty() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");
        Task task = Task.builder().id(id).title("Get One Task Title").description("The test description so that we can test getOneTask endpoint functionality").completed(true).build();

        when(repository.getOneTask(id)).thenReturn(Mono.error(new RuntimeException("Error occurred brother man!")));

        Mono<Task> res = service.getOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testGetAllTasksWithReturnedData() {
        Task task1 = Task.builder().id(new ObjectId()).title("Get One Task Title").description("The test description so that we can test getAllTasks endpoint functionality").completed(true).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Get One Task Title").description("The test description so that we can test getAllTasks endpoint functionality").completed(true).build();

        when(repository.getAllTasks()).thenReturn(Flux.just(task1, task2));

        Flux<Task> res = service.getAllTasks();

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
    void testGetAllTasksWithReturnedEmpty() {
        when(repository.getAllTasks()).thenReturn(Flux.empty());

        Flux<Task> res = service.getAllTasks();

        StepVerifier.create(res)
                .expectSubscription()
                .expectNext()
                .expectComplete();
    }

    @Test
    void testGetAllTasksWithReturnedError() {
        when(repository.getAllTasks()).thenReturn(Flux.error(new RuntimeException("An error occurred brother.")));

        Flux<Task> res = service.getAllTasks();

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testCreateOneTaskWithHappyPath() {
        Task task = Task.builder().title("Get One Task Title").description("The test description so that we can test createOneTask endpoint functionality").completed(true).build();

        when(repository.createOneTask(task)).thenReturn(Mono.just(task));

        Mono<Task> res = service.createOneTask(task);

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
    void testCreateOneTaskWithUnhappyPath() {
        Task task = Task.builder().title("Create One Task Title").description("The test description so that we can test createOneTask endpoint functionality").completed(true).build();

        when(repository.createOneTask(task)).thenReturn(Mono.error(new CouldNotInsertException("Sorry man, couldn't insert into the DB")));

        Mono<Task> res = service.createOneTask(task);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotInsertException.class)
                .verify();
    }

    @Test
    void testCreateTasksWithHappyPath() {
        Task task1 = Task.builder().title("Create One Task Title").description("The test description so that we can test createTasks endpoint functionality").completed(true).build();
        Task task2 = Task.builder().title("Create Two Task Title").description("The test description so that we can test createTasks endpoint functionality").completed(true).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));

        when(repository.createTasks(taskList)).thenReturn(Flux.just(task1, task2));

        Flux<Task> res = service.createTasks(taskList);

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
                .verifyComplete();
    }

    @Test
    void testCreateTasksWithUnhappyPath() {
        Task task1 = Task.builder().title("Create One Task Title").description("The test description so that we can test createTasks endpoint functionality").completed(true).build();
        Task task2 = Task.builder().title("Create Two Task Title").description("The test description so that we can test createTasks endpoint functionality").completed(true).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));

        when(repository.createTasks(taskList)).thenReturn(Flux.error(new CouldNotInsertException("Sorry man, couldn't insert into the DB")));

        Flux<Task> res = service.createTasks(taskList);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotInsertException.class)
                .verify();
    }

    @Test
    void testUpdateOneTaskWithHappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        Task task = Task.builder().id(id).title("Update One Task Title").description("The test description so that we can test updateOneTask endpoint functionality").completed(true).build();

        when(repository.updateOneTask(id, task)).thenReturn(Mono.just(task));

        Mono<Task> res = service.updateOneTask(id, task);

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
    void testUpdateOneTaskWithHappyPathButEmptyReturnValue() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        Task task = Task.builder().id(id).title("Update One Task Title").description("The test description so that we can test updateOneTask endpoint functionality").completed(true).build();

        when(repository.updateOneTask(id, task)).thenReturn(Mono.empty());

        Mono<Task> res = service.updateOneTask(id, task);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateOneTaskWithUnhappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        Task task = Task.builder().id(id).title("Update One Task Title").description("The test description so that we can test updateOneTask endpoint functionality").completed(true).build();

        when(repository.updateOneTask(id, task)).thenReturn(Mono.error(new CouldNotUpdateException("Sorry man, couldn't insert into the DB")));

        Mono<Task> res = service.updateOneTask(id, task);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotUpdateException.class)
                .verify();
    }

    @Test
    void testDeleteOneTaskWithHappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");
        Task task = Task.builder().id(id).title("Delete One Task Title").description("The test description so that we can test deleteOneTask endpoint functionality").completed(true).build();

        when(repository.deleteOneTask(id)).thenReturn(Mono.just(task));

        Mono<Task> res = service.deleteOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void testDeleteOneTaskWithHappyPathEmptyResponse() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        when(repository.deleteOneTask(id)).thenReturn(Mono.empty());

        Mono<Task> res = service.deleteOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testDeleteOneTaskWithUnhappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00c");

        when(repository.deleteOneTask(id)).thenReturn(Mono.error(new CouldNotDeleteException("Could not delete record from DB.")));

        Mono<Task> res = service.deleteOneTask(id);

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotDeleteException.class)
                .verify();
    }
}
