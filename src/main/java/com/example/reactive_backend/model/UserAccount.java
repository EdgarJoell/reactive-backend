package com.example.reactive_backend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Jacksonized
@Builder(toBuilder = true)
@Document("user_accounts")
public class UserAccount {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @JsonSerialize(using = ToStringSerializer.class)
    private ArrayList<ObjectId> taskIds;
}
