package bff

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@RunWith(SpringRunner)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = [
        "api.root=http://localhost","feature.flags.url=http://localhost","bnpl.enabled.countries=http://localhost","regional.config.url=http://localhost","payments.url=http://localhost"
])
class ApplicationTest {

    @LocalServerPort
    private String port

    @Test
    void 'application boots up and health check passes'() {
        assert 'UP' == new RestTemplate().getForObject(
                "http://localhost:$port/actuator/health",
                Map
        ).status
    }
}