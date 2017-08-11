package com.service.im;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 */
public interface HttpRequest {

    String get(String url) throws IOException;

    String post(String url, Map<String, Object> params) throws IOException;

    String upload(String url, Map<String, Object> params, String fieldName, byte[] fileBuffer) throws IOException;

    String upload(String url, Map<String, Object> params, String fieldName, byte[] fileBuffer, ProgressListener listener) throws IOException;

    String upload(String url, Map<String, Object> params, String fieldName, File file) throws IOException;

    String upload(String url, Map<String, Object> params, String fieldName, File file, ProgressListener listener) throws IOException;

    boolean download(String url, File saveFile, ProgressListener listener) throws IOException;

    boolean download(String url, File saveFile) throws IOException;

}
