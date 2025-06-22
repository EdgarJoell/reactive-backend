package com.example.reactive_backend.service;

import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Flux<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    public Mono<Task> createOneTask(Task task) {
        return taskRepository.createOneTask(task);
    }
}
