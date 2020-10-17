package nsbe.com.bankly.model;

/**
 * Created by Charlton on 11/10/17.
 */

public class CapitalPurchase {



    String _id;
    String type;
    String merchant_id;
    String payer_id;
    String purchase_date;
    double amount;
    String status;
    String medium;
    String description;

    public String get_id() {
        return _id;
    }

    public String getType() {
        return type;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public String getPayer_id() {
        return payer_id;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getMedium() {
        return medium;
    }

    public String getDescription() {
        return description;
    }
}
