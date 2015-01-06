package zy.com.slidemenu2.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Simon on 2015/1/5.
 */
public class MyLinearLayout extends LinearLayout {


    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (i == 0){
            return 1;
        }else if (i == 1){
            return 0;
        }
        return super.getChildDrawingOrder(childCount, i);
    }
}
