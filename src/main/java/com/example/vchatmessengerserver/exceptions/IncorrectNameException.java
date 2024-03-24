package com.example.vchatmessengerserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "The name is wrong")
public class IncorrectNameException extends RuntimeException{}
