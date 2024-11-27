package com.honley.kitaitu.request;

public class SmsLoginRequest {
    private String phone;
    private String inputSmsCode;

    public SmsLoginRequest(String phone, String inputSmsCode) {
        this.phone = phone;
        this.inputSmsCode = inputSmsCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getInputSmsCode() {
        return inputSmsCode;
    }

    public void setInputSmsCode(String inputSmsCode) {
        this.inputSmsCode = inputSmsCode;
    }
}

