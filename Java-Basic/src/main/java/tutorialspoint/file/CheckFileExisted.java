package tutorialspoint.file;

import java.io.File;

public class CheckFileExisted {
	public static void main(String[] args) {
		File file = new File("D:/java.txt");
		System.out.println(file.exists());
	}
}
