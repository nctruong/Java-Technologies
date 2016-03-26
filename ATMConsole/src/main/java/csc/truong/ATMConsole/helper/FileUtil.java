package csc.truong.ATMConsole.helper;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import csc.truong.ATMConsole.Customer;

public class FileUtil {
  private static DateFormat formatter ; 
  private static final int NO_PARAM_INLINE = 5;
  DataOutputStream dos;

  /*
   * Utility method to write a given text to a file
   */
  public boolean writeToFile(String fileName, String dataLine,
      boolean isAppendMode, boolean isNewLine) {
    if (isNewLine) {
      dataLine = "\n" + dataLine;
    }

    try {
      File outFile = new File(fileName);
      if (isAppendMode) {
        dos = new DataOutputStream(new FileOutputStream(fileName, true));
      } else {
        dos = new DataOutputStream(new FileOutputStream(outFile));
      }

      dos.writeBytes(dataLine);
      dos.close();
    } catch (FileNotFoundException ex) {
      return (false);
    } catch (IOException ex) {
      return (false);
    }
    return (true);

  }

  /*
   * Reads data from a given file
   */
  public List<?> readFromFile(String fileName) {
    String DataLine = "";
    List<Customer> listCustomer = new ArrayList<Customer>();
    formatter = new SimpleDateFormat("dd-MMM-yy");
    try {
      File inFile = new File(fileName);
      BufferedReader br = new BufferedReader(new InputStreamReader(
          new FileInputStream(inFile)));
   	  while ((DataLine = br.readLine()) != null) {
    	  Customer customer = new Customer();
    	  String line[] = new String[NO_PARAM_INLINE];
    	  line = DataLine.split(",");
    	  
          customer.setName(line[0]);
    	  customer.setPin(Integer.parseInt(line[1]));
    	  customer.setActivated(Integer.parseInt(line[2]));
    	  customer.setAmount(Float.parseFloat(line[3]));
    	  
			customer.setValidTo(line[4]);
    	  
    	  //System.out.println(DataLine);
    	  listCustomer.add(customer);
	  } 
      br.close();
    } catch (FileNotFoundException ex) {
      return (null);
    } catch (IOException ex) {
      return (null);
    }
    return listCustomer;

  }

  public boolean isFileExists(String fileName) {
    File file = new File(fileName);
    return file.exists();
  }

  

}// end of class
