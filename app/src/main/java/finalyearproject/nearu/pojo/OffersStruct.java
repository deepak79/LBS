package finalyearproject.nearu.pojo;

/**
 * Created by deepakgavkar on 25/02/17.
 */
public class OffersStruct {
    String oid, vid, sid,sname,scontactno,slat,slng,saddress,vlogo ,distance,vtitle, vdesc, catid, catname, discounttype, discount, coupon,couponqr, color, link, offerlogo, offerstartfrom, offerexpierson,clicks, priority, status, oncreatedon;

    public String getClicks() {
        return clicks;
    }

    public void setClicks(String clicks) {
        this.clicks = clicks;
    }

    public String getSlng() {
        return slng;
    }

    public void setSlng(String slng) {
        this.slng = slng;
    }

    public String getSlat() {

        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getScontactno() {
        return scontactno;
    }

    public void setScontactno(String scontactno) {
        this.scontactno = scontactno;
    }

    public String getSaddress() {
        return saddress;
    }

    public void setSaddress(String saddress) {
        this.saddress = saddress;
    }

    public String getVlogo() {
        return vlogo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setVlogo(String vlogo) {
        this.vlogo = vlogo;
    }

    public String getCouponqr() {
        return couponqr;
    }

    public void setCouponqr(String couponqr) {
        this.couponqr = couponqr;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getVtitle() {
        return vtitle;
    }

    public void setVtitle(String vtitle) {
        this.vtitle = vtitle;
    }

    public String getVdesc() {
        return vdesc;
    }

    public void setVdesc(String vdesc) {
        this.vdesc = vdesc;
    }

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

    public String getDiscounttype() {
        return discounttype;
    }

    public void setDiscounttype(String discounttype) {
        this.discounttype = discounttype;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOfferlogo() {
        return offerlogo;
    }

    public void setOfferlogo(String offerlogo) {
        this.offerlogo = offerlogo;
    }

    public String getOfferstartfrom() {
        return offerstartfrom;
    }

    public void setOfferstartfrom(String offerstartfrom) {
        this.offerstartfrom = offerstartfrom;
    }

    public String getOfferexpierson() {
        return offerexpierson;
    }

    public void setOfferexpierson(String offerexpierson) {
        this.offerexpierson = offerexpierson;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOncreatedon() {
        return oncreatedon;
    }

    public void setOncreatedon(String oncreatedon) {
        this.oncreatedon = oncreatedon;
    }

    @Override
    public String toString() {
        return this.vtitle;
    }
}
