package apps.hosamazzam.com.intdv_task.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import apps.hosamazzam.com.intdv_task.Db.Address;
import apps.hosamazzam.com.intdv_task.R;

public class SpinerAdapter extends ArrayAdapter<Address> {
    LayoutInflater flater;
    onClickListner listner;

    public SpinerAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Address> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public void registerOnClickListner(onClickListner listner) {
        this.listner = listner;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return rowview(convertView, position, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return rowview(convertView, position, parent);
    }

    private View rowview(View convertView, int position, ViewGroup parent) {

        final Address rowItem = getItem(position);

        viewHolder holder;
        View rowview = convertView;
        if (rowview == null) {

            holder = new viewHolder();
            flater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowview = flater.inflate(R.layout.spiner_item_layout, parent, false);

            holder.title = rowview.findViewById(R.id.title);
            holder.desc = rowview.findViewById(R.id.desc);
            rowview.setTag(holder);
        } else {
            holder = (viewHolder) rowview.getTag();
        }

        holder.title.setText(rowItem.getName());
        holder.desc.setText(rowItem.getDesc());


        return rowview;
    }

    public interface onClickListner {
        void onClick(int id, String name);
    }

    private class viewHolder {
        TextView title, desc;
    }

}
