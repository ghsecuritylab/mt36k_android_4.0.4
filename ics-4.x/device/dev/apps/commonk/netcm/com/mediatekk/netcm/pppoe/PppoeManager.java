package com.mediatekk.netcm.pppoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mediatekk.netcm.util.NetLog;

/**
 * This class use to pppoe operation.
 * <ul>
 * <li> Do a pppoe connection.</li>
 * <li> Get pppoe related information.</li>
 * </ul>
 */
public class PppoeManager {
	public static final int ERR_CODE_UNKNOW = 0;
	public static final int ERR_CODE_BREAKLINK = 1;
	public static final int ERR_CODE_USERNAME = 2;
	public static final int ERR_CODE_PASSWORD = 3;

	public static final int STATUS_DISCONNECT = 0;
	public static final int STATUS_CONNECTING = 1;
	public static final int STATUS_CONNECT = 2;

	private static PppoeManager mPppoeManager = null;
	//private PppoeService pppoeins;
	private String username;
	private String passwd;

	static {
		mPppoeManager = new PppoeManager();
	}

	private PppoeManager() {
		//pppoeins = PppoeService.getInstance();
	}

	/**
	 * Create a new PppoeManager instance. 
	 * Applications will use for pppoe operation.
	 * 
	 */
	public static PppoeManager getInstance() {
		return mPppoeManager;
	}

	/**
	 * Create a dial up link by pppoe.
	 * 
	 * @param username   user name.
	 * @param passwd     the password use to pppoe link.
	 * 
	 */
	public void createDialUpLink(String username, String passwd) {
		this.username = username;
		this.passwd = passwd;
		//pppoeins.dialUp(username, passwd);
	}

	/**
	 * Break a dial up link.
	 * 
	 */
	public void breakDialUpLink() {
		//pppoeins.hangUp();
	}

	/**
	 * Get the current pppoe link status.
	 * 
	 * @return the status of pppoe.
	 * 
	 */
	public int getLinkStatus() {
		return -1;//pppoeins.getStatus();
	}

	/**
	 * Get the user name which use to create pppoe link.
	 * 
	 * @return the user name.
	 */
	public String getUserName() {
		return username;
	}

	/**
	 * Get the password which use to create pppoe link.
	 * 
	 * @return the password.
	 * 
	 */
	public String getPassword() {
		return passwd;
	}
	
	/**
	 * Get the current pppoe link's IP address.
	 * 
	 * @return the IP address.
	 * 
	 */
	public String getIP() {
		String ipPrefix = "inet addr:";
//		String[] command = {"ifconfig", "ppp0"};
		String ip = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig ppp0");
//			process = Runtime.getRuntime().exec(command);
//			Log.d("testPppoe", "ip, ifconfig ppp0");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				Log.d("testPppoe", "line: "+line);
//				line = line.trim();
				index = line.toLowerCase().indexOf(ipPrefix);
				if (index >= 0) {
					ip = line.substring(index+ipPrefix.length()).trim();
					index = ip.indexOf(" ");
					ip = ip.substring(0, index);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return ip;
	}
	
	/**
	 * Get the current pppoe link's net mask.
	 * 
	 * @return the net mask.
	 * 
	 */
	public String getMask() {
		String maskPrefix = "mask:";
		String mask = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig ppp0");
//			Log.d("testPppoe", "mask, ifconfig ppp0");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				Log.d("testPppoe", "line: "+line);
				index = line.toLowerCase().indexOf(maskPrefix);
				if (index >= 0) {
					mask = line.substring(index + maskPrefix.length()).trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return mask;
	}
	
	/**
	 * Get the current pppoe link's gate way.
	 * 
	 * @return the gate way.
	 * 
	 */
	public String getGateway() {
		String gwPrefix = "default";
		String gateway = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("route");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
//				Log.d("testPppoe", "line: "+line);
//				line = line.trim();
				index = line.toLowerCase().indexOf(gwPrefix);
				if (index >= 0) {
					gateway = line.substring(index + gwPrefix.length()).trim();
					index = gateway.indexOf(" ");
					gateway = gateway.substring(0, index);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return gateway;
	}
	
	/**
	 * Get the current pppoe link's MAC address.
	 * 
	 * @return the MAC address.
	 * 
	 */
	public String getMac() {
		String macPrefix = "hwaddr";
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ifconfig eth0");
//			Log.d("testPppoe", "mac, ifconfig eth0");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
//			Log.d("testPppoe", "buffer: "+bufferedReader.toString());
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				NetLog.d("testPppoe", "line: "+line);
				index = line.toLowerCase().indexOf(macPrefix);
				if (index >= 0) {
					mac = line.substring(index + macPrefix.length()).trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return mac;
	}
	
	/**
	 * Get the current pppoe link's DNS address.
	 * 
	 * @return the DNS address.
	 * 
	 */
	public String getDNS() {
		String dnsPrefix = "[net.dns1]: [";
		String dns = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
//			process = Runtime.getRuntime().exec("getprop | grep net.dns1");
			process = Runtime.getRuntime().exec("getprop");
			bufferedReader = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				NetLog.d("testPppoe", "line: "+line);
				index = line.toLowerCase().indexOf(dnsPrefix);
				if (index >= 0) {
					dns = line.substring(index + dnsPrefix.length()).trim();
					index = dns.indexOf("]");
					dns = dns.substring(0, index);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return dns;
	}
}