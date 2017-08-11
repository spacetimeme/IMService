package com.service.im;

/**
 *
 */
public interface ProgressListener {

    void progress(long currentTotal, long total, boolean done);

}
