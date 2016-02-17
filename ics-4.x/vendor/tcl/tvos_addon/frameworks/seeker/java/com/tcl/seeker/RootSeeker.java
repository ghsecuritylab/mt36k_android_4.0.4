package com.tcl.seeker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

public class RootSeeker {

	private static final String TAG = "RootSeeker";
	private static final int SOCKET_PORT = 8090;
	private static final String SOCKET_IP = "127.0.0.1";

	public static int exec(String cmd) {
		
		//Log.d(TAG, "<-----------------in exec() start --------------------->");
		Log.d(TAG, "exec cmd: " + cmd);
		
		Socket socket;
		BufferedReader in;
		PrintWriter out;
		char []buf = new char[256];
		int ret = 0;
		
		try	{
			socket = new Socket(SOCKET_IP, SOCKET_PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
			out.println(cmd);
			if (in.read(buf) != -1 && new String(buf).equals(new String("ok")))
				ret = 0;
			else 
				ret = -1;
			
			Log.d(TAG, "command execute " + ((ret == 0) ? "ok" : "false") + ", return: -->" +buf);
			
			line.close();
			out.close();
			in.close();
			socket.close();
			return 0;	//FIXME
		}
		catch (IOException e){
			Log.d(TAG, e.toString());
		}
		return 0;
	}
}
