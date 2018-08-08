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
public class RollsRequest {

    @ApiModelProperty(reference = "The result of the first roll X or <10", required = true)
    private String firstRoll;
    @ApiModelProperty(notes = "The result of the second roll / or <10")
    private String secondRoll;
}
