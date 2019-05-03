package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import model.Attachment;
import model.Contact;
import model.Message;

public class EmailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    ListView listView ;
    CustomListAdapterEmails adapter = new CustomListAdapterEmails(this, messages);
    public static ArrayList<Message> messages = new ArrayList<>();

    static{
        Message message = new Message();
        message.setId(0);
        message.setContent("Ovo je neki sadrzaj nekog maila :)");
        message.setSubject("Subject nekog maila");
        Contact contact=new Contact();
        contact.setId(0);
        contact.setFirstName("Marko");
        contact.setLastName("Markovic");
        contact.setEmail("adasd@gmail.com");
        Attachment a = new Attachment();
        a.setId(0);
        a.setName("Attachment 1");
        message.setAttachments(new ArrayList<Attachment>(Arrays.asList(a)));
        message.setFrom(contact);
        message.setDateTime(new Date());
        message.setTo(new ArrayList<Contact>(Arrays.asList(contact)));
        messages.add(message);
        Message message1 = new Message();
        message1.setId(1);
        message1.setContent("Ovo je neki sadrzaj nekog maila od Ane :)");
        message1.setSubject("Subject maila");
        Contact contact1=new Contact();
        contact1.setId(1);
        contact1.setFirstName("Ana");
        contact1.setLastName("Anic");
        contact1.setEmail("anaa@gmail.com");
        Attachment a1 = new Attachment();
        a1.setId(1);
        a1.setName("Attachment 2");
        message1.setAttachments(new ArrayList<Attachment>(Arrays.asList(a)));
        message1.setFrom(contact1);
        message1.setDateTime(new Date());
        message1.setTo(new ArrayList<Contact>(Arrays.asList(contact)));
        messages.add(message1);
        Message message2 = new Message();
        message2.setId(2);
        message2.setContent("Ovo je neki sadrzaj nekog maila od Petra :)");
        message2.setSubject("Subject maila");
        Contact contact2=new Contact();
        contact2.setId(2);
        contact2.setFirstName("Petar");
        contact2.setLastName("Petrovic");
        contact2.setEmail("petar@gmail.com");
        Attachment a2 = new Attachment();
        a2.setId(2);
        a2.setName("Attachment 3");
        message2.setAttachments(new ArrayList<Attachment>(Arrays.asList(a)));
        message2.setFrom(contact2);
        message2.setDateTime(new Date());
        message2.setTo(new ArrayList<Contact>(Arrays.asList(contact)));
        messages.add(message2);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emails);
        listView = findViewById(R.id.listView_emails);

        listView.setAdapter(adapter);

        Toolbar toolbar =  findViewById(R.id.toolbar_emails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FloatingActionButton btnCreate = findViewById(R.id.btnCreateEmailAction);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, CreateEmailActivity.class));
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, ProfileActivity.class));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Message value=(Message) adapter.getItem(position);
                Intent i = new Intent(EmailsActivity.this, EmailActivity.class);
                i.putExtra("message", value);
                startActivity(i);
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
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_contacts) {
            startActivity(new Intent(EmailsActivity.this, ContactsActivity.class));
        } else if (id == R.id.nav_emails) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(EmailsActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_folders) {
            startActivity(new Intent(EmailsActivity.this, FoldersActivity.class));
        }else if(id == R.id.nav_logout){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("username");
            editor.remove("password");
            editor.commit();
            startActivity(new Intent(EmailsActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}