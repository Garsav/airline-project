package com.garrett.airline.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @Column(name = "flight_id")
    private Integer flightId;

    @Column(nullable = false, length = 8)
    private String airline;

    @Column(nullable = false, length = 8)
    private String origin;

    @Column(nullable = false, length = 8)
    private String destination;

    @Column(name = "departure_utc", nullable = false)
    private LocalDateTime departure_utc;

    @Column(name = "arrival_utc", nullable = false)
    private LocalDateTime arrival_utc;

    @Column(nullable = false, length = 16)
    private String status;

    public Integer getFlightId() { return flightId; }
    public void setFlightId(Integer flightId) { this.flightId = flightId; }

    public String getAirline() { return airline; }
    public void setAirline(String airline) { this.airline = airline; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDeparture_utc() { return departure_utc; }
    public void setDeparture_utc(LocalDateTime departure_utc) { this.departure_utc = departure_utc; }

    public LocalDateTime getArrival_utc() { return arrival_utc; }
    public void setArrival_utc(LocalDateTime arrival_utc) { this.arrival_utc = arrival_utc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
