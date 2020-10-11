package com.soniya.framework.fordependency;

public class SpringException extends RuntimeException {

	public SpringException(String msg) {
		super(msg);
		System.out.println(msg);
	}

}
