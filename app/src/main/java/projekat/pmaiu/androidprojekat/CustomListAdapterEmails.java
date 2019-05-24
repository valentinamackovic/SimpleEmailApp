package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import model.Message;

public class CustomListAdapterEmails extends BaseAdapter {
    private Context context;
    ArrayList<Message> messages;

    public CustomListAdapterEmails(Context context, ArrayList<Message> messages) {
        this.context=context;
        this.messages=messages;
    }

    @Override
    public int getCount() {
        if(messages != null){
            return  messages.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.email_listview, parent, false);
        }

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        Message currentMessage = (Message) getItem(position);

        TextView textViewFrom =
                convertView.findViewById(R.id.txtListViewFrom);
        TextView textViewSubject =
                convertView.findViewById(R.id.txtListViewSubject);
        TextView textViewDate =
                convertView.findViewById(R.id.txtListViewDate);
        TextView txtContactLetter = convertView.findViewById(R.id.contact_letter_on_mail);

        if(currentMessage.isUnread()){
            textViewFrom.setTypeface(textViewFrom.getTypeface(), Typeface.BOLD);
            textViewSubject.setTypeface(textViewSubject.getTypeface(), Typeface.BOLD);
        }else{
            textViewFrom.setTypeface(textViewFrom.getTypeface(), Typeface.NORMAL);
            textViewFrom.setTypeface(textViewSubject.getTypeface(), Typeface.NORMAL);
        }

        String from = (String) currentMessage.getFrom();

        if(from != null){
            txtContactLetter.setText(String.valueOf(from.charAt(0)));
        }else{
            from = "Draft";
            txtContactLetter.setText("!");
        }
        textViewFrom.setText(from.split(":")[0]);

        String subject = (String) currentMessage.getSubject();
        textViewSubject.setText(subject);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        if(currentMessage.getDateTime()!= null){
            cal.setTime(currentMessage.getDateTime());
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            Date datum = (Date) currentMessage.getDateTime();
            if(datum != null){
                textViewDate.setText(months[month] + " "+day);
            }
        }

// etc.
        return convertView;
    }
}
