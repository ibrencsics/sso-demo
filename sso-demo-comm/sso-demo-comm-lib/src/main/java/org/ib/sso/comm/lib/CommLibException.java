package org.ib.sso.comm.lib;

public class CommLibException extends Exception {

	private static final long serialVersionUID = 1L;

	public CommLibException() {
		super();
	}

	public CommLibException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommLibException(String message) {
		super(message);
	}

	public CommLibException(Throwable cause) {
		super(cause);
	}
}
