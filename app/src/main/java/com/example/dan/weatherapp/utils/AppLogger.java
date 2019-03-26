package com.example.dan.weatherapp.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dan.weatherapp.BuildConfig;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppLogger {

    private static final String TAG = AppLogger.class.getSimpleName();

    public static void info(@Nullable String tag, String message) {
        if (BuildConfig.DEBUG) {
            tag = StringUtils.isNotBlank(tag) ? tag : TAG;
            message = StringUtils.isNotBlank(message) ? message : "Nothing to log message is empty";
            if (message.length() > 4000) {
                Log.i(tag, message.substring(0, 4000));
                info(tag, message.substring(4000));
            } else {
                Log.i(tag, message);
            }
        }

    }


    public static void warn(@Nullable String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            tag = StringUtils.isNotBlank(tag) ? tag : TAG;
            message = StringUtils.isNotBlank(message) ? message : "Nothing to log message is empty";
            if (message.length() > 4000)
            {
                Log.w(tag, message.substring(0, 4000));
                warn(tag, message.substring(4000));
            }
            else
            {
                Log.w(tag, message);
            }
        }
    }

    public static void error(@Nullable String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            tag = StringUtils.isNotBlank(tag) ? tag : TAG;
            message = StringUtils.isNotBlank(message) ? message : "Nothing to log message is empty";
            if (message.length() > 4000)
            {
                Log.e(tag, message.substring(0, 4000));
                error(tag, message.substring(4000));
            }
            else
            {
                Log.e(tag, message);
            }
        }
    }

    public static void printJSON(@Nullable String tag, Object object)
    {
        if (object instanceof JSONObject)
        {
            JSONObject jsonObject = (JSONObject) object;
            printJSONObject(tag, jsonObject, false);
        }
        else if (object instanceof JSONArray)
        {
            JSONArray jsonArray = (JSONArray) object;
            printJSONArray(tag, jsonArray, false);
        }
        else if (object != null)
        {
            info(tag, object.toString());
        }
        else
        {
            error(tag, "Nothing to log");
        }
    }

    public static void printErrorJSON(@Nullable String tag, Object object)
    {
        if (object instanceof JSONObject)
        {
            JSONObject jsonObject = (JSONObject) object;
            printJSONObject(tag, jsonObject, true);
        }
        else if (object instanceof JSONArray)
        {
            JSONArray jsonArray = (JSONArray) object;
            printJSONArray(tag, jsonArray, true);
        }
        else if (object != null)
        {
            error(tag, object.toString());
        }
        else
        {
            error(tag, "Nothing to log");
        }
    }

    private static void printJSONObject(@Nullable String tag, JSONObject object, boolean error)
    {
        try
        {
            String text = object.toString(2);
            String[] temp = text.split("\n");
            if (error)
            {
                error(tag, "-----Start-----");
            }
            else
            {
                info(tag, "-----Start-----");
            }
            for (String s : temp)
            {
                if (error)
                {
                    error(tag, s);
                }
                else
                {
                    info(tag, s);
                }
            }

            if (error)
            {
                error(tag, "------End------");
            }
            else
            {
                info(tag, "------End------");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private static void printJSONArray(@Nullable String tag, JSONArray array, boolean error)
    {
        try
        {
            String text = array.toString(2);
            String[] temp = text.split("\n");
            if (error)
            {
                error(tag, "-----Start-----");
            }
            else
            {
                info(tag, "-----Start-----");
            }
            for (String s : temp)
            {
                if (error)
                {
                    error(tag, s);
                }
                else
                {
                    info(tag, s);
                }
            }
            if (error)
            {
                error(tag, "------End------");
            }
            else
            {
                info(tag, "------End------");
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


}
