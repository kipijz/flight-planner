package io.codelex.flightplanner.database;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import io.codelex.flightplanner.service.AbstractFlightPlannerService;
import io.codelex.flightplanner.service.FlightPlannerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@ConditionalOnProperty(prefix = "flight-planner", name = "store-type", havingValue = "database")
public class FlightPlannerDatabaseImpl extends AbstractFlightPlannerService implements FlightPlannerService {
    private final FlightDatabaseRepository flightDatabaseRepository;
    private final AirportDatabaseRepository airportDatabaseRepository;

    public FlightPlannerDatabaseImpl(FlightDatabaseRepository flightDatabaseRepository,
                                     AirportDatabaseRepository airportDatabaseRepository) {
        this.flightDatabaseRepository = flightDatabaseRepository;
        this.airportDatabaseRepository = airportDatabaseRepository;
    }

    @Override
    public void clearFlights() {
        flightDatabaseRepository.deleteAll();
        airportDatabaseRepository.deleteAll();
    }

    @Override
    public Flight addFlight(Flight flight) {
        if (isFromAndToSameAirport(flight) || hasInvalidDates(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (flightDatabaseRepository.countOfExistingFlights(flight.getFrom(),
                flight.getTo(),
                flight.getCarrier(),
                flight.getDepartureTime(),
                flight.getArrivalTime()) != 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        flight.setFrom(findOrCreate(flight.getFrom()));
        flight.setTo(findOrCreate(flight.getTo()));
        return flightDatabaseRepository.save(flight);
    }

    @Override
    public Flight fetchFlight(long id) {
        return flightDatabaseRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public void
    deleteFlight(long id) {
        flightDatabaseRepository.deleteByIdIfExists(id);
    }

    @Override
    public List<Airport> searchAirports(String search) {
        String searchTrimmed = search.replace(" ", "").toLowerCase();
        return flightDatabaseRepository.searchAirport(searchTrimmed);
    }

    @Override
    public PageResult searchFlights(SearchFlightRequest request) {
        if (request.getTo().equals(request.getFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        PageResult pageResult = new PageResult();
        LocalDateTime dateStart = request.getDepartureDate().atStartOfDay();
        LocalDateTime dateEnd = dateStart.plusDays(1L);
        List<Flight> existingFlightsList = flightDatabaseRepository.getExistingFlight(request.getFrom(),
                request.getTo(),
                dateStart,
                dateEnd);
        pageResult.addItems(existingFlightsList);
        pageResult.addTotalItems(existingFlightsList.size());
        return pageResult;
    }

    private Airport findOrCreate(Airport airport) {
        Optional<Airport> existingAirport = airportDatabaseRepository.findById(airport.getAirport());
        return existingAirport.orElseGet(() -> airportDatabaseRepository.save(airport));
    }
}
