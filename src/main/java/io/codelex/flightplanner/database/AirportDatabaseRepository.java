package io.codelex.flightplanner.database;

import io.codelex.flightplanner.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportDatabaseRepository extends JpaRepository<Airport, String> {
}
