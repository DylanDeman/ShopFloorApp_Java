package domain;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import interfaces.IUserService;
import lombok.AllArgsConstructor;
import utils.Roles;

@AllArgsConstructor
public class UserInvoker implements InvocationHandler {
	private IUserService userService;
	private User user;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException {
		try {
			if (method.getName().equals("createUser")) {
				// Creeeren van een user mag enkel door Admin gebeuren
				if (user.getRole() == Roles.ADMIN) {
					return method.invoke(userService, args);
					// } if() { // Nog meer restricties hieronder toevoegen:
				} else {
					throw new IllegalAccessException("Toegang geweigerd! U heeft geen rechten voor deze actie!");
				}
			}
			return method.invoke(userService, args);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
