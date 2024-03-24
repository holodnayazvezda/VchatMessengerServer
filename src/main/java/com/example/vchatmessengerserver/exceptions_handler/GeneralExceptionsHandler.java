package com.example.vchatmessengerserver.exceptions_handler;

import com.example.vchatmessengerserver.exceptions.*;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GeneralExceptionsHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorResponse handleThrowable() {
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected internal server error occured");
    }

    @Data
    public static class ErrorResponse {
        private final String code;
        private final String message;
    }

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleChatNotFoundException() {
        return new ErrorResponse("CHAT_NOT_FOUND", "The chat was not found");
    }

    @ExceptionHandler(MessageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleMessageNotFoundException() {
        return new ErrorResponse("MESSAGE_NOT_FOUND", "The message was not found");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleUserNotFoundException() {
        return new ErrorResponse("USER_NOT_FOUND", "The user was not found");
    }

    @ExceptionHandler(NoRightsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleNoRightsException() {
        return new ErrorResponse("FORBIDDEN", "You do not have rights to perform this operation");
    }

    @ExceptionHandler(UserUnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleUserUnauthorizedException() {
        return new ErrorResponse("UNAUTHORIZED", "The user was not authorized");
    }

    @ExceptionHandler(IncorrectDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIncorrectDataException() {
        return new ErrorResponse("INCORRECT_DATA", "Incorrect data was transmitted");
    }

    @ExceptionHandler(IncorrectNameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIncorrectNameException() {
        return new ErrorResponse("INCORRECT_NAME", "An incorrect name was passed");
    }

    @ExceptionHandler(IncorrectNicknameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIncorrectNicknameException() {
        return new ErrorResponse("INCORRECT_NICKNAME", "An incorrect nickname was passed");
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIncorrectPasswordException() {
        return new ErrorResponse("INCORRECT_PASSWORD", "An incorrect password was passed");
    }

    @ExceptionHandler(IncorrectSecretKeysException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleIncorrectSecretKeysException() {
        return new ErrorResponse("INCORRECT_SECRET_KEYS", "An incorrect secret keys were passed");
    }
}
