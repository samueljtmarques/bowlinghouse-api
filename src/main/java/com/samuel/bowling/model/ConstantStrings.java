package com.samuel.bowling.model;

public interface ConstantStrings {
    String STRIKE_MESSAGE = "You did a Strike, well done!";
    String STRIKE_MESSAGE_EXTRA_FRAME = "You did a Strike and gained two more rolls, well done!";
    String SPARE_MESSAGE = "You did a Spare, well done!";
    String SPARE_MESSAGE_EXTRA_FRAME = "You did a Spare and gained one more roll, well done!";
    String CURRENT_SCORE_MESSAGE = "Your current score is %d, well done!";

    String READY_TO_START = "Ready to start playing!";
    String GAME_CREATED = "A new game was created!";
    String GAME_OVER_THANKS = "GAME OVER, THANK YOU!!";

    String GAME_RULES = "Request incorrect.\nX on first roll for Strike. / " +
            "on second roll for Spare. \n" +
            "Up to the tenth frame use numbers and the sum of them should not be more then 10.\n" +
            "Over the tenth frame you will have a last frame and Strike and Spare logic are not applicable.";


    String START_GAME_FIRST = "First start the Game!";
    String NO_PLAYERS_YET = "No players, game did not start!";
    String NAMES_NUMBER_MISMATCHING = "The names and the number of names are not matching";
    String PLAYER_NOT_FOUND = "The player with the name %s wasn't found";
    String TOO_MANY_PLAYERS = "Sorry but maximum number of players is %d";
}
