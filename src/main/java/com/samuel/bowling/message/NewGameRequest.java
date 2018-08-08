package com.samuel.bowling.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class NewGameRequest {
    @ApiModelProperty(reference = "The number of the players", required = true)
    private int numberOfPlayers;
    @ApiModelProperty(reference = "The name of the players", required = true)
    private List<String> nameOfThePlayers;
}
