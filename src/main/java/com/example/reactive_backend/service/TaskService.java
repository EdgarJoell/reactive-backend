package com.example.reactive_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class TaskService {
    public Flux<String> getAllTasks() {
        return Flux.just("Hello", "World", "Coming", "From", "The", "Zambrana", "Household");
    }
}
