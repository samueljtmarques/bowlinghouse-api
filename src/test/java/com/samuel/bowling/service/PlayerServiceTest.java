package com.samuel.bowling.service;

import com.samuel.bowling.exception.BadFrameRequestException;
import com.samuel.bowling.exception.PlayerNotFoundException;
import com.samuel.bowling.exception.PlayersBadRequestException;
import com.samuel.bowling.message.FrameRequest;
import com.samuel.bowling.message.NewGameRequest;
import com.samuel.bowling.message.RollsRequest;
import com.samuel.bowling.message.ScoreResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.samuel.bowling.model.ConstantStrings.CURRENT_SCORE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.GAME_CREATED;
import static com.samuel.bowling.model.ConstantStrings.READY_TO_START;
import static com.samuel.bowling.model.ConstantStrings.STRIKE_MESSAGE;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class PlayerServiceTest {

    @Value("${max.players}")
    private int maxPlayersPerGame;

    @Autowired
    PlayerService playerService;

    @Test
    public void startNewGame() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(2);
        newGameRequest.setNameOfThePlayers(Arrays.asList("Samuel", "Marques"));
        Assert.assertEquals(GAME_CREATED, playerService.startNewGame(newGameRequest));
    }

    @Test(expected = PlayersBadRequestException.class)
    public void failToStartNewGame_namesNumberMismatching() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(1);
        newGameRequest.setNameOfThePlayers(Arrays.asList("Samuel", "Marques"));
        playerService.startNewGame(newGameRequest);
    }


    @Test(expected = PlayersBadRequestException.class)
    public void failToStartNewGame_tooManyPlayers() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(9);

        playerService.startNewGame(newGameRequest);
    }

    @Test
    public void playOneFrame() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(2);
        String playerOneName = "Samuel";
        String playerTwoName = "Marques";
        newGameRequest.setNameOfThePlayers(Arrays.asList(playerOneName, playerTwoName));
        playerService.startNewGame(newGameRequest);

        FrameRequest frameRequestOne = new FrameRequest();
        frameRequestOne.setPlayerName(playerOneName);
        frameRequestOne.setRollsRequest(new RollsRequest("2", "2"));

        FrameRequest frameRequestTwo = new FrameRequest();
        frameRequestTwo.setPlayerName(playerTwoName);
        frameRequestTwo.setRollsRequest(new RollsRequest("4", "2"));

        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 4), playerService.playOneFrame(frameRequestOne));
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 6), playerService.playOneFrame(frameRequestTwo));
    }

    @Test(expected = PlayerNotFoundException.class)
    public void playOneFrameInvalidGame() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(2);
        String playerOneName = "Samuel";
        String playerTwoName = "Marques";
        newGameRequest.setNameOfThePlayers(Arrays.asList(playerOneName, playerTwoName));
        playerService.startNewGame(newGameRequest);

        FrameRequest frameRequestOne = new FrameRequest();
        frameRequestOne.setPlayerName("Mariana");
        frameRequestOne.setRollsRequest(new RollsRequest("2", "2"));
        playerService.playOneFrame(frameRequestOne);
    }

    @Test(expected = BadFrameRequestException.class)
    public void playOneFrameBadFrameRequest() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(2);
        String playerOneName = "Samuel";
        String playerTwoName = "Marques";
        newGameRequest.setNameOfThePlayers(Arrays.asList(playerOneName, playerTwoName));
        playerService.startNewGame(newGameRequest);
        FrameRequest frameRequestOne = new FrameRequest();
        frameRequestOne.setPlayerName(playerOneName);
        frameRequestOne.setRollsRequest(new RollsRequest("a", "b"));
        playerService.playOneFrame(frameRequestOne);

    }

    @Test
    public void showTotalScore() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(2);
        String playerOneName = "Samuel";
        String playerTwoName = "Marques";
        newGameRequest.setNameOfThePlayers(Arrays.asList(playerOneName, playerTwoName));
        playerService.startNewGame(newGameRequest);

        List<ScoreResponse> scoreResponses = playerService.showTotalScore();
        Assert.assertNotNull(scoreResponses);
        Assert.assertTrue(scoreResponses.size() == 2);
        ScoreResponse scorePlayerOne = scoreResponses.get(0);
        ScoreResponse scorePlayerTwo = scoreResponses.get(1);
        Assert.assertTrue(scorePlayerOne.getPlayerName().equalsIgnoreCase(playerOneName));
        Assert.assertTrue(scorePlayerTwo.getPlayerName().equalsIgnoreCase(playerTwoName));
    }

    @Test
    public void showScoreByPlayer_simpleCase() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(1);
        String playerOneName = "Samuel";
        newGameRequest.setNameOfThePlayers(Arrays.asList(playerOneName));

        playerService.startNewGame(newGameRequest);

        Optional<ScoreResponse> scoreResponse = playerService.showScoreByName(playerOneName);
        Assert.assertNotNull(scoreResponse);
        Assert.assertTrue(scoreResponse.isPresent());
        Assert.assertEquals(playerOneName, scoreResponse.get().getPlayerName());
        Assert.assertEquals(READY_TO_START, scoreResponse.get().getGameState());
        Assert.assertEquals(0, scoreResponse.get().getScore());

    }

    @Test
    public void showScoreByPlayer_withThreeFrames() throws Exception {
        NewGameRequest newGameRequest = new NewGameRequest();
        newGameRequest.setNumberOfPlayers(1);
        String playerOneName = "Samuel";
        newGameRequest.setNameOfThePlayers(Arrays.asList(playerOneName));

        playerService.startNewGame(newGameRequest);

        Optional<ScoreResponse> scoreResponse = playerService.showScoreByName(playerOneName);

        Assert.assertNotNull(scoreResponse);
        Assert.assertTrue(scoreResponse.isPresent());
        Assert.assertEquals(playerOneName, scoreResponse.get().getPlayerName());
        Assert.assertEquals(READY_TO_START, scoreResponse.get().getGameState());
        Assert.assertEquals(0, scoreResponse.get().getScore());

        playerService.playOneFrame(new FrameRequest(playerOneName, new RollsRequest("1", "0")));

        Optional<ScoreResponse> scoreResponseAfterFirstPlay = playerService.showScoreByName(playerOneName);

        Assert.assertNotNull(scoreResponseAfterFirstPlay);
        Assert.assertTrue(scoreResponseAfterFirstPlay.isPresent());
        Assert.assertEquals(playerOneName, scoreResponseAfterFirstPlay.get().getPlayerName());
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 1), scoreResponseAfterFirstPlay.get().getGameState());
        Assert.assertEquals(1, scoreResponseAfterFirstPlay.get().getScore());

        RollsRequest strike = new RollsRequest();
        strike.setFirstRoll("X");

        playerService.playOneFrame(new FrameRequest(playerOneName, strike));

        Optional<ScoreResponse> scoreOfAStrike = playerService.showScoreByName(playerOneName);

        Assert.assertNotNull(scoreResponseAfterFirstPlay);
        Assert.assertTrue(scoreResponseAfterFirstPlay.isPresent());
        Assert.assertEquals(playerOneName, scoreOfAStrike.get().getPlayerName());
        Assert.assertEquals(STRIKE_MESSAGE, scoreOfAStrike.get().getGameState());
        //score only change after countdown of strike points
        Assert.assertEquals(1, scoreOfAStrike.get().getScore());

        playerService.playOneFrame(new FrameRequest(playerOneName, new RollsRequest("1", "0")));

        Optional<ScoreResponse> scoreResponseAfterStrike = playerService.showScoreByName(playerOneName);

        Assert.assertNotNull(scoreResponseAfterFirstPlay);
        Assert.assertTrue(scoreResponseAfterStrike.isPresent());
        Assert.assertEquals(playerOneName, scoreResponseAfterStrike.get().getPlayerName());
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 13), scoreResponseAfterStrike.get().getGameState());
        Assert.assertEquals(13, scoreResponseAfterStrike.get().getScore());
    }

}