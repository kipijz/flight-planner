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
        return flights.get(flights.size() - 1);
    }

    public Flight fetchFlight(int id) {
        if (isExistingFlight(id)) {
            return findExistingFlight(id);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public synchronized void deleteFlight(int id) {
        if (isExistingFlight(id)) {
            Flight foundFlight = findExistingFlight(id);
            flights.remove(foundFlight);
        }
    }

    public List<Airport> searchAirports(String search) {
        List<Airport> airportList = getExistingAirportList(search);
        if (airportList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return airportList;
    }

    public PageResult searchFlights(SearchFlightRequest flight) {
        PageResult pageResult = getExistingFlight(flight);
        if (flight.getTo().equals(flight.getFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return pageResult;
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

    private boolean isExistingFlight(int id) {
        return flights.stream()
                .map(Flight::getId)
                .anyMatch(integer -> integer == id);
    }

    private Flight findExistingFlight(int id) {
        return FlightPlannerRepository.this.flights.stream()
                .filter(flight -> flight.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private List<Airport> getExistingAirportList(String search) {
        List<Airport> airportList = new ArrayList<>();
        String searchTrimmedLowerCase = search.replace(" ", "").toLowerCase();
        for (Flight flight : flights) {
            if (flight.getFrom().getCountry().toLowerCase().contains(searchTrimmedLowerCase)
                    || flight.getFrom().getAirport().toLowerCase().contains(searchTrimmedLowerCase)
                    || flight.getFrom().getCity().toLowerCase().contains(searchTrimmedLowerCase)) {
                airportList.add(flight.getFrom());
            }
        }
        return airportList;
    }

    private PageResult getExistingFlight(SearchFlightRequest flight) {
        PageResult pageResult = new PageResult();
        for (Flight value : flights) {
            if (value.getFrom().getAirport().equals(flight.getFrom())
                    && value.getTo().getAirport().equals(flight.getTo())
                    && String.valueOf(value.getDepartureTime()).contains(flight.getDepartureDate())) {
                pageResult.setTotalItems(1);
                break;
            }
        }
        return pageResult;
    }
}
