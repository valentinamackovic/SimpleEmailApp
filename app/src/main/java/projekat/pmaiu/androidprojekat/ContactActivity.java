package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import model.Contact;
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
    public static final int PICK_IMAGE = 1;
    File myImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if (!prefTheme.getBoolean("dark_mode", false)) {
            setTheme(R.style.AppThemeLight);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_contact_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();
        contact = (Contact)i.getSerializableExtra("contact");
        imgView = findViewById(R.id.imgContact);

        if( contact.getPhoto()!=null) {
            byte[] decodedString = android.util.Base64.decode(contact.getPhoto().getData(), android.util.Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgView.setImageBitmap(decodedByte);
        }
//        else if(contact.getPhoto().getPath().contains("http")) {
//            Picasso.with(this).load(contact.getPhoto().getPath()).into(picassoImageTarget(getApplicationContext(), "imageDir", "imageFromCOntact" + contact.getId()));
//            ContextWrapper cw = new ContextWrapper(getApplicationContext());
//            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//            myImageFile = new File(directory, "imageFromCOntact" + contact.getId());
//            Log.e("image", "myImageFile" + myImageFile.getAbsolutePath());
//            Picasso.with(this).load(myImageFile).into(imgView);
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        txtFirst = findViewById(R.id.txtFirst);
        txtLast = findViewById(R.id.txtLast);
        txtEmail = findViewById(R.id.txtEmail);

        imgView.setClickable(true);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);
            }
        });

        txtFirst.setText(contact.getFirstName());
        txtLast.setText(contact.getLastName());
        txtEmail.setText((contact.getEmail()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(myImageFile!=null)
            Picasso.with(this).invalidate(myImageFile);
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
                String encodedString = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
//                zavrseno

                File cache = new File(getApplicationContext().getCacheDir(), "picasso-cache");
                deleteDir(cache);
                contact.getPhoto().setData(encodedString);

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
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btnSaveContact) {
            if (validation()) {
                contact.setFirstName(txtFirst.getText().toString());
                contact.setLastName(txtLast.getText().toString());
//            contact.setPhoto();
                contact.setEmail(txtEmail.getText().toString());

                SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                int userId = uPref.getInt("loggedInUserId",-1);

                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Contact> update = service.updateContact(contact, userId);
                update.enqueue(new Callback<Contact>() {
                    @Override
                    public void onResponse(Call<Contact> call, Response<Contact> response) {
                        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
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
            onBackPressed();
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

        if(!message.equals(""))
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        return  pass;
    }


    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.e("picassoImageTarget", imageDir);
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        if (!directory.exists()) {
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
                        Log.e("image", "u run metodi");
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            Log.e("image", "u try metodi");
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
                if (placeHolderDrawable != null) {
                }
            }
        };
    }
}
