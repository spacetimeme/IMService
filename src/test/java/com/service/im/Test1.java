package com.service.im;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Test1 {

    public static void main(String[] args) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params.put("key", System.currentTimeMillis() + "");
        params.put("duration", "0");
        upload("", params, "resource", new File("/Users/sanders/Downloads/image.png"));

//        URL url = new URL("http://test.api.nilai.com:81/rest/utils/upload");
//        String boundary = "---------------------------";
//        String endLine = "\r\n--" + boundary + "--\r\n";
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setDoOutput(true);
//        connection.setDoInput(true);
//        connection.setUseCaches(false);
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Connection", "Keep-Alive");
//        connection.setRequestProperty("Charset", "UTF-8");
//        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
////        connection.setChunkedStreamingMode(100);
//        StringBuilder textEntity = new StringBuilder();
//        if (params.size() > 0) {
//            for (Map.Entry<String, String> entry : params.entrySet()) {//构造文本类型参数的实体数据
//                textEntity.append("--").append(boundary).append("\r\n");
//                textEntity.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
//                textEntity.append(entry.getValue());
//                textEntity.append("\r\n");
//            }
//        }
//        StringBuilder fileEntity = new StringBuilder();
//        fileEntity.append("--").append(boundary).append("\r\n");
//        fileEntity.append("Content-Disposition: form-data; name=\"").append("resource").append("\"; filename=\"").append("resource").append("\"\r\n");
//        fileEntity.append("Content-Type: application/octet-stream\r\n\r\n");
//        OutputStream out = connection.getOutputStream();
//        out.write(fileEntity.toString().getBytes());
//        File file = new File("/Users/sanders/Downloads/image.png");
//        FileInputStream in = new FileInputStream(file);
//        int len = 0;
//        byte[] buffer = new byte[2048];
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        while ((len = in.read(buffer)) != -1) {
//            System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + len);
//            out.write(buffer, 0, len);
//        }
//        out.write(endLine.getBytes());
//        out.flush();
//        InputStream is = connection.getInputStream();
//        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload over!!!");
//        buffer = new byte[1024 * 100];
//        len = is.read(buffer);
//        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload result -> " + new String(buffer, 0, len));
    }

//    public static String upload(String url, Map<String, Object> params, String fieldName, File... files) throws IOException {
//        String boundary = "---------------------------123821742118716";
//        URL _url = new URL(url);
//        String host = _url.getHost();
//        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
//        conn.setConnectTimeout(3000);
//        conn.setReadTimeout(3000);
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Connection", "Keep-Alive");
//        conn.setRequestProperty("Accept-Charset", "UTF-8");
//        conn.setRequestProperty("Charset", "UTF-8");
//        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//        if (params != null && params.size() > 0) {
//            StringBuilder paramsEntity = new StringBuilder();
//            for (Map.Entry<String, Object> entry : params.entrySet()) {//构造文本类型参数的实体数据
//                paramsEntity.append("\r\n--").append(boundary).append("\r\n");
//                paramsEntity.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
//                paramsEntity.append(entry.getValue());
//            }
//            out.writeBytes(paramsEntity.toString());
//        }
//
//        for (File file : files) {
//            StringBuilder fileEntity = new StringBuilder();
//            fileEntity.append("\r\n--").append(boundary).append("\r\n");
//            fileEntity.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(file.getName()).append("\"\r\n");
//            fileEntity.append("Content-Type: application/octet-stream\r\n\r\n");
//            out.writeBytes(fileEntity.toString());
//
//            FileInputStream fis = new FileInputStream(file);
//            byte[] buffer = new byte[1024 * 5];
//            int len;
//            while ((len = fis.read(buffer)) != -1) {
//                out.write(buffer, 0, len);
//            }
//            fis.close();
//        }
//        out.writeBytes("\r\n--" + boundary + "--\r\n");
//        out.flush();
//        StringBuilder result = new StringBuilder();
//        int responseCode = conn.getResponseCode();
//        if (responseCode == 200) {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                result.append(line);
//            }
//            reader.close();
//        } else {
//            System.out.println(responseCode);
//            result = null;
//        }
//        out.close();
//        conn.disconnect();
//        return result != null ? result.toString() : null;
//    }

    public static String upload(String url, Map<String, String> params, String fieldName, File file) throws IOException {
        String boundary = "---------------------------";
        String prefix = "--";
        StringBuilder header = new StringBuilder();
        header.append("POST /rest/utils/upload HTTP/1.1\r\n");
        header.append("Connection: Keep-Alive\r\n");
        header.append("Content-Type: multipart/form-data; boundary=").append(boundary).append("\r\n");
        header.append("Host: test.api.nilai.com:81\r\n");

        StringBuilder bodyParams = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            bodyParams.append(prefix).append(boundary).append("\r\n");
            bodyParams.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
            bodyParams.append(entry.getValue());
            bodyParams.append("\r\n");
        }

        StringBuilder bodyFile = new StringBuilder();
        bodyFile.append(prefix).append(boundary).append("\r\n");
        bodyFile.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(file.getName()).append("\"\r\n");
        bodyFile.append("Content-Type: application/octet-stream\r\n\r\n");

        StringBuilder end = new StringBuilder();
        end.append("\r\n").append(prefix).append(boundary).append(prefix).append("\r\n");

        long count = bodyParams.length() + bodyFile.length() + file.length() + end.length();
        header.append("Content-Length: ").append(count).append("\r\n\r\n");

        System.out.print(header.toString());
        System.out.print(bodyParams.toString());
        System.out.print(bodyFile.toString());
        System.out.print(file.getName());
        System.out.print(end.toString());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Socket socket = new Socket("test.api.nilai.com", 81);
        OutputStream os = socket.getOutputStream();
        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + 0);
        os.write(header.toString().getBytes());
        os.flush();
        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + header.toString().getBytes().length);
        os.write(bodyParams.toString().getBytes());
        os.flush();
        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + bodyParams.toString().getBytes().length);
        os.write(bodyFile.toString().getBytes());
        os.flush();
        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + bodyFile.toString().getBytes().length);
        FileInputStream fis = new FileInputStream(file);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = fis.read(buffer)) != -1){
            System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + len);
            os.write(buffer, 0, len);
            os.flush();
        }
        os.write(end.toString().getBytes());
        os.flush();
        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   upload -> " + end.toString().getBytes().length);
        InputStream is = socket.getInputStream();
        buffer = new byte[1024 * 1024];
        len = is.read(buffer);
        System.out.println(format.format(new Date(System.currentTimeMillis())) + "   result -> \r\n" + new String(buffer, 0, len));
        System.out.print("--------");
        return "";
    }

}
