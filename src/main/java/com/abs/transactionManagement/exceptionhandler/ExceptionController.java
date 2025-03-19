package com.abs.transactionManagement.exceptionhandler;

import com.abs.transactionManagement.config.BaseResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<BaseResponse<Void>> handleCustomException(CustomException e) {
        log.error("Custom Exception :: {}", e.getMessage());

        HttpHeaders headers = new HttpHeaders();

        return ResponseEntity.status(Optional.ofNullable(e.getHttpStatus()).orElse(HttpStatus.BAD_REQUEST))
                .headers(headers)
                .body(BaseResponse.<Void>builder()
                        .flag(false)
                        .code(Optional.ofNullable(e.getCode()).orElse("02"))
                        .message(e.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected BaseResponse<Object> handleResponseStatusException(ResponseStatusException e) {
        log.error("Response Status Exception :: {}", e.getMessage());
        return BaseResponse.builder()
                .flag(false)
                .code("03")
                .message("Something went wrong")
                .build();
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected BaseResponse<Object> handleValidationExceptions(BindException e) {
        List<String> errors = e.getBindingResult().getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String message = errors.get(0);
        return BaseResponse.builder()
                .code("03")
                .message(message)
                .build();
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected BaseResponse<Object> handleValidationExceptions(HandlerMethodValidationException e) {
        List<String> errors = e.getAllErrors()
                .stream().map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        String message = errors.get(0);
        return BaseResponse.builder()
                .flag(false)
                .message(message)
                .build();
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected BaseResponse<Object> handleInvalidFormatException(InvalidFormatException ex) {
        String message = "Invalid value '" + ex.getValue() + "' for field: " + ex.getPath().get(0).getFieldName();

        return BaseResponse.builder()
                .flag(false)
                .message(message)
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected BaseResponse<Object> handleJsonMappingException(HttpMessageNotReadableException ex) {
        Throwable throwable = ex.getMostSpecificCause();
        String message = "Invalid Request Body";
        if (throwable instanceof MismatchedInputException mismatchedInputException) {
            String reference = mismatchedInputException.getPathReference();
            String target = mismatchedInputException.getTargetType().getTypeName();
            log.info("reference :: {}, target :: {}", reference, target);
            String param = reference.substring(reference.lastIndexOf("[") + 1, reference.lastIndexOf("]"));
            log.info("Parameter :: {}", param);
            String expected = target.substring(target.lastIndexOf(".") + 1);
            log.info("Expected :: {}", expected);
            message = String.format("Invalid parameter type %s, expected '%s'", param, expected);
        } else if (throwable instanceof CustomException customException) {
            message = customException.getMessage();
        }
        log.error("HttpMessageNotReadable Exception :: {}", message);
        return BaseResponse.builder()
                .flag(false)
                .message(message)
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected BaseResponse<Object> handleDefaultException(Exception e) {
        log.error("System Error :: cause: {}, message: {}", e.getClass(), e.getMessage());
        return BaseResponse.builder()
                .flag(false)
                .code("02")
                .message("Something went wrong")
                .build();
    }
}
