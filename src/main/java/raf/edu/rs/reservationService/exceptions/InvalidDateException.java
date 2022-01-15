package raf.edu.rs.reservationService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidDateException extends  CustomException{
    public InvalidDateException(String message, ErrorCode errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }
    public InvalidDateException() {
        super("Invalid date!", ErrorCode.INVALID_DATE, HttpStatus.CONFLICT);
    }
}
