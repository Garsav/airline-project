package com.garrett.airline.repo;

import com.garrett.airline.model.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")   // <- forces application-test.properties (H2)
class FlightRepositoryTest {

    @Autowired
    private FlightRepository repo;

    @Test
    void saveAndQueryByOrigin() {
        Flight f = new Flight();
        f.setFlightId(9001);
        f.setAirline("SWA");
        f.setOrigin("DAL");
        f.setDestination("HOU");
        f.setDeparture_utc(LocalDateTime.parse("2025-09-25T08:30:00"));
        f.setArrival_utc(LocalDateTime.parse("2025-09-25T09:25:00"));
        f.setStatus("SCHEDULED");

        repo.save(f);

        List<Flight> fromDal = repo.findByOrigin("DAL");
        assertThat(fromDal).isNotEmpty();
        assertThat(fromDal.get(0).getDestination()).isEqualTo("HOU");
    }
}
