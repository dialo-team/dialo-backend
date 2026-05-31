package com.fit.se;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = IdentityApplication.class)
@ActiveProfiles("test")
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
