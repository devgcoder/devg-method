package com.github.devgcoder.devgmethod.model;

import java.io.Serializable;

public class ResultModel<T> implements Serializable {

  private static final String successMessage = "操作成功";

  private static final String errorMessage = "系统繁忙";

  public static <T> ResultModel<T> newSuccess() {
    return new ResultModel<T>()
        .setData(null)
        .setSuccess(true)
        .setMessage(successMessage)
        .setCode(1);
  }

  public static <T> ResultModel<T> newSuccess(T data) {
    return new ResultModel<T>()
        .setData(data)
        .setSuccess(true)
        .setMessage(successMessage)
        .setCode(1);
  }

  public static <T> ResultModel<T> newFail(String message) {
    return new ResultModel<T>().setMessage(message).setSuccess(false).setData(null).setCode(0);
  }

  public static <T> ResultModel<T> newFail(int code, String message) {
    return new ResultModel<T>().setMessage(message).setSuccess(false).setData(null).setCode(code);
  }

  public static <T> ResultModel<T> newFail() {
    return newFail(errorMessage);
  }

  public static <T> ResultModel<T> newFail(int code) {
    return newFail(code, errorMessage);
  }

  private boolean success = false;
  private int code;
  private String message = "";
  private T data;

  public boolean isSuccess() {
    return success;
  }

  public ResultModel<T> setSuccess(boolean success) {
    this.success = success;
    return this;
  }

  public int getCode() {
    return code;
  }

  public ResultModel<T> setCode(int code) {
    this.code = code;
    return this;
  }

  public String getMessage() {
    return message;
  }

  public ResultModel<T> setMessage(String message) {
    this.message = message;
    return this;
  }

  public T getData() {
    return data;
  }

  public ResultModel<T> setData(T data) {
    this.data = data;
    return this;
  }
}
