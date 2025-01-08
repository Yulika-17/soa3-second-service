package com.itmo.secondspring.exception;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse() {}  // Конструктор без аргументов для JAXB

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // Геттеры и сеттеры
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
