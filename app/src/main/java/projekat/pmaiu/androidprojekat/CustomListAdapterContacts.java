package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.Contact;

public class CustomListAdapterContacts extends BaseAdapter {
    private Context context;
    ArrayList<Contact> contacts;

    public CustomListAdapterContacts(Context context, ArrayList<Contact> contacts) {
        this.context=context;
        this.contacts=contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.contact_listview, parent, false);
        }

        Contact currentContact = (Contact) getItem(position);

        TextView textViewName =
                convertView.findViewById(R.id.txtListViewContact);
        ImageView photo =
                convertView.findViewById(R.id.imgListViewContact);

        String fullName=currentContact.getFirstName() + " "+ currentContact.getLastName();
        textViewName.setText(fullName );
        photo.setImageResource(R.mipmap.profile_ico);

        return convertView;
    }
}
