package io.codelex.flightplanner.service;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import io.codelex.flightplanner.repository.FlightPlannerRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@ConditionalOnProperty(prefix = "flight-planner", name = "store-type", havingValue = "in-memory")
public class FlightPlannerServiceImpl extends AbstractFlightPlannerService implements FlightPlannerService {
    private final FlightPlannerRepository flightPlannerRepository;

    public FlightPlannerServiceImpl(FlightPlannerRepository flightPlannerRepository) {
        this.flightPlannerRepository = flightPlannerRepository;
    }

    @Override
    public void clearFlights() {
        flightPlannerRepository.clearFlights();
    }

    @Override
    public Flight addFlight(Flight flight) {
        if (isFromAndToSameAirport(flight) || hasInvalidDates(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return flightPlannerRepository.addFlight(flight);
    }

    @Override
    public Flight fetchFlight(long id) {
        return flightPlannerRepository.fetchFlight(id);
    }

    @Override
    public void deleteFlight(long id) {
        flightPlannerRepository.deleteFlight(id);
    }

    @Override
    public List<Airport> searchAirports(String search) {
        return flightPlannerRepository.searchAirports(search);
    }

    @Override
    public PageResult searchFlights(SearchFlightRequest request) {
        return flightPlannerRepository.searchFlights(request);
    }
}
