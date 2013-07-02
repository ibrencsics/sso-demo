package org.ib.sso.comm;

public class CommException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CommException() {
		super();
	}

	public CommException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommException(String message) {
		super(message);
	}

	public CommException(Throwable cause) {
		super(cause);
	}
}