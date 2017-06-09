package tskaws.app;

import android.app.usage.UsageEvents;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListBeta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_beta);

        Application application = new Application();

        List<EventItem> eventList;

        eventList = application.getEventItems();

        ListView mListView;

        mListView = (ListView) findViewById(R.id.theList);

        // Populate the list view
        String[] listItems = new String[eventList.size()];

        for(int i = 0; i < eventList.size(); i++){
            listItems[i] =  eventList.get(i).getTitle();
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);
    }
}
