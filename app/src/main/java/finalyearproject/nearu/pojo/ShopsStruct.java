package finalyearproject.nearu.pojo;

/**
 * Created by deepakgavkar on 24/02/17.
 */
public class ShopsStruct {
    String sid,vid,vname,sname,slat,slng,saddress,smobileno,status,screatedon;

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getSlng() {
        return slng;
    }

    public void setSlng(String slng) {
        this.slng = slng;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getSaddress() {
        return saddress;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public String getSmobileno() {
        return smobileno;
    }

    public void setSmobileno(String smobileno) {
        this.smobileno = smobileno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScreatedon() {
        return screatedon;
    }

    public void setScreatedon(String screatedon) {
        this.screatedon = screatedon;
    }

    @Override
    public String toString() {
        return this.sname;
    }
}
