// Using common-io library
package csc.truong.ATMConsole.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class FileUtilCommon {
	// TODO Auto-generated method stub
	private String fileName;
	private static File file;
	public FileUtilCommon(){
		file = new File("account.txt");
	}
	
	public void replace(String str1, String str2) throws IOException{
		// fileString is line was read from file.
		// str have to be entire line.
		String fileString = FileUtils.readFileToString(file);
		String finalString = fileString.replace(str1, str2);
        FileUtils.writeStringToFile(file, finalString);
	
	}
	
	public List<String> getListLine() throws IOException{	
		List<String> lines = FileUtils.readLines(file);			
		return lines;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public static File getFile() {
		return file;
	}

	public static void setFile(File file) {
		FileUtilCommon.file = file;
	}
	 
	/*public static void main(String args[]) throws IOException{
		file = new File("account.txt");
		FileUtilCommon com = new FileUtilCommon();
		List<String> l = com.getListLine();
		for(String ll:l){
			System.out.println(ll);
		}
	}*/
	
}
