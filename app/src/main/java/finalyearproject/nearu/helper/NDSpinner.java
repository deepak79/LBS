package finalyearproject.nearu.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;


/** Spinner extension that calls onItemSelected even when the selection is the same as its previous value */
public class NDSpinner extends Spinner {

    public NDSpinner(Context context)
    { super(context); }

    public NDSpinner(Context context, AttributeSet attrs)
    { super(context, attrs); }

    public NDSpinner(Context context, AttributeSet attrs, int defStyle)
    { super(context, attrs, defStyle); }

    @Override
    public void setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        try {
            if (sameSelected) {
                // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
                //noinspection ConstantConditions
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        try {
            if (sameSelected) {
                // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
                // noinspection ConstantConditions
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}