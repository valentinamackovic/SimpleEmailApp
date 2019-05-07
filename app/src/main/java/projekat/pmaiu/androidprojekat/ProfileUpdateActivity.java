package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import model.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class ProfileUpdateActivity extends AppCompatActivity {

    EditText txtUsername;
    EditText txtPassword;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        Toolbar toolbar =  findViewById(R.id.toolbar_update_profile);
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
        txtUsername = findViewById(R.id.update_profile_username);
        txtPassword = findViewById(R.id.update_profile_password);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0);

        txtUsername.setText(pref.getString("username", "emptyVal"));
        txtPassword.setText(pref.getString("password","emptyVal"));
        id = pref.getInt("loggedInUserId",-1);

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
        getMenuInflater().inflate(R.menu.update_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_cancel_creating_profile){
            onBackPressed();
            return true;
        }
        else if(item.getItemId() == R.id.action_save_profile){
            EditText txtUsername = findViewById(R.id.update_profile_username);
            String username= txtUsername.getText().toString();
            EditText txtPassword = findViewById(R.id.update_profile_password);
            String password= txtPassword.getText().toString();

            if(username.equals("")){
                Toast.makeText(ProfileUpdateActivity.this, "Please enter your username!", Toast.LENGTH_SHORT).show();

            }else if(password.equals("")){
                Toast.makeText(ProfileUpdateActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();

            }else{
                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Account> update = service.updateProfile(id,username, password);
                update.enqueue(new Callback<Account>() {
                    @Override
                    public void onResponse(Call<Account> call, Response<Account> response) {
                        Toast.makeText(ProfileUpdateActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("username");
                        editor.remove("password");
                        editor.commit();
                        startActivity(new Intent(ProfileUpdateActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Account> call, Throwable t) {
                        Toast.makeText(ProfileUpdateActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return true;
    }
}
