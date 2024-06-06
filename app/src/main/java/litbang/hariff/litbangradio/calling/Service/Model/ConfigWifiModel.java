package litbang.hariff.litbangradio.calling.Service.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ConfigWifiModel implements Parcelable {
    private String SSIDName;
    private String SSIDPassword;
    private String myIp;
    private String myGateway;

    public ConfigWifiModel(){

    }

    public ConfigWifiModel(String SSIDName, String SSIDPassword, String myIp, String myGateway) {
        this.SSIDName = SSIDName;
        this.SSIDPassword = SSIDPassword;
        this.myIp = myIp;
        this.myGateway = myGateway;
    }


    public ConfigWifiModel(Parcel source) {
        this.SSIDName = source.readString();
        this.SSIDPassword = source.readString();
        this.myIp = source.readString();
        this.myGateway = source.readString();
    }

    public static final Creator<ConfigWifiModel> CREATOR = new Creator<ConfigWifiModel>() {
        @Override
        public ConfigWifiModel createFromParcel(Parcel in) {
            return new ConfigWifiModel(in);
        }

        @Override
        public ConfigWifiModel[] newArray(int size) {
            return new ConfigWifiModel[size];
        }
    };

    public String getSSIDName() {
        return SSIDName;
    }

    public void setSSIDName(String SSIDName) {
        this.SSIDName = SSIDName;
    }

    public String getSSIDPassword() {
        return SSIDPassword;
    }

    public void setSSIDPassword(String SSIDPassword) {
        this.SSIDPassword = SSIDPassword;
    }

    public String getMyIp() {
        return myIp;
    }

    public void setMyIp(String myIp) {
        this.myIp = myIp;
    }

    public String getMyGateway() {
        return myGateway;
    }

    public void setMyGateway(String myGateway) {
        this.myGateway = myGateway;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(SSIDName);
        dest.writeString(SSIDPassword);
        dest.writeString(myIp);
        dest.writeString(myGateway);
    }


}
