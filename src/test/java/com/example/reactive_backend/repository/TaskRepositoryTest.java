package com.example.reactive_backend.repository;

import com.example.reactive_backend.exception.CouldNotInsertException;
import com.example.reactive_backend.model.Task;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskRepositoryTest {
    @Mock
    private ReactiveMongoTemplate db;

    @InjectMocks
    private TaskRepository repository;

    @Test
    void testGetAllTasksWithMongoHappyPathWithData() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Title Two").description("The testing description for test Title Two").completed(true).build();

        Flux<Task> taskFlux = Flux.just(task1, task2);

        when(db.findAll(Task.class)).thenReturn(taskFlux);

        Flux<Task> res = repository.getAllTasks();

        StepVerifier.create(res)
                .expectNext(task1)
                .expectNext(task2)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetAllTasksWithMongoHappyPathWithEmptyData() {
        when(db.findAll(Task.class)).thenReturn(Flux.empty());

        Flux<Task> res = repository.getAllTasks();

        StepVerifier.create(res)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetAllTasksWithMongoUnhappyPathWithData() {
        when(db.findAll(Task.class)).thenReturn(Flux.error(new RuntimeException("An error occurred: ")));

        Flux<Task> res = repository.getAllTasks();

        StepVerifier.create(res)
                .expectError()
                .verify();
    }

    @Test
    void testCreateOneTaskWithMongoHappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        Mono<Task> taskMono = Mono.just(task1);

        when(db.insert(task1)).thenReturn(taskMono);

        Mono<Task> res = repository.createOneTask(task1);

        StepVerifier.create(res)
                .consumeNextWith(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getTitle()).isEqualTo("Test Title One");
                    assertThat(response.getDescription()).isEqualTo("The testing description for test Title One");
                    assertThat(response.isCompleted()).isEqualTo(false);
                })
                .verifyComplete();
    }

    @Test
    void testCreateNewTaskUnhappyPath() {
        Task task = Task.builder().title("Test Title One").description("The testing description for test Title One").completed(false).build();

        when(db.insert(task)).thenReturn(Mono.error(new CouldNotInsertException("Could not insert Document into 'Tasks' Collection", HttpStatus.INTERNAL_SERVER_ERROR)));

        Mono<Task> res = repository.createOneTask(task);

        StepVerifier.create(res)
                .expectError()
                .verify();
    }
}
