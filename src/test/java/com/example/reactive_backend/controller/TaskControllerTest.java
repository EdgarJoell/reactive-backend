package com.example.reactive_backend.controller;

import com.example.reactive_backend.exception.CouldNotInsertException;
import com.example.reactive_backend.exception.NotFoundException;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.service.TaskService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    @Mock
    private TaskService service;
    @InjectMocks
    private TaskController controller;

    @Test
    void testGetAllTasksEndpointHappyPathWithData() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Title Two").description("The testing description for test Title Two").completed(true).build();

        Flux<Task> taskFlux = Flux.just(task1, task2);

        when(service.getAllTasks()).thenReturn(taskFlux);

        Flux<Task> res = controller.getAllTasks();

        StepVerifier.create(res)
                .expectNext(task1)
                .expectNext(task2)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetAllTasksEndpointHappyPathWithEmptyData() {
        when(service.getAllTasks()).thenReturn(Flux.empty());

        Flux<Task> res = controller.getAllTasks();

        StepVerifier.create(res)
                .expectComplete()
                .verify();
    }

    @Test
    void testGetAllTasksEndpointUnhappyPath() {
        when(service.getAllTasks()).thenReturn(Flux.error(new RuntimeException("An error occurred: ")));

        Flux<Task> res = controller.getAllTasks();

        StepVerifier.create(res)
                .expectError()
                .verify();
    }

    @Test
    void testGetOneTaskEndpointHappyPath() {
        String idString = "685724022e21a9baae11f00c";
        Task task = Task.builder().id(new ObjectId("685724022e21a9baae11f00c")).title("Get One Task Title").description("The test description so that we can test getOneTask endpoint functionality").completed(true).build();

        when(service.getOneTask(new ObjectId(idString))).thenReturn(Mono.just(task));

        Mono<Task> res = controller.getOneTask("685724022e21a9baae11f00c");

        StepVerifier.create(res)
                .consumeNextWith(actual -> {
                    assertThat(actual.getId().toString()).isEqualTo("685724022e21a9baae11f00c");
                    assertThat(actual.getTitle()).isEqualTo("Get One Task Title");
                    assertThat(actual.getDescription()).isEqualTo("The test description so that we can test getOneTask endpoint functionality");
                    assertThat(actual.isCompleted()).isEqualTo(true);
                })
                .verifyComplete();
    }

    @Test
    void testGetOneTaskEndpointUnhappyPathWith404Error() {
        String idString = "685724022e21a9baae11f00c";

        when(service.getOneTask(new ObjectId(idString))).thenReturn(Mono.error(new NotFoundException("Task could not be found!")));

        Mono<Task> res = controller.getOneTask("685724022e21a9baae11f00c");

        StepVerifier.create(res)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testGetOneTaskEndpointUnhappyPath() {
        String idString = "685724022e21a9baae11f00c";

        when(service.getOneTask(new ObjectId(idString))).thenReturn(Mono.error(new RuntimeException("An error occurred: ")));

        Mono<Task> res = controller.getOneTask("685724022e21a9baae11f00c");

        StepVerifier.create(res)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testCreateNewTaskHappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        Mono<Task> taskMono = Mono.just(task1);

        when(service.createOneTask(task1)).thenReturn(taskMono);

        Mono<Task> res = controller.createOneTask(task1);

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

        when(service.createOneTask(task)).thenReturn(Mono.error(new CouldNotInsertException("Could not insert Document into 'Tasks' Collection", HttpStatus.INTERNAL_SERVER_ERROR)));

        Mono<Task> res = controller.createOneTask(task);

        StepVerifier.create(res)
                .expectError()
                .verify();
    }
}
