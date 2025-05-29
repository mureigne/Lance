package com.example.lance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TemplateSpinnerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] templateNames;
    private final int[] templateImages;

    public TemplateSpinnerAdapter(Context context, String[] templateNames, int[] templateImages) {
        super(context, R.layout.spinner_item_template, templateNames);
        this.context = context;
        this.templateNames = templateNames;
        this.templateImages = templateImages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item_template, parent, false);
        ImageView imageView = view.findViewById(R.id.imageTemplateThumb);
        TextView textView = view.findViewById(R.id.textTemplateName);

        imageView.setImageResource(templateImages[position]);
        textView.setText(templateNames[position]);

        return view;
    }
}