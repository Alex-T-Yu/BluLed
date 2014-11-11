package golbang.com.bluled;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import golbang.com.bluled.Service.BluetoothService;

/**
 * Created by yoosung-jong on 14. 10. 28..
 */
public class FragmentTab01 extends Fragment{

    private ListView mListView = null;

    private BluetoothService btService = null;
    private static final int REQUEST_ENABLE_BT = 0;

    private ListAdapterSupport adapterSupport;
    private ArrayList<ListViewData> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View fragmentView = inflater.inflate(R.layout.tab1,container,false);

        dataList = new ArrayList<ListViewData>();

        dataList.add(new ListViewData(getString(R.string.conStat),getString(R.string.conOff),true,getString(R.string.devFind)));
        dataList.add(new ListViewData(getString(R.string.ledStat),"",false,"",true,false));
        dataList.add(new ListViewData(getString(R.string.dataReload),getString(R.string.dataReloadTip),true,getString(R.string.dataReloadBtn)));

        adapterSupport = new ListAdapterSupport(getActivity(),dataList);

        mListView = (ListView)fragmentView.findViewById(R.id.lvTab);
        mListView.setAdapter(adapterSupport);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public void onBtConnect(){
        dataList.remove(0);
        dataList.add(0
                    ,new ListViewData(getString(R.string.conStat)
                                     ,getString(R.string.conOn)
                                     ,true
                                     ,getString(R.string.devDisc))
                    );
        adapterSupport.notifyDataSetChanged();
    }

    public void onBtConnectFail(){
        dataList.remove(0);
        dataList.add(0
                    ,new ListViewData(getString(R.string.conStat)
                                     ,getString(R.string.conFail)
                                     ,true
                                     ,getString(R.string.devFind))
                    );
        adapterSupport.notifyDataSetChanged();
    }

    public void onBtDisconnect(){
        dataList.remove(0);
        dataList.add(0
                    ,new ListViewData(getString(R.string.conStat)
                    ,getString(R.string.conOff)
                    ,true
                    ,getString(R.string.devFind)));
        adapterSupport.notifyDataSetChanged();
    }

}
