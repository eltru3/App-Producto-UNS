package uns.saborea.networking;

import com.google.gson.annotations.SerializedName;

public class ModelResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("tipo_cuenta")
    private String accountType;

    // Métodos Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }

    public String getAccountType() {
        return accountType;
    }
}