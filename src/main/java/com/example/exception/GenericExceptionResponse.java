package com.example.exception;

import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericExceptionResponse {

    private Date timeStamp;
    private String message;
    private String description;
}
