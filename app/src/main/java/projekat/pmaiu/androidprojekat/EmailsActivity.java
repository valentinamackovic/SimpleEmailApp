package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class EmailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emails);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button btnContact = findViewById(R.id.btnContact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailsActivity.this, ContactActivity.class));
            }
        });
        Button btnContacts = findViewById(R.id.btnContacts);
        btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailsActivity.this, ContactsActivity.class));
            }
        });
        Button btnCreateCOntact = findViewById(R.id.btnCreateContact);
        btnCreateCOntact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailsActivity.this, CreateContactActivity.class));
            }
        });
        Button btnEmail = findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailsActivity.this, EmailActivity.class));
            }
        });
        Button btnCreateEmail = findViewById(R.id.btnCreateEmail);
        btnCreateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmailsActivity.this, CreateEmailActivity.class));
            }
        });

        Button btnSettingsActivity = findViewById(R.id.btnSettingsActivity);
        btnSettingsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, SettingsActivity.class));
            }
        });

        Button btnCreateFolderActivity = findViewById(R.id.btnCreateFolderActivity);
        btnCreateFolderActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, CreateFolderActivity.class));

            }
        });

        Button btnFoldersActivity = findViewById(R.id.btnFoldersActivity);
        btnFoldersActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, FoldersActivity.class));

            }
        });

        Button btnFolderActivity = findViewById(R.id.btnFolderActivity);
        btnFolderActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, FolderActivity.class));

            }
        });

        Button btnProfileActivity = findViewById(R.id.btnProfileActivity);
        btnProfileActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, ProfileActivity.class));

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