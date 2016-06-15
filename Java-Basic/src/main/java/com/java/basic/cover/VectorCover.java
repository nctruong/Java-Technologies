package com.java.basic.cover;

import java.util.Enumeration;
import java.util.Vector;

public class VectorCover {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vector vt = new Vector();
		System.out.println("capacity: " + vt.capacity());
		
		for(int i=1;i<=100;i++){
			vt.addElement(i);
		}		
		
		Enumeration en = vt.elements();
		while(en.hasMoreElements()){
			System.out.println("Element: " + en.nextElement());
		}
		
		System.out.println("capacity: " + vt.capacity());
	}

}
