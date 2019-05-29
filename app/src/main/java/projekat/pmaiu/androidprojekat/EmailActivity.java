package projekat.pmaiu.androidprojekat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Attachment;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class EmailActivity extends AppCompatActivity {

    int id;
    Message message;
    private int userId = -1;
    LinearLayout ly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_email_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.e("intent","u email");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void showProgressDialog() {
        final int THREE_SECONDS = 3*1000;
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Dwonloading file...");
        dlg.setCancelable(false);
        dlg.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dlg.dismiss();
            }
        }, THREE_SECONDS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
        userId = uPref.getInt("loggedInUserId",-1);

        Intent i = getIntent();
        message = (Message)i.getSerializableExtra("message");

        TextView txtFrom = findViewById(R.id.textFrom1);
        TextView txtTo = findViewById(R.id.textTo);
        TextView txtSubject = findViewById(R.id.textSubject1);
        TextView txtContent = findViewById(R.id.textView6);
        TextView txtCc = findViewById(R.id.textEmailCc1);
        TextView txtDate = findViewById(R.id.textDate1);
        TextView txtFolder = findViewById(R.id.textFolder1);

        txtFrom.setText(message.getFrom());
        txtTo.setText(message.getTo());
        if(message.getDateTime() != null){
            Date datum = message.getDateTime();

            txtDate.setText(message.toISO8601UTC(datum));
        }

        txtSubject.setText(message.getSubject());
        txtContent.setText(message.getContent());
        txtCc.setText(message.getCc());

        Log.e("intent","u email u resume");

        //attachments
        ly=findViewById(R.id.linear_layout_attachment);

        LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List views = new ArrayList();
        if(message.getAttachments() != null){
            for(Attachment a : message.getAttachments()){
                final Attachment att=a;
                View view = layoutInflator.inflate(R.layout.attacment_row, null);

                ImageView imgView=view.findViewById(R.id.icon_attachment);
                imgView.setImageResource(R.drawable.icon_attachment);
                final TextView textView=view.findViewById(R.id.txt_attachment);

                textView.setText(a.getName());

//                kod za klik na att , da li radi sa vise atributa... id od atributa postaviti????
                textView.setTooltipText(Integer.toString(a.getId()));
                textView.setClickable(true);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //Get the URL entered
                        Attachment att= message.getAttachments().get(0);
                        String name = att.getName();
                        byte[] decodedString = android.util.Base64.decode(message.getAttachments().get(0).getData(), android.util.Base64.DEFAULT);
                        showProgressDialog();
                        try {
                            Log.e("test","pre ifa ");
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                Log.e("test","mimetype " +att.getType());
                                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                File file = new File(dir, "mail"+att.getName());
//                              promeniti da naziv bude kao naziv u att
                                FileOutputStream fos = new FileOutputStream(getFilesDir()+ "mail"+att.getName());
                                fos.write(decodedString);
                                Log.e("test","posle write files dir "+getFilesDir() );

                                fos.flush();
                                fos.close();
                                File src= new File(getFilesDir()+ "mail"+att.getName());
                                FileUtils.copyFile(src, file, true);
                            }
                            else {
                                ActivityCompat.requestPermissions(EmailActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }

                        }catch (Exception e){
                            Log.e("test","downloads u catsh"+e);
                            e.printStackTrace();
                        }
                    }
                });
                views.add(view);
            }
            for(int z = 0; z<views.size(); z++) {
                ly.addView((View) views.get(z));
            }
        }

        int userId = uPref.getInt("loggedInUserId",-1);

        if(message.isUnread()){
            readMessage(userId, message.getId());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ly.removeAllViews();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.email_menu, menu);
        return true;
   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnDeleteEmail)
            Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
        else if(item.getItemId() == R.id.btnEmailForward)
        {

            Intent i = new Intent(getBaseContext(), CreateEmailActivity.class);
            i.putExtra("subject", message.getSubject());
            i.putExtra("content", message.getContent());
            i.putExtra("from", message.getFrom());
            i.putExtra("to", message.getTo());
            Date datum = message.getDateTime();
            i.putExtra("date", message.toISO8601UTC(datum));
            ly=findViewById(R.id.linear_layout_attachment);

            LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            List views = new ArrayList();
            if(message.getAttachments() != null) {
                for (Attachment a : message.getAttachments()) {
                    View view = layoutInflator.inflate(R.layout.attacment_row, null);

                    ImageView imgView = view.findViewById(R.id.icon_attachment);
                    imgView.setImageResource(R.drawable.icon_attachment);
                    views.add(view);
                    i.putExtra("att", a);
                    i.putExtra("img", R.drawable.icon_attachment);
                }

            }
            for(int z = 0; z<views.size(); z++) {
                ly.addView((View) views.get(z));
            }

            startActivity(i);
        }
        else if(item.getItemId() == R.id.btnOptionReply){
                    Intent i = new Intent(getBaseContext(), CreateEmailActivity.class);
                    i.putExtra("subject1", message.getSubject());
                    i.putExtra("content1", message.getContent());
                    i.putExtra("to1", message.getFrom());
                    Date datum = message.getDateTime();
                    i.putExtra("date1", message.toISO8601UTC(datum));

                    ly=findViewById(R.id.linear_layout_attachment);

                    LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    List views = new ArrayList();
                    if(message.getAttachments() != null) {
                        for (Attachment a : message.getAttachments()) {
                            View view1 = layoutInflator.inflate(R.layout.attacment_row, null);

                            //ImageView imgView = view.findViewById(R.id.icon_attachment);
                            //imgView.setImageResource(R.drawable.icon_attachment);
                            //Bitmap bitmap = imgView.getDrawingCache();
                            // i.putExtra("img", bitmap);
                            i.putExtra("att1", a);
                            i.putExtra("img", R.drawable.icon_attachment);
                        }
                    }
                    for(int z = 0; z<views.size(); z++) {
                        ly.addView((View) views.get(z));
                    }
                    startActivity(i);
        }
        else if(item.getItemId() == R.id.btnOptionsToAll){
            Intent ii = new Intent(getBaseContext(), CreateEmailActivity.class);
            ii.putExtra("msgReplyAll", message);
//            ii.putExtra("subject11", message.getSubject());
//            ii.putExtra("content11", message.getContent());
//            SharedPreferences prefForUser = getApplicationContext().getSharedPreferences("MailPref", 0);
//            String loggedInUserEmail = prefForUser.getString("email", "");
//            if(message.getTo().contains(loggedInUserEmail)){
//                message.getTo().replace(loggedInUserEmail, "");
//                ii.putExtra("to11", message.getTo());
//            }
//            ii.putExtra("from11", message.getFrom());
//            Date datum = message.getDateTime();
//            ii.putExtra("date11", message.toISO8601UTC(datum));
//
//            ly=findViewById(R.id.linear_layout_attachment);
//
//            LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            List views = new ArrayList();
//            if(message.getAttachments() != null) {
//                for (Attachment a : message.getAttachments()) {
//                    View view = layoutInflator.inflate(R.layout.attacment_row, null);
//
//                    ImageView imgView = view.findViewById(R.id.icon_attachment);
//                    imgView.setImageResource(R.drawable.icon_attachment);
//                    //ii.putExtra("att11", a.getName());
//                    ii.putExtra("att11", a);
//                    ii.putExtra("img", R.drawable.icon_attachment);
//
//                }
//
//                for(int z = 0; z<views.size(); z++) {
//                    ly.addView((View) views.get(z));
//                }
//            }
            startActivity(ii);
        }

        else
            onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void readMessage(int profileId, int messageId){
        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
        Call<ArrayList<Message>> call = service.readMessage(profileId, messageId);
        call.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {

            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, please try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
