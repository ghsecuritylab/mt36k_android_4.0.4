/*
 * Copyright (C) 2006 The Android Open Source Project
 * 
 * ©2010-2013 TCL CORPORATION All Rights Reserved.
 */

package com.tcl.net.samba;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbException;
import jcifs.smb.NtlmPasswordAuthentication;

import android.util.Log;
import com.tcl.net.sndcmd.SndCmdUtility;
import android.os.Parcelable;
import android.os.Parcel;


public class SmbShareFolder implements Parcelable {

    private static final String TAG = "SmbShareFolder";
    private boolean DEBUG = true;
    private SmbDevice mSmbDevice;
    private String mFolderName;
    private boolean mMountedFlag;

    /* @hide */
    public SmbShareFolder(SmbDevice ssv, String name) {
        mSmbDevice = ssv;
        mFolderName = name;
        mMountedFlag = isMountedProc();
    }

    /**     
     * Mount share folders with auth info and permissions flag 
     * @author TCL TVOS Team
     * @param null
     * @return true/mount successed
     */
    public boolean mount(SmbAuthentication auth, int flags) {

        File dir = new File(makeLocalPath());

        Log.d(TAG, "mount : ip = " + mSmbDevice.mIp + " name = " + mSmbDevice.mName);

        if (!dir.isDirectory() && !SmbcUtils.mkdir(makeLocalPath())) {
            Log.d(TAG, "mount : mkdir failed!");
            return false;
        }

        if (mMountedFlag) {
            Log.d(TAG, "mount : already mounted!");
            return true;
        }

        setAuthentication(auth);

        if ((SndCmdUtility.SndCmd(makeMountCmd()) != null) && isMountedProc()) {
            mMountedFlag = true;
            mSmbDevice.setMountState(true);
            mSmbDevice.mMountCnt++;

            if (DEBUG)
                Log.d(TAG, "mount : success!  mMountedFlag = " + mMountedFlag
                        + " device mount cnt = " + mSmbDevice.mMountCnt);

            return true;
        }

        if (DEBUG)
            Log.d(TAG, "mount : mount failed!");
        SmbcUtils.rmdir(makeLocalPath());
        return false;

    }

    /**     
     * Unmount the share folders
     * @author TCL TVOS Team
     * @param null
     * @return true/unmount successed
     */
    public boolean umount() {

        if (!mMountedFlag) {
            if (DEBUG)
                Log.d(TAG, "mount : already umounted!");
            return true;
        }

        File dir = new File(makeLocalPath());
        if (!dir.isDirectory())
            return false;

        if ((SndCmdUtility.SndCmd(makeUmountCmd()) != null) && !isMountedProc()) {
            SmbcUtils.rmdir(makeLocalPath());
            mMountedFlag = false;
            if (mSmbDevice.mMountCnt > 0)
                mSmbDevice.mMountCnt--;

            if (DEBUG)
                Log.d(TAG, "umount : success! mMountedFlag=" + mMountedFlag
                        + " mSmbDevice.mMountCnt = " + mSmbDevice.mMountCnt);

            if (mSmbDevice.mMountCnt == 0) {
                mSmbDevice.setMountState(false);
                SmbcUtils.rmdir(mSmbDevice.makeLocalPath());
            }

            return true;
        }

        if (DEBUG)
            Log.d(TAG, "umount : failed!");
        return false;

    }

    /**     
     * Get the host that the share folders belongs to
     * @author TCL TVOS Team
     * @param null
     * @return SmbDevice object
     */
    public SmbDevice getSmbDevice() {
        if (DEBUG)
            Log.d(TAG, "getSmbDevice = " + mSmbDevice);
        return mSmbDevice;
    }

    /**     
     * Get the auth info
     * @author TCL TVOS Team
     * @param null
     * @return SmbAuthentication object
     */
    public SmbAuthentication getAuth() {
        return mSmbDevice.getAuth();
    }

    /**     
     * Get local directory
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
     * create a directory base the localPath
     * @author TCL TVOS Team
     * @param null
     * @return the directory with the shared folder name
     */
    public String makeLocalPath() {
        return mSmbDevice.makeLocalPath() + "/" + mFolderName;
    }

    /**     
     * Get remote directory with share folder name
     * @author TCL TVOS Team
     * @param null
     * @return remote directory
     */
    public String remotePath() {
        return mSmbDevice.remotePath() + "/" + mFolderName;
    }

    /**     
     * Judge if the file have the read permission
     * @author TCL TVOS Team
     * @param null
     * @return true/if the file is read-only
     */
    public boolean canRead() {
        try { // any file, directory, or other resource can be read if it exists
            String anthPass = mSmbDevice.mHasPassWord ? (mSmbDevice.mUser + ":" + mSmbDevice.mPassWord)
                    : null;
            SmbFile sf = new SmbFile(currentSmbFileUrl(), new NtlmPasswordAuthentication(anthPass));
            return sf.canRead();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return false;
    }

    /**     
     * Judge if the file have the write permission
     * @author TCL TVOS Team
     * @param null
     * @return true/if the file writeable
     */
    public boolean canWrite() {
        try {
            String anthPass = mSmbDevice.mHasPassWord ? (mSmbDevice.mUser + ":" + mSmbDevice.mPassWord)
                    : null;
            SmbFile sf = new SmbFile(currentSmbFileUrl(), new NtlmPasswordAuthentication(anthPass));
            return sf.canWrite();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return false;
    }

    /**     
     * Judge if the file has mounted
    * @author TCL TVOS Team
     * @param null
     * @return true/if the file mounted
     */
    public boolean isMounted() {
        return mMountedFlag;
    }

    /**     
    * Check if the files mounted successed after mount/umount files
    * @author TCL TVOS Team
     * @param null
     * @return true/if the operation execute successed
     */
    private boolean isMountedProc() { // get information from "/proc/mounts"

        String path = makeLocalPath();
        path = path.substring(0, path.length() - 1);

        StringBuffer buffer = new StringBuffer();
        String line;

        // move these IO ops to block 'finally', to make sure the file be closed
        FileInputStream fis = null;
        BufferedReader reader = null;
        try {
            fis = new FileInputStream("/proc/mounts");
            reader = new BufferedReader(new InputStreamReader(fis));

            if (null == fis || null == reader) {
                Log.d(TAG, "Can not read mount config file, plz check the file /proc/mounts");
                return false;
            }
            line = reader.readLine(); // contents of the line

            while (line != null) {
                String[] segments = line.split(",");
                for (String s : segments) {
                    if (s.startsWith("unc=") && s.substring(4).equals(getUnc())) {
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
            Log.d(TAG, String.format("isMountedProc: %s is not mounted.", path));
        return false;

    }

    private String getUnc() {
        return "\\\\" + mSmbDevice.mIp + "\\" + mFolderName.substring(0, mFolderName.length() - 1);
    }

    private String makeMountCmd() {
        String rmt = remotePath();
        rmt = rmt.substring(0, rmt.length() - 1);

        String local = makeLocalPath();
        local = local.substring(0, local.length() - 1);

        return String.format("mount -o user=%s,pass=%s,iocharset=utf8 -t cifs \"%s\" \"%s\"",
                mSmbDevice.mUser, mSmbDevice.mPassWord, rmt, local);
    }

    private String makeUmountCmd() {
        String local = makeLocalPath();
        local = local.substring(0, local.length() - 1);

        return String.format("umount \"%s\"", local);
    }

    private String currentSmbFileUrl() {
        return "smb:" + remotePath() + "/";
    }

    private void setAuthentication(SmbAuthentication auth) {
        mSmbDevice.mUser = auth.getName();
        mSmbDevice.mPassWord = auth.getPassword();

        if (DEBUG)
            Log.d(TAG, "setAuthentication : user=" + mSmbDevice.mUser + " pass="
                    + mSmbDevice.mPassWord);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(mFolderName);
    }

    /**
     * @hide
     */
    public static final Parcelable.Creator<SmbShareFolder> CREATOR = new Parcelable.Creator<SmbShareFolder>() {
        public SmbShareFolder createFromParcel(Parcel in) {
            return new SmbShareFolder(in);
        }

        public SmbShareFolder[] newArray(int size) {
            return new SmbShareFolder[size];
        }
    };

    /**
     * @hide
     */
    public SmbShareFolder(Parcel in) {
        mFolderName = in.readString();
    }
}
