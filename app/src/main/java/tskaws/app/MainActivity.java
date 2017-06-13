package tskaws.app;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ListView list;
    String[] titles = new String[2];
    String[] description = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        Application app = Application.restore();

        // Testing Dummies
        titles[0]= "Ropes Course";
        description[0] = "Description";
        titles[1]= "Date Night";
        description[1] = "Description";

        // Populate the list
        list = (ListView) findViewById(R.id.events);
        MainActivity.MyAdapter adapter = new MainActivity.MyAdapter(this, titles, description);
        list.setAdapter(adapter);
    }
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

    // Using the row.xml layout create an adapter for the strings to
    // adapt the list for display
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