package com.samuel.bowling.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponse {
    @ApiModelProperty(required = true, notes = "The name of the player.")
    private String playerName;

    @ApiModelProperty(required = true, notes = "The score of the player.")
    private int score;

    @ApiModelProperty(required = true, notes = "The game status of the player.")
    private String gameState;
}
