package com.ingemark.productapp.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ingemark.productapp.app.auth.JwtUtil;
import com.ingemark.productapp.app.auth.Role;
import com.ingemark.productapp.app.auth.User;
import com.ingemark.productapp.app.auth.UserRepository;
import com.ingemark.productapp.app.customer.product.ExchangeRateClient;
import com.ingemark.productapp.app.customer.product.ExchangeRateResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest
{
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestHelper testHelper;

    @MockitoBean
    protected ExchangeRateClient exchangeRateClient;

    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    
    protected static final String
        JWT
        = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJub3QiLCJyb2xlIjoiW1JPTEVfVVNFUl0iLCJpYXQiOjE3NDE3OTg5MzUsImV4cCI6MTc0MTgwMjUzNX0.TQCiStlwoNY3WzCavbdfLbvUo24iDa8vb-tsqjwal3s";
    private static final PostgreSQLContainer<?>
        POSTGRES_CONTAINER
        = new PostgreSQLContainer<>("postgres:17").withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>("redis:7").withExposedPorts(6379);

    static
    {
        POSTGRES_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry)
    {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @BeforeAll
    void setup()
    {
        System.out.println("TestContainers started for PostgreSQL and Redis");
        if (userRepository.findByUsername("testuser").isEmpty()) {
            User testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPassword(new BCryptPasswordEncoder().encode("password"));
            testUser.setRole(Role.ROLE_USER);
            userRepository.save(testUser);
        }
    }

    @BeforeEach
    final void beforeEach()
    {
        prepareExchangeRateServiceResponse();
        prepareJwtMock();
    }

    private void prepareExchangeRateServiceResponse()
    {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        exchangeRateResponse.setCurrency("USD");
        exchangeRateResponse.setExchangeRate("1,00");
        when(exchangeRateClient.getExchangeRate(any())).thenReturn(List.of(exchangeRateResponse));
    }

    @BeforeEach
    void prepareJwtMock()
    {
        when(jwtUtil.extractUsername(any())).thenReturn("testuser");
        when(jwtUtil.validateToken(any(), any())).thenReturn(true);
    }
}
