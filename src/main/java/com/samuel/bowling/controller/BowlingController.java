package com.samuel.bowling.controller;

import com.samuel.bowling.exception.BadFrameRequestException;
import com.samuel.bowling.exception.GameNotStartedException;
import com.samuel.bowling.exception.PlayerNotFoundException;
import com.samuel.bowling.exception.PlayersBadRequestException;
import com.samuel.bowling.message.FrameRequest;
import com.samuel.bowling.message.NewGameRequest;
import com.samuel.bowling.message.ScoreResponse;
import com.samuel.bowling.service.GameService;
import com.samuel.bowling.service.PlayerService;
import com.samuel.bowling.util.ControllerHttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.samuel.bowling.model.ConstantStrings.NO_PLAYERS_YET;

@Slf4j
@RestController
@RequestMapping("/bowlinggame")
@Api("Bowling API Controller")
@NoArgsConstructor
public class BowlingController {

    @Autowired
    private PlayerService playerService;

    @ApiOperation(
            value = "Endpoint to get the current Score",
            response = ResponseEntity.class,
            produces = MediaType.APPLICATION_JSON_VALUE,
            notes = "This endpoint allows you to know the current score"
    )
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "The current score", response = ResponseEntity.class),
                    @ApiResponse(code = 400, message = "When game did not start", response = ResponseEntity.class),
            }
    )
    @GetMapping(value = "/score/total", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<List<ScoreResponse>> showCurrentScore() {
        try {
            return new ResponseEntity<>(playerService.showTotalScore(), ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.OK);
        } catch (GameNotStartedException ex) {
            return new ResponseEntity<>(Arrays.asList(ScoreResponse.builder()
                    .score(0)
                    .playerName(NO_PLAYERS_YET)
                    .gameState(ex.getMessage())
                    .build()), ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(
            value = "Endpoint to get the current Score by Name",
            response = ScoreResponse.class,
            produces = MediaType.APPLICATION_JSON_VALUE,
            notes = "This endpoint allows you to know the current score"
    )
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "The current score", response = ScoreResponse.class),
            }
    )
    @GetMapping(value = "/score/{playerName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<ScoreResponse> showScoreByPlayer(@PathVariable String playerName) {
        try {
            Optional<ScoreResponse> scoreResponse = playerService.showScoreByName(playerName);

            return new ResponseEntity<>(scoreResponse.get(), ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.OK);
        } catch (GameNotStartedException ex) {
            return new ResponseEntity<>(ScoreResponse.builder()
                    .score(0)
                    .playerName(NO_PLAYERS_YET)
                    .gameState(ex.getMessage())
                    .build(), ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.BAD_REQUEST);
        } catch (PlayerNotFoundException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(ScoreResponse.builder()
                    .score(-1)
                    .playerName(playerName)
                    .gameState(ex.getMessage())
                    .build(),
                    ControllerHttpUtil.getApplicationJsonHeader(), HttpStatus.NOT_FOUND);
        }

    }

    @ApiOperation(
            value = "Endpoint to start a new game",
            response = ResponseEntity.class,
            produces = MediaType.TEXT_PLAIN_VALUE,
            notes = "This endpoint allows you to start a new game"
    )
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "Start a new game", response = ResponseEntity.class),
                    @ApiResponse(code = 400, message = "Bad Request - Names/Number mismatching", response = ResponseEntity.class),
                    @ApiResponse(code = 400, message = "Bad Request - Too many players", response = ResponseEntity.class)
            }
    )
    @RequestMapping(method = RequestMethod.POST,
            value = "/startNewGame/", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<String> startNewGame(@RequestBody NewGameRequest newGameRequest) {
        log.info("a game has started!");
        try {
            String response = playerService.startNewGame(newGameRequest);
            return new ResponseEntity<>(response, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.CREATED);
        } catch (PlayersBadRequestException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);

        }

    }

    @ApiOperation(
            value = "Endpoint to make a new play",
            response = ResponseEntity.class,
            produces = MediaType.TEXT_PLAIN_VALUE,
            notes = "Endpoint to make a new play"
    )
    @ApiResponses(
            {
                    @ApiResponse(code = 200, message = "The ball rolled", response = ResponseEntity.class),
            }
    )
    @RequestMapping(method = RequestMethod.POST,
            value = "/play/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    ResponseEntity<String> playOneFrame(FrameRequest frameRequest) {
        log.info("roll the ball.");
        try {
            String response = playerService.playOneFrame(frameRequest);
            return new ResponseEntity<>(response, ControllerHttpUtil.getPlainTextHeader(), HttpStatus.OK);
        } catch (PlayerNotFoundException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), ControllerHttpUtil.getPlainTextHeader(), HttpStatus.NOT_FOUND);
        } catch (GameNotStartedException ex) {
            return new ResponseEntity<>(ex.getMessage(), ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);
        } catch (BadFrameRequestException ex) {
            return new ResponseEntity<>(ex.getMessage(), ControllerHttpUtil.getPlainTextHeader(), HttpStatus.BAD_REQUEST);
        }
    }
}
