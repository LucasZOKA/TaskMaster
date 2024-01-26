package com.lucaszoka.taskmaster_v2.model;

import static android.content.Context.MODE_PRIVATE;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lucaszoka.taskmaster_v2.R;

import java.util.ArrayList;
import java.util.List;

public class IconAdapter extends ArrayAdapter<Integer> {
    Context mContext;
    ImageView imgV;
    SharedPreferences sPref;

    AlertDialog alert;
    public IconAdapter(@NonNull Context context, ArrayList<Integer> iconArrayList, ImageView imgV, AlertDialog alert) {
        super(context, 0, iconArrayList);
        mContext = context;
        this.imgV = imgV;
        this.alert = alert;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.icon_item, parent, false);
        }
        sPref = mContext.getSharedPreferences("SPX", MODE_PRIVATE);

        //CourseModel courseModel = getItem(position);
        Integer iconSRC = getItem(position);

        ImageView iconIMG = listitemView.findViewById(R.id.iconIMG);

        iconIMG.setImageResource(iconSRC);
        //TextView STV = parent.findViewById(R.id.sneakTV);
        TextView STV = listitemView.getRootView().findViewById(R.id.sneakTV);

        iconIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*SharedPreferences.Editor editor = sPref.edit();
                editor.putInt("iconIMG", iconSRC);
                editor.apply();*/
                imgV.setImageResource(iconSRC);
                imgV.setTag(iconSRC);
                imgV.setBackgroundColor(mContext.getColor(R.color.transparente));
                Log.d("ICONIMG", "clicnado no adapter " + iconSRC);
                alert.dismiss();

            }
        });

        return listitemView;
    }

    public void resetAlert(AlertDialog alert){
        this.alert = alert;
    }
}
