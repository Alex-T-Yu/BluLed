package golbang.com.bluled;

/**
 * Created by yoosung-jong on 14. 10. 28..
 * 리스트 어뎁터 data 객체
 */
public class ListViewData {

    public String itemTitle;
    public String itemSubTitle;

    public String itemBtnTxt;
    public boolean itemSwVal;
    public String itemStatusTxt;

    public boolean itemBtnYn;
    public boolean itemSwYn;
    public boolean itemStatusYn;

    public ListViewData(String title, String subTitle, boolean btnYn, String btnTxt, boolean swYn, boolean swVal, boolean statusYn ,String statusTxt){
        this.itemTitle = title;
        this.itemSubTitle = subTitle;
        this.itemBtnYn = btnYn;
        this.itemBtnTxt =btnTxt;
        this.itemSwYn = swYn;
        this.itemSwVal = swVal;
        this.itemStatusYn = statusYn;
        this.itemStatusTxt = statusTxt;
    }

    public ListViewData(String title, String subTitle, boolean btnYn,String btnTxt, boolean swYn, boolean swVal){
        this(title,subTitle,btnYn,btnTxt,swYn,swVal,false,"");
    }

    public ListViewData(String title, String subTitle, boolean btnYn, String btnTxt){
        this(title,subTitle,btnYn,btnTxt,false,false,false,"");
    }

    public ListViewData(String title, String subTitle){
        this(title, subTitle, false, "", false, false, false, "");
    }

    public ListViewData(String title){
        this(title,"",false,"",false,false,false,"");
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemSubTitle() {
        return itemSubTitle;
    }

    public String getItemBtnTxt() {
        return itemBtnTxt;
    }

    public boolean getItemSwVal() {
        return itemSwVal;
    }

    public String getItemStatusTxt() {
        return itemStatusTxt;
    }

    public boolean isItemBtnYn() {
        return itemBtnYn;
    }

    public boolean isItemSwYn() {
        return itemSwYn;
    }

    public boolean isItemStatusYn() {
        return itemStatusYn;
    }
}
