package pranjal.com.ocular1301;


import rx.Observable;

public class RequestStore {
    private static RequestStore instance = new RequestStore();
    private Observable<Langs> langsRequest = null;
    private Observable<Translation> translationRequest = null;

    public static RequestStore getInstance() {
        return instance;
    }

    private RequestStore() {
    }

    public Observable<Langs> getLangsRequest() {
        return langsRequest;
    }

    public void setLangsRequest(Observable<Langs> langsRequest) {
        this.langsRequest = langsRequest;
    }

    public Observable<Translation> getTranslationRequest() {
        return translationRequest;
    }

    public void setTranslation(Observable<Translation> translationRequest) {
        this.translationRequest = translationRequest;
    }
}
