package domain.needsRefactoring;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import domain.Address;
import domain.Site;
import domain.User;
import domain.Site.Builder;
import exceptions.InformationRequiredExceptionSite;
import jakarta.persistence.*;
import util.Role;
import util.Status;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class SiteMySQLPersistenceTest  {

    @Container
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeAll
    static void setupEntityManagerFactory() {
        Map<String, String> properties = Map.of(
            "jakarta.persistence.jdbc.url", mysql.getJdbcUrl(),
            "jakarta.persistence.jdbc.user", mysql.getUsername(),
            "jakarta.persistence.jdbc.password", mysql.getPassword(),
            "jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver",
            "hibernate.hbm2ddl.auto", "create-drop",
            "hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect",
            "hibernate.show_sql", "true"
        );

        emf = Persistence.createEntityManagerFactory("shopfloor-app", properties);
    }

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
    }

    @AfterEach
    void teardown() {
        if (em != null) em.close();
    }

    @AfterAll
    static void close() {
        if (emf != null) emf.close();
    }

    @Test
    void testPersistAndRetrieveSite() throws InformationRequiredExceptionSite {
        Address address = new Address("Main Street", 123, 1000, "Brussels");
        User user = new User(
        		"Jane", 
        		"Doe", 
        		"jane.doe@example.com", 
        		"010101", "testPassword", 
        		LocalDate.now().minusYears(20L),
        		address,
        		Status.ACTIEF,
        		Role.ADMINISTRATOR
        		);

        Site site = new Site.Builder()
                .buildSiteName("Warehouse A")
                .buildAddress(address)
                .buildVerantwoordelijke(user)
                .buildStatus(Status.ACTIEF)
                .build();

        tx.begin();
        em.persist(site);
        tx.commit();

        Site found = em.find(Site.class, site.getId());
        assertNotNull(found);
        assertEquals("Warehouse A", found.getSiteName());
        assertEquals("Jane", found.getVerantwoordelijke().getFirstName());
        assertEquals("Main Street", found.getAddress().getStreet());
    }
}
