package io.codelex.flightplanner.repository;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FlightPlannerRepository {
    private final List<Flight> flights = new ArrayList<>();
    private int flightId;

    public void clearFlights() {
        flights.clear();
    }

    public synchronized Flight addFlight(@RequestBody Flight flight) {
        if (flights.contains(flight)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (isFromAndToSameAirport(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (hasInvalidDates(flight)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        flight.setId(flightId);
        flightId++;
        flights.add(flight);
        return flight;
    }

    public Flight fetchFlight(long id) {
        return flights.stream()
                .filter(flight -> flight.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public synchronized void deleteFlight(long id) {
        flights.stream()
                .filter(flight -> flight.getId() == id)
                .findFirst().ifPresent(flights::remove);
    }

    public List<Airport> searchAirports(String search) {
        return getExistingAirportList(search);
    }

    public PageResult searchFlights(SearchFlightRequest request) {
        if (request.getTo().equals(request.getFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return getExistingFlight(request);
    }

    private boolean isFromAndToSameAirport(Flight flight) {
        String fromAirportTrimmed = flight.getFrom().getAirport().replace(" ", "");
        String toAirportTrimmed = flight.getTo().getAirport().replace(" ", "");
        return fromAirportTrimmed.equalsIgnoreCase(toAirportTrimmed);
    }

    private boolean hasInvalidDates(Flight flight) {
        return flight.getDepartureTime().isEqual(flight.getArrivalTime())
                || flight.getDepartureTime().isAfter(flight.getArrivalTime());
    }

    private List<Airport> getExistingAirportList(String search) {
        String searchTrimmedLowerCase = search.replace(" ", "").toLowerCase();
        return flights.stream()
                .filter(flight -> flight.getFrom().getCountry().toLowerCase().contains(searchTrimmedLowerCase)
                        || flight.getFrom().getAirport().toLowerCase().contains(searchTrimmedLowerCase)
                        || flight.getFrom().getCity().toLowerCase().contains(searchTrimmedLowerCase))
                .map(Flight::getFrom)
                .toList();
    }

    private PageResult getExistingFlight(SearchFlightRequest request) {
        PageResult pageResult = new PageResult();
        List<Flight> existingFlightsList = flights.stream()
                .filter(flight -> flight.getFrom().getAirport().equals(request.getFrom())
                        && flight.getTo().getAirport().equals(request.getTo())
                        && flight.getDepartureTime().toLocalDate().equals(request.getDepartureDate()))
                .toList();
        pageResult.addItems(existingFlightsList);
        pageResult.addTotalItems(existingFlightsList.size());
        return pageResult;
    }
}
