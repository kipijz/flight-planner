package io.codelex.flightplanner.service;

import io.codelex.flightplanner.Flight;

public abstract class AbstractFlightPlannerService {
    protected boolean isFromAndToSameAirport(Flight flight) {
        String fromAirportTrimmed = flight.getFrom().getAirport().replace(" ", "");
        String toAirportTrimmed = flight.getTo().getAirport().replace(" ", "");
        return fromAirportTrimmed.equalsIgnoreCase(toAirportTrimmed);
    }

    protected boolean hasInvalidDates(Flight flight) {
        return flight.getDepartureTime().isEqual(flight.getArrivalTime())
                || flight.getDepartureTime().isAfter(flight.getArrivalTime());
    }

}
