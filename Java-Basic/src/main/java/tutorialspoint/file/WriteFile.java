package tutorialspoint.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {
	public static void main(String[] args) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("outfilename"));
			out.write("aString");
			out.close();
			System.out.println("File created successfully");
		} catch (IOException e) {
		}
	}
}
