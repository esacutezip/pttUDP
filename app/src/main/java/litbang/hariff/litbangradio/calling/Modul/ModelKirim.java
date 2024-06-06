package litbang.hariff.litbangradio.calling.Modul;



public class ModelKirim {
    String Data;
    int  Port;
    byte[] databinary;

    public byte[] getDatabinary() {
        return databinary;
    }

    public void setDatabinary(byte[] databinary) {
        this.databinary = databinary;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
    public int getPort() {
        return Port;
    }

    public void setPort(int data) {
        Port = data;
    }

}
