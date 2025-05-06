package gui.customComponents;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.user.User;
import gui.MainLayout;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import util.AuthenticationUtil;

public class Navbar extends HBox
{

	private Label userName;
	private Label userRole;

	public Navbar(MainLayout mainLayout, boolean isHomeScreen)
	{
		this.getStyleClass().add("navbar");
		this.setAlignment(Pos.CENTER);

		ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/delaware_navbaricon.png")));
		logo.setFitHeight(40);
		logo.setPreserveRatio(true);
		Button logoBtn = new Button();
		logoBtn.setGraphic(logo);
		logoBtn.getStyleClass().add("logo-btn");
		logoBtn.setOnAction(e -> mainLayout.showHomeScreen());

		this.userName = new Label();
		this.userRole = new Label();
		fillUserData();
		VBox userInfo = new VBox(2, userName, userRole);
		userInfo.getStyleClass().add("user-info");


		
		Button notificationBtn = new Button();
		FontIcon bellIcon = new FontIcon("fas-bell");
		bellIcon.setIconSize(20);
		notificationBtn.setGraphic(bellIcon);
		notificationBtn.getStyleClass().add("icon-btn");

		// Create the dropdown (ContextMenu)
		ContextMenu notificationMenu = new ContextMenu();
		notificationMenu.setMinSize(500, 500);
		

		// Example dummy notifications
		MenuItem n1 = new MenuItem("Nieuwe storing gemeld");
		MenuItem n2 = new MenuItem("Machine onderhoud gepland");
		MenuItem seeAll = new MenuItem("Zie alle notificaties");
		notificationMenu.getStyleClass().add("context-menu");
		n1.getStyleClass().add("menu-item");
		n2.getStyleClass().add("menu-item");
		seeAll.getStyleClass().add("menu-item");

		seeAll.setOnAction(e -> mainLayout.showNotificationList());

		seeAll.setStyle("-fx-font-weight: bold;");
		notificationMenu.getItems().addAll(n1, n2, new SeparatorMenuItem(), seeAll);

		notificationBtn.setOnAction(e -> {
		    if (!notificationMenu.isShowing()) {
		        notificationMenu.show(notificationBtn, Side.BOTTOM, 0, 0);
		    } else {
		        notificationMenu.hide();
		    }
		});

		Button logoutBtn = new Button("Uitloggen");
		logoutBtn.getStyleClass().add("logout-btn");
		logoutBtn.setOnAction(e -> mainLayout.showLoginScreen());

		HBox navLinks = new HBox(20);
		navLinks.getStyleClass().add("nav-links-container");
		navLinks.setAlignment(Pos.CENTER);

		if (!isHomeScreen)
		{
			Button sitesBtn = new Button("Sites");
			sitesBtn.getStyleClass().add("nav-link");
			sitesBtn.setOnAction(e -> mainLayout.showSiteList());

			Button machinesBtn = new Button("Machines");
			machinesBtn.getStyleClass().add("nav-link");
			machinesBtn.setOnAction(e -> mainLayout.showMachineScreen());

			Button userBtn = new Button("Gebruikers");
			userBtn.getStyleClass().add("nav-link");
			userBtn.setOnAction(e -> mainLayout.showUserManagementScreen());

			Button maintenanceBtn = new Button("Onderhoud");
			maintenanceBtn.getStyleClass().add("nav-link");
			maintenanceBtn.setOnAction(e -> mainLayout.showMaintenanceList());

			navLinks.getChildren().addAll(sitesBtn, machinesBtn, userBtn, maintenanceBtn);
		}

		Region leftSpacer = new Region();
		Region rightSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		HBox rightElements = new HBox(15, userInfo, notificationBtn, logoutBtn);
		rightElements.setAlignment(Pos.CENTER_RIGHT);

		this.getChildren().addAll(logoBtn, leftSpacer, navLinks, rightSpacer, rightElements);
	}

	private void fillUserData()
	{
		User user = AuthenticationUtil.getAuthenticatedUser();
		userName.setText(user.getFullName());
		String roleFormatted = capitalize(user.getRole().toString().toLowerCase());
		userRole.setText(roleFormatted);
	}

	private String capitalize(String text)
	{
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

}
