package com.service.im;

import okhttp3.*;
import okio.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class OKHttp implements HttpRequest, Interceptor {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String TAG = "OKHttp";
    private final Map<String, List<Cookie>> cookieStore = new HashMap<>();
    private final Map<String, ProgressListener> listeners = new HashMap<>();
    private OkHttpClient client;
    private static OKHttp http;

    public static void main(String[] args) throws Exception {
        final Map<String, Object> params = new HashMap<>();
        params.put("type", "1");
        params.put("key", System.currentTimeMillis() + "");
        params.put("duration", "0");
        final OKHttp http = OKHttp.getOKHttp();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    http.download("https://github.com/SSOOnline/android-orm/archive/master.zip", new File("/Users/sanders/Downloads/master2.zip"), new ProgressListener() {
                        @Override
                        public void progress(long currentTotal, long total, boolean done) {
                            System.out.println(
                                    format.format(new Date(System.currentTimeMillis())) +
                                            String.format(" currentTotal=%d total=%d done=%s", currentTotal, total, String.valueOf(done)));
                        }
                    });
//                    http.upload("http://test.api.nilai.com:81/rest/utils/upload", params, "resource", new File("/Users/sanders/Downloads/image.png"), new ProgressListener() {
//                        @Override
//                        public void progress(long currentTotal, long total, boolean done) {
//                            System.out.println(
//                                    format.format(new Date(System.currentTimeMillis())) +
//                                            String.format(" currentTotal=%d total=%d done=%s", currentTotal, total, String.valueOf(done)));
//                        }
//                    });
                } catch (Exception e) {

                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    http.download("https://github.com/SSOOnline/ApkCustomizationTool/archive/master.zip", new File("/Users/sanders/Downloads/master1.zip"), new ProgressListener() {
                        @Override
                        public void progress(long currentTotal, long total, boolean done) {
                            System.out.println(
                                    format.format(new Date(System.currentTimeMillis())) +
                                            String.format(" currentTotal=%d total=%d done=%s", currentTotal, total, String.valueOf(done)));
                        }
                    });
//                    http.upload("http://test.api.nilai.com:81/rest/utils/upload", params, "resource", new File("/Users/sanders/Downloads/image.png"), new ProgressListener() {
//                        @Override
//                        public void progress(long currentTotal, long total, boolean done) {
//                            System.out.println(
//                                    format.format(new Date(System.currentTimeMillis())) +
//                                            String.format(" currentTotal=%d total=%d done=%s", currentTotal, total, String.valueOf(done)));
//                        }
//                    });
                } catch (Exception e) {

                }
            }
        }).start();
//        System.out.println(
//                format.format(new Date(System.currentTimeMillis())) + " ======================================over!");
    }

    public static OKHttp getOKHttp() {
        if (http == null) {
            http = new OKHttp();
        }
        return http;
    }

    public OKHttp() {
        client = new OkHttpClient.Builder()
                .readTimeout(15 * 1000, TimeUnit.SECONDS)
                .connectTimeout(6 * 1000, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .addInterceptor(this)
                .build();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        Response response = chain.proceed(request);
        return response.newBuilder().body(new DownloadResponseBody(response.body(), new ProgressListener() {
            @Override
            public void progress(long currentTotal, long total, boolean done) {
                String url = request.url().toString();
                ProgressListener listener = listeners.get(url);
                if (listener != null) {
                    System.out.println(url);
                    listener.progress(currentTotal, total, done);
                    if (done) {
                        listeners.remove(url);
                    }
                }
            }
        })).build();
    }

    @Override
    public String get(String url) throws IOException {
        Request.Builder builder = new Request.Builder();
        buildHeader(builder);
        builder.url(url);
        Response response = client.newCall(builder.build()).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("ok http get request fail!");
    }

    @Override
    public String post(String url, Map<String, Object> params) throws IOException {
        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                body.add(entry.getKey(), (String) entry.getValue());
            } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float || value instanceof Short || value instanceof Number) {
                body.add(entry.getKey(), String.valueOf(value));
            } else {
                body.add(entry.getKey(), value.toString());
            }
        }
        Request.Builder builder = new Request.Builder();
        buildHeader(builder);
        builder.url(url);
        builder.post(body.build());
        Response response = client.newCall(builder.build()).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("ok http post request fail!");
    }


    @Override
    public String upload(String url, Map<String, Object> params, String fieldName, byte[] fileBuffer, ProgressListener listener) throws IOException {
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                body.addFormDataPart(entry.getKey(), (String) entry.getValue());
            } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float || value instanceof Short || value instanceof Number) {
                body.addFormDataPart(entry.getKey(), String.valueOf(value));
            } else {
                body.addFormDataPart(entry.getKey(), value.toString());
            }
        }
        body.addFormDataPart(fieldName, String.valueOf(System.currentTimeMillis()), RequestBody.create(MediaType.parse("application/octet-stream"), fileBuffer));
        Request.Builder builder = new Request.Builder();
        buildHeader(builder);
        builder.url(url);
        if (listener != null) {
            builder.post(new UploadRequestBody(body.build(), listener));
        } else {
            builder.post(body.build());
        }
        Response response = client.newCall(builder.build()).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("ok http upload request fail!");
    }

    @Override
    public String upload(String url, Map<String, Object> params, String fieldName, byte[] fileBuffer) throws IOException {
        return upload(url, params, fieldName, fileBuffer, null);
    }

    @Override
    public String upload(String url, Map<String, Object> params, String fieldName, File file) throws IOException {
        return upload(url, params, fieldName, file, null);
    }

    @Override
    public String upload(String url, Map<String, Object> params, String fieldName, File file, ProgressListener listener) throws IOException {
        MultipartBody.Builder body = new MultipartBody.Builder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                body.addFormDataPart(entry.getKey(), (String) entry.getValue());
            } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float || value instanceof Short || value instanceof Number) {
                body.addFormDataPart(entry.getKey(), String.valueOf(value));
            } else {
                body.addFormDataPart(entry.getKey(), value.toString());
            }
        }
        body.addFormDataPart(fieldName, String.valueOf(System.currentTimeMillis()), RequestBody.create(MediaType.parse("application/octet-stream"), file));
        Request.Builder builder = new Request.Builder();
        buildHeader(builder);
        builder.url(url);
        if (listener != null) {
            builder.post(new UploadRequestBody(body.build(), listener));
        } else {
            builder.post(body.build());
        }
        Response response = client.newCall(builder.build()).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        }
        throw new IOException("ok http upload request fail!");
    }

    private void buildHeader(Request.Builder builder) {
//        builder.addHeader("deviceId", SystemInfo.IMEI);
//        builder.addHeader("platform", "Android");
//        builder.addHeader("appVersion", SystemInfo.APP_VERSION_NAME);
//        builder.addHeader("networkType", SystemInfo.getNetWorkType());
//        builder.addHeader("channel", SystemInfo.CHANNEL);
//        builder.addHeader("checkCode", RandomUtil.getCheckCode());
//        builder.addHeader("userId", Config.getStringConfig(IConstant.USER_ID, ""));
//        builder.addHeader("phoneModel", SystemInfo.MODEL_NUMBER);
//        builder.addHeader("osVersion", SystemInfo.OS_VERSION);
    }

    @Override
    public boolean download(String url, File saveFile, ProgressListener listener) throws IOException {
        if (listener != null) {
            if (url.indexOf("?") > 0) {
                url += "&_times=" + System.currentTimeMillis();
            } else {
                url += "?_times=" + System.currentTimeMillis();
            }
            listeners.put(url, listener);
        }
        Request.Builder builder = new Request.Builder();
        buildHeader(builder);
        builder.url(url);
        Response response = client.newCall(builder.build()).execute();
        if (response.isSuccessful()) {
            InputStream is = response.body().byteStream();
            if (is != null) {
                byte[] buffer = new byte[1024 * 10];
                int len;
                FileOutputStream fos = new FileOutputStream(saveFile);
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
                return true;
            }
        }
        throw new IOException("ok http download request fail!");
    }

    @Override
    public boolean download(String url, File saveFile) throws IOException {
        return download(url, saveFile, null);
    }

    private static final class UploadRequestBody extends RequestBody {

        //实际的待包装请求体
        private RequestBody requestBody;
        //包装完成的BufferedSink
        private BufferedSink bufferedSink;

        private ProgressListener listener;

        private UploadRequestBody(RequestBody requestBody, ProgressListener listener) {
            this.requestBody = requestBody;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();
        }

        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    if (listener != null) {
                        listener.progress(bytesWritten, contentLength, bytesWritten == contentLength);
                    }
                }
            };
        }
    }

    private static final class DownloadResponseBody extends ResponseBody {
        //实际的待包装响应体
        private ResponseBody responseBody;
        //进度回调接口
        private ProgressListener listener;
        //包装完成的BufferedSource
        private BufferedSource bufferedSource;

        private DownloadResponseBody(ResponseBody responseBody, ProgressListener listener) {
            this.responseBody = responseBody;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                //包装
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {

            return new ForwardingSource(source) {
                //当前读取字节数
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    //回调，如果contentLength()不知道长度，会返回-1
                    if (listener != null) {
                        listener.progress(totalBytesRead, contentLength(), bytesRead == -1);
                    }
                    return bytesRead;
                }
            };
        }
    }
}
