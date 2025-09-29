package com.garrett.airline.web;

// add to imports:
import org.springframework.web.bind.annotation.RequestMapping;

// (optional) base path
@RequestMapping
@RestController
public class FlightController {
    // ...existing code...

    @GetMapping("/flights")
    public List<Flight> flights(@RequestParam(required = false) String origin,
                                @RequestParam(required = false, name = "destination") String destination,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false, defaultValue = "departure_utc") String sortBy,
                                @RequestParam(required = false, defaultValue = "asc") String order,
                                @RequestParam(required = false, defaultValue = "100") Integer limit) {
        List<Flight> all;
        if (origin != null && !origin.isBlank())        all = repo.findByOrigin(origin);
        else if (destination != null && !destination.isBlank()) all = repo.findByDestination(destination);
        else                                            all = repo.findAll();

        // in-memory filters/sort for demo (ok for capstone)
        if (status != null && !status.isBlank()) {
            all = all.stream().filter(f -> status.equalsIgnoreCase(f.getStatus())).toList();
        }
        var stream = all.stream();
        var cmp = switch (sortBy) {
            case "airline" -> java.util.Comparator.comparing(Flight::getAirline, String.CASE_INSENSITIVE_ORDER);
            case "origin"  -> java.util.Comparator.comparing(Flight::getOrigin,  String.CASE_INSENSITIVE_ORDER);
            case "destination" -> java.util.Comparator.comparing(Flight::getDestination, String.CASE_INSENSITIVE_ORDER);
            case "arrival_utc" -> java.util.Comparator.comparing(Flight::getArrival_utc);
            default -> java.util.Comparator.comparing(Flight::getDeparture_utc);
        };
        if ("desc".equalsIgnoreCase(order)) cmp = cmp.reversed();
        return stream.sorted(cmp).limit(Math.max(1, Math.min(1000, limit))).toList();
    }
}
