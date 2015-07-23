package projet_annuel.esgi.sigma.Modele;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import projet_annuel.esgi.sigma.R;

/**
 * Created by bastien on 17/07/2015.
 */

//The adapter of Comment which synchronize Layout and Data in the list
public class ListCommentAdapter extends BaseAdapter {
    private List<Comment> listComment = null;
    LayoutInflater layoutInflater;

    public ListCommentAdapter(Context context, List lstComment){
        layoutInflater = LayoutInflater.from(context);
        this.listComment = lstComment;
    }
    @Override
    public int getCount() {
        return listComment.size();
    }

    @Override
    public Object getItem(int position) {
        return listComment.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.comment_layout, null);
            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.txtName);
            holder.lblView = (TextView) convertView.findViewById(R.id.txtComment);
            holder.dateView = (TextView) convertView.findViewById(R.id.txtDate);

            holder.lblView.setText(listComment.get(position).getDescription());
            holder.dateView.setText(listComment.get(position).getDate());
            holder.nameView.setText(listComment.get(position).getName());

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
    return convertView;

    }

    static class ViewHolder {
        TextView lblView;
        TextView dateView;
        TextView nameView;

    }
}
