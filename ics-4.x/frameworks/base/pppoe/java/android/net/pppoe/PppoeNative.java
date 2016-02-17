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

/**
 * Native calls for sending requests to the kernel,
 * {@hide}
 */
public class PppoeNative {
	public native static int initpppoeNative();
	public native static String waitEvent();
	public native static int adsldial(String username,String passwd);
	public native static int adsldisconnect(int force);
	public native static String getaddress();
	public native static int pppoeconfigure(String ipaddr,String gateway,String dns1,String dns2);
		
}
