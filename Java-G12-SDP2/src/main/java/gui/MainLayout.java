package gui;

import gui.customComponents.Navbar;
import gui.login.LoginPane;
import gui.machine.MachinesListComponent;
import gui.maintenance.MaintenanceListComponent;
import gui.sitesList.SitesListComponent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

public class MainLayout
{
	private final BorderPane rootLayout;
	private final Stage primaryStage;
	private final Scene mainScene;
	@Getter
	private final AppServices services;

	private static final Insets CONTENT_PADDING = new Insets(50, 80, 0, 80);
	private static final int CONTENT_SPACING = 20;

	public MainLayout(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
		this.services = new AppServices();

		this.rootLayout = new BorderPane();
		applyRootStyles();

		this.mainScene = new Scene(rootLayout);
		// applySceneStyles();

		primaryStage.setScene(mainScene);
		primaryStage.setMaximized(true);

		showLoginScreen();
	}

	private void applyRootStyles()
	{
		BackgroundImage backgroundImage = new BackgroundImage(
				new Image(getClass().getResourceAsStream("/images/background.png")), BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
				new BackgroundSize(100, 100, true, true, true, true));
		rootLayout.setBackground(new Background(backgroundImage));
	}

//	private void applySceneStyles()
//	{
//		mainScene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
//	}

	public void showLoginScreen()
	{
		LoginPane loginPane = new LoginPane(this);
		setContent(loginPane, false);
	}

	public void showHomeScreen()
	{
		ChoicePane choicePane = new ChoicePane(this);
		setContent(choicePane, true);
	}

	public void showUserManagementScreen()
	{
		UserManagementPane userManagement = new UserManagementPane(this);
		setContent(userManagement, true);
	}

	public void showSiteList()
	{
		SitesListComponent siteList = new SitesListComponent(this);
		setContent(siteList, true);
	}

	public void showMachineScreen()
	{
		MachinesListComponent machineList = new MachinesListComponent(this);
		setContent(machineList, true);
	}

	public void showMaintenanceList()
	{
		MaintenanceListComponent maintenanceList = new MaintenanceListComponent(this);
		setContent(maintenanceList, true);
	}

	public void setContent(Parent content, boolean showNavbar)
	{
		VBox contentContainer = new VBox(CONTENT_SPACING);
		contentContainer.setPadding(CONTENT_PADDING);
		contentContainer.getChildren().add(content);

		rootLayout.setCenter(contentContainer);
		rootLayout.setTop(showNavbar ? new Navbar(this) : null);
	}

}