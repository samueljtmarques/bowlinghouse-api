package com.samuel.bowling.service.impl;

import com.samuel.bowling.model.entity.FrameDto;
import com.samuel.bowling.model.entity.Game;
import com.samuel.bowling.service.ScoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Value("${expected.max.frames}")
    private int maxFrames;

    @Override
    public void updateScore(Game game) {
        List<FrameDto> framesPlayed = game.getFrameDtoList();
        game.setScore(calculateScore(framesPlayed, 0));
    }

    public int calculateScore(List<FrameDto> framesPlayed, int index) {
        FrameDto currentFrameDto = framesPlayed.get(index);
        int numberOfFramesPlayed = framesPlayed.size() - 1;

        boolean isLastFrame = (index == numberOfFramesPlayed);
        if (isLastFrame) {
            boolean isExtraFrame = index > maxFrames;
            if (isExtraFrame) {
                return 0;
            }
            return sumFrameRolls(currentFrameDto);
        }

        if (currentFrameDto.isStrike()) {
            return calculateStrike(framesPlayed, index) + calculateScore(framesPlayed, ++index);
        }
        if (currentFrameDto.isSpare()) {
            return calculateSpare(framesPlayed, index) + calculateScore(framesPlayed, ++index);
        }
        return calculateScore(framesPlayed, ++index) + sumFrameRolls(currentFrameDto);
    }

    private int calculateSpare(List<FrameDto> framesPlayed, int index) {
        FrameDto currentFrameDto = framesPlayed.get(index);
        int currentPoints = sumFrameRolls(currentFrameDto);

        FrameDto nextFrameDto = framesPlayed.get(index + 1);

        return currentPoints + nextFrameDto.getFirstRoll();
    }

    private int calculateStrike(List<FrameDto> framesPlayed, int index) {
        int strikePoints = 10;
        int secondRoll = index + 1;
        for (int actualRoll = index; actualRoll <= secondRoll; ++actualRoll) {
            try {
                FrameDto nextFrameDto = framesPlayed.get(actualRoll + 1);
                if (nextFrameDto.isStrike()) {
                    strikePoints += 10;
                } else {
                    if (actualRoll == secondRoll) {
                        strikePoints += nextFrameDto.getFirstRoll();
                    } else {
                        strikePoints += sumFrameRolls(nextFrameDto);
                        break;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        return strikePoints;
    }

    private int sumFrameRolls(FrameDto frameDto) {
        return frameDto.getFirstRoll() + frameDto.getSecondRoll();
    }

}
