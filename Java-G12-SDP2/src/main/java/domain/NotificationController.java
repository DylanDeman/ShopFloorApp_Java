package domain;

import java.util.List;

import dto.NotificationDTO;
import repository.NotificationDao;
import repository.NotificationDaoJpa;

public class NotificationController {
	private NotificationDao notificationRepo;
	private List<Notification> notificationList;

	public NotificationController() {
		notificationRepo = new NotificationDaoJpa();
	}

	public List<NotificationDTO> getAllRead() {
		return notificationRepo.getAllRead().stream().map(this::toDTO).toList();

	}

	public List<NotificationDTO> getAllUnread() {
		return notificationRepo.getAllUnread().stream().map(this::toDTO).toList();
	}

	private NotificationDTO toDTO(Notification n) {
		return new NotificationDTO(n.getId(), n.getTime(), n.getMessage(), n.isRead());
	}

	public void markAsRead(int id) {
		notificationRepo.markAsRead(id);
	}

}
