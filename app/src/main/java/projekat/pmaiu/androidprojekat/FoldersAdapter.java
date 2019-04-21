package projekat.pmaiu.androidprojekat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.Folder;

public class FoldersAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Folder> folders;

    public FoldersAdapter(Context context, ArrayList<Folder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Object getItem(int position) {
        return folders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.folder_list_element, parent, false);

        Folder currentFolder = (Folder) getItem(position);
        TextView folderName = convertView.findViewById(R.id.folder_list_item_title);
        ImageView folderPhoto = convertView.findViewById(R.id.folder_list_item_icon);

        folderName.setText(currentFolder.getName());
        folderPhoto.setImageResource(R.drawable.ic_folder);

        return convertView;
    }
}
