package com.handwin.util;

import java.util.UUID;

public class CUUID {
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}