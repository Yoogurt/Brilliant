package brilliant.elf.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class CalculateLine {
	public static void main(String[] args) {

		File file = new File("src\\com\\marik");

		if (!file.exists())
			throw new RuntimeException("file not exists");

		System.out.println("Total : " + getLine(file) + " lines");

	}

	private static int getLine(File file) {

		System.out.println("at " + file.getAbsolutePath());

		File[] children = file.listFiles();

		int count = 0;

		for (File f : children)
			if (f.isDirectory())
				count += getLine(f);
			else
				count += getFileLine(f);

		return count;

	}

	private static int getFileLine(File file) {

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			int count = 0;

			while (br.readLine() != null)
				count++;

			br.close();

			return count;

		} catch (Throwable e) {
			e.printStackTrace();
			return 0;
		}

	}
}
