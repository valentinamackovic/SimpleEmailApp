package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;

import model.Contact;
import model.Message;

public class LoginActivity extends AppCompatActivity {

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
        super.onResume();
        Button btnStartEmailsActivity = (Button) findViewById(R.id.btnEmails);
        btnStartEmailsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, EmailsActivity.class));
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
}
