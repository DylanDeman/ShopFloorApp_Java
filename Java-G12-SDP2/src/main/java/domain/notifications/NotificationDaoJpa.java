package domain.notifications;

import repository.GenericDaoJpa;

public class NotificationDaoJpa extends GenericDaoJpa<Notification> implements NotificationDao{

	public NotificationDaoJpa() {
		super(Notification.class);
	}

}
