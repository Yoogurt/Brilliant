package com.BB.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LocatableInPutStream extends FileInputStream {

	public long index;

	public LocatableInPutStream(File file) throws FileNotFoundException {
		super(file);
		index = 0;
	}

	@Override
	public int read() throws IOException {
		int result = super.read();
		if (result > 0)
			index++;
		return result;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read = super.read(b);
		if (read > 0)
			index += read;
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = super.read(b, off, len);
		if (read > 0)
			index += read;
		return read;
	}
	
	public long getNextReadPosition(){
		return index;
	}

}
