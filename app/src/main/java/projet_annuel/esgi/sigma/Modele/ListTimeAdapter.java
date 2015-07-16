package projet_annuel.esgi.sigma.Modele;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import projet_annuel.esgi.sigma.R;

/**
 * Created by bastien on 16/07/2015.
 */
public class ListTimeAdapter  extends BaseAdapter {

    private List<Time> listTime = null;
    LayoutInflater layoutInflater;



    public ListTimeAdapter(Context context, List lstTime){
        layoutInflater = LayoutInflater.from(context);
        this.listTime = lstTime;
    }
    @Override
    public int getCount() {
        return listTime.size();
    }

    @Override
    public Object getItem(int position) {
        return listTime.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.time_layout, null);
            holder = new ViewHolder();

            holder.lblView = (TextView) convertView.findViewById(R.id.txt_Lbl);
            holder.tmpEView = (TextView) convertView
                    .findViewById(R.id.txt_Estimer);
            holder.tmpPView = (TextView) convertView
                    .findViewById(R.id.txt_Passer);
            holder.dateView = (TextView) convertView
                    .findViewById(R.id.txt_Date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lblView.setText(listTime.get(position).getLbl());
        holder.tmpPView.setText(listTime.get(position).getTimeP());
        holder.tmpEView.setText(listTime.get(position).getTimeE());
        holder.dateView.setText(listTime.get(position).getDate());

        return convertView;

    }

    static class ViewHolder {
        TextView lblView;
        TextView dateView;
        TextView tmpPView;
        TextView tmpEView;
    }

}
