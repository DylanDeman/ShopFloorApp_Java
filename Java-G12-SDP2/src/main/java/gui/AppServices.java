package gui;

import domain.machine.MachineController;
import domain.maintenance.FileInfoController;
import domain.maintenance.MaintenanceController;
import domain.notifications.NotificationController;
import domain.report.ReportController;
import domain.site.SiteController;
import domain.user.UserController;
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