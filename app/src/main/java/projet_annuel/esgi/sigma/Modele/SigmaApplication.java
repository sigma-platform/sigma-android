package projet_annuel.esgi.sigma.Modele;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class SigmaApplication extends Application
{
    private String jsonProjects;
    private int idTask;
    private int position;


    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getJsonProjects() {
        return jsonProjects;
    }

    public void setJsonProjects(String jsonProjects) {
        this.jsonProjects = jsonProjects;
    }


    public void UpdateTodo(View v){
        CheckBox cb = (CheckBox) v;
        position = Integer.parseInt(cb.getTag().toString()) + 1;

        new UpdateTodo().execute();

    }

    private class UpdateTodo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.v("TEST", "JESPERE" + idTask + position);
            return null;
        }
    }
}