package com.example.reactive_backend.repository;

import com.example.reactive_backend.errorhandling.exception.CouldNotInsertException;
import com.example.reactive_backend.model.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserAccountRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<UserAccount> retrieveUserAccount(ObjectId id) {
        return mongoTemplate.findById(id, UserAccount.class)
                .doOnSubscribe(sub -> log.info("Searching for User Account with ID: %s".formatted(id)))
                .doOnSuccess(acc -> log.info("Retrieved User Account: %s".formatted(acc.toString())))
                .doOnError(err -> log.error("An error occurred while searching for User Account with ID: %s \nError: %s".formatted(id, err.toString())))
                .onErrorMap(err -> new RuntimeException("An error occurred while searching for User Account with ID: %s".formatted(id)));
    }

    public Mono<UserAccount> createNewUserAccount(UserAccount userAccountInfo) {
        return mongoTemplate.insert(userAccountInfo)
                .doOnSubscribe(sub -> log.info("Attempting to create new User Account."))
                .doOnSuccess(acc -> log.info("Successfully created new User Account."))
                .doOnError(err -> log.error("An error occurred while trying to create new User Account: %s".formatted(err)))
                .onErrorMap(err -> new CouldNotInsertException("An error occurred while trying to create new User Account: %s".formatted(err)));
    }
}
