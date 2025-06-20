package com.example.reactive_backend.service;

import com.example.reactive_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    public Flux<String> getAllTasks() {
        return taskRepository.getAllTasks()
                .flatMapIterable(str -> Collections.singleton(str + ", "));
    }
}
