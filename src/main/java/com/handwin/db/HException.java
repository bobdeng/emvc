/*
 * 创建日�?2004-8-12
 *
 * 更改�?生�?�?��模板�
 * 窗口 > 首�?�项 > Java > 代码生�?> 代码和注�?
 */
package com.handwin.db;

/**
 * @author chenbing
 * 更改�?生�?类型注释的模板为 窗口 > 首�?�项 > Java > 代码生�?> 代码和注�?
 */
public class HException extends RuntimeException {

    public HException() {
    }

    public HException(String message) {
        super(message);
    }

    public HException(String message, Throwable cause) {
        super(message, cause);
    }

    public HException(Throwable cause) {
        super(cause);
    }

}