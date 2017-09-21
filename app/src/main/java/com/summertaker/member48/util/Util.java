package com.summertaker.member48.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.summertaker.member48.common.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Util {

    private static String mTag = "== Util";

    public static String getUrlToFileName(String url) {
        String fileName = url;

        fileName = fileName.replace("http://", "");
        fileName = fileName.replace("https://", "");
        fileName = fileName.replace("/", "");
        fileName = fileName.replace("?", "");
        fileName = fileName.replace("&", "");
        fileName = fileName.replace("=", "");
        //Log.d(mTag, fileName);

        return fileName;
    }

    public static String getJapaneseString(String text, String encoding) {
        if (encoding == null) {
            encoding = "ISO-8859-1"; // // JIS, SJIS, 8859_1, SHIFT-JIS
        }
        try {
            return new String(text.getBytes(encoding), Charset.forName("UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }


    public static String readFile(String fileName) {

        String result = "";

        File file = new File(BaseApplication.getDataPath(), fileName);
        if (file.exists()) {
            StringBuilder builder = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    //builder.append('\n');
                }
                reader.close();
            } catch (IOException e) {
                Log.e(mTag, "FILE: " + fileName);
                Log.e(mTag, "ERROR: " + e.getLocalizedMessage());
            }
            //Log.d(mTag, builder.toString());
            result = builder.toString();
        }

        return result;
    }

    public static void writeToFile(String fileName, String data) {
        final File file = new File(BaseApplication.getDataPath(), fileName);

        try {
            boolean isSuccess = file.createNewFile();
            if (isSuccess) {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                myOutWriter.append(data);
                myOutWriter.close();

                fOut.flush();
                fOut.close();
            }
        } catch (IOException e) {
            Log.e(mTag, "FILE: " + fileName);
            Log.e(mTag, "ERROR: " + e.getLocalizedMessage());
        }
    }
}
