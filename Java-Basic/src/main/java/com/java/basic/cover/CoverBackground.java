package com.java.basic.cover;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class CoverBackground {
	public static Client client;
	public static void main(String[] args) 
	{
		//client = new Client("1","Josh","3000");
		//coverVector();
		//coverHashtable();
		//coverStack();
	}
	public static void coverStack()
	{
		Stack st = new Stack();
		st.push(client);
		System.out.println("Size: " + st.size());
		Client cl = (Client)st.pop();
		System.out.println("Client: " + cl.getName());
		System.out.println("Size: " + st.size());
	}
	public static void coverVector()
	{
		Vector vt = new Vector();
		System.out.println("capacity: " + vt.capacity());		
		for(int i=1;i<=10;i++){
			vt.addElement(i);
		}				
		showElements(vt);
		System.out.println("capacity: " + vt.capacity());
	}
	/* no order */
	public static void coverHashtable() 
	{
		Hashtable ht = new Hashtable();
		for(int i=1;i<=10;i++){
			ht.put("key" + i, "value" + i);
		}
		showElements(ht);
		
	}
	public static void showElements(Object ob)
	{
		if (ob instanceof Vector)
		{
			Enumeration en = ((Vector) ob).elements();
		}
		else if (ob instanceof Hashtable)
		{
			Enumeration en = ((Hashtable) ob).elements();
		}
		Enumeration en = ((Vector) ob).elements();
		while(en.hasMoreElements())
		{
			System.out.println("Element: " + en.nextElement());
		}
	}
}
