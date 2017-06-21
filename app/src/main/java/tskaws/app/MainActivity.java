package tskaws.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    public static final String EXTRA_MESSAGE = "tskaws.app.MESSAGE";
    public static final String TAG = "Main_Activity";
    ListView list;
    MainActivity.MyAdapter adapter = null;
    Application app = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        this.app = Application.restore(getApplicationContext());


        // Changes made by TJ
        // trying to get this.app to not equal null
/*        Crawler crawler = new Crawler(this.app);
        try {
            crawler.crawl();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        app.addObserver((Observer) this);

        // Populate the list
        list = (ListView) findViewById(R.id.events);
        this.adapter = new MainActivity.MyAdapter(this, app);
        list.setAdapter(this.adapter);
    }


    @Override
    public void onResume(){
        super.onResume();
        // Load the existing Application from SharedPreferences
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = myPrefs.getString("Application", "");
        if (json != null && !json.isEmpty()) {
            // arrays have to be desearealized seperately
            ArrayList<EventItem> newEvents = new ArrayList<>();
            newEvents = gson.fromJson(json, new TypeToken<List<EventItem>>() {
            }.getType());
            app.setEventItems(newEvents);
        }
    }
    @Override
    public void onStop(){
        super.onStop();

        // Save the EventList as a JSON string
        SharedPreferences myPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
        myPrefsEditor.putString("Application", this.app.sendAppToJson()); // this code is breaking
        myPrefsEditor.apply();
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
            this.clear();
            this.addAll(app.getEventItems());
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EventItem item = this.app.getEventItems().get(position);
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView myTitle = (TextView) row.findViewById(R.id.text1);
            //TextView myDescription = (TextView) row.findViewById(R.id.text2);
            TextView myDate = (TextView) row.findViewById(R.id.date);
            ImageView myLogo = (ImageView) row.findViewById(R.id.logo);
            myTitle.setText(item.getTitle());
            //myDescription.setText(item.getDescription());

            Format formatter = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            String theDate = formatter.format(item.getDate());
            myDate.setText(theDate);

            final CheckBox checkbox = (CheckBox) row.findViewById(R.id.checkbox);
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkbox.isChecked()){
                        System.out.println("Checked");

                    }else{
                        System.out.println("Un-Checked");
                    }
                }
            });

            final Intent intent = new Intent(this.context, EventActivity.class);
            intent.putExtra(EXTRA_MESSAGE, item);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                }
            });



            if (item.getImageUrl() != null) {
                new DownloadImage((ImageView) row.findViewById(R.id.logo)).execute(item.getImageUrl());
            }

            return row;
        }
    }

    class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView image;

        public DownloadImage(ImageView image){
            this.image = image;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);
        }
    }
}

/* I love you all */
    // ~Levi~ //