package com.example.reactive_backend.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks") // This line here will tell the Reactive Mongo Client which collection to find.
@RequiredArgsConstructor
@Data
public class Task {
    @Id
    private ObjectId id;
    private String title;
    private String description;
    private boolean completed;
}
