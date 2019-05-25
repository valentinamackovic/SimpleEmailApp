package projekat.pmaiu.androidprojekat;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import model.Attachment;
import model.Contact;
import model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.IMailService;
import service.MailService;

public class CreateEmailActivity extends AppCompatActivity {

    private String[] myContacts;
    private ArrayList<String> contacts = new ArrayList<>();
    private Message draft  = null;

    ImageView imgView;
    public static final int PICKFILE_RESULT_CODE = 1;
    String encodedString;
    public static ArrayList<Attachment> attachments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        attachments=new ArrayList<>();
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_create_email_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
        int userId = uPref.getInt("loggedInUserId",-1);

        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
        Call<ArrayList<Contact>> call = service.getAllContacts(userId);
        call.enqueue(new Callback<ArrayList<Contact>>() {
            @Override
            public void onResponse(Call<ArrayList<Contact>> call, Response<ArrayList<Contact>> response) {
                for (Contact c: response.body()) {
                    String contact = c.getFirstName() + " " + c.getLastName() + ": " + c.getEmail();
                    contacts.add(contact);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contact>> call, Throwable t) {
                Toast.makeText(CreateEmailActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, contacts);

        MultiAutoCompleteTextView actvTo = findViewById(R.id.autocomplete_to);
        actvTo.setThreshold(1);
        actvTo.setAdapter(adapter);
        actvTo.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        MultiAutoCompleteTextView actvCc = findViewById(R.id.autocomplete_cc);
        actvCc.setThreshold(1);
        actvCc.setAdapter(adapter);
        actvCc.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        MultiAutoCompleteTextView actvBcc = findViewById(R.id.autocomplete_bcc);
        actvBcc.setThreshold(1);
        actvBcc.setAdapter(adapter);
        actvBcc.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


        Message draftM = (Message) getIntent().getSerializableExtra("message");
        if(draftM != null && draftM.isDraft()){
            MultiAutoCompleteTextView txtTo = findViewById(R.id.autocomplete_to);
            MultiAutoCompleteTextView txtCc = findViewById(R.id.autocomplete_cc);
            MultiAutoCompleteTextView txtBcc = findViewById(R.id.autocomplete_bcc);
            EditText txtSubject = findViewById(R.id.txt_email_subject_input);
            EditText txtContent = findViewById(R.id.txt_email_content_input);

            txtTo.setText(draftM.getTo());
            txtCc.setText(draftM.getCc());
            txtBcc.setText(draftM.getBcc());
            txtSubject.setText(draftM.getSubject());
            txtContent.setText(draftM.getContent());

            draft = draftM;
        }

        imgView = findViewById(R.id.createEmailAddAttachment);
        imgView.setClickable(true);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("*/*");
                startActivityForResult(i, PICKFILE_RESULT_CODE);
            }
        });
    }
//  PICK CONTENT NE VRACA DOBAR URI
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && null != data) {
            TextView txtAttachmentName = findViewById(R.id.createEmailAttachment);
            Path pdfPath = Paths.get("/"+data.getData().getPath());

            Uri returnUri = data.getData();
            String uri=returnUri.toString().split("content:")[1];

//            ---------------
            Log.e("test", "ponovo ovo duagacko "+getPath(this, Uri.parse(uri)));
//            ----------------
            Cursor returnCursor =
                    getContentResolver().query(returnUri, null, null, null, null);

            int nameIndex =returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            txtAttachmentName.setText(returnCursor.getString(nameIndex));
//            ovaj size RADI(?), NIJE 0
            int size=returnCursor.getColumnIndex(OpenableColumns.SIZE);

            //izvuce neku velicinu fajla ali dobijem string u obliku AAA ???????
            byte[] bytes = new byte[size];
            try {
//                ovo je sve za test, ne radi
                Log.e("test", "nov nacin u try ");
                Log.e("test","nov nacin 3 "+ pdfPath);

                final byte[] bytesTest = Files.readAllBytes(pdfPath);
                Log.e("test", "nov nacin bytes "+ bytesTest.toString());
                encodedString = android.util.Base64.encodeToString(bytesTest, android.util.Base64.DEFAULT);
//                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
//                buf.read(bytes, 0, bytes.length);
//
//                buf.close();
            }catch (IOException e) {
                Log.e("test", "sta se desava, zasto ne ispise erooooor ");
                Log.e("test", "sta se desava, zasto ne ispise erooooor " + e);
                e.printStackTrace();}

            encodedString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
//          zavrseno

            Attachment a=new Attachment();
            a.setId(hashCode());
            a.setData(encodedString);
            a.setName(returnCursor.getString(nameIndex));
//            a.setType("");
            Log.e("test", "data " + encodedString);
            attachments.add(a);
        }
    }
    public static String getPath(final Context context, final Uri uri) {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("/downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
//
//    private String getRealPathFromURI(Uri contentURI) {
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            result = contentURI.getPath();
//        } else {
//            cursor.moveToFirst();
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            result = cursor.getString(idx);
//            cursor.close();
//        }
//        return result;
//    }

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
        getMenuInflater().inflate(R.menu.create_email_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnCreateEmailSend) {
            final EditText txtTo = findViewById(R.id.autocomplete_to);
            final String to = txtTo.getText().toString();
            final EditText txtSubject = findViewById(R.id.txt_email_subject_input);
            final String subject = txtSubject.getText().toString();
            EditText txtCc = findViewById(R.id.autocomplete_cc);
            String cc = txtCc.getText().toString();
            EditText txtBcc = findViewById(R.id.autocomplete_bcc);
            String bcc = txtBcc.getText().toString();
            EditText txtContent = findViewById(R.id.txt_email_content_input);
            String content = txtContent.getText().toString();

            Message m = new Message();
            m.setTo(to);
            m.setSubject(subject);
            m.setCc(cc);
            m.setBcc(bcc);
            m.setContent(content);
            m.setDateTime(new Date());
            m.setId(0);
            m.setFrom(EmailsActivity.loggedInUserEmail);
            m.setAttachments(attachments);

//            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0);
//            String ime = pref.getString("username", "emptyVal");
//            String email = pref.getString("email", "emptyVal");
//            m.setFrom(ime + "," + email);

            if (to.equals("")) {
                Toast.makeText(CreateEmailActivity.this, "Please enter who you are sending to!", Toast.LENGTH_SHORT).show();
            } else if (subject.equals("")) {
                Toast.makeText(CreateEmailActivity.this, "Please enter subject of email!", Toast.LENGTH_SHORT).show();
            }
            else if (content.equals("")) {
                Toast.makeText(CreateEmailActivity.this, "Please enter content of email!", Toast.LENGTH_SHORT).show();
            }else {
                SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                int userId = uPref.getInt("loggedInUserId", -1);
                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<Message> createMessage = service.createMessage(m, userId);
                createMessage.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Toast.makeText(getApplicationContext(), "Email has been sent!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CreateEmailActivity.this, EmailsActivity.class));
                        finish();

                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(CreateEmailActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
        else if(item.getItemId() == R.id.btnCreateEmailCancel)

        if(draft == null){
            onBackPressed();
        }else{
            SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
            int userId = uPref.getInt("loggedInUserId", -1);
            IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
            Call<ArrayList<Message>> call = service.deleteDraft(draft.getId(), userId);
            call.enqueue(new Callback<ArrayList<Message>>() {
                @Override
                public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                    startActivity(new Intent(CreateEmailActivity.this, FoldersActivity.class));
                }

                @Override
                public void onFailure(Call<ArrayList<Message>> call, Throwable t) {

                }
            });

        }
        else if(item.getItemId() == R.id.btnCreateEmailAttachment)
            Toast.makeText(getApplicationContext(), "Attachment added!", Toast.LENGTH_SHORT).show();
        else
            onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        EditText txtTo = findViewById(R.id.autocomplete_to);
        EditText txtSubject = findViewById(R.id.txt_email_subject_input);
        EditText txtCc = findViewById(R.id.autocomplete_cc);
        EditText txtBcc = findViewById(R.id.autocomplete_bcc);
        EditText txtContent = findViewById(R.id.txt_email_content_input);

        Message message = new Message();
        if(draft != null){
            message.setId(draft.getId());
            message.setSubject(draft.getSubject());
            message.setBcc(draft.getBcc());
        }else{
            message.setId(0);
        }

        int br = 0;
        if(txtTo.getText().toString().length() > 0){
            message.setTo(txtTo.getText().toString());
            br++;
        }if(txtSubject.getText().toString().length() > 0){
            message.setSubject(txtSubject.getText().toString());
            br++;
        }if(txtCc.getText().toString().length() > 0){
            message.setCc(txtCc.getText().toString());
            br++;
        }if(txtBcc.getText().toString().length() > 0){
            message.setBcc(txtBcc.getText().toString());
            br++;
        }if(txtContent.getText().toString().length() > 0){
            message.setContent(txtContent.getText().toString());
            br++;
        }

        if(br > 0 ) {
            SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
            int userId = uPref.getInt("loggedInUserId", -1);
            IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
            Call<Message> call = service.saveToDraft(message, userId);
            call.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Toast.makeText(getApplicationContext(), "Saved to drafts!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong ...", Toast.LENGTH_LONG).show();
                }
            });
        }

        super.onBackPressed();
    }
}
