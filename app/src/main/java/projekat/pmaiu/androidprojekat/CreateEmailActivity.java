package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.Contact;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class CreateEmailActivity extends AppCompatActivity {

    private String[] myContacts;
    private ArrayList<String> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_email_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
        int userId = uPref.getInt("loggedInUserId",-1);

        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
        Call<ArrayList<Contact>> call = service.getAllContacts(userId);
        call.enqueue(new Callback<ArrayList<Contact>>() {
            @Override
            public void onResponse(Call<ArrayList<Contact>> call, Response<ArrayList<Contact>> response) {
                for (Contact c: response.body()) {
                    String contact = c.getFirstName() + " " + c.getLastName() + ", " + c.getEmail();
                    contacts.add(contact);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contact>> call, Throwable t) {
                Toast.makeText(CreateEmailActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, contacts);

        MultiAutoCompleteTextView actvTo = findViewById(R.id.autocomplete_to);
        actvTo.setThreshold(1);
        actvTo.setAdapter(adapter);
        actvTo.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        MultiAutoCompleteTextView actvCc = findViewById(R.id.autocomplete_cc);
        actvCc.setThreshold(1);
        actvCc.setAdapter(adapter);
        actvCc.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        MultiAutoCompleteTextView actvBcc = findViewById(R.id.autocomplete_bcc);
        actvBcc.setThreshold(1);
        actvBcc.setAdapter(adapter);
        actvBcc.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();




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
        getMenuInflater().inflate(R.menu.create_email_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnCreateEmailSend)
            Toast.makeText(getApplicationContext(), "Sent!", Toast.LENGTH_SHORT).show();
        else if(item.getItemId() == R.id.btnCreateEmailCancel)
            onBackPressed();
        else if(item.getItemId() == R.id.btnCreateEmailAttachment)
            Toast.makeText(getApplicationContext(), "Attachment added!", Toast.LENGTH_SHORT).show();
        else
            onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        EditText txtTo = findViewById(R.id.autocomplete_to);
        EditText txtSubject = findViewById(R.id.txt_email_subject_input);
        EditText txtCc = findViewById(R.id.autocomplete_cc);
        EditText txtBcc = findViewById(R.id.autocomplete_bcc);
        EditText txtContent = findViewById(R.id.txt_email_content_input);

        Message message = new Message();

        int br = 0;
        if(txtTo.getText().toString().length() > 0){
            message.setTo(txtTo.getText().toString());
            br++;
        }if(txtSubject.getText().toString().length() > 0){
            message.setSubject(txtSubject.getText().toString());
            br++;
        }if(txtCc.getText().toString().length() > 0){
            message.setCc(txtCc.getText().toString());
            br++;
        }if(txtBcc.getText().toString().length() > 0){
            message.setBcc(txtBcc.getText().toString());
            br++;
        }if(txtContent.getText().toString().length() > 0){
            message.setContent(txtContent.getText().toString());
            br++;
        }

        if(br > 0 ) {
            SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
            int userId = uPref.getInt("loggedInUserId", -1);

            IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
            Call<Message> call = service.saveToDraft(message, userId);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Toast.makeText(getApplicationContext(), "Saved to drafts!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong ...", Toast.LENGTH_LONG).show();
                }
            });
        }

        super.onBackPressed();
    }
}
