package com.example.reactive_backend.service;

import com.example.reactive_backend.errorhandling.exception.NotFoundException;
import com.example.reactive_backend.model.UserAccount;
import com.example.reactive_backend.model.UserAccountDTO;
import com.example.reactive_backend.repository.TaskRepository;
import com.example.reactive_backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserAccountRepository repository;
    private final TaskRepository taskRepository;

    public Mono<UserAccountDTO> retrieveUserAccount(ObjectId id) {
        return repository.retrieveUserAccount(id)
                .switchIfEmpty(Mono.error(new NotFoundException("There was no User Account associated with the ID: %s".formatted(id))))
                .flatMap(acc -> taskRepository.getAllTasksForOneUserAccount(id)
                        .collectList()
                        .map(tasks -> new UserAccountDTO(acc.getId(), acc.getFirstName(), acc.getLastName(), acc.getEmail(), tasks)));
    }

    public Mono<UserAccount> createNewUserAccount(UserAccount userAccountInfo) {
        return repository.createNewUserAccount(userAccountInfo);
    }
}
