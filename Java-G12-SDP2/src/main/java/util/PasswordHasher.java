package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

	public static String hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public static boolean verify(String rawPassword, String hashedPassword) {
		return BCrypt.checkpw(rawPassword, hashedPassword);
	}
}
