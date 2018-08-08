package com.samuel.bowling.service;

import com.samuel.bowling.message.RollsRequest;
import com.samuel.bowling.model.entity.Game;

public interface GameService {
    Game createNewGame();

    void rollTheBall(RollsRequest rollsRequest);

    int getCurrentScore();

    void setActiveGame(Game game);

    Game getActiveGame();
}
