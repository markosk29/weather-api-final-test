package com.hackerrank.weather.controller;

import com.hackerrank.weather.model.WeatherInput;
import com.hackerrank.weather.output.WeatherJSON;
import com.hackerrank.weather.service.WeatherService;
import com.hackerrank.weather.utils.Patterns;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
public class WeatherApiRestController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherApiRestController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping(path = "/weather", produces = "application/json")
    @Operation(summary = "Get weather info", tags = {"Weather",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns a list of weather info based on the input criteria," +
                            " or all if no criteria is given.",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation =  WeatherJSON.class)))),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    public List<WeatherJSON> getWeatherInfo(@Valid @Pattern(regexp = Patterns.YYYY_MM_DD_REGEXP)
                                            @RequestParam(required = false) String date,
                                            @RequestParam(required = false) String city,
                                            @RequestParam(required = false) String sort){

       return weatherService.getWeatherInfo(date, city, sort);
    }

    @GetMapping(path ="/weather/{id}", produces = "application/json")
    @Operation(summary = "Get weather info by id", tags = {"Weather",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns the weather info of the given id.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  WeatherJSON.class))),
                    @ApiResponse(responseCode = "404", description = "Weather entity not found!"),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    public ResponseEntity<WeatherJSON> getWeatherById(@Valid @NotNull @PathVariable Integer id) {
       return new ResponseEntity<>(weatherService.getWeather(id), HttpStatus.OK);
    }

    /**
     * Submit weather.
     *
     */
    @PostMapping(path = "/weather", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Save a weather to the embedded DB.", tags = {"Weather",},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Returns a JSON containing the saved weather.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation =  WeatherJSON.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error.")
            })
    public ResponseEntity<WeatherJSON> submitWeather(@RequestBody @Valid final WeatherInput weatherInput) {
       return new ResponseEntity<>(weatherService.addWeather(weatherInput), HttpStatus.CREATED);
    }
}
