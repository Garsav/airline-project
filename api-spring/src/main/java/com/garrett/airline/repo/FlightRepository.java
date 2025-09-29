package com.garrett.airline.repo;

import com.garrett.airline.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findByOrigin(String origin);
    List<Flight> findByDestination(String destination);
}


