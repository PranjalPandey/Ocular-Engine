package pranjal.com.ocular1301;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;


public class TranslationActivity extends Activity implements TextToSpeech.OnInitListener {
    TextToSpeech TTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        
        TTS = new TextToSpeech(this, this);
        String text = getIntent().getStringExtra("transl");
        if(text != null) {
            ((TextView) findViewById(R.id.translation)).setText(text);
        }
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TTS.speak(((TextView) findViewById(R.id.translation)).getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = TTS.setLanguage(new Locale(""+ getIntent().getStringExtra("language")));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // R.id.speak.setEnabled(true);
                //speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }


}
