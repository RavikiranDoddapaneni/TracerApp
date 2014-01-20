package com.tracer.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	public static SharedPreferences get(Context context) {
		return context.getSharedPreferences("QUIT_BUDDY_PREF", 0);
	}
}
