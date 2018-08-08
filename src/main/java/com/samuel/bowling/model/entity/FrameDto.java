package com.samuel.bowling.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class FrameDto {
    private boolean strike = false;
    private boolean spare = false;

    private int firstRoll;
    private int secondRoll;

    public FrameDto(int firstRoll, int secondRoll) {
        this.firstRoll = firstRoll;
        this.secondRoll = secondRoll;
    }
}
