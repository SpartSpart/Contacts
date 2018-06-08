package com.spart.spart.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import static android.support.v4.content.ContextCompat.startActivity;

public class SendEmail extends AppCompatActivity {
    String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";
   // String path = baseDir + File.separator + "Contacts (01.06.2018.vcf)";

    public void send() {
        String filename = "Contacts (01.06.2018.vcf)";
        File filelocation = new File(baseDir, filename);
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
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
