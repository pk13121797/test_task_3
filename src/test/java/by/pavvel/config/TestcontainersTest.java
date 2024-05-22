package by.pavvel.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class TestcontainersTest extends AbstractTestcontainers {

    @Test
    void canStartPostgresDB() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.isCreated()).isTrue();
    }
}