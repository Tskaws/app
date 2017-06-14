package tskaws.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    ListView list;
    String[] titles = new String[2];
    String[] description = new String[2];
    Application app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        app = Application.restore();

        // Testing Dummies
        titles[0]= "Ropes Course";
        description[0] = "Climb stuff";
        titles[1]= "Date Night";
        description[1] = "Date women";

        // Populate the list
        list = (ListView) findViewById(R.id.events);
        MainActivity.MyAdapter adapter = new MainActivity.MyAdapter(this, titles, description);
        list.setAdapter(adapter);
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

    /** Using the row.xml layout create an adapter for the strings to
      *  adapt the list for display. Code inspired by https://www.youtube.com/watch?v=D0or0X12FMM
     **/
    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String myTitles[];
        String myDescription[];

        MyAdapter(Context c, String[] titles, String[] description) {
            super(c, R.layout.row, R.id.text1, titles);
            this.context = c;
            this.myTitles = titles;
            this.myDescription = description;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            TextView myTitle = (TextView) row.findViewById(R.id.text1);
            TextView myDescription = (TextView) row.findViewById(R.id.text2);
            myTitle.setText(titles[position]);
            myDescription.setText(description[position]);

            return row;
        }
    }
}

/* I love you all */
    // ~Levi~ //