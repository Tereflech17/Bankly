package nsbe.com.bankly.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nsbe.com.bankly.BuildConfig;

/**
 * Created by Charlton on 11/11/17.
 */

public class CapitalPurchaseRequest {

    String merchant_id = BuildConfig.MERCHANT;
    String medium =  "balance";
    String purchase_date;
    double amount;
    String description;

    CapitalPurchaseRequest(UPCModel model){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        this.purchase_date = df.format(Calendar.getInstance().getTime());

        this.amount = (double)model.getPrice() / 100;
        this.description = String.format(Locale.US, "Purchase of $%.2f for %s", (double)model.getPrice() / 100, model.getName());
    }

    public static CapitalPurchaseRequest create(UPCModel model){
        return new CapitalPurchaseRequest(model);
    }

}
