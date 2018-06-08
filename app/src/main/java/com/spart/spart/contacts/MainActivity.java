package com.spart.spart.contacts;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    String TRANSFER_OK = "Transfer success";
    String TRANSFER_FAIL = "Transfer failed";

    Menu menu;
    Cursor cursor;
    ArrayList<String> vCard;
    String vfile;
    Context mContext;
    Button exportContacts;
    Button sendBtn;
    Button sendEmailBtn;
   static ProgressBar bar;
   ProgressBar spinnerBar;

   boolean transferNasDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vccard_export);
        exportContacts = findViewById(R.id.ExportContacts);
        bar = findViewById(R.id.progressBar);
        bar.setVisibility(ProgressBar.INVISIBLE);
        spinnerBar=findViewById(R.id.progressBarRound);
        spinnerBar.setVisibility(View.INVISIBLE);
        exportContacts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    mContext =getBaseContext();
                    String name =share.getString("filename","");
                    if (!name.equals(""))
                       new MyAsyncTaskExport().execute(name);
                    else {
                        Toast.makeText(getApplicationContext(), "Check the file name settings", Toast.LENGTH_LONG).show();
                        return;
                    }

                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
        sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               transferNasDone = false;
                new MyAsyncTaskNas().execute();
                sendingToNas();


            }
        });
        sendEmailBtn = findViewById(R.id.sendMailBtn);
        sendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.network_settings:
                Intent ChangePinObj = new Intent(this, SettingsActivity.class);
                startActivity(ChangePinObj);
                break;
        }
            return super.onOptionsItemSelected(item);
    }

    int progressBarValue = 0;

    private class MyAsyncTaskNas extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while(transferNasDone==false){
                try{
                    TimeUnit.MILLISECONDS.sleep(100);
                }catch (Exception ignore){}


            }


        return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinnerBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            spinnerBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    boolean sendingToNas(){
        Boolean success = false;
        transferNasDone = false;
        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            SendToNas send = new SendToNas();
            send.setAuthorization(share.getString("naslogin", ""),
                    share.getString("naspassword", ""),
                    share.getString("nasfolder", ""));
            String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";
            File lastfile = finder(baseDir);

            FileInputStream file = new FileInputStream(lastfile);
            success = send.copyFiles(file, lastfile.getName());


            if (success)
                Toast.makeText(getApplicationContext(), TRANSFER_OK, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), TRANSFER_FAIL, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), TRANSFER_FAIL, Toast.LENGTH_LONG).show();
        }
        transferNasDone = true;
        return success;
    }

    private class MyAsyncTaskExport extends AsyncTask<String,Integer,Void>{

        int count;
        @Override
        protected Void doInBackground(String... strings) {
            String timeStamp = new SimpleDateFormat(" (dd_MM_yyyy)").format(Calendar.getInstance().getTime());
            final String vfile = strings[0] + timeStamp + ".vcf";
            String path="";

            final Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            phones.moveToFirst();
            try {
                String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";
                File directory = new File(baseDir);
                if (!directory.exists())
                    directory.mkdirs();

                path = baseDir + File.separator + vfile;
                File file = new File(path);
                if (file.exists())
                    file.delete();
            } catch (Exception ignore) {}

            count = phones.getCount();
            bar.setProgress(0);
            bar.setMax(count-1);

                    for (int i = 0;i<count;i++)
                    {

                        try {
                            String lookupKey = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                            AssetFileDescriptor fd;
                            fd = mContext.getContentResolver().openAssetFileDescriptor(uri, "r");
                            FileInputStream fis = fd.createInputStream();
                            byte[] buf = new byte[(int) fd.getDeclaredLength()];
                            fis.read(buf);
                            String VCard = new String(buf);
                            FileOutputStream mFileOutputStream = new FileOutputStream(path, true);
                            mFileOutputStream.write(VCard.toString().getBytes());
                            phones.moveToNext();
                            Log.d("Vcard", VCard);

                            TimeUnit.MILLISECONDS.sleep(50);
                        } catch (Exception e1) {
                            Toast.makeText(mContext, e1.toString(), Toast.LENGTH_LONG).show();
                        }
                        publishProgress(i);
                    }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setProgress(0);
            bar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(mContext, "Export "+count+" records success", Toast.LENGTH_LONG).show();
        }
    }




    String baseDirr = Environment.getExternalStorageDirectory() + File.separator + "Files";


    public void sendMail() {
        String filename = "Contacts (01.06.2018.vcf)";
        File filelocation = new File(baseDirr, filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
        emailIntent.setType("plain/text");
        String to[] = {"spart_85@inbox.ru"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
       this.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }


    public File finder(String dirName){
        File dir = new File(dirName);
        File files[]= dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename)
            { return filename.endsWith(".vcf"); }
        } );
        Collections.sort(Arrays.asList(files), new Comparator<File>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public int compare(File f1, File f2) {
                return Long.compare(f1.lastModified(), f2.lastModified());
            }
        });
        return files[files.length-1];

    }



}






