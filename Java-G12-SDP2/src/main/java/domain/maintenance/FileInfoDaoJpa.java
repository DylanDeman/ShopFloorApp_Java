package domain.maintenance;

import repository.GenericDaoJpa;

public class FileInfoDaoJpa extends GenericDaoJpa<FileInfo> implements FileInfoDao
{
	public FileInfoDaoJpa()
	{
		super(FileInfo.class);
	}
}