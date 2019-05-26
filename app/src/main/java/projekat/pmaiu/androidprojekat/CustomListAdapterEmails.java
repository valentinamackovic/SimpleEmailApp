package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import model.Attachment;
import model.Message;
import model.Tag;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        if (currentMessage.isUnread()) {
            textViewFrom.setTypeface(textViewFrom.getTypeface(), Typeface.BOLD);
            textViewSubject.setTypeface(textViewSubject.getTypeface(), Typeface.BOLD);
        } else {
            textViewFrom.setTypeface(textViewFrom.getTypeface(), Typeface.NORMAL);
            textViewFrom.setTypeface(textViewSubject.getTypeface(), Typeface.NORMAL);
        }

        String from = (String) currentMessage.getFrom();

        if (from != null) {
            txtContactLetter.setText(String.valueOf(from.charAt(0)));
        } else {
            from = "Draft";
            txtContactLetter.setText("!");
        }
        textViewFrom.setText(from.split(":")[0]);

        String subject = (String) currentMessage.getSubject();
        textViewSubject.setText(subject);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        if (currentMessage.getDateTime() != null) {
            cal.setTime(currentMessage.getDateTime());
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            Date datum = (Date) currentMessage.getDateTime();
            if (datum != null) {
                textViewDate.setText(months[month] + " " + day);
            }
        }

        LinearLayout ly = convertView.findViewById(R.id.linear_layout_for_tags);
        ly.removeAllViews();
        if (currentMessage.getTags() != null) {
            if(currentMessage.getTags().size()>0) {
                for (Tag a : currentMessage.getTags()) {
                    ImageView imgView = new ImageView(context);
                    imgView.setMaxHeight(50);
                    imgView.setMaxWidth(50);
                    imgView.setId(a.getId());
                    imgView.setForegroundGravity(Gravity.CENTER);

                    imgView.setImageResource(R.drawable.ic_important_tag);

                    ly.addView(imgView);
                }
            }
        }
        else
            ly.removeAllViews();
        return convertView;
    }
}
