package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Account;
import model.Contact;
import model.Folder;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class LoginActivity extends AppCompatActivity {

    public static Account loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        EditText txtUsername = findViewById(R.id.txtUsername);
        EditText txtPassword = findViewById(R.id.txtPassword);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0);

        txtUsername.setText(pref.getString("username", null));
        txtPassword.setText(pref.getString("password", null));
        super.onResume();
        Button btnStartEmailsActivity = (Button) findViewById(R.id.btnEmails);
        btnStartEmailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtUsername = findViewById(R.id.txtUsername);
                EditText txtPassword = findViewById(R.id.txtPassword);
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                if(username.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter your username!", Toast.LENGTH_SHORT).show();

                }else if(password.equals("")){
                    Toast.makeText(LoginActivity.this, "Please enter your password!", Toast.LENGTH_SHORT).show();

                }else{
                    IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                    Call<Account> login = service.login(username, password);
                    login.enqueue(new Callback<Account>() {
                        @Override
                        public void onResponse(Call<Account> call, Response<Account> response) {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0);
                            SharedPreferences.Editor editor = pref.edit();
                            Account user = response.body();
                            editor.putString("username", user.username);
                            editor.putString("password", user.password);
                            editor.putInt("loggedInUserId",user.id);
                            editor.putString("email",user.email);
                            loggedInUser=user;
                            editor.commit();


                            startActivity(new Intent(LoginActivity.this, EmailsActivity.class));
                            finish();

                        }

                        @Override
                        public void onFailure(Call<Account> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }



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
    public void onBackPressed() {

    }
}
