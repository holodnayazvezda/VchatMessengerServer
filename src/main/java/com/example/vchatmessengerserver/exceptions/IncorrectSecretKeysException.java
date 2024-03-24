package com.example.vchatmessengerserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The secret key is wrong")
public class IncorrectSecretKeysException extends RuntimeException{ }
