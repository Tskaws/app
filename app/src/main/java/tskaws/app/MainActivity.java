package tskaws.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Observable;
import java.util.Observer;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    ListView list;
    String[] titles = new String[2];
    String[] description = new String[2];
    MainActivity.MyAdapter adapter = null;
    Application app = null;

    ListView list;
    MainActivity.MyAdapter adapter = null;
    Application app = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        this.app = Application.restore();
        app.addObserver((Observer) this);

        // Populate the list
        list = (ListView) findViewById(R.id.events);
        this.adapter = new MainActivity.MyAdapter(this, app);
        list.setAdapter(this.adapter);
    }

    /** SearchBar
     * This is the customization work-in-progress of the search feature
     * being in the app bar.
     * @param menu
     * @return
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);


        /*
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Search view options
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        */
        return true;
    }
    @Override
    public void onResume(){
        super.onResume();

        // Load the existing Application from SharedPreferences
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = myPrefs.getString("Application", "");
        app = gson.fromJson(json, Application.class);
    }
    @Override
    public void onStop(){
        super.onStop();

        // Save the EventList as a JSON string
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
        myPrefsEditor.putString("Application", app.sendAppToJson());
        myPrefsEditor.commit();
    }

    public void update(Observable o, Object arg) {
        //this.adapter.notifyDataSetChanged();
        //this.adapter.clear();
        this.adapter.reload();
    }

    /** Using the row.xml layout create an adapter for the strings to
      *  adapt the list for display. Code inspired by https://www.youtube.com/watch?v=D0or0X12FMM
     **/
    class MyAdapter extends ArrayAdapter<EventItem> {
        Context context;
        Application app;

        MyAdapter(Context c, Application app) {
            super(c, R.layout.row, R.id.text1, app.getEventItems());
            this.context = c;
            this.app = app;
        }

        public void reload() {

            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EventItem item = this.app.getEventItems().get(position);
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView myTitle = (TextView) row.findViewById(R.id.text1);
            TextView myDescription = (TextView) row.findViewById(R.id.text2);
            myTitle.setText(item.getTitle());
            myDescription.setText(item.getDescription());

            return row;
        }
    }
}

/* I love you all */
    // ~Levi~ //