package projet_annuel.esgi.sigma.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import projet_annuel.esgi.sigma.Modele.ListTaskAdapter;
import projet_annuel.esgi.sigma.Modele.Task;
import projet_annuel.esgi.sigma.Modele.TaskDelegate;
import projet_annuel.esgi.sigma.R;

// Class loading the list of tasks of a single project
public class TaskListFragment extends Fragment {

    private String TabDateF[];
    private String TabDateD[];
    private String TabLib[];
    private String TabVers[];
    private Float TabTmp[];
    private Integer TabId[];
    private int role;

    private OnFragmentInteractionListener mListener;
    private int projectId;

    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        return fragment;
    }

    public TaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            //we get the arguments here
            projectId = getArguments().getInt("Id");
            role = getArguments().getInt("role");

        }
        //we start the asynctask who load the list
        new LoadTaskList().execute();
        View v = inflater.inflate(R.layout.fragment_task_list, container, false);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

// Task who get the list of tasks
    private class LoadTaskList extends AsyncTask<Void,Void,Void>{

        boolean good;
        String message="";
        public LoadTaskList() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/project/" + projectId + "/task?token=" + setting.getString("Token", ""));
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(api_URL);

            String reponse = null;
            HttpEntity httpEntity = null;

            try {
                HttpResponse response = httpclient.execute(httpGet);
                httpEntity  = response.getEntity();
                reponse = EntityUtils.toString(httpEntity);
                JSONObject update = new JSONObject(reponse);

                good = update.getBoolean("success");
                if(good){
                    JSONArray list = update.getJSONArray("payload");
                    // init all the tab
                    TabLib = new String[list.length()];
                    TabDateD = new String[list.length()];
                    TabDateF = new String[list.length()];
                    TabTmp = new Float[list.length()];
                    TabId = new Integer[list.length()];
                    TabVers = new String[list.length()];

                    //put the good value in it
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject objectInArray = list.getJSONObject(i);
                        TabId[i] = objectInArray.getInt("id");
                        TabLib[i] = objectInArray.getString("label");
                        TabTmp[i] = (float) objectInArray.getInt("estimated_time");
                        TabDateD[i] = objectInArray.getString("date_start");
                        TabDateF[i] = objectInArray.getString("date_end");
                        TabVers[i] = objectInArray.getJSONObject("version").getString("description");
                    }
                }
                else {
                    message = update.getString("error");
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(good) {
                if (TabTmp != null && TabLib != null && TabDateD !=null && TabDateF !=null) {
                    ListView lv = (ListView) getView().findViewById(R.id.listTask);
                    String testeur ="";
                    ArrayList listTask = new ArrayList();
                    if(TabVers.length!=0)
                        testeur = TabVers[0];
                    for (int i = 0; i < TabDateD.length; i++) {
                        //if good we put a separator else we dont
                        if(testeur.equals(TabVers[i]) && i !=0)
                            listTask.add(new Task(TabLib[i],TabDateD[i],TabDateF[i],TabTmp[i],""));
                        else
                           listTask.add(new Task(TabLib[i],TabDateD[i],TabDateF[i],TabTmp[i],TabVers[i]));
                        testeur = TabVers[i];
                    }
                    //set the adapter of the list
                    lv.setAdapter(new ListTaskAdapter(getActivity().getApplicationContext(), listTask));
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Fragment newFragment = new TaskFragment();
                            Bundle args = new Bundle();
                            args.putInt("IdTask",TabId[position]);
                            newFragment.setArguments(args);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.flContent, newFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                }
            }
            else
            {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage(message);

                alertDialogBuilder.setPositiveButton("ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }



}