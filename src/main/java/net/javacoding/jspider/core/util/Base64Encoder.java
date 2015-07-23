package net.javacoding.jspider.core.util;

import org.apache.commons.codec.binary.Base64;

/**
 * This class is derived from the one found in the JavaWorld.com article
 * http://www.javaworld.com/javaworld/javatips/jw-javatip36-p2.html
 *
 * $Id: Base64Encoder.java,v 1.2 2003/02/11 17:27:04 vanrogu Exp $
 *
 */
public abstract class Base64Encoder {

	public final static String base64Encode(String strInput) {
		if (strInput == null) {
			return null;
		}

		byte byteData[] = new byte[strInput.length()];
		byteData = strInput.getBytes();
		return new String(base64Encode(byteData));
	}

	public final static byte[] base64Encode(byte[] byteData) {
		if (byteData == null) {
			return null;
		}
		return Base64.encodeBase64(byteData);
	}
}
