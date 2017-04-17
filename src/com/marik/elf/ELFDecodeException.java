package com.marik.elf;

public class ELFDecodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6635832896412030305L;

	public ELFDecodeException() {
		super();
	}

	public ELFDecodeException(String msg) {
		super(msg);
	}

	public ELFDecodeException(Throwable t, String msg) {
		super(msg, t);
	}

}
