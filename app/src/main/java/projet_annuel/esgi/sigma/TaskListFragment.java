package projet_annuel.esgi.sigma;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.lang.ref.WeakReference;

import projet_annuel.esgi.sigma.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class TaskListFragment extends ListFragment {

    String jsonTasks;
    private OnFragmentInteractionListener mListener;
    private WeakReference<LoadTaskData> asyncTaskWeakRef;


    public TaskListFragment() {
    }

    private void startNewAsyncTask() {
        LoadTaskData asyncTask = new LoadTaskData(this);
        this.asyncTaskWeakRef = new WeakReference<LoadTaskData>(asyncTask );
        asyncTask.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jsonTasks = getArguments().getString("Tasks");

        setRetainInstance(true);



        // TODO: Change Adapter to display your content
        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS));
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
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
        public void onFragmentInteraction(String id);
    }

    private static class LoadTaskData extends AsyncTask<Void, Void, Void> {

        private WeakReference<TaskListFragment> fragmentWeakRef;

        private LoadTaskData (TaskListFragment fragment) {
            this.fragmentWeakRef = new WeakReference<TaskListFragment>(fragment);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //TODO: your background code
            return null;
        }

        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            if (this.fragmentWeakRef.get() != null) {
                //TODO: treat the result
            }
        }
    }
}
