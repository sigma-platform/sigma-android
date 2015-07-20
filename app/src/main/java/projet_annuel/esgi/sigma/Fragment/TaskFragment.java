package projet_annuel.esgi.sigma.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import projet_annuel.esgi.sigma.Modele.ListTaskAdapter;
import projet_annuel.esgi.sigma.Modele.ListTodoAdapter;
import projet_annuel.esgi.sigma.Modele.SigmaApplication;
import projet_annuel.esgi.sigma.Modele.Task;
import projet_annuel.esgi.sigma.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {
    private int idTask;
    ListView lstInfo;
    ListView lstTodo;
    TextView txtLBL;
    String label;
    Button comment;
    Button start;
    int dateD;
    int dateF;
    float timingPast;

    private OnFragmentInteractionListener mListener;

    public TaskFragment() {
        // Required empty public constructor
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task, container, false);
        idTask = getArguments().getInt("IdTask");
        SigmaApplication app = (SigmaApplication) getActivity().getApplication();
        app.setIdTask(idTask);
        new LoadTask().execute();
        comment = (Button) v.findViewById(R.id.button);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new CommentaireFragment();
                Bundle args = new Bundle();
                args.putInt("Id", idTask);
                fragment.setArguments(args);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.flContent, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        start = (Button) v.findViewById(R.id.btnStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start.getText().toString().equals("Start the Task")) {
                    Date d = new Date();
                    dateD = (int) d.getTime();
                    Log.v("Date de base",dateD +"");
                    start.setText("Stop the Task");
                } else {
                    Date d = new Date();
                    dateF = (int) d.getTime();
                    int result = dateF - dateD;
                    timingPast = (float) result / (3600 * 1000);
                    Log.v("Date de base",dateF +"");
                    start.setText("Start the Task");
                    new AddTimeWorked().execute();
                }

            }
        });
        return v;

    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {

        private boolean good;
        private String[] lst;

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/task/" + idTask + "?token=token/" + setting.getString("Token", ""));
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(api_URL);

            String reponse = null;
            HttpEntity httpEntity = null;
            try {
                HttpResponse response = null;
                response = httpclient.execute(httpGet);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);
                JSONObject update = new JSONObject(reponse);
                good = update.getBoolean("success");
                if (good) {
                    JSONObject js = update.getJSONObject("payload");
                    label = js.getString("label");

                    lst = new String[5];
                    lst[0] = js.getString("description");
                    lst[1] = js.getString("status");
                    lst[2] = js.getString("date_start");
                    lst[3] = js.getString("date_end");
                    lst[4] = js.getString("estimated_time") + "h";

                }


            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (good) {
                txtLBL = (TextView) getActivity().findViewById(R.id.txtLBL);
                txtLBL.setText(label);
                Log.v("On look les valeurs", lst[0]);
                lstInfo = (ListView) getActivity().findViewById(R.id.lst_Info);
                lstInfo.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lst));
                new LoadTodo().execute();
            } else {

            }
        }
    }

    private class LoadTodo extends AsyncTask<Void, Void, Void> {
        private boolean good;
        private String[] lst;
        private String[] lstDone;

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/task/" + idTask + "/todo?token=" + setting.getString("Token", ""));
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(api_URL);

            String reponse = null;
            HttpEntity httpEntity = null;
            try {
                HttpResponse response = null;
                response = httpclient.execute(httpGet);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);
                JSONObject update = new JSONObject(reponse);
                good = update.getBoolean("success");
                if (good) {
                    JSONArray js = update.getJSONArray("payload");
                    lst = new String[js.length()];
                    lstDone = new String[js.length()];
                    for (int i = 0; i < js.length(); i++) {
                        JSONObject objectInArray = js.getJSONObject(i);
                        lst[i] = objectInArray.getString("label");
                        lstDone[i] = objectInArray.getString("done");
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (good) {

                if (lst != null) {
                    lstTodo = (ListView) getView().findViewById(R.id.lst_Todo);
                    ArrayList listTodo = new ArrayList();
                    for (int i = 0; i < lst.length; i++) {

                        listTodo.add(new Task(lst[i], lstDone[i], null, (idTask + ((i + 1) / 100)), ""));
                    }
                    lstTodo.setAdapter(new ListTodoAdapter(getActivity().getApplicationContext(), listTodo));
                }
            }
        }

    }


    private class AddTimeWorked extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.v("ALORS SA TIME",timingPast +"");
            return null;
        }
    }
}
