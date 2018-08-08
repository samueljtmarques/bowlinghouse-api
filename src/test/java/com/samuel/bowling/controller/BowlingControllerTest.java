package com.samuel.bowling.controller;

import com.samuel.bowling.exception.BadFrameRequestException;
import com.samuel.bowling.exception.GameNotStartedException;
import com.samuel.bowling.exception.PlayerNotFoundException;
import com.samuel.bowling.exception.PlayersBadRequestException;
import com.samuel.bowling.message.FrameRequest;
import com.samuel.bowling.message.NewGameRequest;
import com.samuel.bowling.message.RollsRequest;
import com.samuel.bowling.message.ScoreResponse;
import com.samuel.bowling.service.PlayerService;
import com.samuel.bowling.util.ControllerHttpUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.samuel.bowling.model.ConstantStrings.CURRENT_SCORE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.GAME_CREATED;
import static com.samuel.bowling.model.ConstantStrings.GAME_RULES;
import static com.samuel.bowling.model.ConstantStrings.NAMES_NUMBER_MISMATCHING;
import static com.samuel.bowling.model.ConstantStrings.NO_PLAYERS_YET;
import static com.samuel.bowling.model.ConstantStrings.PLAYER_NOT_FOUND;
import static com.samuel.bowling.model.ConstantStrings.SPARE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.START_GAME_FIRST;
import static com.samuel.bowling.model.ConstantStrings.STRIKE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.TOO_MANY_PLAYERS;
import static org.mockito.Mockito.doReturn;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan({"com.samuel.bowling"})
@SpringBootTest(classes = {BowlingController.class})
public class BowlingControllerTest {
    @Autowired
    private BowlingController underTest;
    @MockBean
    private PlayerService mockedPlayerService;

    @Value("${max.players}")
    private int maxPlayersPerGame;

    @Test
    public void testShowCurrentTotalScore_successfully() {
        List<ScoreResponse> totalScore = new ArrayList<>();
        totalScore.add(ScoreResponse.builder()
                .gameState(STRIKE_MESSAGE)
                .score(0)
                .playerName("Samuel")
                .build());

        totalScore.add(ScoreResponse.builder()
                .gameState(SPARE_MESSAGE)
                .score(0)
                .playerName("Marques")
                .build());
        ResponseEntity<List<ScoreResponse>> expectedResult = new ResponseEntity<>(totalScore, ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.OK);
        doReturn(totalScore).when(mockedPlayerService).showTotalScore();


        Assert.assertEquals(expectedResult, underTest.showCurrentScore());
    }
    @Test
    public void testShowCurrentTotalScore_gameNotStarted() {
        ResponseEntity<List<ScoreResponse>> expectedResult = new ResponseEntity<>(Arrays.asList(ScoreResponse.builder()
                .score(0)
                .playerName(NO_PLAYERS_YET)
                .gameState(START_GAME_FIRST)
                .build()), ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.BAD_REQUEST);
        doThrow(new GameNotStartedException(START_GAME_FIRST)).when(mockedPlayerService).showTotalScore();


        Assert.assertEquals(expectedResult, underTest.showCurrentScore());

    }
    @Test
    public void testShowScoreByPlayer_successfully() {
        String playerName = "Samuel";
        ScoreResponse scoreResponse =  ScoreResponse.builder()
                .gameState(String.format(CURRENT_SCORE_MESSAGE, 28))
                .score(28)
                .playerName(playerName)
                .build();

        ResponseEntity<ScoreResponse> expectedResult = new ResponseEntity<>(scoreResponse, ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.OK);
        doReturn(Optional.of(scoreResponse)).when(mockedPlayerService).showScoreByName(playerName);


        Assert.assertEquals(expectedResult, underTest.showScoreByPlayer(playerName));
    }
    @Test
    public void testShowScoreByPlayer_gameNotStarted() {
        ResponseEntity<ScoreResponse> expectedScoreResponse = new ResponseEntity<>(ScoreResponse.builder()
                .score(0)
                .playerName(NO_PLAYERS_YET)
                .gameState(START_GAME_FIRST)
                .build(), ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.BAD_REQUEST);
        String playerName = "Samuel";

        doThrow(new GameNotStartedException(START_GAME_FIRST)).when(mockedPlayerService).showScoreByName(playerName);


        Assert.assertEquals(expectedScoreResponse, underTest.showScoreByPlayer(playerName));
    }
    @Test
    public void testShowScoreByPlayer_playerNoFound() {
        String playerName = "Samuel";
        ResponseEntity<ScoreResponse> expectedScoreResponse = new ResponseEntity<>(ScoreResponse.builder()
                .score(-1)
                .playerName(playerName)
                .gameState(PLAYER_NOT_FOUND)
                .build(),
                ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.NOT_FOUND);

        doThrow(new PlayerNotFoundException(PLAYER_NOT_FOUND)).when(mockedPlayerService).showScoreByName(playerName);


        Assert.assertEquals(expectedScoreResponse, underTest.showScoreByPlayer(playerName));
    }
    @Test
    public void testStartNewGame_successfully() {
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(GAME_CREATED, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.CREATED);

        doReturn(GAME_CREATED).when(mockedPlayerService).startNewGame(any());


        Assert.assertEquals(expectedResponse, underTest.startNewGame(new NewGameRequest()));
    }

    @Test
    public void testStartNewGame_badRequest_tooManyPlayers() {
        String expectedMessage = String.format(TOO_MANY_PLAYERS, maxPlayersPerGame);
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(expectedMessage, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);

        doThrow(new PlayersBadRequestException(expectedMessage)).when(mockedPlayerService).startNewGame(any());


        Assert.assertEquals(expectedResponse, underTest.startNewGame(new NewGameRequest()));
    }
    @Test
    public void testStartNewGame_badRequest_Names_NumberMismatching() {
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(NAMES_NUMBER_MISMATCHING, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);

        doThrow(new PlayersBadRequestException(NAMES_NUMBER_MISMATCHING)).when(mockedPlayerService).startNewGame(any());


        Assert.assertEquals(expectedResponse, underTest.startNewGame(new NewGameRequest()));
    }

    @Test
    public void testPlayOneFrame_successfully() {
        String currentScoreIs = String.format(CURRENT_SCORE_MESSAGE, 2);
        ResponseEntity<String> expectedResult = new ResponseEntity<>(currentScoreIs, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.OK);

        doReturn(currentScoreIs).when(mockedPlayerService).playOneFrame(any());

        Assert.assertEquals(expectedResult, underTest.playOneFrame(new FrameRequest("samuel", new RollsRequest("1","1"))));
    }


    @Test
    public void testPlayOneFrame_playerNotFound() {
        String playerName = "playerName";
        String expectedMessage = String.format(PLAYER_NOT_FOUND, playerName);
        ResponseEntity<String> expectedResult = new ResponseEntity<>(expectedMessage, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.NOT_FOUND);

        doThrow(new PlayerNotFoundException(expectedMessage)).when(mockedPlayerService).playOneFrame(any());

        Assert.assertEquals(expectedResult, underTest.playOneFrame(new FrameRequest(playerName, new RollsRequest("1","1"))));
    }

    @Test
    public void testPlayOneFrame_gameNotStarted() {
        ResponseEntity<String> expectedResult = new ResponseEntity<>(START_GAME_FIRST, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);

        doThrow(new GameNotStartedException(START_GAME_FIRST)).when(mockedPlayerService).playOneFrame(any());

        Assert.assertEquals(expectedResult, underTest.playOneFrame(new FrameRequest("samuel", new RollsRequest("1","1"))));
    }
    @Test
    public void testPlayOneFrame_badRequest() {
        ResponseEntity<String> expectedResult = new ResponseEntity<>(GAME_RULES, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);

        doThrow(new BadFrameRequestException(GAME_RULES)).when(mockedPlayerService).playOneFrame(any());

        Assert.assertEquals(expectedResult, underTest.playOneFrame(new FrameRequest("samuel", new RollsRequest("a","1"))));
    }

}