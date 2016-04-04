package com.lodenrogue.socialnetwork.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Security {

	private Security() {

	}

	public static String hashPassword(final String password, final String salt, final int iterations, final int keyLength) {
		return "";
	}

	public static byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {

		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
			SecretKey key = skf.generateSecret(spec);
			byte[] res = key.getEncoded();
			return res;

		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
