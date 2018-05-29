package com.refine.emoji.util;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Refine on 2018/2/1.
 */

public class LogUtils {

    private static final String CLASS_NAME = LogUtils.class.getName();

    /**
     * 默认Tag
     */
    private static String mTag = "refine";

    private static boolean isDebug = true;

    private static final int JSON_INDENT = 2;

    public static void init(String tag, boolean isDebug) {
        if (!TextUtils.isEmpty(tag)) {
            mTag = tag;
        }
        LogUtils.isDebug = isDebug;
    }

    private static String getLocation() {
        final StackTraceElement[] traces = Thread.currentThread()
                .getStackTrace();
        boolean found = false;

        for (StackTraceElement trace : traces) {
            try {
                if (found) {
                    if (!trace.getClassName().startsWith(CLASS_NAME)) {
                        Class<?> clazz = Class.forName(trace.getClassName());

                        String format = "[%s.%s%s]:\n";
                        String traceStr = trace.toString();
                        return String.format(format,
                                getClassName(clazz),
                                trace.getMethodName(),
                                traceStr.substring(traceStr.lastIndexOf('('), traceStr.length()));
                    }
                } else if (trace.getClassName().startsWith(CLASS_NAME)) {
                    found = true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }

        return "[]:\n";
    }

    private static String getClassName(Class<?> clazz) {
        if (clazz != null) {
            if (!TextUtils.isEmpty(clazz.getSimpleName())) {
                return clazz.getSimpleName();
            }

            return getClassName(clazz.getEnclosingClass());
        }

        return "";
    }

    public static void v(String msg) {
        if (isDebug) {
            Log.v(mTag, getLocation() + msg);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug) {
            Log.v(TextUtils.isEmpty(tag) ? mTag : tag, getLocation() + message);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            Log.d(mTag, getLocation() + msg);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(TextUtils.isEmpty(tag) ? mTag : tag, getLocation() + message);
        }
    }

    public static void i(String message) {
        if (isDebug) {
            Log.i(mTag, getLocation() + message);
        }
    }

    public static void i(String tag, String message) {
        if (isDebug) {
            Log.i(TextUtils.isEmpty(tag) ? mTag : tag, getLocation() + message);
        }
    }

    public static void w(String message) {
        if (isDebug) {
            Log.w(mTag, getLocation() + message);
        }
    }

    public static void w(String tag, String message) {
        if (isDebug) {
            Log.w(TextUtils.isEmpty(tag) ? mTag : tag, getLocation() + message);
        }
    }

    public static void e(String message) {
        if (isDebug) {
            Log.e(mTag, getLocation() + message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug) {
            Log.e(TextUtils.isEmpty(tag) ? mTag : tag, getLocation() + message);
        }
    }

    public static void jsonWithTag(String tag, String json) {
        jsonWithTag(tag, "", json);
    }

    public static void jsonWithTag(String tag, String message, String json) {
        if (TextUtils.isEmpty(json)) {
            d(tag, "Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                json = jsonObject.toString(JSON_INDENT);
                jsonFormat(tag, message, json);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                json = jsonArray.toString(JSON_INDENT);
                jsonFormat(tag, message, json);
                return;
            }
            e(tag, "Invalid Json" + "\n" + json);
        } catch (JSONException e) {
            e(tag, "Invalid Json");
        }
    }

    public static void json(String message, String json) {
        jsonWithTag("", message, json);
    }

    public static void json(String json) {
        jsonWithTag("", "", json);
    }

    private static void jsonFormat(String tag, String message, String json) {
        d(tag, message);
        String[] lines = json.split(System.getProperty("line.separator"));
        for (String line : lines) {
            Log.d(TextUtils.isEmpty(tag) ? mTag : tag, line);
        }
    }
}