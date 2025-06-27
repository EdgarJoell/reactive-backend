package com.example.reactive_backend.service;

import com.example.reactive_backend.errorhandling.exception.NotFoundException;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Mono<Task> getOneTask(ObjectId id) {
        return taskRepository.getOneTask(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Could not find task with id: %s".formatted(id))));
    }

    public Flux<Task> getAllTasks() {
        return taskRepository.getAllTasks();
    }

    public Mono<Task> createOneTask(Task task) {
        return taskRepository.createOneTask(task);
    }

    public Flux<Task> createTasks(ArrayList<Task> tasks) {
        return taskRepository.createTasks(tasks);
    }

    public Mono<Task> updateOneTask(ObjectId id, Task task) {
        return taskRepository.updateOneTask(id, task)
                .switchIfEmpty(Mono.error(new NotFoundException("Could not find task with id: %s".formatted(id))));
    }

    public Mono<Task> deleteOneTask(ObjectId id) {
        return taskRepository.deleteOneTask(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Could not find task with id: %s".formatted(id))));
    }
}
