package com.garrett.airline;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AirlineSpringApplicationTests {
    @Test
    void contextLoads() {
        // Boots with the "test" profile (H2), nothing else required.
    }
}
