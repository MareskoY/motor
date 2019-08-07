package c.motor.motor.model;

public class Country {
    public String nameEng;
    public String nameNative;

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

    public String getNameNative() {
        return nameNative;
    }

    public void setNameNative(String nameNative) {
        this.nameNative = nameNative;
    }

    public Country(String nameEng, String nameNative) {

        this.nameEng = nameEng;
        this.nameNative = nameNative;
    }
}
