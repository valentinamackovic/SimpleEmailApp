package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import model.Contact;
import model.Message;

public class ContactActivity extends AppCompatActivity {

    public static Contact contact= null;
    static {
        contact=new Contact();
        contact.setId(0);
        contact.setFirstName("Marko");
        contact.setLastName("Markovic");
        contact.setEmail("adasd@gmail.com");
        Message m=new Message();
        m.setId(0);
        m.setSubject("Subject ");
        m.setContent("Ubicu se koliko me smara ovaj odel podataka, nista ne kontam, boze pomozi.");
        m.setDateTime(new Date());
//        m.getTo().add(contact);
//        m.setFrom(contact);
//        contact.getMessagesFrom().add(m);
//        contact.getMessagesTo().add(m);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact_activity);
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

        EditText txtFirst = findViewById(R.id.txtFirst);
        EditText txtLast = findViewById(R.id.txtLast);
        EditText txtEmail = findViewById(R.id.txtEmail);

        txtFirst.setText(contact.getFirstName());
        txtLast.setText(contact.getLastName());
        txtEmail.setText((contact.getEmail()));

        Button btnContactSave = findViewById(R.id.btnContactSave);
        btnContactSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
            }
        });


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
}
