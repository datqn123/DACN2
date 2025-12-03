package com.example.dacn2.exception;

import com.example.dacn2.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 1. Bắt tất cả các lỗi RuntimeException (Lỗi logic code mình ném ra)
    // Ví dụ: throw new RuntimeException("Mat khau khong dung")
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(1001); // Mã lỗi mặc định cho lỗi runtime
        apiResponse.setMessage(exception.getMessage()); // Lấy câu thông báo lỗi bạn viết bên Service

        return ResponseEntity.badRequest().body(apiResponse); // Trả về HTTP 400
    }

    // 2. Bắt các lỗi không xác định (Lỗi hệ thống, NullPointer, Database...)
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(Exception exception) {

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(9999); // Mã lỗi hệ thống
        apiResponse.setMessage("Lỗi hệ thống không xác định: " + exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
    // Bắt lỗi valid
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1001);
        apiResponse.setMessage(enumKey);
        return ResponseEntity.badRequest().body(apiResponse);
    }

}
