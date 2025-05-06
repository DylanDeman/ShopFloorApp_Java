package domain.notifications;

import java.time.LocalDateTime;

public record NotificationDTO(int id, LocalDateTime time, String message, boolean read) {

}
