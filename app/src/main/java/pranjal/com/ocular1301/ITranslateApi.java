package pranjal.com.ocular1301;


import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface ITranslateApi {
    static final String API_KEY = "trnsl.1.1.20150927T104821Z.0a050ec5a56ab6fb.6ff6f9f4b75fbc975ac624dc42e665b9de2f5eb0";
    static final String API_URL = "https://translate.yandex.net/api";

    @GET("/v1.5/tr.json/translate?key=" + API_KEY)
    Observable<Translation> getTranslation(@Query("lang") String direction, @Query("text") String text);

    @GET("/v1.5/tr.json/getLangs?ui=en&key=" + API_KEY)
    Observable<Langs> getLangs();
}
