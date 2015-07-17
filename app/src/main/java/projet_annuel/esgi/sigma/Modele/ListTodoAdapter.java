package projet_annuel.esgi.sigma.Modele;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;


import projet_annuel.esgi.sigma.R;

/**
 * Created by support on 16/07/2015.
 */
public class ListTodoAdapter extends BaseAdapter {

    private List<Task> listTask = null;
    LayoutInflater layoutInflater;

    int position;
    int id;


    public ListTodoAdapter(Context context, List lstTask){
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
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
            holder.cbTodo = (CheckBox) convertView.findViewById(R.id.CB_Todo);
            holder.separator = (TextView) convertView.findViewById(R.id.separator);
            holder.line = (View) convertView.findViewById(R.id.layout);

            holder.line.setVisibility(View.GONE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(listTask.get(position).getDateD().equals("1"))
            holder.cbTodo.setChecked(true);
        else
            holder.cbTodo.setChecked(false);

        holder.separator.setVisibility(View.GONE);
        holder.lblView.setText(listTask.get(position).getNom());
        holder.tmpView.setVisibility(View.GONE);
        holder.dateDView.setVisibility(View.GONE);
        holder.dateFView.setVisibility(View.GONE);
        holder.cbTodo.setText("Done");
        holder.cbTodo.setTag(position);
        return convertView;

    }

    static class ViewHolder {
        TextView lblView;
        TextView dateFView;
        TextView dateDView;
        TextView tmpView;
        TextView separator;
        View line;
        CheckBox cbTodo;
    }


}
