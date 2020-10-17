package nsbe.com.bankly.service;


import java.util.ArrayList;

import io.reactivex.Observable;
import nsbe.com.bankly.model.CapitalAccount;
import nsbe.com.bankly.model.CapitalCustomer;
import nsbe.com.bankly.model.CapitalPurchase;
import nsbe.com.bankly.model.CapitalPurchaseRequest;
import nsbe.com.bankly.model.CapitalResponse;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Charlton on 11/10/17.
 */

public interface CustomerService {

    @GET("/customers/{id}")
    Observable<CapitalCustomer> getCustomer(@Path("id") String id);

    @GET("/customers/{id}/accounts")
    Observable<ArrayList<CapitalAccount>> getAccounts(@Path("id") String id);


    //region Purchase

    @GET("/accounts/{id}/purchases")
    Observable<ArrayList<CapitalPurchase>> getPurchases(@Path("id") String account_id);

    @POST("/accounts/{id}/purchases")
    Observable<CapitalResponse<CapitalPurchase>> makePurchase(@Path("id") String account_id, @Body CapitalPurchaseRequest request);

    @GET("/purchases/{pid}")
    Observable<CapitalPurchase> getPurchase(@Path("pid") String purchase_id);

    @DELETE("/purchases/{pid}")
    Observable<CapitalPurchase> deletePurchase(@Path("pid") String purchase_id);

    //endregion


}
