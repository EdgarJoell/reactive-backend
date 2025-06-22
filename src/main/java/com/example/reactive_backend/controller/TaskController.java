package com.example.reactive_backend.controller;

import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping(value = "/tasks")
    public Flux<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping(value = "/task")
    public Mono<Task> createOneTask(@RequestBody Task task) {
        return taskService.createOneTask(task);
    }
}
