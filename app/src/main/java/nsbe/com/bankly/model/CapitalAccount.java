package nsbe.com.bankly.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * Created by Charlton on 11/10/17.
 */

public class CapitalAccount {
    @SerializedName("_id")
    String _id;
    @SerializedName("type")
    String type;
    @SerializedName("nickname")
    String nickname;
    @SerializedName("rewards")
    int rewards;
    @SerializedName("balance")
    double balance;
    @SerializedName("customer_id")
    String customer_id;

    public double getTotalBalance(){
        return balance;
    }

    public String get_id() {
        return _id;
    }

    public String getType() {
        return type;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRewards() {
        return "Reward Points: " + String.valueOf(rewards);
    }

    public String getBalance() {
        return String.format(Locale.US, "$%.2f", balance);
    }

    public String getCustomer_id() {
        return customer_id;
    }
}
