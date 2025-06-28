package com.example.reactive_backend.task;

import com.example.reactive_backend.IntegrationTestConfig;
import com.example.reactive_backend.ReactiveBackendIntegrationTest;
import com.example.reactive_backend.controller.TaskController;
import com.example.reactive_backend.errorhandling.ErrorAdviceDto;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.service.TaskService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Import(IntegrationTestConfig.class)
public class TaskControllerIntegrationTest extends ReactiveBackendIntegrationTest {
    @Autowired
    private TaskController controller;

    @Autowired
    private TaskService service;

    @Test
    void testGetAllTasksEndpoint() {
        Task task1 = Task.builder().id(new ObjectId()).title("Integration Test Task One").description("The first Task item in the Flux").completed(true).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Integration Test Task Two").description("The second Task item in the Flux").completed(false).build();

        when(mongoTemplate.findAll(Task.class)).thenReturn(Flux.just(task1, task2));
        when(service.getAllTasks()).thenReturn(Flux.just(task1, task2));

        webTestClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(2)
                .contains(task1, task2);
    }

    @Test
    void testGetAllTasksEndpointException() {
        when(mongoTemplate.findAll(Task.class)).thenReturn(Flux.error(new RuntimeException("Runtime exception")));
        when(service.getAllTasks()).thenReturn(Flux.error(new RuntimeException("Runtime exception")));

        webTestClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/tasks");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                });
    }
}
