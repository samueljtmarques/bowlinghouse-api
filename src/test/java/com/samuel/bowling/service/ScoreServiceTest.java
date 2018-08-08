package com.samuel.bowling.service;

import com.samuel.bowling.model.entity.FrameDto;
import com.samuel.bowling.model.entity.Game;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ScoreServiceTest {

    @Autowired
    ScoreService scoreService;


    private Game game;

    @Before
    public void initGame() {
        game = new Game();
        game.setFrameDtoList(new ArrayList<>());
    }

    @Test
    public void testSimpleCase() {
        game.getFrameDtoList().add(new FrameDto(4, 1));
        game.getFrameDtoList().add(new FrameDto(3, 2));

        Assert.assertEquals(10, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }
    @Test
    public void testSimpleCaseWithStrike() {
        FrameDto strikeFrameDto = new FrameDto(10, 0);
        strikeFrameDto.setStrike(true);
        game.getFrameDtoList().add(strikeFrameDto); // 10 + (10 + 3)
        game.getFrameDtoList().add(strikeFrameDto); // 10 + (3 + 2)
        game.getFrameDtoList().add(new FrameDto(3, 2)); // 5

        Assert.assertEquals(43, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }
    @Test
    public void testGameScoreWithStrikes() {
        FrameDto strikeFrameDto = new FrameDto(10, 0);
        strikeFrameDto.setStrike(true);
        game.getFrameDtoList().add(strikeFrameDto);     // 10 + 8
        game.getFrameDtoList().add(new FrameDto(8, 0)); //8
        game.getFrameDtoList().add(strikeFrameDto);     //10+9
        game.getFrameDtoList().add(new FrameDto(8, 1)); // 9
        game.getFrameDtoList().add(strikeFrameDto);     //10 + 9
        game.getFrameDtoList().add(new FrameDto(8, 1)); // 9
        game.getFrameDtoList().add(new FrameDto(3, 4)); // 7
        game.getFrameDtoList().add(new FrameDto(3, 2)); // 5
        game.getFrameDtoList().add(new FrameDto(6, 2)); // 8
        game.getFrameDtoList().add(new FrameDto(4, 1)); // 5
        Assert.assertEquals(107, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }

    @Test
    public void testGameScoreWithSparesAndStrikes() {
        FrameDto spareFrameDto = new FrameDto(5, 5);
        spareFrameDto.setSpare(true);
        FrameDto strikeFrameDto = new FrameDto(10, 0);
        strikeFrameDto.setStrike(true);
        game.getFrameDtoList().add(strikeFrameDto);     // 10 + 10        = 20
        game.getFrameDtoList().add(spareFrameDto);      // 10 + 5         = 15
        game.getFrameDtoList().add(spareFrameDto);      // 10 + 10        = 20
        game.getFrameDtoList().add(strikeFrameDto);     // 10 + 10 + 5    = 25
        game.getFrameDtoList().add(strikeFrameDto);     // 10 + 10        = 20
        game.getFrameDtoList().add(spareFrameDto);      // 10 + 10        = 20
        game.getFrameDtoList().add(strikeFrameDto);     // 10 + 5         = 15
        game.getFrameDtoList().add(new FrameDto(3, 2)); // 5              = 5
        game.getFrameDtoList().add(spareFrameDto);      // 10 + 4         = 14
        game.getFrameDtoList().add(new FrameDto(4, 1)); // 5              = 5
        Assert.assertEquals(159, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }

    @Test
    public void testGameScoreWithSpares() {
        FrameDto spareFrameDto = new FrameDto(5, 5);
        spareFrameDto.setSpare(true);
        game.getFrameDtoList().add(spareFrameDto);     // 10 + 8
        game.getFrameDtoList().add(new FrameDto(8, 0)); //8
        game.getFrameDtoList().add(spareFrameDto);     //10+8
        game.getFrameDtoList().add(new FrameDto(8, 1)); // 9
        game.getFrameDtoList().add(spareFrameDto);     //10 + 8
        game.getFrameDtoList().add(new FrameDto(8, 1)); // 9
        game.getFrameDtoList().add(new FrameDto(3, 4)); // 7
        game.getFrameDtoList().add(new FrameDto(3, 2)); // 5
        game.getFrameDtoList().add(new FrameDto(6, 2)); // 8
        game.getFrameDtoList().add(new FrameDto(4, 1)); // 5
        Assert.assertEquals(105, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }

    @Test
    public void testGameScoreWithoutStrikesOrSpares() {
        game.getFrameDtoList().add(new FrameDto(8, 1));
        game.getFrameDtoList().add(new FrameDto(8, 0));
        game.getFrameDtoList().add(new FrameDto(3, 2));
        game.getFrameDtoList().add(new FrameDto(4, 1));
        game.getFrameDtoList().add(new FrameDto(8, 1));
        game.getFrameDtoList().add(new FrameDto(8, 1));
        game.getFrameDtoList().add(new FrameDto(3, 4));
        game.getFrameDtoList().add(new FrameDto(3, 2));
        game.getFrameDtoList().add(new FrameDto(6, 2));
        game.getFrameDtoList().add(new FrameDto(4, 1));
        Assert.assertEquals(70, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }

    @Test
    public void testSimpleCaseWithStrikeAndExtraFrame() {

        addNineSimpleFramesWithOneOnFirstRoll();

        FrameDto strikeFrameDto = new FrameDto(10, 0);
        strikeFrameDto.setStrike(true);

        game.getFrameDtoList().add(strikeFrameDto);
        game.getFrameDtoList().add(new FrameDto(10, 1));


        Assert.assertEquals(30, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }

    @Test
    public void testSimpleCaseWithSpareAndExtraFrame() {

        addNineSimpleFramesWithOneOnFirstRoll();

        FrameDto spareFrameDto = new FrameDto(5, 5);
        spareFrameDto.setSpare(true);

        game.getFrameDtoList().add(spareFrameDto);

        FrameDto extraFrameDto = new FrameDto();
        extraFrameDto.setFirstRoll(10);
        game.getFrameDtoList().add(extraFrameDto);


        Assert.assertEquals(29, scoreService.calculateScore(game.getFrameDtoList(), 0));
    }

    private void addNineSimpleFramesWithOneOnFirstRoll() {
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
        game.getFrameDtoList().add(new FrameDto(1, 0));
    }
}