package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import model.Account;
import model.Contact;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class CreateContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_contact_activity);
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
    protected void onResume() { super.onResume(); }

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
        getMenuInflater().inflate(R.menu.create_contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnSaveNewContact) {
            EditText txtFirstName = findViewById(R.id.txtFirstNew);
            final String firstName= txtFirstName.getText().toString();
            EditText txtLastName = findViewById(R.id.txtLastNew);
            final String lastName= txtLastName.getText().toString();
            EditText txtEmail = findViewById(R.id.txtEmailNew);
            final String email= txtEmail.getText().toString();

            Contact c = new Contact();
            c.setFirstName(txtFirstName.getText().toString());
            c.setLastName(txtLastName.getText().toString());
            c.setEmail(txtEmail.getText().toString());

            if(firstName.equals("")){
                Toast.makeText(CreateContactActivity.this, "Please enter first name!", Toast.LENGTH_SHORT).show();

            }else if(lastName.equals("")){
                Toast.makeText(CreateContactActivity.this, "Please enter last name!", Toast.LENGTH_SHORT).show();

            }else if(email.equals("")){
                 Toast.makeText(CreateContactActivity.this, "Please enter email!", Toast.LENGTH_SHORT).show();
            }
            else{
                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Contact> createContact = service.createContact(c);
                createContact.enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                        Toast.makeText(CreateContactActivity.this, "Created new contact!", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("firstName", firstName);
                        editor.putString("lastName",lastName);
                        editor.commit();
                        startActivity(new Intent(CreateContactActivity.this, ContactsActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Contact> call, Throwable t) {
                        Toast.makeText(CreateContactActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        else
            onBackPressed();
        return true;
    }
}
