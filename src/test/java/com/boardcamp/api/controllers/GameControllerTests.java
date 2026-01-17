package com.boardcamp.api.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.web.client.TestRestTemplate;


import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;
import com.boardcamp.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GameControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    @AfterEach
    void cleanDatabase() {
        rentalRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenRepeatedGame_whenCreatingGame_thenThrowsError() {
        GameModel gameConflict = new GameModel(null, "Test", "Test", 3, 1500);
        gameRepository.save(gameConflict);

        GameDTO gameDTO = new GameDTO("Test", "Test", 3, 1500);

        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        ResponseEntity<String> response = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body,
                String.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("A game with this name already exists", response.getBody());
        assertEquals(1, gameRepository.count());
    }

    @Test
    void givenValidGame_whenCreatingGame_thenReturnsGame() {
        GameDTO gameDTO = new GameDTO("Valid Game", "Test", 2, 2500);

        HttpEntity<GameDTO> body = new HttpEntity<>(gameDTO);

        ResponseEntity<GameModel> response = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body,
                GameModel.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("Valid Game", response.getBody().getName());
    }

    @Test
    void givenInvalidGame_whenCreatingGame_thenThrowsError() {
        GameDTO invalidGame = new GameDTO("Invalid Game", "Test", 0, 1500);

        HttpEntity<GameDTO> body = new HttpEntity<>(invalidGame);

        ResponseEntity<String> response = restTemplate.exchange(
                "/games",
                HttpMethod.POST,
                body,
                String.class
            );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
