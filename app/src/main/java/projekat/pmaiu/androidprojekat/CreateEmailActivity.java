package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private Message draft  = null;

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


        Message draftM = (Message) getIntent().getSerializableExtra("message");
        if(draftM != null && draftM.isDraft()){
            MultiAutoCompleteTextView txtTo = findViewById(R.id.autocomplete_to);
            MultiAutoCompleteTextView txtCc = findViewById(R.id.autocomplete_cc);
            MultiAutoCompleteTextView txtBcc = findViewById(R.id.autocomplete_bcc);
            EditText txtSubject = findViewById(R.id.txt_email_subject_input);
            EditText txtContent = findViewById(R.id.txt_email_content_input);

            txtTo.setText(draftM.getTo());
            txtCc.setText(draftM.getCc());
            txtBcc.setText(draftM.getBcc());
            txtSubject.setText(draftM.getSubject());
            txtContent.setText(draftM.getContent());

            draft = draftM;

        }



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
        if(item.getItemId() == R.id.btnCreateEmailSend) {
            final EditText txtTo = findViewById(R.id.autocomplete_to);
            final String to = txtTo.getText().toString();
            final EditText txtSubject = findViewById(R.id.txt_email_subject_input);
            final String subject = txtSubject.getText().toString();
            EditText txtCc = findViewById(R.id.autocomplete_cc);
            String cc = txtCc.getText().toString();
            EditText txtBcc = findViewById(R.id.autocomplete_bcc);
            String bcc = txtBcc.getText().toString();
            EditText txtContent = findViewById(R.id.txt_email_content_input);
            String content = txtContent.getText().toString();

            Message m = new Message();
            m.setTo(to);
            m.setSubject(subject);
            if(txtCc != null)
                m.setCc(cc);
            if(txtBcc != null)
                m.setBcc(bcc);
            m.setContent(content);
            m.setDateTime(new Date());
            m.setId(0);
            m.setFrom(EmailsActivity.loggedInUserEmail);

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0);
            String ime = pref.getString("username", "emptyVal");
            String email = pref.getString("email", "emptyVal");
            m.setFrom(ime + "," + email);

            if (to.equals("")) {
                Toast.makeText(CreateEmailActivity.this, "Please enter who you are sending to!", Toast.LENGTH_SHORT).show();
            } else if (subject.equals("")) {
                Toast.makeText(CreateEmailActivity.this, "Please enter subject of email!", Toast.LENGTH_SHORT).show();
            }
            else if (content.equals("")) {
                Toast.makeText(CreateEmailActivity.this, "Please enter content of email!", Toast.LENGTH_SHORT).show();
            }else {
                SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                int userId = uPref.getInt("loggedInUserId", -1);
                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Message> createMessage = service.createMessage(m, userId);
                createMessage.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Toast.makeText(getApplicationContext(), "Email has been sent!", Toast.LENGTH_SHORT).show();
                       /* SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("from", ime);
                        editor.putString("to", to);
                        editor.putString("subject",subject);
                        editor.commit();*/
                        startActivity(new Intent(CreateEmailActivity.this, EmailsActivity.class));
                        finish();

                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(CreateEmailActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        else if(item.getItemId() == R.id.btnCreateEmailCancel)

        if(draft == null){
            onBackPressed();
        }else{
            SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
            int userId = uPref.getInt("loggedInUserId", -1);
            IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
            Call<ArrayList<Message>> call = service.deleteDraft(draft.getId(), userId);
            call.enqueue(new Callback<ArrayList<Message>>() {
                @Override
                public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                    startActivity(new Intent(CreateEmailActivity.this, FoldersActivity.class));
                }

                @Override
                public void onFailure(Call<ArrayList<Message>> call, Throwable t) {

                }
            });

        }
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
        if(draft != null){
            message.setId(draft.getId());
            message.setSubject(draft.getSubject());
            message.setBcc(draft.getBcc());
        }else{
            message.setId(0);
        }

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
