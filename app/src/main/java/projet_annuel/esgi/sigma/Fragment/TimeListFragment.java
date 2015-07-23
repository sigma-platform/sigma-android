package projet_annuel.esgi.sigma.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import projet_annuel.esgi.sigma.Modele.ListTimeAdapter;
import projet_annuel.esgi.sigma.Modele.ListTodoAdapter;
import projet_annuel.esgi.sigma.Modele.Task;
import projet_annuel.esgi.sigma.Modele.Time;
import projet_annuel.esgi.sigma.R;

// The Fragment which stock the list of time
public class TimeListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    ListView lstTime;
    private String[] lstLbl;
    private String[] lstDateE;
    private String[] lstDateP;
    private String[] lstDate;

    // TODO: Rename and change types and number of parameters
    public static TimeListFragment newInstance(String param1, String param2) {
        TimeListFragment fragment = new TimeListFragment();
        return fragment;
    }

    public TimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new LoadTime().execute();
        return inflater.inflate(R.layout.fragment_time_list, container, false);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
    // Get all the time pasted from the database
    private class LoadTime extends AsyncTask<Void, Void, Void> {

        private boolean good;

        @Override
        protected Void doInBackground(Void... params) {

            SharedPreferences setting = getActivity().getSharedPreferences(getString(R.string.PREFS_DATA), Context.MODE_PRIVATE);
            String api_URL = getString(R.string.webservice).concat("/api/time?token=" + setting.getString("Token", ""));
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
                    lstDate = new String[js.length()];
                    lstDateE = new String[js.length()];
                    lstDateP = new String[js.length()];
                    lstLbl = new String[js.length()];
                    //put in tabs all the data
                    for (int i = 0; i < js.length(); i++) {
                        JSONObject objectInArray = js.getJSONObject(i);
                        lstDate[i] = objectInArray.getString("date");
                        lstLbl[i] = objectInArray.getJSONObject("task").getString("label");
                        lstDateP[i] = objectInArray.getString("time");
                        lstDateE[i] = objectInArray.getJSONObject("task").getString("estimated_time");
                    }
                }

            }catch(IOException e1){
                e1.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute (Void aVoid){
            if (good) {
                //create all the elements of the list
                if (lstDateE != null) {
                    lstTime = (ListView) getView().findViewById(R.id.listView);
                    ArrayList<Time> list = new ArrayList<>();
                    for (int i = 0; i < lstDateE.length; i++) {
                        list.add(new Time(lstLbl[i],lstDate[i],lstDateE[i],lstDateP[i]));
                    }
                    lstTime.setAdapter(new ListTimeAdapter(getActivity().getApplicationContext(), list));
                }
            }
        }

    }

}
