package projet_annuel.esgi.sigma.Modele;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import projet_annuel.esgi.sigma.R;

/**
 * Created by bastien on 30/06/2015.
 */
public class ListTaskAdapter extends BaseAdapter {

    private List<Task> listTask = null;
    LayoutInflater layoutInflater;

    public ListTaskAdapter(Context context, List lstTask){
        layoutInflater = LayoutInflater.from(context);
        this.listTask = lstTask;
    }
    @Override
    public int getCount() {
        return listTask.size();
    }

    @Override
    public Object getItem(int position) {
        return listTask.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.task_layout, null);
            holder = new ViewHolder();

            holder.lblView = (TextView) convertView.findViewById(R.id.txt_libelle);
            holder.tmpView = (TextView) convertView
                    .findViewById(R.id.txt_tmp);
            holder.dateDView = (TextView) convertView
                    .findViewById(R.id.dateD);
            holder.dateFView = (TextView) convertView
                    .findViewById(R.id.dateF);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lblView.setText(listTask.get(position).getNom());
        holder.tmpView.setText(String.valueOf(listTask.get(position).getTmp()) + "h");
        holder.dateDView.setText(listTask.get(position).getDateD());
        holder.dateFView.setText(listTask.get(position).getDateF());
        return convertView;

    }

    static class ViewHolder {
        TextView lblView;
        TextView dateFView;
        TextView dateDView;
        TextView tmpView;
    }
}
