package csc.truong.ATMConsole.helper;
public class MainClass {

  public static void main(String[] args) {

    String date = "2000-11-01";
    java.sql.Date javaSqlDate = java.sql.Date.valueOf(date);
    System.out.println(javaSqlDate);

  }

}