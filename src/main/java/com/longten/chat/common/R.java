package com.longten.chat.common;

public class R<T> {

    private final static int SUCCESS = 0;
    private int code;
    private String message;
    private T data;

    private R(T data) {
        this(SUCCESS, "SUCCESS", data);
    }

    private R(int code, String message) {
        this(code, message, null);
    }

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private R(){
    }

    public static <T> R<T> succeed(T data) {
        return new R<>(data);
    }

    public static <T> R<T> success() {
        return new R<>(null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public R<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isSuccess() {
        return code == SUCCESS;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
