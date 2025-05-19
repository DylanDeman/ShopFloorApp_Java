package domain.maintenance;

import java.io.Serializable;
import java.time.LocalDateTime;

import domain.Maintenance;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_files")
public class FileInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private int id;

	@Getter
	@Setter
	public String name;

	@Getter
	@Setter
	public String type;

	@Lob
	@Getter
	@Setter
	private byte[] content;

	@Getter
	@Setter
	private LocalDateTime uploadDate;

	@Getter
	@Setter
	private long size;

	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	@Getter
	@Setter
	private Maintenance maintenance;

	public FileInfo()
	{
		this.uploadDate = LocalDateTime.now();
	}

	public FileInfo(String name, String type, byte[] content, Maintenance maintenance)
	{
		this.name = name;
		this.type = type;
		this.content = content;
		this.maintenance = maintenance;
		this.uploadDate = LocalDateTime.now();
		this.size = content != null ? content.length : 0;
	}

	@Override
	public String toString()
	{
		return "FileInfo{" + "name='" + name + '\'' + ", type='" + type + '\'' + '}';
	}
}