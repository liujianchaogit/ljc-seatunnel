package com.ljc.seatunnel.common;

public class Result<T> {
    private static final Result<Void> OK = success();

    private int code = 0;

    private String msg;

    private T data;

    public Result() {
        this.data = null;
    }

    private Result(SeatunnelErrorEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMsg();
        this.data = null;
    }

    private Result(SeatunnelErrorEnum errorEnum, String... messages) {
        this.code = errorEnum.getCode();
        this.msg = String.format(errorEnum.getTemplate(), messages);
        this.data = null;
    }

    public static <T> Result<T> success() {
        return new Result<>();
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(SeatunnelErrorEnum errorEnum) {
        Result<T> result = new Result<>(errorEnum);
        return result;
    }

    public static <T> Result<T> failure(SeatunnelErrorEnum errorEnum, String... messages) {
        Result<T> result = new Result<>(errorEnum, messages);
        return result;
    }

    public boolean isSuccess() {
        return OK.getCode() == this.code;
    }

    public boolean isFailed() {
        return !this.isSuccess();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
