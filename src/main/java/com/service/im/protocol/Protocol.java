package com.service.im.protocol;

public interface Protocol {

    //{起始标记   -byte     -1}
    //{包总长度   -int      -4}
    //{包的类型   -short    -2}
    //{包体内容   -byte[n]  -n}
    //{结束标记   -byte     -1}

    /**
     * 包头长度(算上头和最后一个结束标记)
     */
    int HEADER_LENGTH = 8;

    /**
     * 起始标记
     */
    byte START_TAG = '<';

    /**
     * 结束标记
     */
    byte END_TAG = '>';

}