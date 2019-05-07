package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

//    public static ArrayList<Contact> contacts=new ArrayList<>();
//    public static Contact user=null;
//
//    static{
//        for(int i=0; i<3;i++){
//            Contact c=new Contact();
//            c.setId(i);
//            c.setFirstName("Ime "+ i);
//            c.setLastName("Prezime "+ i);
//            c.setEmail("m@gmail.com");
//            for(int j=0;j<5;j++){
//                Message m=new Message();
//                m.setId(j);
//                m.setSubject("Subject "+ j);
//                m.setContent("Bla bla bla, neki sadrzaj "+ j);
//                m.setDateTime(new Date());
//                m.getTo().add(c);
//                m.setFrom(c);
//                c.getMessagesFrom().add(m);
//                c.getMessagesTo().add(m);
//            }
//            contacts.add(c);
//        }
//        user=new Contact();
//        user.setId(0);
//        user.setEmail("mimcnbdjvs@gmail.com");
//        user.setLastName("Mackovic");
//        user.setFirstName("Tina");
//        Message m=new Message();
//        m.setId(0);
//        m.setSubject("Subject ");
//        m.setContent("Ubicu se koliko me smara ovaj odel podataka, nista ne kontam, boze pomozi.");
//        m.setDateTime(new Date());
//        m.getTo().add(user);
//        m.setFrom(user);
//        user.getMessagesFrom().add(m);
//        user.getMessagesTo().add(m);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                            loggedInUser=user;
                            editor.commit();

                            Toast.makeText(LoginActivity.this, user.username, Toast.LENGTH_SHORT).show();
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
