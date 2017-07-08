package tskaws.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import static tskaws.app.MainActivity.FILE_KEY;

/**
 * Created by Jason on 6/5/17.
 */

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "EventActivity";
    Application app = null;

    @Override
    public void onStop(){
        super.onStop();
        save();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        Log.d(TAG, "About to start Activity");
        app = Application.getInstance();

        // Get the Intent that started this activity and extract the event
        Intent intent = getIntent();
        String eventId = (String) intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        final EventItem event = app.findEventById(eventId);
        //app = (Application) intent.getSerializableExtra(MainActivity.EXTRA_TEXT);

        Log.d(TAG, "Received Intent and made an event " + event.getTitle());


        if (event.getImageUrl() != null) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            new DownloadImage(imageView).execute(event.getImageUrl());
        }

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(event.getTitle());

        setTitle(event.getTitle());

        Format formatter = new SimpleDateFormat("MMMM dd, yyyy");
        String theDate = formatter.format(event.getDate());
        TextView dateView = (TextView) findViewById(R.id.date);
        dateView.setText(theDate);

        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(event.getDescription());

        Log.d(TAG, "Got to the end Activity");

        Button button = (Button) findViewById(R.id.calendarButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(event.addToCalendar());
            }
        });

        Button share = (Button) findViewById(R.id.facebook);
        share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shareEvent(event);
            }
        });

        final CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
        checkbox.setChecked(event.isStarred());
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setStarred(checkbox.isChecked());
                EventActivity.this.save();
            }
        });

    }

    public void shareEvent (EventItem event) {
        String urlToShare = event.getLink();
        Intent fIntent = new Intent(Intent.ACTION_SEND);
        fIntent.setType("text/plain");
        fIntent.putExtra(Intent.EXTRA_TEXT, urlToShare);

        // See if official Facebook app is found
        boolean facebookAppFound = false;
        List<ResolveInfo> matches = getPackageManager().queryIntentActivities(fIntent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                fIntent.setPackage(info.activityInfo.packageName);
                facebookAppFound = true;
                break;
            }
        }

        // As fallback, launch sharer.php in a browser
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
            fIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
        }

        startActivity(fIntent);
    }

    public void save(){
        // @TODO do firebase stuff here
        SharedPreferences myPrefs = getSharedPreferences(FILE_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefsEditor = myPrefs.edit();
        myPrefsEditor.putString("Application", app.sendAppToJson());
        myPrefsEditor.commit();
    }
}