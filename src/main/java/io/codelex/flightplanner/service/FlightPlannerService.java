package io.codelex.flightplanner.service;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import io.codelex.flightplanner.repository.FlightPlannerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class FlightPlannerService {
    private final FlightPlannerRepository flightPlannerRepository;

    public FlightPlannerService(FlightPlannerRepository flightPlannerRepository) {
        this.flightPlannerRepository = flightPlannerRepository;
    }

    public void clearFlights() {
        flightPlannerRepository.clearFlights();
    }

    public CompletableFuture<ResponseEntity<Flight>> addFlight(Flight flight) {
        return flightPlannerRepository.addFlight(flight);
    }

    public ResponseEntity<Flight> fetchFlight(int id) {
        return flightPlannerRepository.fetchFlight(id);
    }

    public CompletableFuture<ResponseEntity<Flight>> deleteFlight(int id) {
        return flightPlannerRepository.deleteFlight(id);

    }

    public ResponseEntity<List<Airport>> searchAirports(String search) {
        return flightPlannerRepository.searchAirports(search);
    }

    public ResponseEntity<PageResult> searchFlights(SearchFlightRequest flight) {
        return  flightPlannerRepository.searchFlights(flight);
    }
}
