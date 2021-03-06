package projekat.pmaiu.androidprojekat;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//import org.apache.commons.io.FileUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Text;

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
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private static ArrayList<String> contacts = new ArrayList<>();
    private Message draft  = null;

    public static final int PICKFILE_RESULT_CODE = 1;
    public static ArrayList<Attachment> attachments;
    public LinearLayout ly;
    List views;
    Message messageReplyToAll;
    public static String loggedInUserEmail;



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
                contacts.clear();
                for (Contact c: response.body()) {
                    String contact = c.getFirstName() + " " + c.getLastName() + ", " + c.getEmail();
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

        SharedPreferences prefForUser = getApplicationContext().getSharedPreferences("MailPref", 0);
        loggedInUserEmail = prefForUser.getString("email", "");
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        EditText sd = findViewById(R.id.txt_email_content_input);



        Message draftM = (Message) getIntent().getSerializableExtra("message");
        if(draftM != null && draftM.isDraft()){
            EditText txtTo = findViewById(R.id.autocomplete_to);
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

//        imgView = findViewById(R.id.createEmailAddAttachment);
         EditText txtContent = findViewById(R.id.txt_email_content_input);
         EditText txtSubject = findViewById(R.id.txt_email_subject_input);
         String fromOriginal = getIntent().getStringExtra("from");
         String content1 = getIntent().getStringExtra("content");
         String to = getIntent().getStringExtra("to");
         String date = getIntent().getStringExtra("date");
         String subject = getIntent().getStringExtra("subject");
        Attachment att = (Attachment) getIntent().getSerializableExtra("att");
            if(att !=null && fromOriginal != null && content1 != null && to != null && date != null && subject != null) {
                txtContent.setText("------------------Forwarded message------------------ " + " " + "From: " + "  " + fromOriginal + "     " +
                "                               " + "To: " + "       " + to + "              " + "Date: " + "   " + date +
                "   " +
                "----------------------------------------------------------------------- "
                + " " + content1 + " "   +att.getName());

                txtSubject.setText("Fwd: " + subject);
            }
            else if(att ==null && fromOriginal != null && content1 != null && to != null && date != null && subject != null) {
            txtContent.setText("-----------------Forwarded message----------------- " + " " + "From: " + "  " + fromOriginal + "     " +
                    "                               " + "To: " + "       " + to + "              " + "Date: " + "   " + date +
                    "   " +
                    "---------------------------------------------------------------------- "
                    + " " + content1 );

            txtSubject.setText("Fwd: " + subject);
        }

        String content2 = getIntent().getStringExtra("content1");
        String to2 = getIntent().getStringExtra("to1");
        String date2 = getIntent().getStringExtra("date1");
        String subject2 = getIntent().getStringExtra("subject1");
        Attachment att2 = (Attachment) getIntent().getSerializableExtra("att1");
        EditText toReply = findViewById(R.id.autocomplete_to);
        toReply.setText(to2);
        toReply.setSelection(toReply.getText().length());
        Toast.makeText(getApplicationContext(), to2, Toast.LENGTH_LONG).show();

        if(att2 !=null &&  content2 != null && to2 != null && date2 != null && subject2 != null) {
            txtContent.setText("----------------------Replied message---------------------- " + " " + "From: " + "  " + to2 + "     " +
                    "                               " +  "              " + "Date: " + "   " + date2 +
                    "   " +
                    "------------------------------------------------------------------------ "
                    + " " + content2 + " "   +att2.getName());
            txtSubject.setText("Re: " + subject2);

        }
        else if(att2 ==null &&  content2 != null && to2 != null && date2 != null && subject2 != null) {
            txtContent.setText("----------------------Replied message---------------------- " + " " + "From: " + "  " + to2 + "     " +
                    "                               " +  "              " + "Date: " + "   " + date2 +
                    "   " +
                    "------------------------------------------------------------------------ "
                    + " " + content2 );

            txtSubject.setText("Re: " + subject2);
        }

        String content3 = getIntent().getStringExtra("content11");
        String to3 = getIntent().getStringExtra("to11");
        String from3 = getIntent().getStringExtra("from11");
        String date3 = getIntent().getStringExtra("date11");
        String subject3 = getIntent().getStringExtra("subject11");
        Attachment att3 = (Attachment) getIntent().getSerializableExtra("att11");
        MultiAutoCompleteTextView toReplyAll = findViewById(R.id.autocomplete_to);
        if(to3 != null){
            toReplyAll.setText(to3);
        }



        ImageView image = (ImageView) findViewById(R.id.icon_attachment);
        if(att3 !=null &&  content3 != null && to3 != null && date3 != null && subject3 != null) {
            txtContent.setText("----------------------Replied message---------------------- " + " " + "From: " + "  " + from3 + "     " +
                    "                               " +  "              "+ "Date: " + "   " + date3 +
                    "   " +
                    "--------------------------------------------------------------------- "
                    + " " + content3 + " " +att3.getName());

            txtSubject.setText("Re: " + subject3);
        }
        else if(att3 ==null &&  content3 != null && to3 != null && date3 != null && subject3 != null) {
            txtContent.setText("----------------------Replied message---------------------- " + " " + "From: " + "  " + from3 + "     " +
                    "                               " +  "              " + "Date: " + "   " + date3 +
                    "   " +
                    "--------------------------------------------------------------------- "
                    + " " + content3 );

            txtSubject.setText("Re: " + subject3);
        }

        Intent i = getIntent();
        messageReplyToAll = (Message)i.getSerializableExtra("msgReplyAll");
        if(messageReplyToAll!=null){
            if(messageReplyToAll.getContent() != null){
                txtContent.setText("----------------------Replied message---------------------- " + " " + "From: " + "  " + messageReplyToAll.getFrom() + "     " +
                        "                               " +  "              "+ "Date: " + "   " + messageReplyToAll.getDateTime() +
                        "   " +
                        "--------------------------------------------------------------------- "
                        + " " + messageReplyToAll.getContent() + " " );
            }

            MultiAutoCompleteTextView replyToAll = findViewById(R.id.autocomplete_to);
//            if(messageReplyToAll.getCc()!=null)
//                replyToAll.setText(removeLoggedInUserEmail(messageReplyToAll.getCc()));
//            else if(messageReplyToAll.getBcc()!=null)
//                replyToAll.setText(removeLoggedInUserEmail(messageReplyToAll.getBcc()));
//            else
                replyToAll.setText(removeLoggedInUserEmail(messageReplyToAll.getTo()));
                Log.e("test", messageReplyToAll.getTo());
        }


    }

    public String removeLoggedInUserEmail(String emails){
        String ret="";
        if(emails.contains(loggedInUserEmail))
            ret=emails.replace(loggedInUserEmail, "");
        return ret;
    }

    public void showProgressDialog() {
        final int THREE_SECONDS = 3*1000;
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Uploading file...");
        dlg.setCancelable(false);
        dlg.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dlg.dismiss();
            }
        }, THREE_SECONDS);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        showProgressDialog();
//        super.onActivityResult(requestCode, resultCode, data);
        String filename;
        String encodedString="";
        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri returnUri1 = data.getData();
                Cursor returnCursor1 =
                        getContentResolver().query(returnUri1, null, null, null, null);

                returnCursor1.moveToFirst();
                int nameIndex1 =returnCursor1.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                String Filename=returnCursor1.getString(nameIndex1);
                String mimeType="";
                try {
                    Uri uri = data.getData();

                        mimeType = getContentResolver().getType(uri);
                        if (mimeType == null) {
                            String path = getPath(this, uri);
                            if (path == null) {
                                filename = FilenameUtils.getName(uri.toString());
                            } else {
                                File file = new File(path);
                                filename = file.getName();
                            }
                        } else {
                            Uri returnUri = data.getData();
                            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);

                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            filename = returnCursor.getString(nameIndex);
                            String size = Long.toString(returnCursor.getLong(sizeIndex));
                        }
                        File fileSave = getExternalFilesDir(null);
                        String sourcePath = getExternalFilesDir(null).toString();
                        try {
//
                            copyFileStream(new File(sourcePath + "/" + filename), uri,this);
                            File proba=new File(sourcePath + "/" + filename);
                            final byte[] bytesTestProbaRAdi = Files.readAllBytes(Paths.get(proba.getPath()));
                            encodedString = android.util.Base64.encodeToString(bytesTestProbaRAdi, android.util.Base64.DEFAULT);
                            Log.e("test", "nesto cudno 2 proba "+ proba.getPath());
                            Log.e("test", "nesto cudno 2  proooslo proba string "+ encodedString );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                } catch (Exception e) {
                    Log.e("test", "upload att nije proslo  "+ e);
                    e.printStackTrace();
                }
                ly=findViewById(R.id.linear_layout_attachments_create_email);

                LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                views = new ArrayList();

                View view = layoutInflator.inflate(R.layout.attacment_row, null);

                ImageView imgView=view.findViewById(R.id.icon_attachment);
                imgView.setImageResource(R.drawable.icon_attachment);
                final TextView textView=view.findViewById(R.id.txt_attachment);

                textView.setText(returnCursor1.getString(nameIndex1));

                views.add(view);

                Log.e("test", "tip" + mimeType);
                if(mimeType.equals("application/pdf"))
                    mimeType="pdf";
                if(mimeType.equals("image/jpeg"))
                    mimeType="jpeg";
                Attachment a=new Attachment();
                a.setId(hashCode());
                a.setData(encodedString);
                a.setName(returnCursor1.getString(nameIndex1));
                a.setType(mimeType);
                Log.e("test", "data " + encodedString);
                attachments.add(a);

                for(int z = 0; z<views.size(); z++) {
                    ly.addView((View) views.get(z));
                }
            }
        }
    }
    private void copyFileStream(File dest, Uri uri, Context context) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
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

    public boolean validation(){
        boolean pass=true;
        String message="";
        MultiAutoCompleteTextView to = findViewById(R.id.autocomplete_to);
        MultiAutoCompleteTextView cc = findViewById(R.id.autocomplete_cc);
        MultiAutoCompleteTextView bcc = findViewById(R.id.autocomplete_bcc);

        if(!to.getText().toString().contains("@")){
            pass=false;
            message+="Email is not valid! \n";
        }
        else if(!cc.getText().toString().contains("@")){
            if(!cc.getText().toString().equals("")){
                pass=false;
                message+="Email is not valid! \n";
            }

        }
        else if(!bcc.getText().toString().contains("@")){
            pass=false;
            message+="Email is not valid! \n";
        }
        else if(!to.getText().toString().contains(".")){
            pass=false;
            message+="Email is not valid! \n";
        }
        else if(!cc.getText().toString().contains(".")){
            pass=false;
            message+="Email is not valid! \n";
        }
        else if(!bcc.getText().toString().contains(".")){
            pass=false;
            message+="Email is not valid! \n";
        }

        if(!message.equals(""))
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        return  pass;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnCreateEmailSend) {
            if(validation()) {
                MultiAutoCompleteTextView txtTo = findViewById(R.id.autocomplete_to);
                final String to = txtTo.getText().toString();
                final EditText txtSubject = findViewById(R.id.txt_email_subject_input);
                final String subject = txtSubject.getText().toString();
                MultiAutoCompleteTextView txtCc = findViewById(R.id.autocomplete_cc);
                String cc = txtCc.getText().toString();
                MultiAutoCompleteTextView txtBcc = findViewById(R.id.autocomplete_bcc);
                String bcc = txtBcc.getText().toString();
                EditText txtContent = findViewById(R.id.txt_email_content_input);
                String content = txtContent.getText().toString();

                Message m = new Message();

                String[] toArray = to.split(",");

                if(toArray.length != 1){
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0 ; i < toArray.length; i++){
                        if(toArray[i].contains("@")){
                            sb.append(toArray[i]);
                            sb.append(",");
                        }
                    }
                    m.setTo(sb.toString());
                }else{
                    m.setTo(to);
                }


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
                } else if (content.equals("")) {
                    Toast.makeText(CreateEmailActivity.this, "Please enter content of email!", Toast.LENGTH_SHORT).show();
                } else {
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
        else if(item.getItemId() == R.id.btnCreateEmailAttachment) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(i, PICKFILE_RESULT_CODE);
            Toast.makeText(getApplicationContext(), "Attachment added!", Toast.LENGTH_SHORT).show();
        }
        else
            onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        MultiAutoCompleteTextView txtTo = findViewById(R.id.autocomplete_to);
        EditText txtSubject = findViewById(R.id.txt_email_subject_input);
        MultiAutoCompleteTextView txtCc = findViewById(R.id.autocomplete_cc);
        MultiAutoCompleteTextView txtBcc = findViewById(R.id.autocomplete_bcc);
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
