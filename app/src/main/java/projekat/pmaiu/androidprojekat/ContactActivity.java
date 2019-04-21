package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import model.Attachment;
import model.Contact;
import model.Message;

public class ContactActivity extends AppCompatActivity {

//    sve ovo ----------------------------------
    ListView listViewTo ;
    ListView listViewFrom ;
    ListView listViewCc ;
    ListView listViewBcc ;
    CustomListAdapterEmails adapterTo = new CustomListAdapterEmails(this, messagesTo);
    public static ArrayList<Message> messagesTo = new ArrayList<>();
    CustomListAdapterEmails adapterFrom = new CustomListAdapterEmails(this, messagesFrom);
    public static ArrayList<Message> messagesFrom = new ArrayList<>();
    CustomListAdapterEmails adapterCc = new CustomListAdapterEmails(this, messagesCc);
    public static ArrayList<Message> messagesCc = new ArrayList<>();
    CustomListAdapterEmails adapterBcc = new CustomListAdapterEmails(this, messagesBcc);
    public static ArrayList<Message> messagesBcc = new ArrayList<>();

    static {
        for(int i=0; i<1; i++){
            Message message = new Message();
            message.setId(i);
            message.setSubject("Subject " + i);
            Contact contact=new Contact();
            contact.setId(i);
            contact.setFirstName("Ime "+ i);
            contact.setLastName("Markovic");
            contact.setEmail("adasd@gmail.com");
            message.setFrom(contact);
            messagesTo.add(message);
            messagesFrom.add(message);
            messagesCc.add(message);
            messagesBcc.add(message);
        }
    }

//    ------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

//        ------------------------------------------------------------------------------------------------
        listViewTo = findViewById(R.id.listView_contact_messagesTo);
        listViewTo.setAdapter(adapterTo);

        listViewFrom = findViewById(R.id.listView_contact_messagesFrom);
        listViewFrom.setAdapter(adapterFrom);

        listViewCc = findViewById(R.id.listView_contact_messagesCc);
        listViewCc.setAdapter(adapterCc);

        listViewBcc = findViewById(R.id.listView_contact_messagesBcc);
        listViewBcc.setAdapter(adapterBcc);
//         --------------------------------------------------------------------------------------------------
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

        Intent i = getIntent();
        Contact contact = (Contact)i.getSerializableExtra("contact");

        EditText txtFirst = findViewById(R.id.txtFirst);
        EditText txtLast = findViewById(R.id.txtLast);
        EditText txtEmail = findViewById(R.id.txtEmail);

        txtFirst.setText(contact.getFirstName());
        txtLast.setText(contact.getLastName());
        txtEmail.setText((contact.getEmail()));

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
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnSaveContact)
            Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
        else
            onBackPressed();
        return true;
    }
}
