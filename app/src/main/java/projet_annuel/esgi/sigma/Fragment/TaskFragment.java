package projet_annuel.esgi.sigma.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    Button add;
    Button start;
    int dateF;
    float timingPast;
    EditText todoTxt;

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
        SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);

        SigmaApplication app = (SigmaApplication) getActivity().getApplication();
        app.setIdTask(idTask);
        new LoadTask().execute();

        todoTxt = (EditText) v.findViewById(R.id.editTodo);
        add = (Button) v.findViewById(R.id.btn_AJoutTodo);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todoTxt.getText().toString() != "") {
                    new AddTodo().execute();
                } else {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Error");
                    adb.setMessage("You need to write something in your todo");
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    adb.show();
                }
            }
        });
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
                SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
                if (start.getText().toString().equals("Start the Task")) {
                    Date d = new Date();
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putInt("dateD", (int) d.getTime());
                    editor.putInt("IdTask", idTask);
                    editor.commit();

                    start.setText("Stop the Task");
                } else {
                    Date d = new Date();
                    dateF = (int) d.getTime();
                    int result = dateF - setting.getInt("dateD", 0);
                    timingPast = (float) result / (3600 * 1000);
                    Log.v("Date de base", dateF + "");
                    start.setText("Start the Task");
                    SharedPreferences.Editor editor = setting.edit();
                    editor.putInt("dateD", 0);
                    editor.putInt("IdTask", 0);
                    editor.commit();
                    new AddTimeWorked().execute();
                }


            }
        });

        if (setting.getInt("IdTask", 0) != idTask && setting.getInt("IdTask", 0) != 0) {
            Log.v("Condtion", setting.getInt("IdTask", 0) + "");
            start.setVisibility(View.GONE);
        } else {
            if (setting.getInt("IdTask", 0) == idTask) {
                start.setText("Stop the Task");
            }
        }
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

        private boolean good;

        @Override
        protected Void doInBackground(Void... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/time?token=" + setting.getString("Token", ""));


            try {

                String reponse = null;
                HttpEntity httpEntity = null;
                Date actuelle = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                JSONObject ajout = null;

                HttpPost httpPost = new HttpPost(api_URL);
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("date", dateFormat.format(actuelle)));
                pairs.add(new BasicNameValuePair("time", timingPast + ""));
                pairs.add(new BasicNameValuePair("task_id", idTask + ""));
                pairs.add(new BasicNameValuePair("user_id", setting.getInt("IdClient", 0) + ""));


                httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httpPost.setHeader("X-Requested-With", "XMLHttpRequest");

                httpPost.setEntity(new UrlEncodedFormEntity(pairs));
                HttpResponse response = httpclient.execute(httpPost);
                httpEntity = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);


                ajout = new JSONObject(reponse);
                good = ajout.getBoolean("success");
                if (!good)
                    Log.v("CESPASKADO", ajout.getString("error"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (good) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Succes");
                adb.setMessage("Your time has been update with succes");
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                adb.show();
            }
        }
    }

    private class AddTodo extends AsyncTask<Void, Void, Void> {

        private boolean good;

        @Override
        protected Void doInBackground(Void... params) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
        String api_URL = getString(R.string.webservice).concat("/api/todo?token=" + setting.getString("Token", ""));


        try {

            String reponse = null;
            HttpEntity httpEntity = null;

            JSONObject ajout = null;

            HttpPost httpPost = new HttpPost(api_URL);
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("done", 0+""));
            pairs.add(new BasicNameValuePair("label", todoTxt.getText().toString()));
            pairs.add(new BasicNameValuePair("task_id", idTask + ""));


            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httpPost.setHeader("X-Requested-With", "XMLHttpRequest");

            httpPost.setEntity(new UrlEncodedFormEntity(pairs));
            HttpResponse response = httpclient.execute(httpPost);
            httpEntity = response.getEntity();
            reponse = EntityUtils.toString(httpEntity);


            ajout = new JSONObject(reponse);
            good = ajout.getBoolean("success");
            if (!good)
                Log.v("CESPASKADO", ajout.getString("error"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        if (good) {
          todoTxt.setText("");
          new LoadTodo().execute();
        }
        else {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle("Error");
            adb.setMessage("Problem with the database or with your session id");
            adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            adb.show();
        }
    }
    }
}
