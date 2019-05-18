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
import java.util.Date;

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
        textViewFrom.setText(from);

        String subject = (String) currentMessage.getSubject();
        textViewSubject.setText(subject);




        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date datum = (Date) currentMessage.getDateTime();
        if(datum != null){
            int month = datum.getMonth();
            textViewDate.setText(months[month] + " "+String.valueOf(datum.getDay()));
        }

        return convertView;
    }
}
