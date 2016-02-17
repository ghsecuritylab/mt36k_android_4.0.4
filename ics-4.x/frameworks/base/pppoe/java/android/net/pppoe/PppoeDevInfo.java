/*
 * Copyright (C) 2010 The Android-X86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */
package android.net.pppoe;


import android.net.pppoe.PppoeDevInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.*;

public class PppoeDevInfo implements Parcelable {
	private String dev_name;
	private String ipaddr;
	private String netmask;
	private String route;
	private String dns1;
	private String dns2;
	private String mode;


	public PppoeDevInfo () {
		dev_name = null;
		ipaddr = null;
		dns1 = null;
		dns2 = null;
		route = null;
		netmask = null;
	}

	public void setIfName(String ifname) {
		this.dev_name = ifname;
	}

	public String getIfName() {
		return this.dev_name;
	}

	public void setIpAddress(String ip) {
		this.ipaddr = ip;
	}

	public String getIpAddress( ) {
		return this.ipaddr;
	}
	public void setNetMask(String ip) {
		this.netmask = ip;
	}

	public String getNetMask( ) {
		return this.netmask;
	}

	public void setRouteAddr(String route) {
		this.route = route;
	}

	public String getRouteAddr() {
		return this.route;
	}

	public void setDnsAddr(String dns1,String dns2) {
		this.dns1 = dns1;
		this.dns2 = dns2;
	}

	public String getDns1Addr( ) {

		return this.dns1;
	}

	public String getDns2Addr( ) {

		return this.dns2;
	}

	public String getConnectMode() {
		return this.mode;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.dev_name);
		dest.writeString(this.ipaddr);
		dest.writeString(this.netmask);
		dest.writeString(this.route);
		dest.writeString(this.dns1);
		dest.writeString(this.dns2);
	}
	  /** Implement the Parcelable interface {@hide} */
    public static final Creator<PppoeDevInfo> CREATOR =
        new Creator<PppoeDevInfo>() {
            public PppoeDevInfo createFromParcel(Parcel in) {
                PppoeDevInfo info = new PppoeDevInfo();
                info.setIfName(in.readString());
                info.setIpAddress(in.readString());
                info.setNetMask(in.readString());
                info.setRouteAddr(in.readString());
                info.setDnsAddr(in.readString(),in.readString());
                return info;
            }

            public PppoeDevInfo[] newArray(int size) {
                return new PppoeDevInfo[size];
            }
        };
}

