package com.samuel.bowling.service.impl;

import com.samuel.bowling.exception.GameNotStartedException;
import com.samuel.bowling.exception.PlayerNotFoundException;
import com.samuel.bowling.exception.PlayersBadRequestException;
import com.samuel.bowling.message.FrameRequest;
import com.samuel.bowling.message.NewGameRequest;
import com.samuel.bowling.message.ScoreResponse;
import com.samuel.bowling.model.entity.Game;
import com.samuel.bowling.model.entity.Player;
import com.samuel.bowling.service.GameService;
import com.samuel.bowling.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.samuel.bowling.model.ConstantStrings.GAME_CREATED;
import static com.samuel.bowling.model.ConstantStrings.NAMES_NUMBER_MISMATCHING;
import static com.samuel.bowling.model.ConstantStrings.PLAYER_NOT_FOUND;
import static com.samuel.bowling.model.ConstantStrings.START_GAME_FIRST;
import static com.samuel.bowling.model.ConstantStrings.TOO_MANY_PLAYERS;

@Slf4j
@Service
public class PlayerServiceImpl implements PlayerService {
    private Set<Player> players;
    @Value("${max.players}")
    private int maxPlayersPerGame;
    @Resource
    GameService gameService;

    @Override
    public String startNewGame(NewGameRequest newGameRequest) {
        players = new HashSet<>();

        int numberPlayers = newGameRequest.getNumberOfPlayers();

        if (numberPlayers > maxPlayersPerGame) {
            throw new PlayersBadRequestException(String.format(TOO_MANY_PLAYERS, maxPlayersPerGame));
        }

        int numberOfNames = newGameRequest.getNameOfThePlayers().size();

        if (numberOfNames != numberPlayers) {
            throw new PlayersBadRequestException(NAMES_NUMBER_MISMATCHING);
        }

        newGameRequest.getNameOfThePlayers().stream()
                .forEach(name -> players.add(new Player(name, gameService.createNewGame())));

        log.info(GAME_CREATED);

        return GAME_CREATED;
    }

    @Override
    public String playOneFrame(FrameRequest frameRequest) {
        String playerName = frameRequest.getPlayerName();
        if (players == null) {
            throw new GameNotStartedException(START_GAME_FIRST);
        }
        Optional<Player> matchingPlayer = findPlayerByName(Optional.ofNullable(playerName));

        if (matchingPlayer.isPresent()) {
            gameService.setActiveGame(matchingPlayer.get().getGame());
        } else {
            throw new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerName));
        }

        gameService.rollTheBall(frameRequest.getRollsRequest());

        return saveActiveGameFromPlayer(matchingPlayer.get());

    }

    @Override
    public List<ScoreResponse> showTotalScore() {
        List<ScoreResponse> scoreResponses = new ArrayList<>();

        if (players == null || players.isEmpty()) {
            throw new GameNotStartedException(START_GAME_FIRST);
        }

        players.stream()
                .forEach(player ->{
                    scoreResponses.add(ScoreResponse.builder()
                    .gameState(player.getGame().getStateOfTheGame())
                    .score(player.getGame().getScore())
                    .playerName(player.getName())
                    .build());
                });

        return scoreResponses;
    }

    @Override
    public Optional<ScoreResponse> showScoreByName(String playerName) {
        if (players == null) {
            throw new GameNotStartedException(START_GAME_FIRST);
        }
        Optional<Player> matchingPlayer = findPlayerByName(Optional.ofNullable(playerName));
        if (matchingPlayer.isPresent()) {
            Game playerGame = matchingPlayer.get().getGame();
            return Optional.of(ScoreResponse.builder()
                    .gameState(playerGame.getStateOfTheGame())
                    .playerName(playerName)
                    .score(playerGame.getScore())
                    .build());
        } else {
            throw new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND, playerName));
        }
    }

    private String saveActiveGameFromPlayer(Player matchingPlayer) {
        players.remove(matchingPlayer);
        Game activeGame = gameService.getActiveGame();
        matchingPlayer.setGame(activeGame);
        players.add(matchingPlayer);
        return activeGame.getStateOfTheGame();
    }

    private Optional<Player> findPlayerByName(Optional <String> playerName) {
        if(playerName.isPresent()) {
            return players.stream().
                    filter(p -> p.getName().equalsIgnoreCase(playerName.get())).
                    findFirst();
        } return Optional.empty();
    }
}
