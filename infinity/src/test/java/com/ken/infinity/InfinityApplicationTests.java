package com.ken.infinity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", "spring.datasource.driverClassName=org.h2.Driver", "spring.jpa.hibernate.ddl-auto=update" })
class InfinityApplicationTests {

    @Test
    void contextLoads() {}
}
