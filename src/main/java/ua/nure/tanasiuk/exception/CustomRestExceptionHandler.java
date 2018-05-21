package ua.nure.tanasiuk.exception;

import static java.util.Collections.singletonList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @SuppressWarnings("unchecked")
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
        HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(PhoneValidationException.class)
    public ResponseEntity handlePhoneValidationException(Exception ex) {
        return ResponseEntity.badRequest().body(singletonList(ex.getMessage()));
    }
}
