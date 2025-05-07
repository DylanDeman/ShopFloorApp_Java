package domain.maintenance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import repository.GenericDao;
import repository.GenericDaoJpa;

public class FileInfoController
{
	private final GenericDao<FileInfo> fileInfoDao;

	public FileInfoController()
	{
		this.fileInfoDao = new GenericDaoJpa<>(FileInfo.class);
	}

	protected GenericDao<FileInfo> getFileInfoDao() {
		return fileInfoDao;
	}

	public List<FileInfo> getFilesForMaintenance(int maintenanceId)
	{
		return fileInfoDao.findAll().stream()
				.filter(file -> file.getMaintenance() != null && file.getMaintenance().getId() == maintenanceId)
				.collect(Collectors.toList());
	}

	public void saveFile(FileInfo fileInfo)
	{
		fileInfoDao.startTransaction();
		fileInfoDao.insert(fileInfo);
		fileInfoDao.commitTransaction();
	}

	public void deleteFile(FileInfo fileInfo)
	{
		fileInfoDao.startTransaction();
		fileInfoDao.delete(fileInfo);
		fileInfoDao.commitTransaction();
	}

	public byte[] getFileContent(FileInfo fileInfo)
	{
		return fileInfo.getContent();
	}

	public void saveFileContent(File file, FileInfo fileInfo) throws IOException
	{
		try (FileInputStream fis = new FileInputStream(file))
		{
			byte[] content = fis.readAllBytes();
			fileInfo.setContent(content);
			fileInfo.setSize(content.length);
			fileInfo.setUploadDate(java.time.LocalDateTime.now());
			saveFile(fileInfo);
		}
	}

	public void saveFileContent(byte[] content, FileInfo fileInfo)
	{
		fileInfo.setContent(content);
		fileInfo.setSize(content.length);
		fileInfo.setUploadDate(java.time.LocalDateTime.now());
		saveFile(fileInfo);
	}
}