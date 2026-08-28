package com.smms.report;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "jwt.secret="
})
class ReportServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
