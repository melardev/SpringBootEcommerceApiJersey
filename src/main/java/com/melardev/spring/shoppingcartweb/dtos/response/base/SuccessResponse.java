package com.melardev.spring.shoppingcartweb.dtos.response.base;

public class SuccessResponse extends AppResponse {
    private Object dto;

    public SuccessResponse() {
        super(true);
    }

    public SuccessResponse(String message) {
        this();
        addFullMessage(message);
    }


    public static SuccessResponse build(String message) {
        return new SuccessResponse(message);
    }

    public Object getDto() {
        return dto;
    }
}
