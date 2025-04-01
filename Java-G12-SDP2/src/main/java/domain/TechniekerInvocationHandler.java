package domain;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TechniekerInvocationHandler implements InvocationHandler {

	private User user;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException {
		try {
			String methodName = method.getName();
			
			// Hier moeten de acties komen die de technieker kan uitvoeren 
			// met de juiste exceptie wanneer hij niet de juiste rol heeft:
//			if (methodName.startsWith("get")) {
//                return method.invoke(person, args);
//            }
//			if (methodName.equals("setHotOrNotRating")) {
//                throw new IllegalAccessException();
//            }
			method.invoke(user, args);
		} 
		catch (InvocationTargetException  e) {
			e.printStackTrace(); // TODO hier een mooie exceptie teruggeven
		}
		return null;
	}
}
