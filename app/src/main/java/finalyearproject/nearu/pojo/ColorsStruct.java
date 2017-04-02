package finalyearproject.nearu.pojo;

/**
 * Created by deepakgavkar on 13/03/17.
 */
public class ColorsStruct {
    String colorid,colorname,colorcode;

    public String getColorid() {
        return colorid;
    }

    public void setColorid(String colorid) {
        this.colorid = colorid;
    }

    public String getColorname() {
        return colorname;
    }

    public void setColorname(String colorname) {
        this.colorname = colorname;
    }

    public String getColorcode() {
        return colorcode;
    }

    public void setColorcode(String colorcode) {
        this.colorcode = colorcode;
    }

    @Override
    public String toString() {
        return this.colorname;
    }
}
