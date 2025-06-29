package com.example.reactive_backend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Jacksonized
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UserAccountDTO {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Task> tasks;
}
