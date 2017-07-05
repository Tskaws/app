package tskaws.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    public static final String EXTRA_MESSAGE = "tskaws.app.MESSAGE";
    public static final String EXTRA_TEXT = "tskaws.app.TEXT";
    public static final String TAG = "Main_Activity";
    private MaterialSearchBar searchBar;
    List<String> suggestions = new ArrayList<>();
    CustomSuggestionsAdapter customSuggestionsAdapter;

    ListView list;
    MainActivity.MyAdapter adapter = null;
    Application app = null;
    int currentTab = 0;

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
        this.adapter = new MainActivity.MyAdapter(this, app);
        list.setAdapter(this.adapter);

        // Create suggestions for the search bar
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        customSuggestionsAdapter = new CustomSuggestionsAdapter(inflater);
        searchBar.setCustomSuggestionAdapter(customSuggestionsAdapter);
        getCategories();

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

    public void getCategories() {
        List<EventItem>  eventList = app.getEventItems();
        customSuggestionsAdapter.clearSuggestions();
        List<String> added = new ArrayList<>();

        for(EventItem item : eventList) {
            boolean found = false;
            for(String existing : added) {
                if (existing.equals(item.getCategory())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                added.add(item.getCategory());
                customSuggestionsAdapter.addSuggestion(item.getCategory());
            }
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = myPrefs.getString("Application", "");
        if (json != null && !json.isEmpty()) {
            List<EventItem> newEvents = new ArrayList<>();
            newEvents = gson.fromJson(json, new TypeToken<List<EventItem>>() {
            }.getType());
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

        // @TODO add logic to add stars to database when an event gets starred
/*        for (int i = 0; i < app.getEventItems().size(); i++) {
            String guid = app.getEventItems().get(i).getGuid();
            int numStars = app.getEventItems().get(i).getTotalStars();
            jsonObject.addProperty("guid", guid);
            jsonObject.addProperty("numStars", numStars);
            Ion.with(this.getApplicationContext())
                    .load("https://tmcd.cloudant.com/student_activities/")
                    .setJsonObjectBody(jsonObject)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null)
                                Log.e("Error in", "posting to database");
                        }
                    });
        }
*/
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
        myPrefsEditor.putString("Application", this.app.sendAppToJson()); // this code is breaking
        myPrefsEditor.apply();
    }

    public void update(Observable o, Object arg) {
        rerender();
    }

    public void rerender(){
        this.adapter.reload();
        getCategories();
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
    class MyAdapter extends ArrayAdapter<EventItem> {
        Context context;
        Application app;

        MyAdapter(Context c, Application app) {
            super(c, R.layout.row, R.id.text1, app.getEventItems());
            this.context = c;
            this.app = app;
        }

        public List<EventItem> filter(List<EventItem> list) {
            List<EventItem> returned = new ArrayList<>();
            for(EventItem item : list) {
                if ((MainActivity.this.currentTab == 0) // first tab shows everything
                || (MainActivity.this.currentTab == 1) // @TODO show only trending
                || (MainActivity.this.currentTab == 2 && item.isStarred()) // show only items starred
                /*@todo add case for searching and categories here*/
                ) {
                    returned.add(item);
                }
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
            ImageView myLogo = (ImageView) row.findViewById(R.id.logo);
            myTitle.setText(item.getTitle());

            Format formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            String theDate = formatter.format(item.getDate());
            myDate.setText(theDate);

            final CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
            checkbox.setChecked(item.isStarred());
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setStarred(checkbox.isChecked());
                    if(item.isStarred()) {
                        // Post to the data base!!!
                        JsonObject jsonObject = new JsonObject();
                        String guid = item.getGuid();
                        jsonObject.addProperty("guid", guid);
                        jsonObject.addProperty("title", item.getTitle());
                        Ion.with(v.getContext())
                                .load("https://tmcd.cloudant.com/student_activities/")
                                .setJsonObjectBody(jsonObject)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        if (e != null)
                                            Log.e("Error in", "posting to database");
                                    }
                                });
                    }
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

    static class SuggestionHolder extends RecyclerView.ViewHolder{
        protected TextView title;

        public SuggestionHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.Title);
        }
    }

    public class CustomSuggestionsAdapter extends SuggestionsAdapter<String, SuggestionHolder> {


        public CustomSuggestionsAdapter(LayoutInflater inflater) {
            super(inflater);
        }

        @Override
        public void onBindSuggestionHolder(String suggestion, SuggestionHolder holder, int position) {
            holder.title.setText(suggestion);
        }

        @Override
        public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.suggestion, parent, false);
            return new SuggestionHolder(view);
        }

        @Override
        public int getSingleViewHeight() {
            return 19;
        }

    }
}

/* I love you all */
    // ~Levi~ //