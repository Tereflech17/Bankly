package nsbe.com.bankly.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Charlton on 11/10/17.
 */

public class CapitalCustomer {

    @SerializedName("_id")
    String _id;
    @SerializedName("first_name")
    String first_name;
    @SerializedName("last_name")
    String last_name;
    @SerializedName("address")
    CapitalAddress address;

    public String getId() {
        return _id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public CapitalAddress getAddress() {
        return address;
    }

    /**
     * Created by Charlton on 11/10/17.
     */

    public static class CapitalAddress {

        @SerializedName("street_number")
        String street_number;
        @SerializedName("street_name")
        String street_name;
        @SerializedName("city")
        String city;
        @SerializedName("state")
        String state;
        @SerializedName("zip")
        String zip;

        public String getStreet_number() {
            return street_number;
        }

        public String getStreet_name() {
            return street_name;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        public String getZip() {
            return zip;
        }
    }
}
