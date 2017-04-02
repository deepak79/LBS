package finalyearproject.nearu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import finalyearproject.nearu.R;
import finalyearproject.nearu.pojo.CategoryStruct;

/**
 * Created by deepakgavkar on 13/03/17.
 */
public class CategoriesAdaptor extends BaseAdapter {
    private Context mContext;
    private ArrayList<CategoryStruct> categoryStruct = new ArrayList<CategoryStruct>();

    public CategoriesAdaptor(Context c, ArrayList<CategoryStruct> categoryStructs) {
        mContext = c;
        this.categoryStruct = categoryStructs;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return categoryStruct.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return categoryStruct.get(position);
    }

    @Override
    public boolean hasStableIds() {
        return super.hasStableIds();
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            if (convertView == null) {
                grid = new View(mContext);
                grid = inflater.inflate(R.layout.channel_grid_single, null);
                TextView textView = (TextView) grid.findViewById(R.id.grid_text);
                ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);

                CategoryStruct single =  categoryStruct.get(position);

                textView.setText(single.getCatname());

                Picasso.with(mContext).load(single.getCatlogo()).into(imageView);

            } else {
                grid = (View) convertView;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return grid;
    }
}
