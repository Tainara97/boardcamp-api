package com.boardcamp.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.boardcamp.api.dtos.RentalDTO;
import com.boardcamp.api.models.CustomerModel;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.models.RentalModel;
import com.boardcamp.api.repositories.CustomerRepository;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ControllerAdvice("test")
class RentalControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        rentalRepository.deleteAll();
        gameRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void givenValidRental_whenCreatingRental_thenSavesRental() {

        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        GameModel game = gameRepository.save(
                new GameModel(1L, "Test", null, 1, 1000));

        RentalDTO dto = new RentalDTO(customer.getId(), game.getId(), 2);

        HttpEntity<RentalDTO> body = new HttpEntity<>(dto);

        ResponseEntity<RentalModel> response = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                RentalModel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getDelayFee());
        assertNull(response.getBody().getReturnDate());
    }

    @Test
    void givenInvalidGameId_whenCreatingRental_thenThrowsError() {

        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        RentalDTO dto = new RentalDTO(customer.getId(), 999L, 2);

        HttpEntity<RentalDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void givenNoStockAvailable_whenCreatingRental_thenThrowsError() {

        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        GameModel game = gameRepository.save(
                new GameModel(1L, "Test", null, 1, 1000));

        rentalRepository.save(
                new RentalModel(null, customer, game, LocalDate.now(), 2, null, 2000, 0));

        RentalDTO dto = new RentalDTO(customer.getId(), game.getId(), 2);

        HttpEntity<RentalDTO> body = new HttpEntity<>(dto);

        ResponseEntity<String> response = restTemplate.exchange(
                "/rentals",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void givenOpenRental_whenReturningRental_thenReturnRental() {

        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        GameModel game = gameRepository.save(
                new GameModel(1L, "Test", null, 1, 1000));

        RentalModel rental = rentalRepository.save(
                new RentalModel(null, customer, game, LocalDate.now(), 2, null, 2000, 0));

        ResponseEntity<RentalModel> response = restTemplate.exchange(
                "/rentals/" + rental.getId() + "/return",
                HttpMethod.POST,
                null,
                RentalModel.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getReturnDate());
    }

    @Test
    void givenReturnedRental_whenReturningRental_thenThrowsError() {
        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        GameModel game = gameRepository.save(
                new GameModel(1L, "Test", null, 1, 1000));

        RentalModel rental = rentalRepository.save(
                new RentalModel(null, customer, game, LocalDate.now(), 2, LocalDate.now(), 2000, 0)
            );

        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals/" + rental.getId() + "/return",
            HttpMethod.POST,
            null,
            String.class
        );

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }

    @Test
    void givenReturnedRental_whenDeletingRental_thenDeleteRental() {
        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        GameModel game = gameRepository.save(
                new GameModel(1L, "Test", null, 1, 1000));

        RentalModel rental = rentalRepository.save(
                new RentalModel(null, customer, game, LocalDate.now(), 2, LocalDate.now(), 2000, 0)
        );
        
        ResponseEntity<Void> response = restTemplate.exchange(
           "/rentals/" + rental.getId(),
           HttpMethod.DELETE,
           null,
           Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void givenOpenRental_whenDeletingRental_thenThrowsError() {
        CustomerModel customer = customerRepository.save(
                new CustomerModel(1L, "Test", "22222222222", "12345678910"));

        GameModel game = gameRepository.save(
                new GameModel(1L, "Test", null, 1, 1000));

        RentalModel rental = rentalRepository.save(
                new RentalModel(null, customer, game, LocalDate.now(), 2, null, 2000, 0)
        );

        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals/" + rental.getId(),
            HttpMethod.DELETE,
            null,
            String.class    
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
