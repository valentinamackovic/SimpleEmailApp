package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
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

import model.Account;
import model.Attachment;
import model.Contact;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class ContactActivity extends AppCompatActivity {
    static Contact contact;

    EditText txtFirst;
    EditText txtLast;
    EditText txtEmail;

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

        Intent i = getIntent();
        contact = (Contact)i.getSerializableExtra("contact");

        txtFirst = findViewById(R.id.txtFirst);
        txtLast = findViewById(R.id.txtLast);
        txtEmail = findViewById(R.id.txtEmail);

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
        if(item.getItemId() == R.id.btnSaveContact) {
            if(validation()) {
                contact.setFirstName(txtFirst.getText().toString());
                contact.setLastName(txtLast.getText().toString());
//            contact.setPhoto();
                contact.setEmail(txtEmail.getText().toString());

                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Contact> update = service.updateContact(contact);
                update.enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.remove("username");
//                        editor.remove("password");
//                        editor.commit();
                        startActivity(new Intent(ContactActivity.this, ContactsActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Contact> call, Throwable t) {
                        Toast.makeText(ContactActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else
            super.onBackPressed();
        return true;
    }

    public boolean validation(){
        boolean pass=true;
        String message="";

        if(txtFirst.getText().equals("")){
            pass=false;
            message+="Field first name is required! \n";
        }
        else if(txtLast.getText().equals("")){
            pass=false;
            message+="Field last name is required! \n";
        }
        else if(txtEmail.getText().equals("")){
            pass=false;
            message+="Field email is required! \n";
        }
        else if(!txtEmail.getText().toString().contains("@")){
            pass=false;
            message+="Email is not valid! \n";
        }
        else if(!txtEmail.getText().toString().contains(".")){
            pass=false;
            message+="Email is not valid! \n";
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        return  pass;
    }
}
