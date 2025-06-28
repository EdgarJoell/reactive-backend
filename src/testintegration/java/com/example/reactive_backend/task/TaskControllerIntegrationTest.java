package com.example.reactive_backend.task;

import com.example.reactive_backend.IntegrationTestConfig;
import com.example.reactive_backend.ReactiveBackendIntegrationTest;
import com.example.reactive_backend.controller.TaskController;
import com.example.reactive_backend.errorhandling.ErrorAdviceDto;
import com.example.reactive_backend.errorhandling.exception.CouldNotInsertException;
import com.example.reactive_backend.errorhandling.exception.CouldNotUpdateException;
import com.example.reactive_backend.errorhandling.exception.NotFoundException;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.repository.TaskRepository;
import com.example.reactive_backend.service.TaskService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Import(IntegrationTestConfig.class)
public class TaskControllerIntegrationTest extends ReactiveBackendIntegrationTest {
    @Autowired
    private TaskController controller;

    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository repository;

    private final String id = "685724022e21a9baae11f00f";
    private final String badId = "685724022";

    @Test
    void testGetAllTasksEndpoint() {
        Task task1 = Task.builder().id(new ObjectId()).title("Integration Test Task One").description("The first Task item in the Flux").completed(true).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Integration Test Task Two").description("The second Task item in the Flux").completed(false).build();

        when(mongoTemplate.findAll(Task.class)).thenReturn(Flux.just(task1, task2));

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

    @Test
    void testGetOneTaskEndpoint() {
        Task task = Task.builder().id(new ObjectId(id)).title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        when(mongoTemplate.findById(new ObjectId(id), Task.class)).thenReturn(Mono.just(task));

        webTestClient.get()
                .uri("/api/task?id=%s".formatted(id))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .isEqualTo(task);
    }

    @Test
    void testGetOneTaskEndpointBadRequestException() {
        webTestClient.get()
                .uri("/api/task?id=%s".formatted(badId))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.GET.name());
                });
    }

    @Test
    void testGetOneTaskEndpointNotFoundException() {
        when(mongoTemplate.findById(new ObjectId(id), Task.class)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/task?id=%s".formatted(id))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.GET.name());
                });
    }

    @Test
    void testGetOneTaskEndpointRuntimeException() {
        when(mongoTemplate.findById(new ObjectId(id), Task.class)).thenReturn(Mono.error(new RuntimeException("Runtime exception.")));

        webTestClient.get()
                .uri("/api/task?id=%s".formatted(id))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.GET.name());
                });
    }

    @Test
    void testCreateOneTaskEndpoint() {
        Task task = Task.builder().title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        when(mongoTemplate.insert(task)).thenReturn(Mono.just(task));

        webTestClient.post()
                .uri("/api/task")
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .isEqualTo(task);
    }

    @Test
    void testCreateOneTaskEndpointCouldNotInsertException() {
        Task task = Task.builder().title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        when(mongoTemplate.insert(task)).thenReturn(Mono.error(new CouldNotInsertException("Could not insert.")));

        webTestClient.post()
                .uri("/api/task")
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.POST.name());
                });
    }

    @Test
    void testCreateTasksEndpoint() {
        Task task = Task.builder().title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        when(mongoTemplate.insert(task)).thenReturn(Mono.just(task));

        webTestClient.post()
                .uri("/api/task")
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .isEqualTo(task);
    }

    @Test
    void testCreateTasksEndpointCouldNotInsertException() {
        Task task1 = Task.builder().title("Integration Test Task One").description("The description for the first Document being returned.").completed(true).build();
        Task task2 = Task.builder().title("Integration Test Task Two").description("The description for the second Document being returned.").completed(true).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));

        when(mongoTemplate.insertAll(taskList)).thenReturn(Flux.error(new CouldNotInsertException("Could not insert Documents.")));

        webTestClient.post()
                .uri("/api/task")
                .body(BodyInserters.fromValue(taskList))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.POST.name());
                });
    }

    @Test
    void testUpdateOneTaskEndpoint() {
        Task task = Task.builder().id(new ObjectId(id)).title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Task.class))).thenReturn(Mono.just(task));

        webTestClient.put()
                .uri("/api/task?id=%s".formatted(id))
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .isEqualTo(task);
    }

    @Test
    void testUpdateOneTaskEndpointBadRequestException() {
        Task task = Task.builder().id(new ObjectId(id)).title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        webTestClient.put()
                .uri("/api/task?id=%s".formatted(badId))
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.PUT.name());
                });
    }

    @Test
    void testUpdateOneTaskEndpointNotFoundException() {
        Task task = Task.builder().id(new ObjectId(id)).title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();
        Query query = new Query(Criteria.where("_id").is(id));
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Update update = new Update()
                .set("description", task.getDescription())
                .set("title", task.getTitle())
                .set("completed", task.isCompleted());

        when(mongoTemplate.findAndModify(eq(query), eq(update), eq(options), eq(Task.class))).thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/api/task?id=%s".formatted(id))
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.PUT.name());
                });
    }

    @Test
    void testUpdateOneTaskEndpointCouldNotUpdateException() {
        Task task = Task.builder().id(new ObjectId(id)).title("Integration Test Task").description("The description for the Document being returned.").completed(true).build();

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Task.class))).thenReturn(Mono.error(new CouldNotUpdateException("Could not update.")));

        webTestClient.put()
                .uri("/api/task?id=%s".formatted(id))
                .body(BodyInserters.fromValue(task))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorAdviceDto.class)
                .consumeWith(actual -> {
                    assertThat(actual.getResponseBody().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    assertThat(actual.getResponseBody().getPath()).isEqualTo("/api/task");
                    assertThat(actual.getResponseBody().getMessage()).isNotEmpty();
                    assertThat(actual.getResponseBody().getHttpMethod()).isEqualTo(HttpMethod.PUT.name());
                });
    }
}
