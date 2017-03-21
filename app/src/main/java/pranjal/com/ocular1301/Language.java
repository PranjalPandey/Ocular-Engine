package pranjal.com.ocular1301;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Map;

import retrofit.RestAdapter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class Language extends Fragment {

    private Subscription sub;
String str;
    public Language() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        str = getActivity().getIntent().getStringExtra("myString");
        return inflater.inflate(R.layout.fragment_language, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View view = getView();
final EditText Etext=(EditText) view.findViewById(R.id.textTranslate);
        Etext.setText(""+str);
        if(view != null) {
            view.findViewById(R.id.translButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String text = Etext.getText().toString();

                    //detect direction
                    Spinner lang_from = (Spinner) getActivity().findViewById(R.id.lang_from_spinner);
                    final Spinner lang_to = (Spinner) getActivity().findViewById(R.id.lang_to_spinner);

                    String direction = getKeyFromValue(LangData.getLangs(), lang_from.getSelectedItem().toString())
                            + "-"
                            + getKeyFromValue(LangData.getLangs(), lang_to.getSelectedItem().toString());

                    if (RequestStore.getInstance().getTranslationRequest() == null) {
                        RestAdapter restAdapter = new RestAdapter.Builder()
                                .setLogLevel(RestAdapter.LogLevel.FULL)
                                .setEndpoint(ITranslateApi.API_URL)
                                .build();

                        ITranslateApi translateApi = restAdapter.create(ITranslateApi.class);
                        RequestStore.getInstance().setTranslation(translateApi.getTranslation(direction, text).cache());
                    }



                    sub = RequestStore.getInstance().getTranslationRequest()
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<Translation>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    RequestStore.getInstance().setTranslation(null);
                                    AlertDialog alertDialog = new AlertDialog.Builder(Language.this.getActivity()).create();
                                    alertDialog.setTitle(getString(R.string.error_title));
                                    alertDialog.setMessage(e.getMessage());
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, Language.this.getString(R.string.ok_word),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }

                                @Override
                                public void onNext(Translation translation) {
                                    RequestStore.getInstance().setTranslation(null);
                                    Intent intent = new Intent(getActivity(), TranslationActivity.class);
                                    intent.putExtra("transl", translation.text.get(0));
                                    intent.putExtra("language",getKeyFromValue(LangData.getLangs(), lang_to.getSelectedItem().toString()));
                                    startActivity(intent);
                                }
                            });

                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sub != null) {
            sub.unsubscribe();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private static String getKeyFromValue(Map<String, String> hm, String value) {
        for (String o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

}
