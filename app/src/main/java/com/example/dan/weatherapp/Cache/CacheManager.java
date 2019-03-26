package com.example.dan.weatherapp.Cache;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class CacheManager {

    private Context context;

    public static CacheManager with(Context context)
    {
       return new CacheManager(context);
    }

    private CacheManager(Context context) {
        this.context = context;
    }

    public boolean refreshCache(String fileName)
    {
        if (context == null)
        {
            return false;
        }
        File file = context.getFileStreamPath(fileName);
        return !file.exists();
    }
    public boolean fileExist(String fileName)
    {
        if (context == null)
        {
            return false;
        }
        File file = context.getFileStreamPath(fileName);
        return file.exists();
    }

    public boolean deleteFromFile(String fileName) {
        if (context == null) {
            return false;
        }
        File file = context.getFileStreamPath(fileName);
        return file.exists() && file.delete();
    }

    public String readFromFile(String filename)
    {
        String ret = "";

        try
        {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null)
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return ret;
    }


    public void writeToFile(String data, String filename)
    {
        try
        {
            OutputStreamWriter outputStreamWriter =
                    new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



}
