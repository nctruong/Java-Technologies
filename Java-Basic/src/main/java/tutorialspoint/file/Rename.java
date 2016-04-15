package tutorialspoint.file;

import java.io.File;

public class Rename {
	public static void main(String[] args) {
		File oldName = new File("D:/myfile.txt");
		File newName = new File("D:/java.txt");
		if (oldName.renameTo(newName)) {
			System.out.println("renamed");
		} else {
			System.out.println("Error");
		}
	}
}
