package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;
import java.util.List;

import model.Folder;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class FolderActivity extends AppCompatActivity {

    ListView listView;
    CustomListAdapterEmails adapter;

    ListView listViewFolders;
    FoldersAdapter foldersAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_folder_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        Folder folder = (Folder) i.getSerializableExtra("folder");

        adapter = new CustomListAdapterEmails(this, folder.getMessages());
        listView = findViewById(R.id.folder_list_view_emails);
        listView.setAdapter(adapter);

        foldersAdapter = new FoldersAdapter(this, folder.getChildFolders());
        listViewFolders = findViewById(R.id.folder_list_view_folders);
        listViewFolders.setAdapter(foldersAdapter);
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
        Intent i = getIntent();
        Folder folder = (Folder) i.getSerializableExtra("folder");
        TextView folderTitle = findViewById(R.id.toolbar_folder_activity_title);

        folderTitle.setText(folder.getName());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Message value=(Message) adapter.getItem(position);
                Intent i = new Intent(FolderActivity.this, EmailActivity.class);
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.folder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edit_folder){
            Toast toast = Toast.makeText(getApplicationContext(), "Edit", Toast.LENGTH_SHORT);
            toast.show();
        }
        else
            onBackPressed();

        return true;
    }
}
