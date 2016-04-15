package tutorialspoint.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AppendStringToExestedFile {
	public static void main(String[] args) throws Exception {
	      try {
	         BufferedWriter out = new BufferedWriter
	         (new FileWriter("FileToAppend.txt"));
	         out.write("aString1\n");
	         out.close();
	         out = new BufferedWriter(new FileWriter
	         ("filename",true));
	         out.write("aString2");
	         out.close();
	         
	         BufferedReader in = new BufferedReader
	         (new FileReader("filename"));
	         String str;
	         while ((str = in.readLine()) != null) {
	            System.out.println(str);
	         }
	         in.close();
	      }	      
	      catch (IOException e) {
	         System.out.println("exception occoured"+ e);
	      }
	      
	   }
}
