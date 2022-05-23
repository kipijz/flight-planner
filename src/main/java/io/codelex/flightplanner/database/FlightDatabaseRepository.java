package io.codelex.flightplanner.database;

import io.codelex.flightplanner.Airport;
import io.codelex.flightplanner.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface FlightDatabaseRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.from = :from_id and f.to = :to_id and f.carrier = :carrier and f.departureTime = :departure_time and f.arrivalTime = :arrival_time")
    int countOfExistingFlights(@Param("from_id") Airport from,
                               @Param("to_id") Airport to,
                               @Param("carrier") String carrier,
                               @Param("departure_time") LocalDateTime departureTime,
                               @Param("arrival_time") LocalDateTime arrivalTime);

    @Modifying
    @Query("DELETE FROM Flight f WHERE f.id = :id")
    void deleteByIdIfExists(@Param("id") long id);

    @Query("SELECT a FROM Airport a WHERE lower(a.airport) LIKE %:search% or lower(a.city) LIKE %:search% or lower(a.country) LIKE %:search%")
    List<Airport> searchAirport(@Param("search") String search);

    @Query("SELECT f FROM Flight f WHERE f.from.airport = :from_airport and f.to.airport = :to_airport and f.departureTime >= :date_start and f.departureTime < :date_end")
    List<Flight> getExistingFlight(@Param("from_airport") String fromAirport,
                                   @Param("to_airport") String toAirport,
                                   @Param("date_start") LocalDateTime dateStart,
                                   @Param("date_end") LocalDateTime dateEnd);
}

