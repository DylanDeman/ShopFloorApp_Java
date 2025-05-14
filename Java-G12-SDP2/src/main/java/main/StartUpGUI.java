package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import domain.Address;
import domain.machine.Machine;
import domain.maintenance.Maintenance;
import domain.maintenance.MaintenanceController;
import domain.notifications.Notification;
import domain.report.Report;
import domain.site.Site;
import domain.user.User;
import gui.MainLayout;
import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.JPAUtil;
import util.MachineStatus;
import util.MaintenanceStatus;
import util.PasswordHasher;
import util.ProductionStatus;
import util.Role;
import util.Status;

public class StartUpGUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainLayout mainLayout = new MainLayout(primaryStage);

        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon-32x32.png")));
        primaryStage.setTitle("Shopfloor application");

        primaryStage.show();

        // Create database test data
        createTestData();
    }
    
    private void createTestData() {
        String gehashteWW = PasswordHasher.hash("password");
        EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            entityManager.getTransaction().begin();
            
            // Create users
            User u1 = new User("Jan", "Janssen", "jan@email.com", "0412345678", gehashteWW, LocalDate.of(1990, 1, 1),
                    new Address("Straat 1", 10, 1001, "Stad"), Status.ACTIEF, Role.TECHNIEKER);

            User u2 = new User("Piet", "Pietersen", "piet@email.com", "0423456789", gehashteWW, LocalDate.of(1985, 5, 15),
                    new Address("Straat 2", 20, 2000, "Stad"), Status.ACTIEF, Role.ADMIN);

            User u3 = new User("Anna", "Dekker", "anna@email.com", "0434567890", gehashteWW, LocalDate.of(1995, 3, 12),
                    new Address("Straat 3", 30, 3000, "Stad"), Status.ACTIEF, Role.VERANTWOORDELIJKE);

            User u4 = new User("Eva", "Smit", "eva@email.com", "0445678901", gehashteWW, LocalDate.of(1988, 7, 22),
                    new Address("Straat 4", 40, 4000, "Stad"), Status.ACTIEF, Role.TECHNIEKER);

            User u5 = new User("Mark", "Visser", "mark@email.com", "0456789012", gehashteWW, LocalDate.of(1992, 11, 5),
                    new Address("Straat 5", 50, 5000, "Stad"), Status.ACTIEF, Role.ADMIN);

            User u6 = new User("Sophie", "Koster", "sophie@email.com", "0467890123", gehashteWW, LocalDate.of(1993, 9, 18),
                    new Address("Straat 6", 60, 6000, "Stad"), Status.ACTIEF, Role.MANAGER);

            User u7 = new User("Tom", "Hendriks", "tom@email.com", "0478901234", gehashteWW, LocalDate.of(1987, 12, 30),
                    new Address("Straat 7", 70, 7000, "Stad"), Status.ACTIEF, Role.TECHNIEKER);

            User u8 = new User("Laura", "Bakker", "laura@email.com", "0489012345", gehashteWW, LocalDate.of(1994, 2, 25),
                    new Address("Straat 8", 80, 8000, "Stad"), Status.ACTIEF, Role.VERANTWOORDELIJKE);

            User u9 = new User("Rob", "Jansen", "rob@email.com", "0490123456", gehashteWW, LocalDate.of(1986, 6, 10),
                    new Address("Straat 9", 90, 9000, "Stad"), Status.ACTIEF, Role.ADMIN);

            User u10 = new User("Kim", "De Vries", "kim@email.com", "0412345678", gehashteWW, LocalDate.of(1991, 4, 20),
                    new Address("Straat 10", 100, 9999, "Stad"), Status.ACTIEF, Role.MANAGER);

            // Persist users first
            entityManager.persist(u1);
            entityManager.persist(u2);
            entityManager.persist(u3);
            entityManager.persist(u4);
            entityManager.persist(u5);
            entityManager.persist(u6);
            entityManager.persist(u7);
            entityManager.persist(u8);
            entityManager.persist(u9);
            entityManager.persist(u10);
                        
            // Create notifications
            List<Notification> notifications = Arrays.asList(
                    new Notification(0, false, "📢 Storing gemeld op machine M1-3096", LocalDateTime.now().minusHours(1)),
                    new Notification(0, false, "🛠 Onderhoud ingepland op 2025-06-02", LocalDateTime.now().minusDays(1)),
                    new Notification(0, true, "✅ Onderhoud succesvol afgerond", LocalDateTime.now().minusDays(2)),
                    new Notification(0, false, "! Productiefout gemeld bij Line 4", LocalDateTime.now().minusMinutes(30)),
                    new Notification(0, true, "ℹ️ Nieuwe update beschikbaar voor machinegegevens",
                            LocalDateTime.now().minusDays(5)));

            // Persist notifications
            notifications.forEach(entityManager::persist);

            // Create site addresses
            List<Address> siteAddresses = IntStream.range(0, 14).mapToObj(
                    i -> new Address("SiteStraat " + (i + 1), (i + 1) * 10, 1000 + (i * 100), "SiteStad " + (i + 1)))
                    .collect(Collectors.toList());

            // Create sites
            List<Site> sites = IntStream.range(0, 14).mapToObj(
                    i -> new Site("Site" + i, u10, i % 2 == 0 ? Status.ACTIEF : Status.INACTIEF, siteAddresses.get(i)))
                    .collect(Collectors.toList());

            sites.forEach(entityManager::persist);
            entityManager.flush(); 
            
            for (int siteIndex = 0; siteIndex < sites.size(); siteIndex++) {
                Site site = sites.get(siteIndex);
                
                int numMachines = Math.min(5, Math.max(3, 10 - siteIndex));
                
                for (int j = 0; j < numMachines; j++) {
                    User technician = j % 4 == 0 ? u1 : (j % 4 == 1 ? u4 : (j % 4 == 2 ? u7 : u8));
                    
                    MachineStatus machineStatus = j % 3 == 0 ? MachineStatus.DRAAIT : 
                                               (j % 3 == 1 ? MachineStatus.MANUEEL_GESTOPT : 
                                                            MachineStatus.AUTOMATISCH_GESTOPT);
                                                            
                    ProductionStatus productionStatus = j % 3 == 0 ? ProductionStatus.GEZOND : 
                                                     (j % 3 == 1 ? ProductionStatus.FALEND : 
                                                                  ProductionStatus.NOOD_ONDERHOUD);
                    
                    Machine machine = new Machine(
                        site,
                        technician,
                        String.format("M%d-%04d", siteIndex + 1, j + 1000),
                        String.format("Line %d", j + 1),
                        String.format("Product %c", 'A' + (j % 26)),
                        machineStatus,
                        productionStatus,
                        LocalDate.now().plusDays(30 + j)
                    );
                    
                    if (j % 2 == 0) {
                        machine.setLastMaintenance(LocalDate.now().minusDays(j * 5));
                    }
                    
                    entityManager.persist(machine);
                    
                    int maintenanceCount = j % 3 + 1; 
                    
                    for (int k = 0; k < maintenanceCount; k++) {
                        Maintenance maintenance = new Maintenance(
                            LocalDate.now().plusDays(k * 7),
                            LocalDateTime.now().plusDays(k * 7),
                            LocalDateTime.now().plusDays(k * 7).plusHours(k + 2),
                            technician,
                            String.format("Scheduled maintenance #%d", k + 1),
                            String.format("Regular maintenance for machine %s", machine.getCode()),
                            k == 0 ? MaintenanceStatus.IN_UITVOERING : 
                                   (k == 1 ? MaintenanceStatus.VOLTOOID : 
                                            MaintenanceStatus.INGEPLAND),
                            machine
                        );
                        
                        entityManager.persist(maintenance);
                        
                        if (k == 1) {
                            Report report = new Report(
                                maintenance, 
                                technician,
                                LocalDate.now().minusDays(7),
                                LocalTime.of(9, 0),
                                LocalDate.now().minusDays(7),
                                LocalTime.of(13, 30),
                                "Routine maintenance completed",
                                "All systems functioning normally after maintenance",
                                site
                            );
                            
                            entityManager.persist(report);
                        }
                    }
                }
                
                entityManager.flush();
            }
            
            entityManager.getTransaction().commit();
            System.out.println("Test data created successfully.");
            
        } catch (Exception e) {
            System.err.println("Error during transaction: " + e.getMessage());
            e.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        } finally {
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}