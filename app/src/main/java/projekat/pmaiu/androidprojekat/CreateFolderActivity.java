package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import enums.Condition;
import enums.Operation;
import model.Contact;
import model.Folder;
import model.Rule;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class CreateFolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_folder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_folder);
        toolbar.setTitle(getIntent().getStringExtra("action") + " folder");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_folder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save_folder){

            EditText txtFolderName = findViewById(R.id.folder_name_create_folder_activity);
            EditText txtword= findViewById(R.id.word_create_folder_activity);
            final String folderName= txtFolderName.getText().toString();
            Spinner spinner = (Spinner)findViewById(R.id.spinnerCondition);
            String selectedCondition = spinner.getSelectedItem().toString();
            Spinner spinner1 = (Spinner)findViewById(R.id.spinnerOperation);
            String selectedOperation = spinner1.getSelectedItem().toString();

            Rule r = new Rule();
            r.id = hashCode();
            Condition e = Condition.valueOf(selectedCondition);
            r.condition = e;
            Operation o = Operation.valueOf(selectedOperation);
            r.operation = o;
            String word=txtword.getText().toString().trim();

            Folder f = new Folder();
            f.setName(txtFolderName.getText().toString());
            f.setRule(r);
            f.setWord(word);

            if(folderName.equals("")){
                Toast.makeText(CreateFolderActivity.this, "Please enter folder name!", Toast.LENGTH_SHORT).show();
            }
            else{
                SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                int userId = uPref.getInt("loggedInUserId",-1);

                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Folder> createFolder = service.createFolder(f, userId);
                createFolder.enqueue(new Callback<Folder>() {
                    @Override
                    public void onResponse(Call<Folder> call, Response<Folder> response) {
                        Toast.makeText(CreateFolderActivity.this, "Created new folder!", Toast.LENGTH_SHORT).show();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("name", folderName);
                        editor.commit();

                        startActivity(new Intent(CreateFolderActivity.this, FoldersActivity.class));
                        finish();


                    }

                    @Override
                    public void onFailure(Call<Folder> call, Throwable t) {
                        Toast.makeText(CreateFolderActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else if(item.getItemId() == R.id.action_cancel_creating_folder){
            onBackPressed();
        }


        return true;
    }

}
