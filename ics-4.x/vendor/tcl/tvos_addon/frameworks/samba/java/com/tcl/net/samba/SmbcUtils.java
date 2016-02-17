/***************************************************************************
 *
 *   Author : liukun
 *   Date : 2011-7-11
 *   Description :
 *
 ***************************************************************************/

package com.tcl.net.samba;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.tcl.net.sndcmd.SndCmdUtility;

/**
 * @hide
 */
public class SmbcUtils {

    private static final String TAG = "SmbcUtils";

    public static boolean getNetworkStatus(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();

        if (network != null)
            return true;// connected

        return false; // disconnect
    }

    public static boolean mkdir(String path) {

        Log.d(TAG, "mkdir: " + path);

        File f = new File(path);

        if (!f.isAbsolute()) {
            Log.d(TAG, String.format("Error: SmbcUtils can (mkdir) only absolut directory path!\n"
                    + "path: %s", path));
            return false;
        }

        if (!f.isDirectory()) {
            SndCmdUtility.SndCmd("mount -o remount rootfs /");
            SndCmdUtility.SndCmd(String.format("mkdir -p \"%s\"", path));
            // we change all the permission of /smb and sub directories to 777,
            // except for JAVA can not
            // get a correct result when File.isDirectory() be invoked. @douzy
            // SndCmdUtility.SndCmd(String.format("chmod 777 \"%s\"", path));
            String smbPath = "/mnt/smb";
            File smbDir = new File(smbPath);
            if (smbDir.exists()) {
                SndCmdUtility.SndCmd(String.format("chmod -R 777 %s", smbPath));
            } else {
                Log.e(TAG, "dir /mnt/smb not exists!");
                return false;
            }
            if (f.isDirectory()) {
                Log.d(TAG, "mkdir success");
                return true;
            } else {
                Log.d(TAG, "mkdir fail");
                return false;
            }
        } else {
            Log.d(TAG, String.format("mkdir: file already exist: %s", path));
            return false;
        }
    }

    public static boolean rmdir(String path) {
        Log.d(TAG, "rmdir: " + path);

        File f = new File(path);

        if (!f.isAbsolute()) {
            Log.d(TAG, String.format("Error: SmbcUtils can (rmdir) only absolut directory path!\n"
                    + "path: %s", path));
            return false;
        }

        if (f.isDirectory()) {
            SndCmdUtility.SndCmd("mount -o remount rootfs /");
            SndCmdUtility.SndCmd(String.format("rmdir \"%s\"", path));

            if (f.exists())
                return false;
            else
                return true;
        } else {
            Log.d(TAG, String.format("mkdir: %s is not a directory", path));
            return false;
        }
    }

}

