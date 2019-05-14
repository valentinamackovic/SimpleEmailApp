package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import enums.Condition;
import model.Folder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class UpdateFolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }


        setContentView(R.layout.activity_update_folder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_update_folder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private Folder folder;

    @Override
    protected void onResume(

    ) {
        super.onResume();

        Button save = findViewById(R.id.btn_save_folder_update);

        folder = (Folder) getIntent().getSerializableExtra("folder");
        final EditText folderName = findViewById(R.id.folder_name_update_folder_activity);
        folderName.setText(folder.getName(), TextView.BufferType.EDITABLE);
        folderName.setSelection(folderName.getText().length());

        final Spinner condition = findViewById(R.id.spinnerConditionUpdate);
        final Spinner operation = findViewById(R.id.spinnerOperationUpdate);

        if(folder.getRule().condition.toString().toLowerCase().equals("to")){
            condition.setSelection(0);
        }else if(folder.getRule().condition.toString().toLowerCase().equals("from")){
            condition.setSelection(1);
        }else if(folder.getRule().condition.toString().toLowerCase().equals("cc")){
            condition.setSelection(2);
        }else if(folder.getRule().condition.toString().toLowerCase().equals("subject")){
            condition.setSelection(3);
        }

        if(folder.getRule().operation.toString().toLowerCase().equals("move")){
            operation.setSelection(0);
        }else if(folder.getRule().operation.toString().toLowerCase().equals("copy")){
            operation.setSelection(1);
        }else if(folder.getRule().operation.toString().toLowerCase().equals("delete")){
            operation.setSelection(2);
        }




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!folderName.getText().toString().equals("")){
                    IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                    Call<ArrayList<Folder>> update = service.updateFolder(folder.getId(), folderName.getText().toString(), operation.getSelectedItem().toString(), condition.getSelectedItem().toString());
                    update.enqueue(new Callback<ArrayList<Folder>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Folder>> call, Response<ArrayList<Folder>> response) {
                            startActivity(new Intent(UpdateFolderActivity.this, FoldersActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Folder>> call, Throwable t) {

                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter folder name", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
