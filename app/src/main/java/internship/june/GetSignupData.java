package internship.june;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSignupData {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("message")
    @Expose
    public String message;

}
