package pranjal.com.ocular1301;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class Start extends Activity {

    private final static int TIMEOUT_TIME = 3;
    private Subscription sub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (RequestStore.getInstance().getLangsRequest() == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setEndpoint(ITranslateApi.API_URL)
                    .build();
            ITranslateApi translateApi = restAdapter.create(ITranslateApi.class);

            RequestStore.getInstance().setLangsRequest(translateApi.getLangs().cache());
        }

        sub = RequestStore.getInstance().getLangsRequest()
                .timeout(TIMEOUT_TIME, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Langs>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        RequestStore.getInstance().setLangsRequest(null);
                        AlertDialog alertDialog = new AlertDialog.Builder(Start.this).create();
                        alertDialog.setTitle(getString(R.string.error_title));
                        alertDialog.setMessage(getString(R.string.no_internet_message));
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok_word),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                       finish();
                                    }
                                });
                        alertDialog.show();
                    }

                    @Override
                    public void onNext(Langs langs) {
                        RequestStore.getInstance().setLangsRequest(null);
                        Intent intent = new Intent(Start.this, TranslateActivity.class);
                       intent.putExtra("myString", getIntent().getExtras().getString("myString"));                        intent.putExtra("dirs", langs.getDirs());
                        intent.putExtra("langs", langs.getLangs());
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sub != null) {
            sub.unsubscribe();
        }
    }
}
