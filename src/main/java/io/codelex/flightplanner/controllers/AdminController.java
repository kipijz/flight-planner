package io.codelex.flightplanner.controllers;

import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.service.FlightPlannerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/admin-api")
@RestController
public class AdminController {
    private final FlightPlannerService flightPlannerService;

    public AdminController(FlightPlannerService flightPlannerService) {
        this.flightPlannerService = flightPlannerService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping("flights")
    public Flight addFlight(@Valid @RequestBody Flight flight) {
        return flightPlannerService.addFlight(flight);
    }

    @GetMapping("flights/{id}")
    public Flight fetchFlight(@PathVariable("id") int id) {
        return flightPlannerService.fetchFlight(id);
    }

    @DeleteMapping("flights/{id}")
    public void deleteFlight(@PathVariable("id") int id) {
        flightPlannerService.deleteFlight(id);
    }
}
