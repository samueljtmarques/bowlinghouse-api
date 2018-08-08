package com.samuel.bowling.service;

import com.samuel.bowling.model.entity.FrameDto;
import com.samuel.bowling.model.entity.Game;

import java.util.List;

public interface ScoreService {
    void updateScore(Game game);
    int calculateScore(List<FrameDto> framesPlayed, int i);
}
