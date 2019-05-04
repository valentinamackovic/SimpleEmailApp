package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.Contact;
import model.Folder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class ContactsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    ListView listView ;
    CustomListAdapterContacts adapter;
//    public static ArrayList<Contact> contactsList = new ArrayList<>();
//
//    static {
//        for(int i=0; i<9; i++){
//            Contact c=new Contact();
//            c.setEmail("email " + i);
//            c.setLastName("prezime "+ i);
//            c.setFirstName("ime "+ i);
//            c.setId(i);
//            contactsList.add(c);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar =  findViewById(R.id.toolbar_contacts);
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactsActivity.this, ProfileActivity.class));
            }
        });

        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
        Call<ArrayList<Contact>> call = service.getAllContacts();
        call.enqueue(new Callback<ArrayList<Contact>>() {
            @Override
            public void onResponse(Call<ArrayList<Contact>> call, Response<ArrayList<Contact>> response) {
                generateContactsList(response.body());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                        if(adapter.getCount() > 0){
                            Contact value=(Contact) adapter.getItem(position);
                            Intent i = new Intent(ContactsActivity.this, ContactActivity.class);
                            i.putExtra("contact", value);
                            startActivity(i);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "Empty contact-adapter", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<Contact>> call, Throwable t) {
                Toast.makeText(ContactsActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                // TODO Auto-generated method stub
//                Log.w("position------------", Integer.toString(position));
//                Contact value=(Contact) adapter.getItem(position);
//                Intent i = new Intent(ContactsActivity.this, ContactActivity.class);
//                i.putExtra("contact", value);
//                startActivity(i);
//            }
//        });

        FloatingActionButton btnCreateContact = findViewById(R.id.btnCreateContactAction);
        btnCreateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactsActivity.this, CreateContactActivity.class));
            }
        });
    }
    public void generateContactsList(ArrayList<Contact> contacts){
        listView = findViewById(R.id.listView_contacts);
        adapter = new CustomListAdapterContacts(this, contacts);
        listView.setAdapter(adapter);
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
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_emails) {
            startActivity(new Intent(ContactsActivity.this, EmailsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(ContactsActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_folders) {
            startActivity(new Intent(ContactsActivity.this, FoldersActivity.class));
        }else if(id == R.id.nav_logout){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("username");
            editor.remove("password");
            editor.commit();
            startActivity(new Intent(ContactsActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.contacts_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.action_new_contact)
//            startActivity(new Intent(ContactsActivity.this, CreateContactActivity.class));
//
//        return true;
//    }
}
