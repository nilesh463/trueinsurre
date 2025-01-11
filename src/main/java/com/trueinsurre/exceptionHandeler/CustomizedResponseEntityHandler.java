package com.trueinsurre.exceptionHandeler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomizedResponseEntityHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(NotFound.class)
	public final ResponseEntity<Object> handlerUserNotFoundException(NotFound userNotFound) {
		ExceptionResponce responce = new ExceptionResponce(userNotFound.getMessage(), userNotFound.getCause(),
				HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(responce, HttpStatus.NOT_FOUND);
	}


	@ExceptionHandler(DateException.class)
	public final ResponseEntity<Object> handlerDateException(DateException dateException) {
		ExceptionResponce responce = new ExceptionResponce(dateException.getMessage(), dateException.getCause(),
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(responce, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmptyFields.class)
	public final ResponseEntity<Object> handlerEmptyFieldsFaield(EmptyFields emptyFields) {
		ExceptionResponce responce = new ExceptionResponce(emptyFields.getMessage(), emptyFields.getCause(),
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(responce, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidData.class)
	public final ResponseEntity<Object> invalidEmail(InvalidData invaliddata) {
		ExceptionResponce responce = new ExceptionResponce(invaliddata.getMessage(), invaliddata.getCause(),
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(responce, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(InternalServerError.class)
	public final ResponseEntity<Object> internalServerError(InternalServerError invalid) {
		ExceptionResponce responce = new ExceptionResponce(invalid.getMessage(), invalid.getCause(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(responce, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
