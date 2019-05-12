package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import model.Account;
import model.Attachment;
import model.Contact;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class ContactActivity extends AppCompatActivity {
    static Contact contact;

    EditText txtFirst;
    EditText txtLast;
    EditText txtEmail;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact_activity);
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

        Intent i = getIntent();
        contact = (Contact)i.getSerializableExtra("contact");

        txtFirst = findViewById(R.id.txtFirst);
        txtLast = findViewById(R.id.txtLast);
        txtEmail = findViewById(R.id.txtEmail);
        imgView=findViewById(R.id.imgContact);

        Picasso.with(this).load(contact.getPhoto().getPath()).into(picassoImageTarget(getApplicationContext(), "imageDir", "imageFromCOntact"+contact.getId()));
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Log.e("dir", "directory: " + directory.getAbsolutePath());
        File myImageFile = new File(directory, "imageFromCOntact"+contact.getId());
        Log.e("image", "myImageFile" + myImageFile.getAbsolutePath());
        Picasso.with(this).load(myImageFile).into(imgView);

        txtFirst.setText(contact.getFirstName());
        txtLast.setText(contact.getLastName());
        txtEmail.setText((contact.getEmail()));

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
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnSaveContact) {
            if(validation()) {
                contact.setFirstName(txtFirst.getText().toString());
                contact.setLastName(txtLast.getText().toString());
//            contact.setPhoto();
                contact.setEmail(txtEmail.getText().toString());

                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Contact> update = service.updateContact(contact);
                update.enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
//                        SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.remove("username");
//                        editor.remove("password");
//                        editor.commit();
                        startActivity(new Intent(ContactActivity.this, ContactsActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Contact> call, Throwable t) {
                        Toast.makeText(ContactActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else
            super.onBackPressed();
        return true;
    }

    public boolean validation(){
        boolean pass=true;
        String message="";

        if(txtFirst.getText().equals("")){
            pass=false;
            message+="Field first name is required! \n";
        }
        else if(txtLast.getText().equals("")){
            pass=false;
            message+="Field last name is required! \n";
        }
        else if(txtEmail.getText().equals("")){
            pass=false;
            message+="Field email is required! \n";
        }
        else if(!txtEmail.getText().toString().contains("@")){
            pass=false;
            message+="Email is not valid! \n";
        }
        else if(!txtEmail.getText().toString().contains(".")){
            pass=false;
            message+="Email is not valid! \n";
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

        return  pass;
    }

    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.e("picassoImageTarget", imageDir);
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        if(!directory.exists())
        {
            directory.mkdir();
            Log.e("MKDIR", "inside mkdir");
        }
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        Log.e("image", "u run metodi" );
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            Log.e("image", "u try metodi" );
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("image", "image saved to >>>" + myImageFile.getAbsolutePath());
                        Log.e("image", "image saved to >>>" + myImageFile.getAbsolutePath());
                        Log.e("image", "image saved to >>>" + myImageFile.getAbsolutePath());
                        Log.e("image", "image saved to >>>" + myImageFile.getAbsolutePath());
                        Log.e("image", "image saved to >>>" + myImageFile.getAbsolutePath());
//                        Toast.makeText(getApplicationContext(), "image saved to >>>" + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }
}
