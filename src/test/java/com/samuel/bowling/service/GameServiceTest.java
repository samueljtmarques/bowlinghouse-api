package com.samuel.bowling.service;

import com.samuel.bowling.exception.BadFrameRequestException;
import com.samuel.bowling.exception.GameNotStartedException;
import com.samuel.bowling.message.RollsRequest;
import com.samuel.bowling.model.entity.Game;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.samuel.bowling.model.ConstantStrings.CURRENT_SCORE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.GAME_OVER_THANKS;
import static com.samuel.bowling.model.ConstantStrings.SPARE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.SPARE_MESSAGE_EXTRA_FRAME;
import static com.samuel.bowling.model.ConstantStrings.STRIKE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.STRIKE_MESSAGE_EXTRA_FRAME;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class GameServiceTest {

    @Autowired
    GameService gameService;

    @Test
    public void testCreateNewGame() {
        Assert.assertNotNull(gameService.createNewGame());
    }


    @Test
    public void testStrike() {
        RollsRequest rollsRequest = new RollsRequest();
        rollsRequest.setFirstRoll("X");

        gameService.setActiveGame(gameService.createNewGame());

        gameService.rollTheBall(rollsRequest);

        Game game = gameService.getActiveGame();
        Assert.assertEquals(STRIKE_MESSAGE, game.getStateOfTheGame());
    }

    @Test
    public void testSpare() {
        RollsRequest rollsRequest = new RollsRequest("3", "/");

        gameService.setActiveGame(gameService.createNewGame());

        gameService.rollTheBall(rollsRequest);

        Game game = gameService.getActiveGame();
        Assert.assertEquals(SPARE_MESSAGE, game.getStateOfTheGame());
    }

    @Test
    public void testSimpleExtraFrameStrike() {

        gameService.setActiveGame(gameService.createNewGame());

        RollsRequest rollsRequest = new RollsRequest("1", "0");
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 1), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 2), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 3), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 4), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 5), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 6), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 7), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 8), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 9), gameService.getActiveGame().getStateOfTheGame());
        RollsRequest strikeRollsRequest = new RollsRequest();
        strikeRollsRequest.setFirstRoll("X");
        // 10+10+1 = 21
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE_EXTRA_FRAME, gameService.getActiveGame().getStateOfTheGame());

        RollsRequest extraFrameMaximum = new RollsRequest("10", "1");
        // ----
        gameService.rollTheBall(extraFrameMaximum);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 30), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(GAME_OVER_THANKS, gameService.getActiveGame().getStateOfTheGame());
    }

    @Test
    public void testSimpleExtraFrameSpare() {

        gameService.setActiveGame(gameService.createNewGame());

        RollsRequest rollsRequest = new RollsRequest("1", "0");
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 1), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 2), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 3), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 4), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 5), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 6), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 7), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 8), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 9), gameService.getActiveGame().getStateOfTheGame());
        RollsRequest frameSpareRequest = new RollsRequest("5", "/");
        // 10+10 = 20
        gameService.rollTheBall(frameSpareRequest);
        Assert.assertEquals(SPARE_MESSAGE_EXTRA_FRAME, gameService.getActiveGame().getStateOfTheGame());

        RollsRequest extraFrame = new RollsRequest();
        extraFrame.setFirstRoll("10");
        // ----
        gameService.rollTheBall(extraFrame);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 29), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(GAME_OVER_THANKS, gameService.getActiveGame().getStateOfTheGame());
    }

    @Test
    public void testMaximumPoints() {
        RollsRequest strikeRollsRequest = new RollsRequest();
        RollsRequest extraFrameMaximum = new RollsRequest("10", "10");

        strikeRollsRequest.setFirstRoll("X");

        gameService.setActiveGame(gameService.createNewGame());

        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(STRIKE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        //10+10+10
        gameService.rollTheBall(strikeRollsRequest); // max frames = 10
        Assert.assertEquals(STRIKE_MESSAGE_EXTRA_FRAME, gameService.getActiveGame().getStateOfTheGame());
        // ---                                                                                        // extra balls
        gameService.rollTheBall(extraFrameMaximum);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 300), gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(strikeRollsRequest);
        Assert.assertEquals(GAME_OVER_THANKS, gameService.getActiveGame().getStateOfTheGame());

    }

    @Test
    public void testGetCurrentScore() {
        gameService.setActiveGame(gameService.createNewGame());
        RollsRequest rollsRequest = new RollsRequest("3", "4");

        gameService.rollTheBall(rollsRequest);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 3 + 4), gameService.getActiveGame().getStateOfTheGame());
        Assert.assertEquals(7, gameService.getCurrentScore());
    }

    @Test(expected = GameNotStartedException.class)
    public void testGameNotStarted() {
        gameService.setActiveGame(null);
        RollsRequest rollsRequest = new RollsRequest("3", "4");

        gameService.rollTheBall(rollsRequest);
    }

    @Test(expected = BadFrameRequestException.class)
    public void testBadFrameRequestException() {

        gameService.setActiveGame(gameService.createNewGame());
        RollsRequest rollsRequest = new RollsRequest("7", "4");

        gameService.rollTheBall(rollsRequest);
    }

    @Test
    public void testGetCurrentScoreWithStrike() {
        gameService.setActiveGame(gameService.createNewGame());

        RollsRequest strikeRollsRequest = new RollsRequest();
        strikeRollsRequest.setFirstRoll("X");

        RollsRequest normalRollsRequest = new RollsRequest("1", "4");

        gameService.rollTheBall(strikeRollsRequest);
        gameService.rollTheBall(normalRollsRequest);

        Assert.assertEquals(20, gameService.getCurrentScore());
    }

    @Test
    public void testGetCurrentScoreWithSpare() {
        gameService.setActiveGame(gameService.createNewGame());
        // 10+1
        RollsRequest spareRollsRequest = new RollsRequest("5", "/");
        // 5
        RollsRequest normalRollsRequest = new RollsRequest("1", "4");

        gameService.rollTheBall(spareRollsRequest);
        gameService.rollTheBall(normalRollsRequest);

        Assert.assertEquals(16, gameService.getCurrentScore());
    }

    @Test
    public void testAllSpares() {
        RollsRequest spareRollsRequest = new RollsRequest("5", "/");

        gameService.setActiveGame(gameService.createNewGame());

        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE, gameService.getActiveGame().getStateOfTheGame());
        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(SPARE_MESSAGE_EXTRA_FRAME, gameService.getActiveGame().getStateOfTheGame());
        RollsRequest extraFrame = new RollsRequest();
        extraFrame.setFirstRoll("5");
        gameService.rollTheBall(extraFrame);
        Assert.assertEquals(String.format(CURRENT_SCORE_MESSAGE, 150), gameService.getActiveGame().getStateOfTheGame());

        gameService.rollTheBall(spareRollsRequest);
        Assert.assertEquals(GAME_OVER_THANKS, gameService.getActiveGame().getStateOfTheGame());
    }
}