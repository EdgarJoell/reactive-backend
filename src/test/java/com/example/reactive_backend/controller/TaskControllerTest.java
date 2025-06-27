package com.example.reactive_backend.controller;

import com.example.reactive_backend.errorhandling.exception.*;
import com.example.reactive_backend.model.Task;
import com.example.reactive_backend.service.TaskService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Description;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    @Mock
    private TaskService service;
    @InjectMocks
    private TaskController controller;

    @Test
    @Description("Tests a 200 response for the getAllTasks() endpoint workflow and returns data.")
    void testGetAllTasksEndpointHappyPathWithData() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Title Two").description("The testing description for test Title Two").completed(true).build();

        Flux<Task> taskFlux = Flux.just(task1, task2);

        when(service.getAllTasks()).thenReturn(taskFlux);

        Flux<Task> res = controller.getAllTasks();

        StepVerifier.create(res)
                .expectNext(task1)
                .expectNext(task2)
                .expectComplete()
                .verify();
    }

    @Test
    @Description("Tests a 200 response for the getAllTasks() endpoint workflow and returns an empty Flux.")
    void testGetAllTasksEndpointHappyPathWithEmptyData() {
        when(service.getAllTasks()).thenReturn(Flux.empty());

        Flux<Task> res = controller.getAllTasks();

        StepVerifier.create(res)
                .expectComplete()
                .verify();
    }

    @Test
    @Description("Tests a 500 response for the getAllTasks() endpoint workflow and returns a RuntimeException class to show that the server failed.")
    void testGetAllTasksEndpointUnhappyPath() {
        when(service.getAllTasks()).thenReturn(Flux.error(new RuntimeException("An error occurred: ")));

        Flux<Task> res = controller.getAllTasks();

        StepVerifier.create(res)
                .expectError()
                .verify();
    }

    @Test
    @Description("Tests a 200 response for the getOneTask() endpoint workflow and returns data.")
    void testGetOneTaskEndpointHappyPath() {
        String idString = "685724022e21a9baae11f00c";
        Task task = Task.builder().id(new ObjectId("685724022e21a9baae11f00c")).title("Get One Task Title").description("The test description so that we can test getOneTask endpoint functionality").completed(true).build();

        when(service.getOneTask(new ObjectId(idString))).thenReturn(Mono.just(task));

        Mono<Task> res = controller.getOneTask("685724022e21a9baae11f00c");

        StepVerifier.create(res)
                .consumeNextWith(actual -> {
                    assertThat(actual.getId().toString()).isEqualTo("685724022e21a9baae11f00c");
                    assertThat(actual.getTitle()).isEqualTo("Get One Task Title");
                    assertThat(actual.getDescription()).isEqualTo("The test description so that we can test getOneTask endpoint functionality");
                    assertThat(actual.isCompleted()).isEqualTo(true);
                })
                .verifyComplete();
    }

    @Test
    @Description("Tests a 404 response for the getOneTask() endpoint workflow and returns the NotFoundException class.")
    void testGetOneTaskEndpointUnhappyPathWith404Error() {
        String idString = "685724022e21a9baae11f00c";

        when(service.getOneTask(new ObjectId(idString))).thenReturn(Mono.error(new NotFoundException("Task could not be found!")));

        Mono<Task> res = controller.getOneTask(idString);

        StepVerifier.create(res)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    @Description("Tests a 500 response for the getOneTask() endpoint workflow and returns RuntimeException so show the server failed.")
    void testGetOneTaskEndpointUnhappyPath() {
        String idString = "685724022e21a9baae11f00c";

        when(service.getOneTask(new ObjectId(idString))).thenReturn(Mono.error(new RuntimeException("An error occurred: ")));

        Mono<Task> res = controller.getOneTask(idString);

        StepVerifier.create(res)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @Description("Tests a 400 response for the getOneTask() endpoint workflow and returns RuntimeException so show the server failed.")
    void testGetOneTaskEndpointUnhappyPathWithInvalidObjectIdString() {
        String idString = "This is the invalid ObjectID string.";

        Mono<Task> res = controller.getOneTask(idString);

        StepVerifier.create(res)
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    @Description("Tests a 201 response for the createOneTask() endpoint workflow and returns data.")
    void testCreateNewTaskHappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        Mono<Task> taskMono = Mono.just(task1);

        when(service.createOneTask(task1)).thenReturn(taskMono);

        Mono<Task> res = controller.createOneTask(task1);

        StepVerifier.create(res)
                .consumeNextWith(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getTitle()).isEqualTo("Test Title One");
                    assertThat(response.getDescription()).isEqualTo("The testing description for test Title One");
                    assertThat(response.isCompleted()).isEqualTo(false);
                })
                .verifyComplete();
    }

    @Test
    @Description("Tests a 500 response for the createOneTask() endpoint workflow and returns CouldNotInsertException class.")
    void testCreateNewTaskUnhappyPath() {
        Task task = Task.builder().title("Test Title One").description("The testing description for test Title One").completed(false).build();

        when(service.createOneTask(task)).thenReturn(Mono.error(new CouldNotInsertException("Could not insert Document into 'Tasks' Collection")));

        Mono<Task> res = controller.createOneTask(task);

        StepVerifier.create(res)
                .expectError(CouldNotInsertException.class)
                .verify();
    }

    @Test
    @Description("Tests a 201 response for the createTasks() endpoint workflow and returns data.")
    void testCreateTasksHappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Group Insert Title One").description("The testing description for test Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Group Insert Title Two").description("The testing description for test Title Two").completed(true).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));

        Flux<Task> tasksFlux = Flux.just(task1);

        when(service.createTasks(taskList)).thenReturn(tasksFlux);

        Flux<Task> res = controller.createTasks(taskList);

        StepVerifier.create(res)
                .consumeNextWith(actual -> {
                    assertThat(actual.getId()).isEqualTo(taskList.get(0).getId());
                    assertThat(actual.getTitle()).isEqualTo(taskList.get(0).getTitle());
                    assertThat(actual.getDescription()).isEqualTo(taskList.get(0).getDescription());
                    assertThat(actual.isCompleted()).isEqualTo(taskList.get(0).isCompleted());
                })
                .consumeNextWith(actual ->{
                    assertThat(actual.getId()).isEqualTo(taskList.get(1).getId());
                    assertThat(actual.getTitle()).isEqualTo(taskList.get(1).getTitle());
                    assertThat(actual.getDescription()).isEqualTo(taskList.get(1).getDescription());
                    assertThat(actual.isCompleted()).isEqualTo(taskList.get(1).isCompleted());
                })
                .expectComplete();
    }

    @Test
    @Description("Tests a 500 response for the createTasks() endpoint workflow and returns CouldNotInsertException class.")
    void testCreateTasksUnhappyPath() {
        Task task1 = Task.builder().id(new ObjectId()).title("Test Group Insert Title One").description("The testing description for test Title One").completed(false).build();
        Task task2 = Task.builder().id(new ObjectId()).title("Test Group Insert Title Two").description("The testing description for test Title Two").completed(true).build();
        ArrayList<Task> taskList = new ArrayList<>(Arrays.asList(task1, task2));

        when(service.createTasks(taskList)).thenReturn(Flux.error(new CouldNotInsertException("Could not insert Document into 'Tasks' Collection")));

        Flux<Task> res = controller.createTasks(taskList);

        StepVerifier.create(res)
                .expectError(CouldNotInsertException.class)
                .verify();
    }

    @Test
    @Description("Tests a 200 response for the updateOneTask() endpoint workflow and returns data.")
    void testUpdateTaskHappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");
        Task task = Task.builder().id(id).title("Test Update Title One").description("The testing description for updating test Title One").completed(false).build();

        Mono<Task> taskMono = Mono.just(task);

        when(service.updateOneTask(id, task)).thenReturn(taskMono);

        Mono<Task> res = controller.updateOneTask(String.valueOf(id), task);

        StepVerifier.create(res)
                .expectSubscription()
                .consumeNextWith(actual -> {
                    assertThat(actual.getId()).isEqualTo(task.getId());
                    assertThat(actual.getTitle()).isEqualTo(task.getTitle());
                    assertThat(actual.getDescription()).isEqualTo(task.getDescription());
                    assertThat(actual.isCompleted()).isEqualTo(task.isCompleted());
                })
                .verifyComplete();
    }

    @Test
    @Description("Tests a 500 response for the updateOneTask() endpoint workflow and returns CouldNotUpdateException class.")
    void testUpdateTaskUnhappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");
        Task task = Task.builder().id(id).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        when(service.updateOneTask(id, task)).thenReturn(Mono.error(new CouldNotUpdateException("Could not insert Document into 'Tasks' Collection")));

        Mono<Task> res = controller.updateOneTask(String.valueOf(id), task);

        StepVerifier.create(res)
                .expectError(CouldNotUpdateException.class)
                .verify();
    }

    @Test
    @Description("Tests a 400 response for the updateOneTask() endpoint workflow and returns RuntimeException so show the server failed.")
    void testUpdateOneTaskEndpointUnhappyPathWithInvalidObjectIdString() {
        String idString = "This is the invalid ObjectID string.";
        Task task = Task.builder().title("Test Title One").description("The testing description for test Title One").completed(false).build();

        Mono<Task> res = controller.updateOneTask(idString, task);

        StepVerifier.create(res)
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    @Description("Tests happy path for deleteOneTask.")
    void testDeleteOneTaskEndpointHappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");
        Task task = Task.builder().id(id).title("Test Title One").description("The testing description for test Title One").completed(false).build();

        when(service.deleteOneTask(id)).thenReturn(Mono.just(task));

        Mono<Task> res = controller.deleteOneTask(id.toString());

        StepVerifier.create(res)
                .expectSubscription()
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    @Description("Tests unhappy path for deleteOneRecord but no Document was found.")
    void testDeleteOneTaskEndpointUnhappyPathNotFound() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");

        when(service.deleteOneTask(id)).thenReturn(Mono.error(new NotFoundException("No Document with this id.")));

        Mono<Task> res = controller.deleteOneTask(id.toString());

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    @Description("Tests unhappy path for deleteOneRecord but an unexpected error occurred.")
    void testDeleteOneTaskEndpointUnhappyPath() {
        ObjectId id = new ObjectId("685724022e21a9baae11f00f");

        when(service.deleteOneTask(id)).thenReturn(Mono.error(new CouldNotDeleteException("Unexpected error. Could not delete.")));

        Mono<Task> res = controller.deleteOneTask(id.toString());

        StepVerifier.create(res)
                .expectSubscription()
                .expectError(CouldNotDeleteException.class)
                .verify();
    }

    @Test
    @Description("Tests unhappy path for deleteOneRecord but ID was in the incorrect format.")
    void testDeleteOneTaskEndpointUnhappyPathWithBadId() {
        Mono<Task> res = controller.deleteOneTask("Bad ID format");

        StepVerifier.create(res)
                .expectError(BadRequestException.class)
                .verify();
    }
}
