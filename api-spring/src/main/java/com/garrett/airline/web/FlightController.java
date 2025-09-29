package com.garrett.airline.web;

import com.garrett.airline.model.Flight;
import com.garrett.airline.repo.FlightRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
public class FlightController {

    private final FlightRepository repo;

    public FlightController(FlightRepository repo) {
        this.repo = repo;
    }

    // GET /flights with optional filters and sorting
    @GetMapping("/flights")
    public List<Flight> flights(@RequestParam(required = false) String origin,
                                @RequestParam(required = false, name = "destination") String destination,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false, defaultValue = "departure_utc") String sortBy,
                                @RequestParam(required = false, defaultValue = "asc") String order,
                                @RequestParam(required = false, defaultValue = "100") Integer limit) {
        List<Flight> all;
        if (origin != null && !origin.isBlank()) {
            all = repo.findByOrigin(origin);
        } else if (destination != null && !destination.isBlank()) {
            all = repo.findByDestination(destination);
        } else {
            all = repo.findAll();
        }

        // filter by status if provided
        if (status != null && !status.isBlank()) {
            all = all.stream()
                    .filter(f -> status.equalsIgnoreCase(f.getStatus()))
                    .toList();
        }

        // sort logic
        Comparator<Flight> cmp = switch (sortBy) {
            case "airline" -> Comparator.comparing(Flight::getAirline, String.CASE_INSENSITIVE_ORDER);
            case "origin" -> Comparator.comparing(Flight::getOrigin, String.CASE_INSENSITIVE_ORDER);
            case "destination" -> Comparator.comparing(Flight::getDestination, String.CASE_INSENSITIVE_ORDER);
            case "arrival_utc" -> Comparator.comparing(Flight::getArrival_utc);
            default -> Comparator.comparing(Flight::getDeparture_utc);
        };
        if ("desc".equalsIgnoreCase(order)) {
            cmp = cmp.reversed();
        }

        return all.stream()
                .sorted(cmp)
                .limit(Math.max(1, Math.min(1000, limit))) // keep limit safe
                .toList();
    }

    // GET /flights/{id} — lookup single flight
    @GetMapping("/flights/{id}")
    public Optional<Flight> getOne(@PathVariable Integer id) {
        return repo.findById(id);
    }
}
