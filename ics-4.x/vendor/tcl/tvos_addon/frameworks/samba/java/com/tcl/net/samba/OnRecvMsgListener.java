/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * ©2010-2013 TCL CORPORATION All Rights Reserved.
 */

package com.tcl.net.samba;

public interface OnRecvMsgListener {
    public static final int MSG_UPDATE_DEVLIST_CANCEL = 0x01;
    public static final int MSG_UPDATE_DEVLIST_DONE = 0x02;
    public static final int MSG_UPDATE_DEVLIST_ADD = 0x03;

     /**     
     * The samba scan result monitor
     * @author TCL TVOS Team
     * @param msg the samba scan result of update done/cancel/add
     */
    public void onRecvMsgListener(int msg);
}
