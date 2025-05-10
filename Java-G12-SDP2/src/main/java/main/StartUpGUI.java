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
	public void start(Stage primaryStage)
	{
		MainLayout mainLayout = new MainLayout(primaryStage);

		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon-32x32.png")));
		primaryStage.setTitle("Shopfloor application");

		primaryStage.show();

		String gehashteWW = PasswordHasher.hash("password");

		User u1 = new User("Jan", "Janssen", "jan@email.com", "0412345678", gehashteWW, LocalDate.of(1990, 1, 1),
				new Address("Straat 1", 10, 1001, "Stad"), Status.INACTIEF, Role.TECHNIEKER);

		User u2 = new User("Piet", "Pietersen", "piet@email.com", "0423456789", gehashteWW, LocalDate.of(1985, 5, 15),
				new Address("Straat 2", 20, 2000, "Stad"), Status.INACTIEF, Role.ADMIN);

		User u3 = new User("Anna", "Dekker", "anna@email.com", "0434567890", "password", LocalDate.of(1995, 3, 12),
				new Address("Straat 3", 30, 3000, "Stad"), Status.ACTIEF, Role.VERANTWOORDELIJKE);

		User u4 = new User("Eva", "Smit", "eva@email.com", "0445678901", "password", LocalDate.of(1988, 7, 22),
				new Address("Straat 4", 40, 4000, "Stad"), Status.ACTIEF, Role.TECHNIEKER);

		User u5 = new User("Mark", "Visser", "mark@email.com", "0456789012", "password", LocalDate.of(1992, 11, 5),
				new Address("Straat 5", 50, 5000, "Stad"), Status.INACTIEF, Role.ADMIN);

		User u6 = new User("Sophie", "Koster", "sophie@email.com", "0467890123", "password", LocalDate.of(1993, 9, 18),
				new Address("Straat 6", 60, 6000, "Stad"), Status.ACTIEF, Role.MANAGER);

		User u7 = new User("Tom", "Hendriks", "tom@email.com", "0478901234", "password", LocalDate.of(1987, 12, 30),
				new Address("Straat 7", 70, 7000, "Stad"), Status.INACTIEF, Role.TECHNIEKER);

		User u8 = new User("Laura", "Bakker", "laura@email.com", "0489012345", "password", LocalDate.of(1994, 2, 25),
				new Address("Straat 8", 80, 8000, "Stad"), Status.ACTIEF, Role.VERANTWOORDELIJKE);

		User u9 = new User("Rob", "Jansen", "rob@email.com", "0490123456", "password", LocalDate.of(1986, 6, 10),
				new Address("Straat 9", 90, 9000, "Stad"), Status.ACTIEF, Role.ADMIN);

		User u10 = new User("Kim", "De Vries", "kim@email.com", "0412345678", "password", LocalDate.of(1991, 4, 20),
				new Address("Straat 10", 100, 9999, "Stad"), Status.INACTIEF, Role.MANAGER);

		List<Notification> notifications = Arrays.asList(
				new Notification(0, false, "📢 Storing gemeld op machine M1-3096", LocalDateTime.now().minusHours(1)),
				new Notification(0, false, "🛠 Onderhoud ingepland op 2025-06-02", LocalDateTime.now().minusDays(1)),
				new Notification(0, true, "✅ Onderhoud succesvol afgerond", LocalDateTime.now().minusDays(2)),
				new Notification(0, false, "⚠️ Productiefout gemeld bij Line 4", LocalDateTime.now().minusMinutes(30)),
				new Notification(0, true, "ℹ️ Nieuwe update beschikbaar voor machinegegevens",
						LocalDateTime.now().minusDays(5)));

		List<Address> siteAddresses = IntStream.range(0, 14).mapToObj(
				i -> new Address("SiteStraat " + (i + 1), (i + 1) * 10, 1000 + (i * 100), "SiteStad " + (i + 1)))
				.collect(Collectors.toList());

		List<Site> sites = IntStream.range(0, 14).mapToObj(
				i -> new Site("Site" + i, u10, i % 2 == 0 ? Status.ACTIEF : Status.INACTIEF, siteAddresses.get(i)))
				.collect(Collectors.toList());

		Site site1 = sites.get(0);

		MaintenanceController mc = new MaintenanceController();

		EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

		try
		{

			Machine m1 = new Machine(site1, u1, "M1-3096", "Line 1", "Product A", MachineStatus.DRAAIT,
					ProductionStatus.GEZOND, LocalDate.of(2025, 6, 2));
			m1.setLastMaintenance(LocalDate.of(2025, 5, 1));

			Machine m2 = new Machine(site1, u4, "M2-2359", "Line 4", "Product B", MachineStatus.MANUEEL_GESTOPT,
					ProductionStatus.FALEND, LocalDate.of(2025, 8, 15));

			List<Maintenance> maintenances = IntStream.range(0, 14)
					.mapToObj(i -> new Maintenance(LocalDate.now().plusDays(i), LocalDateTime.now().plusDays(i),
							LocalDateTime.now().plusDays(i).plusHours(i), u7, "reason", String.format("reason %d", i),
							MaintenanceStatus.IN_UITVOERING, m1))
					.collect(Collectors.toList());

			Report r1 = new Report(mc.getMaintenance(maintenances.getFirst().getId()), u7, LocalDate.now(),
					LocalTime.now(), LocalDate.now().plusDays(1), LocalTime.now().plusHours(6), "Test reason", "",
					site1);

			entityManager.getTransaction().begin();

			entityManager.persist(m1);
			entityManager.persist(m2);

			maintenances = IntStream.range(0, 14)
					.mapToObj(i -> new Maintenance(LocalDate.now().plusDays(i), LocalDateTime.now().plusDays(i),
							LocalDateTime.now().plusDays(i).plusHours(i), u7, "reason", String.format("reason %d", i),
							MaintenanceStatus.IN_UITVOERING, i % 2 == 0 ? m1 : m2))
					.collect(Collectors.toList());

			entityManager.persist(m1);
			entityManager.persist(m2);

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
			sites.forEach(site -> entityManager.persist(site));
			notifications.forEach(n -> entityManager.persist(n));

			maintenances.forEach(maintenance -> entityManager.persist(maintenance));

			entityManager.getTransaction().commit();
		} catch (Exception e)
		{
			System.err.println("Error during transaction: " + e.getMessage());
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().rollback();
			}
		} finally
		{
			if (entityManager != null && entityManager.isOpen())
			{
				entityManager.close();
			}
		}
	}
}