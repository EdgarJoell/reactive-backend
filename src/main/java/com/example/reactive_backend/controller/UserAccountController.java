package com.example.reactive_backend.controller;

import com.example.reactive_backend.errorhandling.exception.BadRequestException;
import com.example.reactive_backend.model.UserAccount;
import com.example.reactive_backend.model.UserAccountDTO;
import com.example.reactive_backend.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.example.reactive_backend.utils.UtilMethods.checkIdIntegrity;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class UserAccountController {
    private final UserAccountService service;

    @GetMapping(value = "/user")
    public Mono<UserAccountDTO> retrieveUserAccount(@RequestParam String id) {
        if(checkIdIntegrity(id)) return Mono.error(new BadRequestException("This supplied ID: %s is in the correct format".formatted(id)));

        return service.retrieveUserAccount(new ObjectId(id));
    }

    @PostMapping(value = "/user/new")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserAccount> createNewUserAccount(@RequestBody UserAccount userAccountInfo) {
        return service.createNewUserAccount(userAccountInfo);
    }
}
