package tskaws.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    public static final String EXTRA_MESSAGE = "tskaws.app.MESSAGE";
    public static final String EXTRA_TEXT = "tskaws.app.TEXT";
    public static final String TAG = "Main_Activity";
    public static final String FILE_KEY= "tskaws.app.PREFERENCE_FILE_KEY";
    private MaterialSearchBar searchBar;
    List<String> suggestions = new ArrayList<>();

    ListView list;
    MainActivity.ActivityListAdapter adapter = null;
    Application app = null;
    int currentTab = 0;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        this.app = Application.restore(getApplicationContext());

        app.addObserver(this);

        // Populate the list
        list = (ListView) findViewById(R.id.events);
        this.adapter = new MainActivity.ActivityListAdapter(this, app);
        list.setAdapter(this.adapter);

        // Create suggestions for the search bar
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        // Categories!
        searchBar.inflateMenu(R.menu.categories);
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                query = item.getTitle().toString();
                MainActivity.this.rerender();
                searchBar.setText(query);
                return false;
            }
        });


        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    MainActivity.this.query = null;
                    MainActivity.this.rerender();
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                MainActivity.this.query = text.toString();
                MainActivity.this.rerender();
                searchBar.clearFocus();
            }

            @Override
            public void onButtonClicked(int buttonCode) {
            }
        });


        ((TabLayout)this.findViewById(R.id.tabs)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        feed();
                        break;
                    case 1:
                        trending();
                        break;
                    case 2:
                        favorites();
                        break;
                    default:
                        feed();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences myPrefs = getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = myPrefs.getString("Application", "");
        if (json != null && !json.isEmpty()) {
            List<EventItem> newEvents = new ArrayList<>();
            newEvents = gson.fromJson(json, new TypeToken<List<EventItem>>() {
            }.getType());
            // Clear stars
            for(EventItem item : newEvents) {
                item.clearStars();
            }
            app.setEventItems(newEvents);
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        save();
    }

    public void save(){

        JsonObject jsonObject = new JsonObject();

        SharedPreferences myPrefs = getSharedPreferences(FILE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
        myPrefsEditor.putString("Application", app.toJson());
        myPrefsEditor.commit();
    }

    public void update(Observable o, Object arg) {
        rerender();
    }

    public void rerender(){
        this.adapter.reload();
    }

    public void feed() {
        currentTab = 0;
        rerender();
    }

    public void trending() {
        currentTab = 1;
        rerender();
    }

    public void favorites() {
        currentTab = 2;
        rerender();
    }

    /** Using the row.xml layout create an adapter for the strings to
      *  adapt the list for display. Code inspired by https://www.youtube.com/watch?v=D0or0X12FMM
     **/
    class ActivityListAdapter extends ArrayAdapter<EventItem> {
        Context context;
        Application app;

        ActivityListAdapter(Context c, Application app) {
            super(c, R.layout.row, R.id.text1, app.getEventItems());
            this.context = c;
            this.app = app;
        }

        public List<EventItem> filter(List<EventItem> list) {
            List<EventItem> returned = new ArrayList<>();
            for(EventItem item : list) {
                if (
                        // This section is if everything is normal and the search bar is empty
                        (
                            ((MainActivity.this.currentTab == 0) // first tab shows everything
                            || (MainActivity.this.currentTab == 2 && item.isStarred()) // show only items starred
                            ) && (query == null) // Search bar is empty
                        )
                        || (query != null && (item.getTitle().toLowerCase().contains(query.toLowerCase())
                                        || item.getCategory().toLowerCase().contains(query.toLowerCase()))
                        ) // If a query is in the search bar, it displays that instead.
                    )
                {
                    returned.add(item);

                } else if (MainActivity.this.currentTab == 1) {
                    if (item.getStarsCount() > 0)
                        returned.add(item);
                }
            }
            // Tells the user there are no activities if the list is empty.
            if (returned.size() == 0) {
                Toast.makeText(MainActivity.this, "There are no activities here.", Toast.LENGTH_LONG).show();
            }

            if (MainActivity.this.currentTab == 1) {
                Collections.sort(returned);
                Collections.reverse(returned);
                return returned;
           }
            return returned;
        }

        public void reload() {
            this.clear();
            this.addAll(filter(app.getEventItems()));
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final EventItem item = this.getItem(position);
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView myTitle = (TextView) row.findViewById(R.id.text1);
            TextView myDate = (TextView) row.findViewById(R.id.date);
            TextView myStars = (TextView) row.findViewById(R.id.stars);
            ImageView myLogo = (ImageView) row.findViewById(R.id.logo);
            myTitle.setText(item.getTitle());

            Format formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            String theDate = formatter.format(item.getDate());
            myDate.setText(theDate);

            myStars.setText(""+item.getStarsCount() + " Star" + (item.getStarsCount() != 1 ? "s" : ""));

            final CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
            checkbox.setChecked(item.isStarred());
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setStarred(checkbox.isChecked(), true);
                    MainActivity.this.save();
                }
            });

            final Intent intent = new Intent(this.context, EventActivity.class);
            intent.putExtra(EXTRA_MESSAGE, item.getGuid());
            //intent.putExtra(EXTRA_TEXT, this.app);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });


            ImageView image = (ImageView) row.findViewById(R.id.logo);
            if (item.getImageUrl() != null) {
                new DownloadImage(image).execute(item.getImageUrl());
            }

            return row;
        }
    }

}

/* I love you all */
    // ~Levi~ //