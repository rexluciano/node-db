package com.rex.db.node.utils;

import java.util.ArrayList;

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

	public static int getIndexOf(ArrayList<Object> arrayList, String query) {
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i).equals(query)) {
				return i;
			}
		}
		return -1;
	}

}