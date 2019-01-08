package com.eaiggi.api.exception;

public class BaseException extends Exception {
	/**
	* 
	*/
	private static final long serialVersionUID = 3952065886507812018L;

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String msg, Exception e) {
		super(msg, e);
	}
}
