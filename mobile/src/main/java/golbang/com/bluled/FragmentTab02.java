package golbang.com.bluled;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import golbang.com.bluled.Utils.ColorPicker;


/**
 * Created by yoosung-jong on 14. 10. 28..
 */
public class FragmentTab02 extends Fragment {

    private static final String TAG = "FragmentTab02";

    private ColorPicker colorPicker;
    private Button button;
    private TextView colorCode;
    private ArrayList<String> rgbCode;
    private String rgbString = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View fragmentView = inflater.inflate(R.layout.tab2,container,false);

        rgbCode = new ArrayList<String>();

        colorCode = (TextView) fragmentView.findViewById(R.id.colorCode);
        colorPicker = (ColorPicker) fragmentView.findViewById(R.id.multiColorPicker);

        colorPicker.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int color = colorPicker.getColor();
                rgbCode.add(String.valueOf(Color.red(color)));
                rgbCode.add(String.valueOf(Color.green(color)));
                rgbCode.add(String.valueOf(Color.blue(color)));

                for(int i=0;i<rgbCode.size();i++){

                    if(rgbCode.get(i).length()==2){
                        rgbString+="0"+rgbCode.get(i);
                    }else if(rgbCode.get(i).length()==1){
                        rgbString=rgbString+"00"+rgbCode.get(i);
                    }else{
                        rgbString+=rgbCode.get(i);
                    }

                }

                Log.d(TAG,rgbString);

                if(!rgbString.equals("")) {
//                    ((main_phone) getActivity()).setLedColor(rgbString);

                    ((main_phone) getActivity()).postMessage(rgbString);
                }

                rgbCode.clear();
                rgbString = "";

                return false;
            }
        });

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
