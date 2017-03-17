package pranjal.com.ocular1301;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TranslateActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_main);


        Intent intent = getIntent();
        List<String> dirs = (ArrayList<String>) intent.getSerializableExtra("dirs");
        Map<String, String> langs = (HashMap<String, String>) intent.getSerializableExtra("langs");
        if(dirs != null || langs != null) {
            LangData.setDirs(dirs);
            LangData.setLangs(langs);
        }


        Spinner langFromSpinner = (Spinner) findViewById(R.id.lang_from_spinner);
        final Tooltip tooltip = new Tooltip.Builder(langFromSpinner)
                .setText("Choose the Language of the text here!")
                .setBackgroundColor(getResources().getColor(R.color.colorAccent))
                .setTextColor(getResources().getColor(android.R.color.white))
                .show();
       langFromSpinner.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               tooltip.dismiss();
               return false;
           }
       });
        langFromSpinner.getBackground().setColorFilter(getResources().getColor(android.R.color.white),
                PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                LangData.getLangs().values().toArray(new String[LangData.getLangs().size()]));

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        adapter.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });

        langFromSpinner.setAdapter(adapter);
        Spinner langToSpinner = (Spinner) findViewById(R.id.lang_to_spinner);
        langToSpinner.getBackground().setColorFilter(getResources().getColor(android.R.color.white),
                PorterDuff.Mode.SRC_ATOP);
        langToSpinner.setAdapter(adapter);

    }
}
