package com.boardcamp.api.services;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.errors.GameNameConflictError;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;

@ExtendWith(MockitoExtension.class)
class GameServiceTests {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;
    
    @Test
    void givenRepeatedGame_whenCreatingGame_thenThrowsError() {
        GameDTO game = new GameDTO("Test", "Test", 1, 1);

        doReturn(true).when(gameRepository).existsByName(any());

        GameNameConflictError error = assertThrows(
            GameNameConflictError.class, 
            () -> gameService.createGame(game));

        assertNotNull(error);
        assertEquals("A game with this name already exists", error.getMessage());
    }

    @Test
    void givenNewGame_whenCreatingGame_thenSavesGame() {
        GameDTO game = new GameDTO("New Game", "Test", 2, 1000);

        doReturn(false).when(gameRepository).existsByName(game.getName());

        gameService.createGame(game);

        verify(gameRepository, times(1)).save(any());
    }

    @Test
    void givenValidGame_whenCreatingGame_thenReturnsGame() {
        GameDTO game = new GameDTO("Valid Game", "Test", 3, 1500);

        doReturn(false).when(gameRepository).existsByName(game.getName());

        GameModel savedGame = new GameModel(game);
        doReturn(savedGame).when(gameRepository).save(any());

        GameModel result = gameService.createGame(game);

        assertNotNull(result);
        assertEquals("Valid Game", result.getName());
        assertEquals(3, result.getStockTotal());
        assertEquals(1500, result.getPricePerDay());
    }
}
