package com.example.reactive_backend;

import com.example.reactive_backend.controller.TaskController;
import com.example.reactive_backend.repository.TaskRepository;
import com.example.reactive_backend.service.TaskService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
public class IntegrationTestConfig {
    @Bean
    public TaskService taskService(TaskRepository taskRepository) {
        return new TaskService(taskRepository);
    }

    @Bean
    public TaskController taskController(TaskService taskService) {
        return new TaskController(taskService);
    }

    @Bean
    public TaskRepository taskRepository(ReactiveMongoTemplate mongoTemplate) {
        return new TaskRepository(mongoTemplate);
    }

    @Bean
    public ReactiveMongoTemplate mongoTemplate() {
        return Mockito.mock(ReactiveMongoTemplate.class);
    }
}
