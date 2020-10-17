package nsbe.com.bankly.service;

import io.reactivex.Observable;
import nsbe.com.bankly.model.UPCModel;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Charlton on 11/11/17.
 */

public interface UPCService {

    @GET("api/upc/{code}")
    Observable<UPCModel> getUPCModel(@Path("code") String code);
}
