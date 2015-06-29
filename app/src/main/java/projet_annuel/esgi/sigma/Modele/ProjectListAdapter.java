package projet_annuel.esgi.sigma.Modele;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import projet_annuel.esgi.sigma.R;

/**
 * Created by bastien on 03/05/2015.
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectListHolder> {

    private LayoutInflater inflater;
    List<ProjectData> data = Collections.emptyList();

    public ProjectListAdapter(Context context,List<ProjectData> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ProjectListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_line,parent,false);
        ProjectListHolder holder = new ProjectListHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProjectListHolder holder, int position) {
        ProjectData current = data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconID);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ProjectListHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView icon;

        public ProjectListHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.listText);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }
    }
}
