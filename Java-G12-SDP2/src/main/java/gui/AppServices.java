package gui;

import domain.machine.MachineController;
import domain.maintenance.FileInfoController;
import domain.maintenance.MaintenanceController;
import domain.notifications.NotificationController;
import domain.report.ReportController;
import domain.site.Site;
import domain.site.SiteController;
import domain.user.User;
import domain.user.UserController;
import lombok.Getter;
import repository.GenericDao;
import repository.GenericDaoJpa;
import repository.SiteRepository;
import repository.UserRepository;

@Getter
public class AppServices {
    // Static instance for singleton pattern
    private static AppServices instance;
    
    // All the existing fields
    private final UserRepository userRepo;
    private final UserController userController;
    private final SiteRepository siteRepo;
    private final SiteController siteController;
    private final MachineController machineController;
    private final MaintenanceController maintenanceController;
    private final FileInfoController fileInfoController;
    private final ReportController reportController;
    private final NotificationController notificationController;
    
    // Private constructor to prevent instantiation from outside
    private AppServices() {
        GenericDao<User> userDao = new GenericDaoJpa<>(User.class);
        this.userRepo = new UserRepository(userDao);
        GenericDao<Site> siteDao = new GenericDaoJpa<>(Site.class);
        this.siteRepo = new SiteRepository(siteDao);
        this.siteController = new SiteController();
        this.machineController = new MachineController();
        this.maintenanceController = new MaintenanceController();
        this.fileInfoController = new FileInfoController();
        this.reportController = new ReportController();
        this.userController = new UserController();
        this.notificationController = new NotificationController();
    }
    
    // Public static method to get the singleton instance
    public static synchronized AppServices getInstance() {
        if (instance == null) {
            instance = new AppServices();
        }
        return instance;
    }
}