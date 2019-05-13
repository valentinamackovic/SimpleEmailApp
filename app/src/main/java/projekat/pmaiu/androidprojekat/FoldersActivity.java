package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import model.Contact;
import model.Folder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class FoldersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    ListView listView;
    FoldersAdapter adapter ;
    private long mInterval = 0;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);
        mHandler = new Handler();

        Toolbar toolbar =  findViewById(R.id.toolbar_folders);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String syncTimeStr = pref.getString("refresh_rate", "0");
                mInterval= TimeUnit.MINUTES.toMillis(Integer.parseInt(syncTimeStr));

                Toast toast = Toast.makeText(getApplicationContext(), "Syncing...", Toast.LENGTH_SHORT);
                toast.show();

                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<List<Folder>> call = service.getAllFolders();
                call.enqueue(new Callback<List<Folder>>() {
                    @Override
                    public void onResponse(Call<List<Folder>> call, Response<List<Folder>> response) {
                        generateFoldersList(response.body());
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                if (adapter.getCount() > 0) {
                                    Folder value = (Folder) adapter.getItem(position);
                                    Intent i = new Intent(FoldersActivity.this, FolderActivity.class);
                                    i.putExtra("folder", value);
                                    startActivity(i);
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Empty contact-adapter", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Folder>> call, Throwable t) {
                        Toast.makeText(FoldersActivity.this, "Empty folder-adapter", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FloatingActionButton btnCreateFolder = findViewById(R.id.btnCreateFolder);
        btnCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(FoldersActivity.this, CreateFolderActivity.class));
                i.putExtra("action", "create");
                startActivity(i);
            }
        });

        startRepeatingTask();

        //Getting data from service
//        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
//        Call<List<Folder>> call = service.getAllFolders();
//        call.enqueue(new Callback<List<Folder>>() {
//            @Override
//            public void onResponse(Call<List<Folder>> call, Response<List<Folder>> response) {
//                generateFoldersList(response.body());
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                        if(adapter.getCount() > 0){
//                            Folder value=(Folder) adapter.getItem(position);
//                            Intent i = new Intent(FoldersActivity.this, FolderActivity.class);
//                            i.putExtra("folder", value);
//                            startActivity(i);
//                        }else{
//                            Toast toast = Toast.makeText(getApplicationContext(), "Empty folder-adapter", Toast.LENGTH_SHORT);
//                            toast.show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call<List<Folder>> call, Throwable t) {
//
//                Toast.makeText(FoldersActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    public void generateFoldersList(List<Folder> folders){
        listView = findViewById(R.id.folders_list_view);
        adapter = new FoldersAdapter(this, folders);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        registerForContextMenu(listView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
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
            startActivity(new Intent(FoldersActivity.this, ContactsActivity.class));
        } else if (id == R.id.nav_emails) {
            startActivity(new Intent(FoldersActivity.this, EmailsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(FoldersActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_folders) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(id == R.id.nav_logout){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("username");
            editor.remove("password");
            editor.commit();
            startActivity(new Intent(FoldersActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Folder selected_folder;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Folder folder = (Folder) lv.getItemAtPosition(acmi.position);

        selected_folder = folder;
        menu.add("Update");
        menu.add("Delete");
        menu.setHeaderTitle(folder.getName());

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle() == "Update"){
            Intent i = new Intent(FoldersActivity.this, CreateFolderActivity.class);
            i.putExtra("action", "update");

            i.putExtra("folder", selected_folder);
            startActivity(i);

        }else if(item.getTitle() == "Delete"){
            Toast.makeText(getApplicationContext(),"Delete",Toast.LENGTH_SHORT).show();
        }
        else{
            return false;
        }
        return true;
    }
}
