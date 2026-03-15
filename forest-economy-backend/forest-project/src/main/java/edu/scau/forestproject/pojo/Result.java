package edu.scau.forestproject.pojo;

import edu.scau.forestproject.exception.ErrorCode;
import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.code = 1;
        result.message = "success";
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.code = 1;
        result.data = data;
        result.message = "success";
        return result;
    }

    public static Result error(String message) {
        Result result = new Result();
        result.code = ErrorCode.BAD_REQUEST.getCode();
        result.message = message;
        return result;
    }

    public static Result error(ErrorCode errorCode, String message) {
        Result result = new Result();
        result.code = errorCode.getCode();
        result.message = message;
        return result;
    }
}
