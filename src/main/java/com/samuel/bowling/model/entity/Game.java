package com.samuel.bowling.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Game {
    private int score = 0;
    private int extraFrame;
    private List<FrameDto> frameDtoList;
    private String stateOfTheGame;
    private String playerName;
}
