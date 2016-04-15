package tutorialspoint.file;

import java.io.File;

public class GetFileSize {
	public static long getFileSize(String filename) {
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			System.out.println("File doesn\'t exist");
			return -1;
		}
		return file.length();
	}

	public static void main(String[] args) {
		long size = getFileSize("D:/java.txt");
		System.out.println("Filesize in bytes: " + size);
	}
}
