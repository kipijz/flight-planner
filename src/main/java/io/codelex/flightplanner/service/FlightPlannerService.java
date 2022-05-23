package io.codelex.flightplanner.service;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import io.codelex.flightplanner.PageResult;
import io.codelex.flightplanner.SearchFlightRequest;

import java.util.List;

public interface FlightPlannerService {
    void clearFlights();

    Flight addFlight(Flight flight);

    Flight fetchFlight(long id);

    void deleteFlight(long id);

    List<Airport> searchAirports(String search);

    PageResult searchFlights(SearchFlightRequest request);
}
