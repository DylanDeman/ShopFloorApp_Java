package domain.notifications;

import java.util.List;

public class NotificationController {

	
	private NotificationDao notificationRepo;
	private List<Notification> notificationList;
	
	
	public NotificationController() {
		notificationRepo = new NotificationDaoJpa();
	}
}
