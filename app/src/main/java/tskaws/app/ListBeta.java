package tskaws.app;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListBeta extends AppCompatActivity {


    ListView list;
    String[] titles = new String[2];
    String[] description = new String[2];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_beta);

        titles[0]= "Ropes Course";
        description[0] = "Description";

        titles[1]= "Date Night";
        description[1] = "Description";

        list = (ListView) findViewById(R.id.events);
        ListBeta.MyAdapter adapter = new ListBeta.MyAdapter(this, titles, description);
        list.setAdapter(adapter);

    }

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
