package com.kaldar.kaldar.dtos.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private boolean isSuccess;
    private int status;
    private String message;
    private T data;


}
