package com.fich.sarh.common.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class ApiResponse {

    private Date date = new Date();
    private String message;
    private String url;

    public ApiResponse(String message, String url){
        this.message = message;
        this.url = url;
    }
}
