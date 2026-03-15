package edu.scau.forestproject.exception;

import edu.scau.forestproject.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException ex) {
        return Result.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
    public Result handleBadRequest(Exception ex) {
        return Result.error(ErrorCode.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception ex) {
        log.error("unexpected error", ex);
        return Result.error(ErrorCode.INTERNAL_ERROR, "internal server error");
    }
}
