package domain;

import interfaces.ICommand;
import interfaces.IUserService;

public class CommandFactory {
	private IUserService service;

	public ICommand createCommand(ICommand command,String userName, String[] args) {
		// hier een switch met de commandos voor User-gerelateerde dingen:
		return null;
	}
}
