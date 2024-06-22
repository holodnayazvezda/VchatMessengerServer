package com.example.vchatmessengerserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "There is no such data")
public class DataNotFoundException extends RuntimeException {}
