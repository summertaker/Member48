package com.summertaker.member48.common;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Util {

    private static String mTag = "== Util";

    public static String getUrlToFileName(String url) {
        String fileName = url;

        fileName = fileName.replace(":", "");
        fileName = fileName.replace("/", "");
        fileName = fileName.replace("?", "");
        fileName = fileName.replace("=", "");
        //Log.d(mTag, fileName);

        return fileName;
    }

    public static String readFile(Context context, String pathName, String fileName) {

        String result = "";

        try {
            InputStream inputStream = context.openFileInput(pathName + File.separator + fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                result = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(mTag, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(mTag, "Can not read file: " + e.toString());
        }

        return result;
    }

    public static void writeToFile(String path, String fileName, String data) {
        final File file = new File(path, fileName);

        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
