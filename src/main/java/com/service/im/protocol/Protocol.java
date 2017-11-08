package com.service.im.protocol;

public interface Protocol {

    //{起始标记   -byte     -1}
    //{包总长度   -int      -4}
    //{包头保留   -byte[26] -26}
    //{包体内容   -byte[n]  -n}
    //{结束标记   -byte     -1}

    /**
     * 包头长度(算上最后一个结束标记)
     */
    int HEADER_LENGTH = 32;

    /**
     * 起始标记
     */
    byte START_TAG = '<';

    /**
     * 结束标记
     */
    byte END_TAG = '>';

    /**
     * 保留位置
     */
    byte[] RETAIN = new byte[26];

}