package in.thelordtech.facultydashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FacultyListAdapter extends ArrayAdapter<Faculty> {

    private List<Faculty> userList;
    private Context context;

    public FacultyListAdapter(Context context, int resource, List<Faculty> userList) {
        super(context, resource, userList);
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.faculty_list_item, parent, false);
        }

        ImageView targetIcon = listItemView.findViewById(R.id.icon);
        TextView targetName = listItemView.findViewById(R.id.name);
        TextView targetID = listItemView.findViewById(R.id.uuid);

        Faculty item = userList.get(position);

        String url = item.getFacultyIconLink();
        Picasso.get().load(url).into(targetIcon);
        targetName.setText(item.getFacutyName());
        targetID.setText(item.getFacultyBio());


        return listItemView;
    }
}
