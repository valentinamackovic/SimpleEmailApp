package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
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
        return messages.size();
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

        Message currentMessage = (Message) getItem(position);

        TextView textViewFrom =
                convertView.findViewById(R.id.txtListViewFrom);
        TextView textViewSubject =
                convertView.findViewById(R.id.txtListViewSubject);

        String from = (String) currentMessage.getFrom().getFirstName();
        textViewFrom.setText(from);
        String subject = (String) currentMessage.getSubject();
        textViewSubject.setText(subject);
        return convertView;
    }
}
