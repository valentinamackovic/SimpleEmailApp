package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import model.Attachment;
import model.Contact;
import model.Message;

public class EmailActivity extends AppCompatActivity {

//    public static Message message = null;
//    static{
//        message = new Message();
//        message.setId(0);
//        message.setContent("Ovo je neki sadrzaj nekog maila :)");
//        message.setSubject("Subject nekog maila");
//        Contact contact=new Contact();
//        contact.setId(0);
//        contact.setFirstName("Marko");
//        contact.setLastName("Markovic");
//        contact.setEmail("adasd@gmail.com");
//        Message m=new Message();
//        m.setId(0);
//        m.setSubject("Subject ");
//        m.setContent("Ovo je sadrzaj nekog maila.");
//        m.setDateTime(new Date());
//        message.setFrom(contact);
//        message.setTo(new ArrayList<Contact>(Arrays.asList(contact)));
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        TextView txtAttachment = findViewById(R.id.textAttachment);

        txtFrom.setText(message.getFrom().getEmail());
        txtTo.setText(message.getTo().get(0).getEmail());
        txtSubject.setText(message.getSubject());
        txtContent.setText(message.getContent());
        txtAttachment.setText(message.getAttachments().get(0).getName());
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
