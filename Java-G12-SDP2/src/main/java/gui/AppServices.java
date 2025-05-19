package gui;

import domain.MachineController;
import domain.MaintenanceController;
import domain.NotificationController;
import domain.ReportController;
import domain.SiteController;
import domain.UserController;
import domain.maintenance.FileInfoController;
import lombok.Getter;

@Getter
public class AppServices {
	private static AppServices instance;

	private final UserController userController;
	private final SiteController siteController;
	private final MachineController machineController;
	private final MaintenanceController maintenanceController;
	private final FileInfoController fileInfoController;
	private final ReportController reportController;
	private final NotificationController notificationController;

	private AppServices() {
		this.siteController = new SiteController();
		this.machineController = new MachineController();
		this.maintenanceController = new MaintenanceController();
		this.fileInfoController = new FileInfoController();
		this.reportController = new ReportController();
		this.userController = new UserController();
		this.notificationController = new NotificationController();
	}

	public static synchronized AppServices getInstance() {
		if (instance == null) {
			instance = new AppServices();
		}
		return instance;
	}
}