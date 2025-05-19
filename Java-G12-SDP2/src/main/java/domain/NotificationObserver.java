package domain;

import java.time.LocalDateTime;

import interfaces.Observer;

public class NotificationObserver implements Observer {
	private NotificationDao notificationRepo;

	public NotificationObserver() {
		this.notificationRepo = new NotificationDaoJpa();
	}

	@Override
	public void update(String message) {
		Notification notification = new Notification(false, message, LocalDateTime.now());

		notificationRepo.startTransaction();
		notificationRepo.insert(notification);
		notificationRepo.commitTransaction();
	}
}
