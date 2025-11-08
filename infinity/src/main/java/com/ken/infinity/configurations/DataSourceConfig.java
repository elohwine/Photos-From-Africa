package com.ken.infinity.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Creates a DataSource from environment variables. Supports DATABASE_URL in
 * the following forms:
 *  - postgresql://user:pass@host:port/dbname?params
 *  - postgresql://host:port/dbname?params  (user/pass supplied via DB_USER/DB_PASSWORD)
 *
 * This avoids Spring Boot mis-parsing a DATABASE_URL that contains credentials
 * (which would make the JDBC URL contain user:pass@host and cause UnknownHost).
 */
@Configuration
public class DataSourceConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource() throws URISyntaxException {
        String databaseUrl = System.getenv("DATABASE_URL");

        String jdbcUrl = null;
        String username = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (databaseUrl != null && (databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("postgres://"))) {
            // parse url which may contain user:pass@host
            URI uri = new URI(databaseUrl);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String path = uri.getPath() == null ? "" : uri.getPath();
            String query = uri.getQuery();

            // handle user info
            String userInfo = uri.getUserInfo();
            if (userInfo != null && (username == null || username.isBlank())) {
                String[] parts = userInfo.split(":", 2);
                username = parts.length > 0 ? parts[0] : username;
                if (parts.length > 1) password = parts[1];
            }

            StringBuilder sb = new StringBuilder();
            sb.append("jdbc:postgresql://").append(host).append(":").append(port).append(path);
            if (query != null && !query.isBlank()) sb.append("?").append(query);
            jdbcUrl = sb.toString();
        }

        // Fallbacks: allow explicit SPRING JDBC url or default to an in-repo property
        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            // allow standard spring env var SPRING_DATASOURCE_URL or JDBC_DATABASE_URL
            String envJdbc = System.getenv("SPRING_DATASOURCE_URL");
            if (envJdbc == null) envJdbc = System.getenv("JDBC_DATABASE_URL");
            if (envJdbc != null && !envJdbc.isBlank()) {
                jdbcUrl = envJdbc;
            }
        }

        if (jdbcUrl == null || jdbcUrl.isBlank()) {
            // Last resort: rely on Spring property configured in application.properties
            // Let Spring Boot create the DataSource normally by returning null.
            log.warn("No DATABASE_URL / SPRING_DATASOURCE_URL / JDBC_DATABASE_URL found - falling back to Spring Boot defaults");
            return null;
        }

        log.info("Configuring DataSource -> {} (user={})", jdbcUrl, username);

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        if (username != null) cfg.setUsername(username);
        if (password != null) cfg.setPassword(password);
        // reasonable defaults
        cfg.setMaximumPoolSize(10);
        cfg.setMinimumIdle(1);
        cfg.setPoolName("HikariPool-Infinity");

        return new HikariDataSource(cfg);
    }
}
