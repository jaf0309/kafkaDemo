package com.demo.bigdata.util;

import java.io.*;

public class JsonUtil {
    /**
     * 读取json文件，返回json串
     * @param fileName
     * @return
     */
    public static String readJsonFile(String fileName) {
        try {
            return readJsonFile(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static String readJsonFile(InputStream inputStream) {
        String jsonStr = "";
        try(Reader reader = new InputStreamReader(inputStream,"utf-8")) {
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            reader.close();

            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            return null;
        }
    }
}