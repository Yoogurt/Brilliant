package com.BB.elf;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.BB.elf.ELF_SectionHeader.ELF_Shdr;
import com.BB.util.Util;

public class ELF_Section {

	protected ELF_Shdr mHeader;

	protected byte[] s_data;

	private RandomAccessFile raf;

	public ELF_Section(RandomAccessFile raf, ELF_Header header, ELF_Shdr mHeader) throws IOException {
		this.raf = raf;
		this.mHeader = mHeader;
		s_data = new byte[Util.bytes2Int32(mHeader.sh_size)];
		loadDataFromStream();
	}

	private void loadDataFromStream() throws IOException {
		raf.seek(Util.bytes2Int32(mHeader.sh_offset, mHeader.sh_offset.length));
		raf.read(s_data);
	}

	public ELF_Shdr getHeader() {
		return mHeader;
	}

	@Override
	public String toString() {
		return new String(s_data);
	}

	public String getStringAtIndex(byte[] index) {
		int index_int32 = Util.bytes2Int32(index);
		StringBuilder sb = new StringBuilder();

		for (;; index_int32++)
			if (s_data[index_int32] != 0)
				sb.append((char) s_data[index_int32]);
			else
				break;

		return sb.toString();
	}
}
