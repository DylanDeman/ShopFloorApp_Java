package gui.customComponents;

import org.kordamp.ikonli.javafx.FontIcon;

import domain.user.User;
import gui.MainLayout;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
		notificationBtn.setOnAction(e -> System.out.println("notificatiepagina"));

		Button logoutBtn = new Button("Uitloggen");
		logoutBtn.getStyleClass().add("logout-btn");
		logoutBtn.setOnAction(e -> mainLayout.showLoginScreen());

		HBox navLinks = new HBox(20);
		if (!isHomeScreen)
		{
			Button sitesBtn = new Button("Sites");
			sitesBtn.getStyleClass().add("nav-link");
			sitesBtn.setOnAction(e -> mainLayout.showSiteList());

			Button maintenanceBtn = new Button("Onderhoud");
			maintenanceBtn.getStyleClass().add("nav-link");
			maintenanceBtn.setOnAction(e -> mainLayout.showMaintenanceList());

			navLinks.getChildren().addAll(sitesBtn, maintenanceBtn);
		}

		Region leftSpacer = new Region();
		Region rightSpacer = new Region();
		HBox.setHgrow(leftSpacer, Priority.ALWAYS);
		HBox.setHgrow(rightSpacer, Priority.ALWAYS);

		this.getChildren().addAll(logoBtn, leftSpacer, navLinks, rightSpacer, userInfo, notificationBtn, logoutBtn);
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