package com.boardcamp.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcamp.api.dtos.GameDTO;
import com.boardcamp.api.errors.GameNameConflictError;
import com.boardcamp.api.models.GameModel;
import com.boardcamp.api.repositories.GameRepository;

@Service
public class GameService {
    final GameRepository gameRepository;

    GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameModel createGame(GameDTO body) {
        if(gameRepository.existsByName(body.getName())) {
            throw new GameNameConflictError("A game with this name already exists");
        }

        GameModel game = new GameModel(body);
        return gameRepository.save(game);
    }

    public List<GameModel> getGames() {
        return gameRepository.findAll();
    }


}
