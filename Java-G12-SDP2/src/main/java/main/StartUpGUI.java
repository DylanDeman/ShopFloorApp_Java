package main;

import java.time.LocalDate;

import domain.Address;
import domain.User;
import domain.site.Site;
import gui.ChoicePane;
import jakarta.persistence.EntityManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.JPAUtil;
import util.Role;
import util.Status;

public class StartUpGUI extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		ChoicePane pane = new ChoicePane(primaryStage);

		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon-32x32.png")));

		Scene scene = new Scene(pane, 600, 200);
		primaryStage.setTitle("Kies je paneel");
		primaryStage.setScene(scene);
		primaryStage.show();

		User u1 = new User("Jan", "Janssen", "jan@email.com", "0412345678", "password", LocalDate.of(1990, 1, 1),
				new Address("Straat 1", 10, 1001, "Stad"), Status.INACTIEF, Role.TECHNIEKER);

		User u2 = new User("Piet", "Pietersen", "piet@email.com", "0423456789", "password", LocalDate.of(1985, 5, 15),
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

		Site s1 = new Site("A", u10, Status.ACTIEF);

		EntityManager entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();

		try
		{
			entityManager.getTransaction().begin();

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
			entityManager.persist(s1);

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

	public static void main(String[] args)
	{
		launch(args);
	}
}
