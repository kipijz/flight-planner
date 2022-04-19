package io.codelex.flightplanner.controllers;

import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.service.FlightPlannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RequestMapping("/admin-api")
@RestController
public class AdminController {
    private final FlightPlannerService flightPlannerService;

    public AdminController(FlightPlannerService flightPlannerService) {
        this.flightPlannerService = flightPlannerService;
    }

    @PutMapping("flights")
    public CompletableFuture<ResponseEntity<Flight>> addFlight(@Valid @RequestBody Flight flight) {
        return  flightPlannerService.addFlight(flight);
    }

    @GetMapping("flights/{id}")
    public ResponseEntity<Flight> fetchFlight(@PathVariable("id") int id) {
        return  flightPlannerService.fetchFlight(id);
    }

    @DeleteMapping("flights/{id}")
    public CompletableFuture<ResponseEntity<Flight>> deleteFlight(@PathVariable("id") int id) {
        return  flightPlannerService.deleteFlight(id);
    }
}
