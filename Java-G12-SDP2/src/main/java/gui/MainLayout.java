package gui;

import domain.maintenance.MaintenanceDTO;
import gui.customComponents.Navbar;
import gui.login.LoginPane;
import gui.machine.MachinesListComponent;
import gui.maintenance.MaintenanceDetailView;
import gui.maintenance.MaintenanceListComponent;
import gui.notification.NotificationListComponent;
import gui.report.AddReportForm;
import gui.site.SiteDetailsComponent;
import gui.site.SitesListComponent;
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
	@Getter
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
		applySceneStyles();

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

	private void applySceneStyles()
	{
		mainScene.getStylesheets().add(getClass().getResource("/css/navbar.css").toExternalForm());
	}

	public void showLoginScreen()
	{
		LoginPane loginPane = new LoginPane(this);
		setContent(loginPane, false, false);
	}

	public void showHomeScreen()
	{
		ChoicePane choicePane = new ChoicePane(this);
		setContent(choicePane, true, true);
	}

	public void showUserManagementScreen()
	{
		UserManagementPane userManagement = new UserManagementPane(this);
		setContent(userManagement, true, false);
	}

	public void showSiteList()
	{
		SitesListComponent siteList = new SitesListComponent(this);
		setContent(siteList, true, false);
	}

	public void showMachineScreen()
	{
		MachinesListComponent machineList = new MachinesListComponent(this);
		setContent(machineList, true, false);
	}

	public void showMaintenanceList()
	{
		MaintenanceListComponent maintenanceList = new MaintenanceListComponent(this);
		setContent(maintenanceList, true, false);
	}

	public void showMaintenanceDetails(MaintenanceDTO maintenance)
	{
		MaintenanceDetailView detailView = new MaintenanceDetailView(this, maintenance);
		setContent(detailView, true, false);
	}

	public void showAddReport(MaintenanceDTO maintenance)
	{
		AddReportForm addReport = new AddReportForm(this, maintenance);
		setContent(addReport, true, false);
	}

	public void showNotificationList()
	{
		NotificationListComponent notificationList = new NotificationListComponent(this);
		setContent(notificationList, true, false);
	}

	public void showSitesList()
	{
		SitesListComponent sitesListComponent = new SitesListComponent(this);
		setContent(sitesListComponent, true, false);
	}

	public void showSiteDetails(int siteId)
	{
		SiteDetailsComponent siteDetailsComponent = new SiteDetailsComponent(this, siteId);
		setContent(siteDetailsComponent, true, false);
	}

	public void setContent(Parent content, boolean showNavbar, boolean isHomeScreen)
	{
		VBox contentContainer = new VBox(CONTENT_SPACING);
		contentContainer.setPadding(CONTENT_PADDING);
		contentContainer.getChildren().add(content);

		rootLayout.setCenter(contentContainer);
		rootLayout.setTop(showNavbar ? new Navbar(this, isHomeScreen) : null);
	}

}