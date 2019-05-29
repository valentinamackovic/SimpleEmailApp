package projekat.pmaiu.androidprojekat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import model.Account;
import model.Contact;
import model.Photo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class CreateContactActivity extends AppCompatActivity {

    ImageView imgView;
    public static final int PICK_IMAGE = 1;
    String encodedString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_contact_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        imgView = findViewById(R.id.imgCreateContact);
        imgView.setClickable(true);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                final Bitmap img=Bitmap.createScaledBitmap(selectedImage, 80, 80, true);

//                pretvaranje u base64 da se posalje
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                encodedString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
//                zavrseno

                File cache = new File(getApplicationContext().getCacheDir(), "picasso-cache");
                deleteDir(cache);
//                contact.getPhoto().setData(encodedString);

                imgView.setImageBitmap(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_contact_menu, menu);
        return true;
    }

    public boolean validation(){
        boolean pass=true;
        String contact="";
        EditText txtEmail = findViewById(R.id.txtEmailNew);

        if(!txtEmail.getText().toString().contains("@")){
            pass=false;
            contact+="Email is not valid! \n";
        }
        else if(!txtEmail.getText().toString().contains(".")){
            pass=false;
            contact+="Email is not valid! \n";
        }
        if(!contact.equals(""))
            Toast.makeText(getApplicationContext(), contact, Toast.LENGTH_LONG).show();
        return  pass;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnSaveNewContact) {
            if (validation()) {
                EditText txtFirstName = findViewById(R.id.txtFirstNew);
                final String firstName = txtFirstName.getText().toString();
                EditText txtLastName = findViewById(R.id.txtLastNew);
                final String lastName = txtLastName.getText().toString();
                EditText txtEmail = findViewById(R.id.txtEmailNew);
                final String email = txtEmail.getText().toString();

                Contact c = new Contact();
                c.setFirstName(txtFirstName.getText().toString());
                c.setLastName(txtLastName.getText().toString());
                c.setEmail(txtEmail.getText().toString());
                c.setPhoto(new Photo());
                c.getPhoto().setData(encodedString);

                if (firstName.equals("")) {
                    Toast.makeText(CreateContactActivity.this, "Please enter first name!", Toast.LENGTH_SHORT).show();

                } else if (lastName.equals("")) {
                    Toast.makeText(CreateContactActivity.this, "Please enter last name!", Toast.LENGTH_SHORT).show();

                } else if (email.equals("")) {
                    Toast.makeText(CreateContactActivity.this, "Please enter email!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                    int userId = uPref.getInt("loggedInUserId", -1);

                    IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                    Call<Contact> createContact = service.createContact(c, userId);
                    createContact.enqueue(new Callback<Contact>() {
                        @Override
                        public void onResponse(Call<Contact> call, Response<Contact> response) {
                            Toast.makeText(CreateContactActivity.this, "Created new contact!", Toast.LENGTH_SHORT).show();
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("firstName", firstName);
                            editor.putString("lastName", lastName);
                            editor.commit();
                            startActivity(new Intent(CreateContactActivity.this, ContactsActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Call<Contact> call, Throwable t) {
                            Toast.makeText(CreateContactActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }

        else
            onBackPressed();
        return true;
    }
}
