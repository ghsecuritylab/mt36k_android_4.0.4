/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * ©2010-2013 TCL CORPORATION All Rights Reserved.
 */

package com.tcl.net.samba;


public class SmbAuthentication {

    public static final SmbAuthentication ANONYMOUS = new SmbAuthentication(null, null);

    private String mName;
    private String mPassword;

     /**     
     * The SmbAuthentication constructed function
     * @author TCL TVOS Team
     * @param name the auth username;password the auth password;
     */
    public SmbAuthentication(String name, String password) {
        mName = name;
        mPassword = password;
    }

    /**     
     * Get user name used to auth
     * @author TCL TVOS Team
     * @param null
     * @return user name
     */
    public String getName() {
        return mName;
    }

    /**     
     * Get password used to auth
     * @author TCL TVOS Team
     * @param null
     * @return password
     */
    public String getPassword() {
        return mPassword;
    }
}

