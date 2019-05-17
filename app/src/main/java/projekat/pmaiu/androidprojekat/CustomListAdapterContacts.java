package projekat.pmaiu.androidprojekat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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

        byte[] decodedString = android.util.Base64.decode(currentContact.getPhoto().getData(), android.util.Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        photo.setImageBitmap(decodedByte);

        return convertView;
    }
}
