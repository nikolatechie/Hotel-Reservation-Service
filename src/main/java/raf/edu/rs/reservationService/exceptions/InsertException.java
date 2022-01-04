package raf.edu.rs.reservationService.exceptions;

import org.springframework.http.HttpStatus;

public class InsertException extends CustomException {
    public InsertException(String message) {
        super(message, ErrorCode.DUPLICATE, HttpStatus.NOT_ACCEPTABLE);
    }
}