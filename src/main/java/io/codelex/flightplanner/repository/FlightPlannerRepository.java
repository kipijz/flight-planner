package io.codelex.flightplanner.repository;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class FlightPlannerRepository {
    private final List<Flight> flights = new ArrayList<>();
    private volatile int flightId;

    public void clearFlights() {
        flights.clear();
    }

    @Async
    public CompletableFuture<ResponseEntity<Flight>> addFlight(@RequestBody Flight flight) {
        if (flights.contains(flight)) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.CONFLICT));
        }

        if (isFromAndToSameAirport(flight)) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        if (hasInvalidDates(flight)) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        }

        synchronized (this) {
            flight.setId(flightId);
            flightId++;
            flights.add(flight);
            return CompletableFuture.completedFuture(new ResponseEntity<>(flight, HttpStatus.CREATED));
        }
    }

    public ResponseEntity<Flight> fetchFlight(int id) {
        if (isExistingFlight(id)) {
            Flight foundFlight = findAndReturnExistingFlight(id);
            return new ResponseEntity<>(foundFlight, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Async
    public CompletableFuture<ResponseEntity<Flight>> deleteFlight(int id) {
        if (isExistingFlight(id)) {
            Flight foundFlight = findAndReturnExistingFlight(id);
            flights.remove(foundFlight);
            return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.OK));
        }
        return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.OK));
    }

    public ResponseEntity<List<Airport>> searchAirports(String search) {
        List<Airport> airportList = getExistingAirportList(search);
        if (airportList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(airportList, HttpStatus.OK);
        }
    }

    public ResponseEntity<PageResult> searchFlights(SearchFlightRequest flight) {
        PageResult pageResult = getExistingFlight(flight);
        if (pageResult.getTotalItems() > 0) {
            return new ResponseEntity<>(pageResult, HttpStatus.OK);
        }

        if (flight.getTo().equals(flight.getFrom())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(pageResult, HttpStatus.OK);
    }

    private boolean isFromAndToSameAirport(Flight flight) {
        String fromAirportTrimmed = flight.getFrom().getAirport().replace(" ", "");
        String toAirportTrimmed = flight.getTo().getAirport().replace(" ", "");
        return fromAirportTrimmed.equalsIgnoreCase(toAirportTrimmed);
    }

    private boolean hasInvalidDates(Flight flight) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime departureTime = LocalDateTime.parse(flight.getDepartureTime(), formatter);
        LocalDateTime arrivalTime = LocalDateTime.parse(flight.getArrivalTime(), formatter);
        return departureTime.isEqual(arrivalTime) || departureTime.isAfter(arrivalTime);
    }

    private boolean isExistingFlight(int id) {
        return flights.stream()
                .map(Flight::getId)
                .anyMatch(integer -> integer == id);
    }

    private Flight findAndReturnExistingFlight(int id) {
        return flights.stream()
                .filter(flight -> flight.getId() == id)
                .findFirst()
                .get();
    }

    private List<Airport> getExistingAirportList(String search) {
        List<Airport> airportList = new ArrayList<>();
        String searchTrimmedLowerCase = search.replace(" ", "").toLowerCase();
        for (Flight flight : flights) {
            if (flight.getFrom().getCountry().toLowerCase().contains(searchTrimmedLowerCase) || flight.getFrom().getAirport().toLowerCase().contains(searchTrimmedLowerCase) || flight.getFrom().getCity().toLowerCase().contains(searchTrimmedLowerCase)) {
                airportList.add(flight.getFrom());
            }
        }
        return airportList;
    }

    private PageResult getExistingFlight(SearchFlightRequest flight) {
        PageResult pageResult = new PageResult();
        for (Flight value : flights) {
            String departureDate = value.getDepartureTime().substring(0, 10);
            if (value.getFrom().getAirport().equals(flight.getFrom()) && value.getTo().getAirport().equals(flight.getTo()) && departureDate.equals(flight.getDepartureDate())) {
                pageResult.setTotalItems(1);
                break;
            }
        }
        return pageResult;
    }
}
