package com.example.exchange_money.Fragments;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.exchange_money.R;

public class CountryAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] countries;
    private final int[] flags;

    public CountryAdapter(Context context, String[] countries, int[] flags) {
        super(context, R.layout.dialog_item_country, countries);
        this.context = context;
        this.countries = countries;
        this.flags = flags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.dialog_item_country, null, true);

        ImageView flagImage = rowView.findViewById(R.id.country_flag);
        TextView countryName = rowView.findViewById(R.id.country_name);

        flagImage.setImageResource(flags[position]);
        countryName.setText(countries[position]);

        return rowView;
    }
}
