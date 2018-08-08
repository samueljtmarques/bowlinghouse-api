package com.samuel.bowling.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class FrameRequest {
    @ApiModelProperty(reference = "The name of the player", required = true)
    private String playerName;
    @ApiModelProperty(reference = "The request with the rolls", required = true)
    private RollsRequest rollsRequest;
}
