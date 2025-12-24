package com.example.dacn2.dto.request.email;

import lombok.Data;

@Data
public class EmailRequest {
    private Long bookId;
    private TypeEmailEnum typeEmail;
}
