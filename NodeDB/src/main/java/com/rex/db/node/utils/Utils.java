package com.rex.db.node.utils;

import android.text.TextUtils;
import android.util.Base64;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class Utils {

	public static String generateKey(int n) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}

		return sb.toString();
	}

	public static int getIndexOf(List<String> arrayList, String query) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(query)) {
				return i;
			}
		}
		return -1;
	}

	public static Map<String, Object> toMap(JSONObject jsonobj) throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<String> keys = jsonobj.keys();
		while (keys.hasNext()) {
			String key = keys.next();
			Object value = jsonobj.get(key);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			map.put(key, value);
		}
		return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < array.length(); i++) {
			Object value = array.get(i);
			if (value instanceof JSONArray) {
				value = toList((JSONArray) value);
			} else if (value instanceof JSONObject) {
				value = toMap((JSONObject) value);
			}
			list.add(value);
		}
		return list;

	}

	public static String encrypt(String key, String value) {
		try {
			SecretKey sckey = generateKey(key);
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, sckey);
			byte[] encVal = c.doFinal(value.getBytes());
			return Base64.encodeToString(encVal, Base64.DEFAULT);
		} catch (Exception ex) {
			return value;
		}
	}

	public static String decrypt(String key, String value) {
		try {
			SecretKeySpec sckey = (SecretKeySpec) generateKey("key");
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, sckey);
			byte[] decode = Base64.decode(value, Base64.DEFAULT);
			byte[] decval = c.doFinal(decode);
			return new String(decval);
		} catch (Exception ex) {
			return value;
		}
	}

	protected static SecretKey generateKey(String pwd) throws Exception {

		final MessageDigest digest = MessageDigest.getInstance("SHA-256");

		byte[] b = pwd.getBytes("UTF-8");

		digest.update(b, 0, b.length);

		byte[] key = digest.digest();

		SecretKeySpec sec = new SecretKeySpec(key, "AES");

		return sec;

	}
	
	public static boolean isEmpty(String str) {
		if (TextUtils.isEmpty(str)) {
			return true;
		}
		return false;
	}

}