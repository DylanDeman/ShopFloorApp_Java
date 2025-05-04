package domain.maintenance;

import java.io.Serializable;

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

	@ManyToOne
	@JoinColumn(name = "maintenance_id")
	@Getter
	@Setter
	private Maintenance maintenance;

	public FileInfo()
	{
	}

	public FileInfo(String name, String type, byte[] content, Maintenance maintenance)
	{
		this.name = name;
		this.type = type;
		this.content = content;
		this.maintenance = maintenance;
	}

	@Override
	public String toString()
	{
		return "FileInfo{" + "name='" + name + '\'' + ", type='" + type + '\'' + '}';
	}
}