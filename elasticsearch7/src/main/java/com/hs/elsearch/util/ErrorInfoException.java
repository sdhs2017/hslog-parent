package com.hs.elsearch.util;

/**
 * 自定义异常类，用来处理异常信息的打印。
 */
public class ErrorInfoException extends Exception {
    /**
     * 提供异常信息的输入，使用e.getMessage获取
     * @param message
     */
    public ErrorInfoException(String message) {
        super(message);
    }
}
