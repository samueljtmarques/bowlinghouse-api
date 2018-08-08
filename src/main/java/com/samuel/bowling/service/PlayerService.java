package com.samuel.bowling.service;

import com.samuel.bowling.message.FrameRequest;
import com.samuel.bowling.message.NewGameRequest;
import com.samuel.bowling.message.ScoreResponse;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    String startNewGame(NewGameRequest newGameRequest);

    String playOneFrame(FrameRequest frameRequest);

    List<ScoreResponse> showTotalScore();

    Optional<ScoreResponse> showScoreByName(String playerName);
}
