package com.example.reactive_backend.controller;

import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.service.TaskService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
}
