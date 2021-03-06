package io.codelex.flightplanner.controllers;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import io.codelex.flightplanner.service.FlightPlannerService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api")
@RestController
public class CustomerController {
    private final FlightPlannerService flightPlannerService;

    public CustomerController(FlightPlannerService flightPlannerService) {
        this.flightPlannerService = flightPlannerService;
    }

    @GetMapping("airports")
    public List<Airport> searchAirports(@RequestParam("search") String search) {
        return flightPlannerService.searchAirports(search);
    }

    @PostMapping("flights/search")
    public PageResult searchFlights(@Valid @RequestBody SearchFlightRequest request) {
        return flightPlannerService.searchFlights(request);
    }

    @GetMapping("flights/{id}")
    public Flight fetchFlight(@PathVariable("id") int id) {
        return flightPlannerService.fetchFlight(id);
    }
}
