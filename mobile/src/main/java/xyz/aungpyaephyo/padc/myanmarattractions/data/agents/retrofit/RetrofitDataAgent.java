package xyz.aungpyaephyo.padc.myanmarattractions.data.agents.retrofit;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import xyz.aungpyaephyo.padc.myanmarattractions.data.agents.AttractionDataAgent;
import xyz.aungpyaephyo.padc.myanmarattractions.data.models.AttractionModel;
import xyz.aungpyaephyo.padc.myanmarattractions.data.responses.AttractionListResponse;
import xyz.aungpyaephyo.padc.myanmarattractions.data.responses.RegisterResponse;
import xyz.aungpyaephyo.padc.myanmarattractions.events.UserEvent;
import xyz.aungpyaephyo.padc.myanmarattractions.utils.CommonInstances;
import xyz.aungpyaephyo.padc.myanmarattractions.utils.MyanmarAttractionsConstants;

/**
 * Created by aung on 7/9/16.
 */
public class RetrofitDataAgent implements AttractionDataAgent {

    private static RetrofitDataAgent objInstance;

    private final AttractionApi theApi;

    private RetrofitDataAgent() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyanmarAttractionsConstants.ATTRACTION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(CommonInstances.getGsonInstance()))
                .client(okHttpClient)
                .build();

        // theApi already include all the implementation for AttractionApi
        theApi = retrofit.create(AttractionApi.class);
    }

    public static RetrofitDataAgent getInstance() {
        if (objInstance == null) {
            objInstance = new RetrofitDataAgent();
        }
        return objInstance;
    }

    @Override
    public void loadAttractions() {
        Call<AttractionListResponse> loadAttractionCall = theApi.loadAttractions(MyanmarAttractionsConstants.ACCESS_TOKEN);
        loadAttractionCall.enqueue(new Callback<AttractionListResponse>() {
            @Override
            public void onResponse(Call<AttractionListResponse> call, Response<AttractionListResponse> response) {
                AttractionListResponse attractionListResponse = response.body();
                if (attractionListResponse == null) {
                    AttractionModel.getInstance().notifyErrorInLoadingAttractions(response.message());
                } else {
                    AttractionModel.getInstance().notifyAttractionsLoaded(attractionListResponse.getAttractionList());
                }
            }

            @Override
            public void onFailure(Call<AttractionListResponse> call, Throwable throwable) {
                AttractionModel.getInstance().notifyErrorInLoadingAttractions(throwable.getMessage());
            }
        });
    }

    @Override
    public void register(String name, String email, String password, String dateOfBirth, String countryOfOrigin) {
        Call<RegisterResponse> registerCall = theApi.register(name, email, password, dateOfBirth, countryOfOrigin);
        registerCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse registerResponse = response.body();
                if(registerResponse == null) {
                    UserEvent.FailedRegistrationEvent event = new UserEvent.FailedRegistrationEvent(response.message());
                    EventBus.getDefault().post(event);
                } else {
                    if (registerResponse.getCode() == MyanmarAttractionsConstants.RESPONSE_CODE_FAILED) {
                        UserEvent.FailedRegistrationEvent event = new UserEvent.FailedRegistrationEvent(registerResponse.getMessage());
                        EventBus.getDefault().post(event);
                    } else {
                        UserEvent.SuccessRegistrationEvent event = new UserEvent.SuccessRegistrationEvent(registerResponse.getLoginUser());
                        EventBus.getDefault().post(event);
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable throwable) {
                UserEvent.FailedRegistrationEvent event = new UserEvent.FailedRegistrationEvent(throwable.getMessage());
                EventBus.getDefault().post(event);
            }
        });
    }

    @Override
    public void login(String name, String password) {
        Call<RegisterResponse> loginCall = theApi.login(name, password);
        loginCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                RegisterResponse loginResponse = response.body();
                if (loginResponse == null) {
                    UserEvent.FailedLoginEvent loginEvent = new UserEvent.FailedLoginEvent(response.message());
                    EventBus.getDefault().post(loginEvent);
                } else {
                    switch (loginResponse.getCode()) {
                        case 401:
                            UserEvent.FailedLoginEvent loginEvent = new UserEvent.FailedLoginEvent(loginResponse.getMessage());
                            EventBus.getDefault().post(loginEvent);
                            break;
                        case 200:
                            UserEvent.SuccessLoginEvent successLoginEvent = new UserEvent.SuccessLoginEvent(loginResponse.getLoginUser());
                            EventBus.getDefault().post(successLoginEvent);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                UserEvent.FailedLoginEvent loginEvent = new UserEvent.FailedLoginEvent(t.getMessage());
                EventBus.getDefault().post(loginEvent);
            }
        });
    }
}