package com.gzmelife.app.tools;

public class StringUtil {
public static String byeArray2HexStr(byte[] b){
		String a = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}

			a = a + hex;
		}

		return a;
}
}
