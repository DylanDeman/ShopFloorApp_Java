package gui;

import domain.machine.MachineDTO;
import domain.maintenance.MaintenanceDTO;
import domain.notifications.NotificationDTO;
import gui.customComponents.Navbar;
import gui.login.LoginPane;
import gui.machine.MachinesListComponent;
import gui.maintenance.MaintenanceDetailView;
import gui.maintenance.MaintenanceListComponent;
import gui.maintenance.MaintenancePlanningForm;
import gui.notification.NotificationDetailComponent;
import gui.notification.NotificationListComponent;
import gui.report.AddReportForm;
import gui.site.SiteDetailsComponent;
import gui.site.SitesListComponent;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import util.AuthenticationUtil;
import util.CurrentPage;
import util.Role;

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
		this.services = AppServices.getInstance();

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
		setContent(loginPane, false, false, CurrentPage.NONE);
	}

	public void showHomeScreen()
	{
		ChoicePane choicePane = new ChoicePane(this);
		setContent(choicePane, true, true, CurrentPage.HOME);
	}

	public void showUserManagementScreen()
	{
		if (!AuthenticationUtil.hasRole(Role.ADMIN))
		{
			showNotAllowedAlert();
		} else
		{
			UserManagementPane userManagement = new UserManagementPane(this);
			setContent(userManagement, true, false, CurrentPage.USERS);
		}
	}

	public void showSitesList()
	{
		SitesListComponent sitesListComponent = new SitesListComponent(this);
		setContent(sitesListComponent, true, false, CurrentPage.SITES);
	}

	public void showSiteDetails(int siteId)
	{
		SiteDetailsComponent siteDetailsComponent = new SiteDetailsComponent(this, siteId);
		setContent(siteDetailsComponent, true, false, CurrentPage.NONE);
	}

	public void showMachineScreen()
	{
		MachinesListComponent machineList = new MachinesListComponent(this);
		setContent(machineList, true, false, CurrentPage.MACHINES);
	}

	public void showMaintenanceList()
	{
		MaintenanceListComponent maintenanceList = new MaintenanceListComponent(this);
		setContent(maintenanceList, true, false, CurrentPage.MAINTENANCE);
	}

	public void showMaintenanceList(MachineDTO machine)
	{
		MaintenanceListComponent maintenanceList = new MaintenanceListComponent(this, machine);
		setContent(maintenanceList, true, false, CurrentPage.MAINTENANCE);
	}

	public void showMaintenanceDetails(MaintenanceDTO maintenance)
	{
		if (!AuthenticationUtil.hasRole(Role.VERANTWOORDELIJKE) && !AuthenticationUtil.hasRole(Role.ADMIN))
		{
			showNotAllowedAlert();
		} else
		{
			MaintenanceDetailView detailView = new MaintenanceDetailView(this, maintenance);
			setContent(detailView, true, false, CurrentPage.NONE);
		}
	}

	public void showNotificationDetails(NotificationDTO notification)
	{
		if (!AuthenticationUtil.hasRole(Role.VERANTWOORDELIJKE) && !AuthenticationUtil.hasRole(Role.ADMIN))
		{
			showNotAllowedAlert();
		} else
		{
			NotificationDetailComponent detail = new NotificationDetailComponent(this, notification);
			setContent(detail, true, false, CurrentPage.NONE);
		}
	}

	public void showAddReport(MaintenanceDTO maintenance)
	{
		if (!AuthenticationUtil.hasRole(Role.TECHNIEKER) && !AuthenticationUtil.hasRole(Role.ADMIN))
		{
			showNotAllowedAlert();
		} else
		{
			AddReportForm addReport = new AddReportForm(this, maintenance);
			setContent(addReport, true, false, CurrentPage.NONE);
		}
	}

	public void showNotificationList()
	{
		NotificationListComponent notificationList = new NotificationListComponent(this);
		setContent(notificationList, true, false, CurrentPage.NONE);
	}

	public void showMaintenancePlanning(MachineDTO machineDTO)
	{
		if (!AuthenticationUtil.hasRole(Role.VERANTWOORDELIJKE) && !AuthenticationUtil.hasRole(Role.ADMIN))
		{
			showNotAllowedAlert();
		} else
		{
			MaintenancePlanningForm maintenancePlanningForm = new MaintenancePlanningForm(this, machineDTO);
			setContent(maintenancePlanningForm, true, false, CurrentPage.NONE);
		}
	}

	public void setContent(Parent content, boolean showNavbar, boolean isHomeScreen, CurrentPage activePage)
	{
		VBox contentContainer = new VBox(CONTENT_SPACING);
		contentContainer.setPadding(CONTENT_PADDING);
		contentContainer.getChildren().add(content);

		rootLayout.setCenter(contentContainer);
		rootLayout.setTop(showNavbar ? new Navbar(this, isHomeScreen, activePage) : null);
	}

	public void showNotAllowedAlert()
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Geen toegang!");
		alert.setHeaderText("U heeft geen toegang om dit deel van de applicatie te bekijken!");
		alert.setContentText(
				"Neem contact op met een administrator of verantwoordelijke als u dit deel van de applicatie wel zou moeten kunnen bekijken.");
		alert.showAndWait();
	}
}