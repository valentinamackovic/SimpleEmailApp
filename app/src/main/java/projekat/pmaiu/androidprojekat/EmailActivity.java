package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class EmailActivity extends AppCompatActivity {

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
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        Message message = (Message)i.getSerializableExtra("message");

        Button btnReply = findViewById(R.id.btnReply);
        btnReply.setOnClickListener(new View.OnClickListener() {
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

        txtFrom.setText(message.getFrom().getEmail());
        txtTo.setText(message.getTo().get(0).getFirstName());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date datum = (Date) message.getDateTime();
        txtDate.setText(df.format(datum));
        txtSubject.setText(message.getSubject());
        txtContent.setText(message.getContent());

        //attachments
        LinearLayout ly=findViewById(R.id.linear_layout_attachment);

        LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List views = new ArrayList();
        for(Attachment a : message.getAttachments()){
            View view = layoutInflator.inflate(R.layout.attacment_row, null);

            ImageView imgView=view.findViewById(R.id.icon_attachment);
            imgView.setImageResource(R.drawable.icon_attachment);
            TextView textView=view.findViewById(R.id.txt_attachment);

            textView.setText(a.getName());

            views.add(view);
        }
        for(int z = 0; z<views.size(); z++) {
            ly.addView((View) views.get(z));
        }
        btnReply.setVisibility(View.VISIBLE);
        btnReplyAll.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
}
