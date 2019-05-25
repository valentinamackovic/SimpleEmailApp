package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import model.Attachment;
import model.Contact;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
        userId = uPref.getInt("loggedInUserId",-1);

        Intent i = getIntent();
        message = (Message)i.getSerializableExtra("message");

        Button btnReply = findViewById(R.id.btnReply);
        btnReply.setClickable(true);
        btnReply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Replied!",Toast.LENGTH_SHORT).show();
            }
        });

        Button btnReplyAll = findViewById(R.id.btnReplyToAll);
        btnReplyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Replied to all!",Toast.LENGTH_SHORT).show();
            }
        });

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

                        //napravi samo prazan fajl, a sve prodje
                        try {
                            Log.e("test","test");
                            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "abcde71.pdf"));
                            Log.e("test","downloads "+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                            fos.write(decodedString);
                            fos.flush();
                            fos.close();
                        }catch (Exception e){
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

        btnReply.setVisibility(View.VISIBLE);
        btnReplyAll.setVisibility(View.VISIBLE);

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
            Toast.makeText(getApplicationContext(), "Forwarded!", Toast.LENGTH_SHORT).show();
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
