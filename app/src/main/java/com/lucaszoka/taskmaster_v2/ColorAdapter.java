package com.lucaszoka.taskmaster_v2;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ColorAdapter extends ArrayAdapter<Integer> {
    Context mContext;
    Button cBtn;
    ImageView colorBtn;
    ImageView imgV;
    SharedPreferences sPref;

    AlertDialog alert;
    public ColorAdapter(@NonNull Context context, ArrayList<Integer> colorArrayList, ImageView colorBtn, AlertDialog alert) {
        super(context, 0, colorArrayList);
        mContext = context;
        this.colorBtn = colorBtn;
        this.alert = alert;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.color_item, parent, false);
        }
        sPref = mContext.getSharedPreferences("SPX", MODE_PRIVATE);

        //CourseModel courseModel = getItem(position);
        //Integer iconSRC = getItem(position);
        int colorSRC = getItem(position);
        Log.d("COLORIMG", "colorSRC: " + colorSRC);

        ImageView colorIMG = listitemView.findViewById(R.id.colorIMG);

        colorIMG.setBackgroundColor(colorSRC);
        //TextView STV = parent.findViewById(R.id.sneakTV);
        TextView STV = listitemView.getRootView().findViewById(R.id.sneakTV);

        colorIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //colorBtn.setBackgroundTintList((ColorStateList.valueOf(mContext.getColor(colorSRC))));
                colorBtn.setBackgroundColor(colorSRC);
                colorBtn.setTag(colorSRC);
                Log.d("ICONIMG", "clicnado no adapter " + colorSRC);
                alert.dismiss();

            }
        });

        return listitemView;
    }

    public void resetAlert(AlertDialog alert){
        this.alert = alert;
    }
}