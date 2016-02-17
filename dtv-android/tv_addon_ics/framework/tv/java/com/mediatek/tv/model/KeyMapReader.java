package com.mediatek.tv.model;

import java.io.BufferedReader; 
import java.io.FileReader; 
import java.io.IOException; 
import java.util.HashMap; 

public class KeyMapReader {
	
	protected HashMap<Integer, Integer> Android_to_MTK_keyCode = new HashMap<Integer,Integer>(); 

	public KeyMapReader(String filename) throws IOException { 
		Android_to_MTK_keyCode.clear();
		FileReader  fr = new FileReader(filename);
	    BufferedReader reader = new BufferedReader(fr); 
	    read(reader); 
		fr.close();
	    reader.close(); 
	} 

	protected void read(BufferedReader reader) throws IOException { 
	    String line; 
	    while ((line = reader.readLine()) != null) { 
	        parseLine(line); 
	    } 
	} 

	protected void parseLine(String line) { 
	   String currentLine = line.trim(); 
	   //android.util.Log.e("KeyMapReader ", "GotLine "  + currentLine);
	   if ( 0 != currentLine.indexOf("#")){ 
		  // android.util.Log.e("KeyMapReader ", "parseLine "  + currentLine);
	       String Count[] = currentLine.split("\\s+");
	       //android.util.Log.e("KeyMapReader ", "parseLine 1 :"  + Count[1]);
	      // android.util.Log.e("KeyMapReader ", "parseLine 3 :"  + Count[3]);
	       int AndroidKeyCode = Integer.parseInt(Count[3]); 	      
	       int MtkKeyCode = Integer.parseInt(Count[1], 16);
	      // android.util.Log.e("KeyMapReader ", " MtkKeyCode "  +MtkKeyCode);
	      // android.util.Log.e("KeyMapReader ", "MtkKeyCode "  + AndroidKeyCode);
	       Android_to_MTK_keyCode.put( new Integer(AndroidKeyCode), new Integer(MtkKeyCode));
	   }
	} 

	public int getMTKKeyCode(int AndroidKeyCode) {	
	 if(Android_to_MTK_keyCode.containsKey(AndroidKeyCode))
	 {
		 return Android_to_MTK_keyCode.get(AndroidKeyCode);
	 }	 
	     return -1;
	 }
}
