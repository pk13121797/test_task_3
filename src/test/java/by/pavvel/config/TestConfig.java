package by.pavvel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;

@Configuration
public class TestConfig extends AbstractTestcontainers {

    @Bean
    public DataSource dataSource() {  // PostgreSQLContainer configuration
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(postgres.getDriverClassName());
        dataSource.setUrl(postgres.getJdbcUrl());
        dataSource.setUsername(postgres.getUsername());
        dataSource.setPassword(postgres.getPassword());
        return dataSource;
    }

//    @Bean
//    public DataSource dataSource() {  // H2 configuration
//        return new EmbeddedDatabaseBuilder()
//                .generateUniqueName(true)
//                .setType(H2)
//                .setScriptEncoding("UTF-8")
//                .build();
//    }
}
