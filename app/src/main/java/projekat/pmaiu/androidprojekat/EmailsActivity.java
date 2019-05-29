package projekat.pmaiu.androidprojekat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import enums.Condition;
import enums.Operation;
import model.Account;
import model.Contact;
import model.Folder;
import model.Rule;
import model.Tag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import model.Message;
import service.IMailService;
import service.MailService;


public class EmailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    ListView listView ;
    CustomListAdapterEmails adapter;
    private long mInterval = 0;
    private Handler mHandler;
    private ScheduledExecutorService scheduler;
    private int userId = -1;
    String NOTIFICATION_CHANNEL_ID;
    private boolean active;
    private int id;
    public static ArrayList<Message> messages;
    Context context;
    public static String loggedInUserEmail;
    List<Folder> folders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        active=true;
        messages= new ArrayList<>();
        context=getApplicationContext();
        SharedPreferences prefTheme = context.getSharedPreferences("ThemePref", 0);
        SharedPreferences prefForUser = context.getSharedPreferences("MailPref", 0);
        loggedInUserEmail = prefForUser.getString("email", "");
        Toast.makeText(getApplicationContext(), loggedInUserEmail, Toast.LENGTH_LONG).show();
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emails);
        listView = findViewById(R.id.listView_emails);
        mHandler = new Handler();

        listView.setAdapter(adapter);

        Toolbar toolbar =  findViewById(R.id.toolbar_emails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String syncTimeStr = pref.getString("refresh_rate", "0");
        scheduler =Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {

                        SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                        userId = uPref.getInt("loggedInUserId",-1);
                        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                        Call<ArrayList<Message>> call = service.getAllMessages(userId);
                        call.enqueue(new Callback<ArrayList<Message>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                                messages=response.body();
                                List<Folder> foldersForUser=getFoldersForUser(userId);

                                Log.e("test", "userId" + userId);
                                Log.e("test", "folderi" + folders.size());
                                for(Folder f : foldersForUser){
                                    ArrayList<Message> messagesForFolder=new ArrayList<>();
                                    if(!f.getName().equals("Inbox") && !f.getName().equals("Outbox") && !f.getName().equals("Drafts")){
                                        messagesForFolder.addAll(EmailsActivity.filterMessagesToFolder(EmailsActivity.messages,f, "" ));
                                        f.setMessages(messagesForFolder);
                                    }
                                }
//                           generateEmailsList(response.body());

                                if(response.body().size()>0 && numberOfUnreadMessages(response.body())==1 && !active){
                                    for(Message m : response.body()) {
                                        if (m.isUnread())
                                            notificationDialog(m);
                                    }
                                }
                                else if(numberOfUnreadMessages(response.body())>1 && !active){
                                    notificationDialogForNMessages(numberOfUnreadMessages(response.body()));
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                                Toast.makeText(EmailsActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, 0,45 , TimeUnit.SECONDS);
       // Integer.parseInt(syncTimeStr);
    }

    private List<Folder> getFoldersForUser(int userId){
        folders=new ArrayList<>();
        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
        Call<List<Folder>> call = service.getAllFolders(userId);
        call.enqueue(new Callback<List<Folder>>() {
            @Override
            public void onResponse(Call<List<Folder>> call, Response<List<Folder>> response) {
                Log.e("test","u folderu "+ response.body());
                folders=response.body();
                Log.e("test","u folderu 2"+ folders);
            }

            @Override
            public void onFailure(Call<List<Folder>> call, Throwable t) {
            }
        });
        return folders;
    }

    private int numberOfUnreadMessages(ArrayList<Message> messages){
        int number=0;
        for(Message m : messages) {
            if (m.isUnread())
                number += 1;
        }
        return number;
    }

    private void notificationDialog(Message m) {
        Intent intent = new Intent(this, EmailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("message", m);
        if(m.isUnread()) {
           // readMessage(userId, m.getId());
        }

        int requestID = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                requestID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_CHANNEL_ID = "not";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "not",
                    "not",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Notification")
                .setContentTitle(m.getFrom())
                .setContentText(m.getContent())
                .setContentInfo("Information");

        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(18, notificationBuilder.build());
    }

    private void notificationDialogForNMessages(int numberOfMessages) {
        Intent intent = new Intent(this, EmailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                requestID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NOTIFICATION_CHANNEL_ID = "not";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "not",
                    "not",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Notification")
                //.setPriority(Notification.PRIORITY_MAX)
                .setContentTitle("New messages")
                .setContentText("You have "+ numberOfMessages + " messages")
                .setContentInfo("Information")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(1, notificationBuilder.build());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();

//        SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
//        userId = uPref.getInt("loggedInUserId",-1);
//        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
//        Call<ArrayList<Message>> call = service.getAllMessages(userId);
//        call.enqueue(new Callback<ArrayList<Message>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
//                generateEmailsList(response.body());
//                if(response.body().size()>0 && numberOfUnreadMessages(response.body())==1){
//                    for(Message m : response.body()) {
//                        if (m.isUnread())
//                            notificationDialog(m);
//                    }
//                }
//                else if(numberOfUnreadMessages(response.body())>1){
//                    notificationDialogForNMessages(numberOfUnreadMessages(response.body()));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
//                Toast.makeText(EmailsActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
//            }
//        });

        FloatingActionButton btnCreate = findViewById(R.id.btnCreateEmailAction);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, CreateEmailActivity.class));
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailsActivity.this, ProfileActivity.class));
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(adapter.getCount() > 0){
                    Message value=(Message) adapter.getItem(position);
                    Intent i = new Intent(EmailsActivity.this, EmailActivity.class);
                    i.putExtra("message", value);
                    if(value.isUnread()) {
                        readMessage(userId, value.getId());
                        value.setUnread(false);
                    }
                    startActivity(i);
                  //  finish();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Empty contact-adapter", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    public void generateEmailsList(ArrayList<Message> messages){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sort = pref.getString("sort_by_date", "0");

        if(!sort.equals("0")){
            if(sort.equals("Asceding")){
                Collections.sort(messages, new Comparator<Message>() {
                    public int compare(Message o1, Message o2) {
                        return o1.getDateTime().compareTo(o2.getDateTime());
                    }
                });
            }
           else if(sort.equals(("Desceding"))){
                //opadajuce
                Collections.sort(messages, new Comparator<Message>() {
                    public int compare(Message o1, Message o2) {

                        if (o1.getDateTime().after(o2.getDateTime())) {return -1;}
                        if (o1.getDateTime().before(o2.getDateTime())) {return 0;}
                        else {return 0;}
                    }
                });
            }
        }
        listView = findViewById(R.id.listView_emails);
        adapter = new CustomListAdapterEmails(this, messages);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        registerForContextMenu(listView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        active=false;
//        stopRepeatingTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduler.shutdown();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
            startActivity(new Intent(EmailsActivity.this, ContactsActivity.class));
        } else if (id == R.id.nav_emails) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(EmailsActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_folders) {
            startActivity(new Intent(EmailsActivity.this, FoldersActivity.class));
        }else if(id == R.id.nav_logout){
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MailPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.remove("username");
            editor.remove("password");
            editor.commit();
            startActivity(new Intent(EmailsActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void readMessage(int profileId, int messageId){
        IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
        Call<ArrayList<Message>> call = service.readMessage(profileId, messageId);
        call.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, please try again...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Message m;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        m = (Message) lv.getItemAtPosition(acmi.position);

        menu.add("Delete");
        boolean hasTag=false;
        if(m.getTags()!=null){
            for(Tag t : m.getTags()){
                if(t.getName().equals("IMPORTANT"))
                    hasTag=true;
            }
        }
        if(!hasTag)
            menu.add("Add important tag");
        menu.setHeaderTitle(m.getSubject());
        id=m.getId();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle() == "Delete"){
            SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
            int userId = uPref.getInt("loggedInUserId",-1);

            IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
            Call<ArrayList<Message>> delete = service.deleteMessage(id, userId);
            delete.enqueue(new Callback<ArrayList<Message>>() {
                @Override
                public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                    Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EmailsActivity.this, EmailsActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                    Toast.makeText(EmailsActivity.this, "Something went wrong, please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (item.getTitle() == "Add important tag") {
            Tag tag1=new Tag();
            tag1.setName("IMPORTANT");
            m.setTags(new ArrayList<>(Arrays.asList(tag1)));

            IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
            Call<ArrayList<Message>> call = service.updateMessageTag(userId, m.getId(), tag1);
            call.enqueue(new Callback<ArrayList<Message>>() {
                @Override
                public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                        Log.i("messages", "Updated tag");
                    };
                @Override
                public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                    Toast.makeText(EmailsActivity.this, "Empty message-adapter", Toast.LENGTH_SHORT).show();
                }
            });

            generateEmailsList(filterMessagesToFolder(messages,null, "inbox" ));
        } else {
            return false;
        }
        return true;
    }

    public static ArrayList<Message> filterMessagesToFolder(ArrayList<Message> mess, Folder folder, String inboxutbox){
        ArrayList<Message> messReturn=new ArrayList<>();
        if(mess.size()!=0) {
            for (Message m : mess) {

                String oneContact=m.getFrom();
                Log.e("test", "filtiranje poruka from " +oneContact );
                Log.e("test", "filtiranje poruka to " +m.getTo() );
                if(oneContact.contains(":"))
                    oneContact=oneContact.split(":")[1];
                if (  oneContact.equals(loggedInUserEmail) && inboxutbox.equals("outbox")) {
                    messReturn.add(m);
                }
                if(m.getTo() != null){
                    if (  inboxutbox.equals("inbox") && (m.getTo().contains(loggedInUserEmail) )) {
                        messReturn.add(m);
                    }
                }
                if(m.getBcc() != null){
                    if (  inboxutbox.equals("inbox") && m.getBcc().contains(loggedInUserEmail)) {
                        messReturn.add(m);
                    }
                }
                if(m.getCc() != null){
                    if ( inboxutbox.equals("inbox") && m.getCc().contains(loggedInUserEmail)) {
                        messReturn.add(m);
                    }
                }

            }
//          proizvoljni folderi ---------------------------------------------------------
//          TRENUTNO SAMO COPY
            if(folder!=null) {
                String word=folder.getWord();
                Rule ruleForFolder=folder.getRule();
                ArrayList<Message> messRemove=new ArrayList<>();
                for(Message n: messages ){
                    Log.e("test", "iz liste u klasi "+ messages.get(0));
                    Log.e("test", "iz poslatih mess "+ n);
                    if(ruleForFolder.condition== Condition.TO){
                        if(n.getTo().toLowerCase().contains(word.toLowerCase()))
                            if(ruleForFolder.operation== Operation.COPY)
                                messReturn.add(n);
                            else if(ruleForFolder.operation== Operation.MOVE){
                                messReturn.add(n);
                                messRemove.add(n);
                            }else
                                messRemove.add(n);
                    }
                    else if(ruleForFolder.condition== Condition.CC){
                        if(word != null){
                            if(n.getCc().toLowerCase().contains(word.toLowerCase()))
                                if(ruleForFolder.operation== Operation.COPY)
                                    messReturn.add(n);
                                else if(ruleForFolder.operation== Operation.MOVE){
                                    messReturn.add(n);
                                    messRemove.add(n);
                                }else
                                    messRemove.add(n);
                        }
                    }
                    else if(ruleForFolder.condition== Condition.FROM){
                        if(n.getFrom().toLowerCase().contains(word.toLowerCase()))
                            if(ruleForFolder.operation== Operation.COPY)
                                messReturn.add(n);
                            else if(ruleForFolder.operation== Operation.MOVE){
                                messReturn.add(n);
                                messRemove.add(n);
                            }else
                                messRemove.add(n);
                    }
                    else if(ruleForFolder.condition== Condition.SUBJECT){
                        if(n.getSubject().toLowerCase().contains(word.toLowerCase()))
                            if(ruleForFolder.operation== Operation.COPY)
                                messReturn.add(n);
                            else if(ruleForFolder.operation== Operation.MOVE){
                                messReturn.add(n);
                                messRemove.add(n);
                            }else
                                messRemove.add(n);
                    }
                }
                EmailsActivity.messages.removeAll(messRemove);
                mess.removeAll(messRemove);
                if(folder.getMessages()!=null)
                    folder.setMessages(mess);
            }
        }
        return messReturn;
    }

    public void showProgressDialogSearch() {
        final int THREE_SECONDS = 3*1000;
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage("Searching");
        dlg.setCancelable(false);
        dlg.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dlg.dismiss();
            }
        }, THREE_SECONDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.emails_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_emails);
        final SearchView serchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        serchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                showProgressDialogSearch();


                SharedPreferences uPref = getApplicationContext().getSharedPreferences("MailPref", 0);
                int userId = uPref.getInt("loggedInUserId",-1);
                IMailService service = MailService.getRetrofitInstance().create(IMailService.class);
                Call<ArrayList<Message>> filter = service.searchMessages(userId, serchView.getQuery().toString());
                filter.enqueue(new Callback<ArrayList<Message>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                        if(response.body()==null){
                            Toast.makeText(getApplicationContext(), "No matching result for this search!", Toast.LENGTH_LONG).show();
                        }
                        else if(response.body().size() == 0){
                            Toast.makeText(getApplicationContext(), "No matching result for this search!", Toast.LENGTH_LONG).show();
                        }else{
                            generateEmailsList(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Message>> call, Throwable t) {

                    }
                });
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });
        return true;
    }
}