package finalyearproject.nearu.pojo;

/**
 * Created by deepakgavkar on 25/02/17.
 */
public class CategoryStruct {
    String catid,catname,catlogo;

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getCatlogo() {
        return catlogo;
    }

    public void setCatlogo(String catlogo) {
        this.catlogo = catlogo;
    }

    @Override
    public String toString() {
        return this.catname;
    }
}
