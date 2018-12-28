package com.samuel.bowling.service.impl;

import com.samuel.bowling.exception.BadFrameRequestException;
import com.samuel.bowling.exception.GameNotStartedException;
import com.samuel.bowling.message.RollsRequest;
import com.samuel.bowling.model.entity.FrameDto;
import com.samuel.bowling.model.entity.Game;
import com.samuel.bowling.service.GameService;
import com.samuel.bowling.service.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Optional;

import static com.samuel.bowling.model.ConstantStrings.CURRENT_SCORE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.GAME_OVER_THANKS;
import static com.samuel.bowling.model.ConstantStrings.GAME_RULES;
import static com.samuel.bowling.model.ConstantStrings.READY_TO_START;
import static com.samuel.bowling.model.ConstantStrings.SPARE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.SPARE_MESSAGE_EXTRA_FRAME;
import static com.samuel.bowling.model.ConstantStrings.START_GAME_FIRST;
import static com.samuel.bowling.model.ConstantStrings.STRIKE_MESSAGE;
import static com.samuel.bowling.model.ConstantStrings.STRIKE_MESSAGE_EXTRA_FRAME;

@Slf4j
@Service
public class GameServiceImpl implements GameService {


    @Value("${expected.max.frames}")
    private int maxFrames;
    @Value("${max.points.frame}")
    private int maxPoints;

    private Game game;

    @Resource
    ScoreService scoreService;

    @Override
    public Game createNewGame() {
        Game game = new Game();
        game.setFrameDtoList(new ArrayList<>());
        game.setExtraFrame(0);
        game.setStateOfTheGame(READY_TO_START);
        return game;
    }

    @Override
    public void rollTheBall(RollsRequest rollsRequest) {

        if (game == null) {
            throw new GameNotStartedException(START_GAME_FIRST);
        }
        int CURRENT_NUMBER_OF_FRAMES = game.getFrameDtoList().size();

        boolean isAStrike = isStrike(rollsRequest.getFirstRoll());

        boolean isASpare = isASpare(Optional.ofNullable(rollsRequest.getSecondRoll()));

        if (!isGameValid(rollsRequest, CURRENT_NUMBER_OF_FRAMES, isAStrike)) {
            throw new BadFrameRequestException(GAME_RULES);
        }

        FrameDto frameDto = generateFrameFromRequest(rollsRequest, isAStrike, isASpare);

        saveRollAndUpdateScore(frameDto, isAStrike, isASpare, CURRENT_NUMBER_OF_FRAMES);
    }

    private boolean isASpare(Optional<String> secondRoll) {
        if (!secondRoll.isPresent()) {
            return false;
        }
        if (secondRoll.get().equalsIgnoreCase("/")) {
            return true;
        }
        return false;
    }

    private boolean isStrike(String firstRoll) {
        return firstRoll.equalsIgnoreCase("X");
    }

    private boolean isGameValid(RollsRequest rollsRequest, int currentNumberOfFrames, boolean isAStrike) {
        if (rollsRequest.getSecondRoll() == null) {
            if (isAStrike) {
                return true;
            } else {
                rollsRequest.setSecondRoll("0");
            }
        }
        try {
            int valueFirstRoll = Integer.valueOf(rollsRequest.getFirstRoll());
            //spares should be done by writing "/" on the second roll field
            if (!rollsRequest.getSecondRoll().equalsIgnoreCase("/")) {

                int valueSecondRoll = Integer.valueOf(rollsRequest.getSecondRoll());
                //until the tenth frame the sum of first and second roll should not be more then 10.
                //in the extra frame the logic is based on number of rolls (one or two - spare or strike)
                if (currentNumberOfFrames <= maxFrames && valueFirstRoll + valueSecondRoll > maxPoints) {
                    return false;
                }
            }

        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private FrameDto generateFrameFromRequest(RollsRequest rollsRequest, boolean isAStrike, boolean isASpare) {
        if (isAStrike) {
            return generateFrameStrike();
        } else {
            if (isASpare) {
                return generateSpareFrame(rollsRequest.getFirstRoll());
            }
            int firstRoll = Integer.valueOf(rollsRequest.getFirstRoll());

            int secondRoll = Integer.valueOf(rollsRequest.getSecondRoll());

            return new FrameDto(firstRoll, secondRoll);
        }

    }

    private FrameDto generateSpareFrame(String firstRoll) {
        FrameDto frameDto = new FrameDto();
        frameDto.setStrike(false);
        frameDto.setSpare(true);
        frameDto.setFirstRoll(Integer.valueOf(firstRoll));
        frameDto.setSecondRoll(maxPoints - frameDto.getFirstRoll());
        return frameDto;
    }

    private void saveRollAndUpdateScore(FrameDto frameDto, boolean isAStrike, boolean isASpare, int CURRENT_NUMBER_OF_FRAMES) {
        int extraFrame = game.getExtraFrame();
        if (isAStrike) {
            if (CURRENT_NUMBER_OF_FRAMES < maxFrames) {
                saveFrame(frameDto);
                game.setStateOfTheGame(STRIKE_MESSAGE);
                return;
            }
            if (extraFrame < 1) {
                saveAndAddExtraFrame(frameDto, STRIKE_MESSAGE_EXTRA_FRAME);
                return;
            }
        }

        if (isASpare) {
            if (CURRENT_NUMBER_OF_FRAMES < maxFrames + extraFrame) {
                saveFrame(frameDto);
                game.setStateOfTheGame( SPARE_MESSAGE);
                return;
            }
            if (extraFrame < 1) {
                saveAndAddExtraFrame(frameDto, SPARE_MESSAGE_EXTRA_FRAME);
                return;
            }
        }

        if (CURRENT_NUMBER_OF_FRAMES <= (maxFrames + extraFrame)) {
            saveFrameAndUpdateScore(frameDto);
            game.setStateOfTheGame(String.format(CURRENT_SCORE_MESSAGE, game.getScore()));
            return;
        }

        game.setStateOfTheGame(GAME_OVER_THANKS);

    }

    private void saveAndAddExtraFrame(FrameDto frameDto, String extraFrameMessage) {
        game.setExtraFrame(1);
        saveFrame(frameDto);
        game.setStateOfTheGame(extraFrameMessage);
    }

    private void saveFrame(FrameDto frameDto) {
        game.getFrameDtoList().add(frameDto);
    }

    @Override
    public int getCurrentScore() {
        return game != null ? game.getScore() : -1;
    }

    @Override
    public void setActiveGame(Game game) {
        this.game = game;
    }

    @Override
    public Game getActiveGame() {
        return this.game;
    }

    private void saveFrameAndUpdateScore(FrameDto frameDto) {
        game.getFrameDtoList().add(frameDto);
        scoreService.updateScore(game);
    }

    private FrameDto generateFrameStrike() {
        FrameDto frameDto = new FrameDto();
        frameDto.setStrike(true);
        frameDto.setSpare(false);
        frameDto.setFirstRoll(10);
        frameDto.setSecondRoll(0);
        return frameDto;
    }
}
