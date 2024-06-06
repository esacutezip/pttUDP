package litbang.hariff.litbangradio.calling.Modul;

public class DataImage {
    public byte[] data;
    public boolean ack;

    public DataImage(byte[] bytes, boolean b) {
        this.data = bytes;
        this.ack = b;
    }
}
