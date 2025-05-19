package domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@NamedQueries({
	@NamedQuery(name = "Notification.getAllRead", query ="""
			SELECT n FROM Notification n
			WHERE n.isRead = 1
			"""),
	@NamedQuery(name = "Notification.getAllUnread", query ="""
	SELECT n FROM Notification n
	WHERE n.isRead = 0
	""")
})
public class Notification implements Serializable{

	public Notification(boolean isRead, String message, LocalDateTime time) {
		this.isRead = isRead;
		this.message = message;
		this.time = time;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private boolean isRead;
	private String message;
	private LocalDateTime time;
}
