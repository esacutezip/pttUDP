package litbang.hariff.litbangradio.calling.Modul;

public class ImageSender {
    public long sizeimage;
    public long arraynow;
    public long arraysize;
    public String title;
    public String location;


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public long getSizeimage() {
        return sizeimage;
    }

    public void setSizeimage(long sizeimage) {
        this.sizeimage = sizeimage;
    }


    public long getArraynow() {
        return arraynow;
    }

    public void setArraynow(long arraynow) {
        this.arraynow = arraynow;
    }

    public long getArraysize() {
        return arraysize;
    }

    public void setArraysize(long arraysize) {
        this.arraysize = arraysize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] data;
}
