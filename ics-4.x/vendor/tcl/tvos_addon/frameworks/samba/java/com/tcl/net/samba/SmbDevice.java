/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * ©2010-2013 TCL CORPORATION All Rights Reserved.
 */

package com.tcl.net.samba;

import java.net.InetAddress;
import java.util.List;
import java.util.ArrayList;
import java.net.UnknownHostException;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbException;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.netbios.NbtAddress;

import android.util.Log;
import android.os.Parcelable;
import android.os.Parcel;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

public class SmbDevice implements Parcelable {

    private static final String TAG = "SmbDevice";
    private static final String DEF_SMB_DEV_NAME = "Default Smb Device";
    private boolean DEBUG = true;

    public static final int FLAG_WRITEABLE = 0x01;

    /**
     * @hide
     */
    public String mIp = "";
    /**
     * @hide
     */
    public String mUser = "";
    /**
     * @hide
     */
    public String mPassWord = "";
    /**
     * @hide
     */
    public String mName = DEF_SMB_DEV_NAME;
    /**
     * @hide
     */
    public boolean mHasPassWord = false;

    /**
     * @hide
     */
    public int mFlags = 0x00;

    /**
     * @hide
     */
    public int mMountCnt = 0;

    private boolean mMountedFlag = false;
    private List<SmbShareFolder> mShareFolderList = new ArrayList<SmbShareFolder>();;

    /**     
     * Get the host's InetAddress object,IP/Hostname can get from the object
     * @author TCL TVOS Team
     * @param null
     * @return InetAddress object.
     */
    public InetAddress getAddress() {
        try {
            InetAddress iAds = InetAddress.getByName(mIp);
            return iAds;
        } catch (UnknownHostException e) {
            Log.d(TAG, "getAddress : " + e.toString());
            return null;
        }
    }

    /**     
     * Set the IP of smbdevice object
     * @author TCL TVOS Team
     * @param ip the ip that you want to set
     */
    public void setAddress(String ip) {
        mIp = ip;
    }

    private String getServerName() {
        if (DEBUG)
            Log.d(TAG, "getServerName : " + mIp);

        try {
            NbtAddress nbt = NbtAddress.getByName(mIp);

            if (nbt != null && nbt.isActive()) {
                NbtAddress[] all = NbtAddress.getAllByAddress(nbt);
                for (int i = 0; i < all.length; i++) {
                    NbtAddress n = all[i];
                    if (!n.isGroupAddress() && n.getNameType() == 0) {
                        if (n.getHostName() != null)
                            return n.getHostName();
                    }
                }
            }

        } catch (UnknownHostException e) {
            // wrong user name or password
            e.printStackTrace();
        }

        return null;
    }

    /**     
     * Get  hostname attribute of the host
     * @author TCL TVOS Team
     * @param null
     * @return hostname
     */
    public String getHostName() {
        if (DEBUG)
            Log.d(TAG, "getHostName entry : " + mName);

        if (mName.equals(DEF_SMB_DEV_NAME))
            mName = getServerName();

        if (DEBUG)
            Log.d(TAG, "getHostName ---> mName = " + mName);

        if (mName == null)
            return getAddress().getHostName();
        else
            return mName;
    }

    /**     
     * Set  hostname attribute of the host
     * @author TCL TVOS Team
     * @param name the string that you want to used as the hostname of the host
     */
    public void setHostName(String name) {
        mName = name;
    }

    /**     
     * Get  ShareFolder list
     * @author TCL TVOS Team
     * @param null
     * @return SmbShareFolder list
     */
    public List<SmbShareFolder> getShareFolderList() {
        Log.d(TAG, "getShareFolderList : " + this.toString());

        mShareFolderList.clear();

        String anthPass = this.mHasPassWord ? (mUser + ":" + mPassWord) : null;

        try {
            SmbFile sf = new SmbFile(currentSmbFileUrl(), new NtlmPasswordAuthentication(anthPass));
            SmbFile[] files = sf.listFiles();

            for (int i = 0; i < files.length; i++) {
                SmbFile f = files[i];
                Log.d(TAG, files[i].getName() + " " + files[i].getType() + "  :  "
                        + " Attributes: " + f.getAttributes());

                if (!isShare(f))
                    continue;

                mShareFolderList.add(new SmbShareFolder(this, files[i].getName()));
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return mShareFolderList;
    }

    /**     
     * Mount  the ShareFolder
     * @author TCL TVOS Team
     * @param auth the auth info of share folder;flags if the mount folder can WRITEABLE
     * @return the  mounted share folder  number
     */
    public int mount(SmbAuthentication auth, int flags) {
        int i = 0;
        if (mShareFolderList.size() == 0) {
            Log.d(TAG, "mount failed! No share folder to mount!!!");
            return 0;
        }

        for (SmbShareFolder ssf : mShareFolderList) {
            if (ssf.mount(auth, flags) && !mMountedFlag) {
                mMountedFlag = true;
                i++;
            }
        }

        return i;
    }

    /**     
     * Unmount all the mounted  ShareFolder
     * @author TCL TVOS Team
     * @param null
     */
    public void umount() {
        if (mShareFolderList.size() == 0) {
            Log.d(TAG, "umount failed! No share folder to umount!!!");
            return;
        }

        int fail_cnt = 0;

        for (SmbShareFolder ssf : mShareFolderList) {
            if (!ssf.umount())
                fail_cnt++;
        }

        if (fail_cnt > 0) {
            Log.d(TAG, "umount : failed times : " + fail_cnt);
        } else
            mMountedFlag = false;
    }

    /**     
     * Get auth info of the host 
     * @author TCL TVOS Team
     * @param null
     * @return SmbAuthentication
     */
    public SmbAuthentication getAuth() {
        if (!this.mHasPassWord)
            return SmbAuthentication.ANONYMOUS;

        return (new SmbAuthentication(mUser, mPassWord));
    }

    /**     
     * Set auth info of the host 
     * @author TCL TVOS Team
     * @param auth auth info of the host
     */
    public void setAuth(SmbAuthentication auth) {
		if( null == auth){
			//do nothiong---HQS
		}else{
			mUser = auth.getName();
			mPassWord = auth.getPassword();
			if (mPassWord != null)
				mHasPassWord = true;
		}
	}

    /**     
     * Create local path used to mount share folders 
     * @author TCL TVOS Team
     * @param null
     * @return local directory
     */
    public String localPath() {
        if (DEBUG)
            Log.d(TAG, "localPath : mMountedFlag=" + mMountedFlag);

        if (!mMountedFlag)
            return null;

        return makeLocalPath();
    }

    /**     
     * The funtion that implemented the localPath
     * @author TCL TVOS Team
     * @param null
     * @return local directory
     */
    public String makeLocalPath() {
        return SmbClient.SMB_LOCAL_PATH + "/" + mIp;
    }

    /**     
     * Get the remote host directory 
     * @author TCL TVOS Team
     * @param null
     * @return remote host directory
     */
    public String remotePath() {
        return "//" + mIp;
    }

    /**     
     * Get the remote host status 
     * @author TCL TVOS Team
     * @param null
     * @return true/the host is active
     */
    public boolean isActive() {

        try {
            if (mIp != null) {
                NbtAddress nbt = NbtAddress.getByName(mIp);
                if (nbt != null)
                    return nbt.isActive();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
    * @hide
    */
    public int getFlags() {
        return mFlags;
    }

    /**     
     * Test the auth info wether invalid 
     * @author TCL TVOS Team
     * @param user user name used to auth;pw the password used to auth
     * @return true/invalid
     */
    public boolean testPassword(String user, String pw) {
        try {
            if (mIp == null) {
                if (DEBUG)
                    Log.d(TAG, "testPassword --> mIp is null!");
                return false;
            }

            SmbFile sf = new SmbFile("smb://" + mIp + "/",
                    new jcifs.smb.NtlmPasswordAuthentication(null, user, pw));
            // SmbFile[] urls = sf.listFiles();
            if (sf.exists()) {
                Log.d(TAG, "testPassword : password is correct!");

                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "testPassword : password is wrong!");
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**     
     * Test the host have password or not 
     * @author TCL TVOS Team
     * @param user user name used to auth;pw the password used to auth
     * @return true/has password
     */
    public boolean hasPassword() {

        try { // use the empty user name and password to check the server.
            SmbFile sf = new SmbFile("smb://" + mIp + "/",
                    new jcifs.smb.NtlmPasswordAuthentication(null));
            SmbFile[] urls = sf.listFiles();
            if (sf.exists()) {
                Log.d(TAG, "hasPassword : smb server has no password!");
                for (SmbFile url : urls) {
                    if (url.getPath().endsWith("$/")) 
                        continue;
                    // url.getPermission();
                    Log.d(TAG, "hasPassword : Test shared folder: " + url.getPath());
                    SmbFile sf_temp = new SmbFile(url.getPath(),
                            new jcifs.smb.NtlmPasswordAuthentication(null));
                    sf_temp.list();
                }
                return false;
            }
        } catch (Exception e) {
            /* Do Nothing */
            Log.d(TAG, mName + ": hasPassword : yes !");
            return true;
        }
        return true;
    }

    private boolean isShare(SmbFile file) {
        try {
            if (file.getType() != SmbFile.TYPE_SHARE)
                return false;

            String filename = file.getName();
            if (filename.startsWith(".") || filename.endsWith("$/"))
                return false;
            if (file.isHidden())
                return false;
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     *
     * @hide
     */
    public void setMountState(boolean flag) {
        mMountedFlag = flag;
    }

    /**
     *
     * @hide
     */
    public void scanShareFolder(List<SmbShareFolder> sfList) {

    }

    @Override
    public String toString() {
        return String.format("SMB device ->Name: %s, Ip: %s, hasPassword: %b(%s:%s)", mName, mIp,
                mHasPassWord, mUser, mPassWord);
    }

    private String currentSmbFileUrl() {
        return "smb://" + mIp + "/";
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(mIp);
        dest.writeString(mUser);
        dest.writeString(mPassWord);
        dest.writeString(mName);
    }

    /**
     *
     * @hide
     */
    public static final Parcelable.Creator<SmbDevice> CREATOR = new Parcelable.Creator<SmbDevice>() {
        public SmbDevice createFromParcel(Parcel in) {
            return new SmbDevice(in);
        }

        public SmbDevice[] newArray(int size) {
            return new SmbDevice[size];
        }
    };

    /**
     * @hide
     */
    public SmbDevice(Parcel in) {
        mIp = in.readString();
        mUser = in.readString();
        mPassWord = in.readString();
        mName = in.readString();
    }

    public SmbDevice() {
        mIp = "";
        mUser = "";
        mPassWord = "";
        mName = DEF_SMB_DEV_NAME;
    }

    private String getUnc() {
        return "\\\\" + mIp + "\\";
    }

    /**     
     * Get the mount status
     * @author TCL TVOS Team
     * @param null
     * @return true/mounted already
     */
    public boolean isMounted() { 

        StringBuffer buffer = new StringBuffer();
        String line;

        // move these IO ops to block 'finally', to make sure the file be closed. 
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream("/proc/mounts");
            reader = new BufferedReader(new InputStreamReader(fis));

            if (null == reader || null == fis) {
                Log.d(TAG, "Can not read mount config file, plz check the file /proc/mounts");
                return false;
            }

            line = reader.readLine(); // contents of the line

            while (line != null) {
                String[] segments = line.split(",");
                for (String s : segments) {
                    if (s.startsWith("unc=") && s.substring(4).startsWith(getUnc())) {
                        if (DEBUG)
                            Log.d(TAG, "unc=" + s.substring(4) + "  getUnc:" + getUnc());
                        return true;
                    }
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fis && null != reader) {
                try {
                    reader.close();
                    fis.close();
                } catch (IOException e) {
                    Log.d(TAG, "[close file error] " + e.getStackTrace());
                    return false;
                }
            }
        }
        if (DEBUG)
            Log.d(TAG, String.format("isMounted: %s is not mounted.", mName));
        return false;

    }
}

