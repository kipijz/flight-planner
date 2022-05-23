package io.codelex.flightplanner.database;

import io.codelex.flightplanner.Airport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "flight-planner", name = "store-type", havingValue = "database")
public class AirportDatabaseService {
    private final AirportDatabaseRepository airportDatabaseRepository;

    public AirportDatabaseService(AirportDatabaseRepository airportDatabaseRepository) {
        this.airportDatabaseRepository = airportDatabaseRepository;
    }

    public void addAirport(Airport airport) {
        airportDatabaseRepository.save(airport);
    }
}
