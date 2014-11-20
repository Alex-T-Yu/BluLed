package golbang.com.bluled;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import golbang.com.bluled.Utils.Constants;

/**
 * Created by yoosung-jong on 14. 10. 28..
 */
public class ListAdapterSupport extends BaseAdapter {

    private LayoutInflater inflater = null;
    private ArrayList<ListViewData> list = null;
    private Context mContext = null;

    private TextView itemTitle = null;
    private TextView itemSubtitle = null;
    private Switch itemSw = null;
    private Button itemBtn = null;
    private TextView itemStatus = null;

    public ListAdapterSupport(Context c, ArrayList<ListViewData> arrays){
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
        this.list = arrays;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ListViewData getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.list_adapter,null);

        itemTitle = (TextView) view.findViewById(R.id.itemTitle);
        itemSubtitle = (TextView) view.findViewById(R.id.itemSubtitle);
        itemSw = (Switch) view.findViewById(R.id.itemSw);
        itemBtn = (Button) view.findViewById(R.id.itemBtn);
        itemStatus = (TextView) view.findViewById(R.id.itemStatus);

        itemSw.setTag(i);

        if(getItem(i).getItemBtnTxt().equals(view.getContext().getString(R.string.devDisc))){
            itemBtn.setTag(i+10);
        }else{
            itemBtn.setTag(i);
        }

        itemTitle.setText(getItem(i).getItemTitle());
        itemSubtitle.setText(getItem(i).getItemSubTitle());

        if(!getItem(i).isItemSwYn()){
            itemSw.setVisibility(View.GONE);
        }else{
            itemSw.setChecked(getItem(i).getItemSwVal());
        }

        if(!getItem(i).isItemBtnYn()){
            itemBtn.setVisibility(View.GONE);
        }else{
            itemBtn.setText(getItem(i).getItemBtnTxt());
        }

        if(!getItem(i).isItemStatusYn()){
            itemStatus.setVisibility(View.GONE);
        }else {
            itemStatus.setText(getItem(i).getItemStatusTxt());
        }

        itemSw.setOnClickListener(buttonClickListener);
        itemBtn.setOnClickListener(buttonClickListener);

        return view;
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.itemSw:
                    Log.d("alert",view.getTag().toString());

//                  스위치 이벤트
                    if(view.getTag().toString().equals("1")){
                        Switch sw = (Switch)view.findViewById(R.id.itemSw);
                        if(sw.isChecked()){
//                            Toast.makeText(view.getContext(),"isChecked",Toast.LENGTH_SHORT).show();
                            ((main_phone)view.getContext()).setLedFlag(true);
                            ((main_phone)view.getContext()).postLedkey(Constants.POST_MASSAGE_LED_ON);
                        }else{
//                            Toast.makeText(view.getContext(),"isNotChecked",Toast.LENGTH_SHORT).show();
                            ((main_phone)view.getContext()).setLedFlag(false);
                            ((main_phone)view.getContext()).postLedkey(Constants.POST_MASSAGE_LED_OFF);
                        }
                    }

                    break;
                case R.id.itemBtn:
                    Log.d("alert",view.getTag().toString());

//                  버튼 이벤트
                    if(view.getTag().toString().equals("0")){
                        ((main_phone)view.getContext()).doScan();
                    }else if(view.getTag().toString().equals("2")){
                        Toast.makeText(view.getContext(),"click row 2",Toast.LENGTH_SHORT).show();
                    }else if(view.getTag().toString().equals("10")){
                        ((main_phone)view.getContext()).disconnectBt();
                    }

                    break;
                default:
                    break;
            }
        }
    };

}
