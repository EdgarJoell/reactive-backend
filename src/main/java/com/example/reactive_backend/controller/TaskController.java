package com.example.reactive_backend.controller;

import com.example.reactive_backend.errorhandling.exception.BadRequestException;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping(value = "/task")
    public Mono<Task> getOneTask(@RequestParam String id) {
        try {
            return taskService.getOneTask(new ObjectId(id));
        } catch (IllegalArgumentException ex) {
            log.error("The id: '%s' was not in the correct ObjectID format.".formatted(id));
            return Mono.error(new BadRequestException("The id: '%s' was not in the correct ObjectID format.".formatted(id)));
        }
    }

    @GetMapping(value = "/tasks")
    public Flux<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping(value = "/task")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> createOneTask(@RequestBody Task task) {
        return taskService.createOneTask(task);
    }

    @PostMapping(value = "/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public Flux<Task> createTasks(@RequestBody ArrayList<Task> tasks) {
        return taskService.createTasks(tasks);
    }
}
