package com.BB.util;

public class BytesBuilder {

	private byte[] mInternal;
	private int length;

	public BytesBuilder(int capacity) {
		length = 0;
		mInternal = new byte[capacity];
	}

	public synchronized BytesBuilder append(byte[] data, int offset, int length) {

		while (this.length + length > mInternal.length) {
			byte[] mTmp = new byte[length + mInternal.length];
			System.arraycopy(mInternal, 0, mTmp, 0, mInternal.length);
			mInternal = mTmp;
		}

		System.arraycopy(data, offset, mInternal, this.length, length);

		this.length += length;

		return this;
	}

	public BytesBuilder append(byte[] data) {
		return append(data, 0, data.length);
	}

	public byte[] trim2Bytes() {

		if (mInternal.length == length)
			return mInternal;

		byte[] mReturn = new byte[length];
		System.arraycopy(mInternal, 0, mReturn, 0, length);

		return mReturn;

	}
	
}
