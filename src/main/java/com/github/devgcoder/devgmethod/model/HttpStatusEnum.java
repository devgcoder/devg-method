package com.github.devgcoder.devgmethod.model;

/**
 * @author devg
 * @Date 2020/2/27 10:38
 */
public enum HttpStatusEnum {
  SUCCESS200(200, "OK"),
  ERROR400(400, "Bad Request"),
  ERROR401(401,"Unauthorized"),
  ERROR402(402,"Payment Required"),
  ERROR403(403,"Forbidden"),
  ERROR404(404,"Not Found"),
  ERROR408(408,"Request Frequent"),
  ERROR500(500,"Internal Server Error"),
  ERROR502(502,"Bad Gateway"),
  ERROR503(503,"Service Unavailable"),
  ERROR504(504,"Gateway Timeout");

  private int code;
  private String message;

  HttpStatusEnum(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
