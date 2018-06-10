package com.spart.spart.contacts;

import android.os.StrictMode;
import android.util.Log;
import java.io.FileInputStream;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;


public class SendToNas {

    String USER_NAME;// ="111";
    String PASSWORD;// = "12345678";
    String NETWORK_FOLDER;// = "smb://192.168.1...";
    public boolean copyFiles(FileInputStream file, String fileName) {
        boolean successful = false;
        SmbFileOutputStream sfos;
        SmbFile sFile;
        String path;
        NtlmPasswordAuthentication auth;
        try{
            String user = USER_NAME + ":" + PASSWORD;

            auth = new NtlmPasswordAuthentication(user);
            StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
            StrictMode.setThreadPolicy(tp);

            path = NETWORK_FOLDER + fileName;

            sFile = new SmbFile(path, auth);
            sfos = new SmbFileOutputStream(sFile);

            long t0 = System.currentTimeMillis();

            byte[] b = new byte[8192];
            int n, tot = 0;
            Log.d("asdf","initiating : total="+tot);


            while((n = file.read(b))>0){
                sfos.write( b, 0, n );
                tot += n;
                Log.d("asdf","writing : total="+tot);
            }
            successful = true;
            Log.d("asdf","Successful : total="+tot);

        }

        catch (Exception e) {
            successful = false;
            e.printStackTrace();
            Log.d("asdf","exxeption ");

        }
        return successful;
    }

     void setAuthorization(String login,String password,String folder){
         USER_NAME=login;
         PASSWORD = password;
         NETWORK_FOLDER = "smb:"+folder;

    }
}
