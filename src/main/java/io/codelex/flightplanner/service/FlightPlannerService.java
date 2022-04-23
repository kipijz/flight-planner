package io.codelex.flightplanner.service;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import io.codelex.flightplanner.repository.FlightPlannerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightPlannerService {
    private final FlightPlannerRepository flightPlannerRepository;

    public FlightPlannerService(FlightPlannerRepository flightPlannerRepository) {
        this.flightPlannerRepository = flightPlannerRepository;
    }

    public void clearFlights() {
        flightPlannerRepository.clearFlights();
    }

    public Flight addFlight(Flight flight) {
        return flightPlannerRepository.addFlight(flight);
    }

    public Flight fetchFlight(int id) {
        return flightPlannerRepository.fetchFlight(id);
    }

    public void deleteFlight(int id) {
        flightPlannerRepository.deleteFlight(id);

    }

    public List<Airport> searchAirports(String search) {
        return flightPlannerRepository.searchAirports(search);
    }

    public PageResult searchFlights(SearchFlightRequest flight) {
        return flightPlannerRepository.searchFlights(flight);
    }
}
