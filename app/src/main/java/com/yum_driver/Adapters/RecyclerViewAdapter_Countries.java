package com.yum_driver.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yum_driver.Interfaces.RecyclerViewClickListener;
import com.yum_driver.Pojo.Country;
import com.yum_driver.R;
import com.yum_driver.utils.MyTextView;
import com.yum_driver.utils.MyTextViewBold;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter_Countries extends RecyclerView.Adapter<RecyclerViewAdapter_Countries.ViewHolder> {
    private List<String> mCountries = Arrays.asList(new Country().getCountries());
    private List<String> mCodes = Arrays.asList(new Country().getCodes());
    private List<String> mCountries_all = Arrays.asList(new Country().getCountries());
    private List<String> mCodes_all = Arrays.asList(new Country().getCodes());
    private Context mCtx;
    private RecyclerViewClickListener mClickListener;

    private String implode(String separator, List<String> input) {
        if (input == null || input.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.size(); i++) {
            sb.append(input.get(i));

            // if not the last item
            if (i != input.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    private String formatDateFromDateString(String inputDateFormat,
                                            String outputDateFormat,
                                            String inputDate) throws Exception {
        Date mParsedDate;
        String mOutputDateString;
        SimpleDateFormat mInputDateFormat =
                new SimpleDateFormat(inputDateFormat, Locale.getDefault());
        SimpleDateFormat mOutputDateFormat =
                new SimpleDateFormat(outputDateFormat, Locale.getDefault());
        mParsedDate = mInputDateFormat.parse(inputDate);
        mOutputDateString = mOutputDateFormat.format(mParsedDate);
        return mOutputDateString;
    }

    public String getCountryCode(String countryName) {
        String[] isoCountryCodes = Locale.getISOCountries();
        for (String code : isoCountryCodes) {
            Locale locale = new Locale("", code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return code;
            }
        }
        String[] isoLanguageCodes = Locale.getISOCountries();
        for (String code : isoLanguageCodes) {
            Locale locale = new Locale("", code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return code.toUpperCase();
            }
        }
        return "";
    }

    public void performSearch(String searchQuery) {
        mCountries = new ArrayList<>();
        mCodes = new ArrayList<>();

        if (searchQuery.startsWith("+")) {
            searchQuery = searchQuery.substring(1);
        }

        for (int k = 0; k < mCountries_all.size(); k++) {
            String country = mCountries_all.get(k);
            String code = mCodes_all.get(k);

            if (country.toLowerCase().startsWith(searchQuery.toLowerCase()) ||
                code.toLowerCase().startsWith(searchQuery.toLowerCase())
            ) {
                mCountries.add(country);
                mCodes.add(code);
            }
        }

        notifyDataSetChanged();
    }

    public String get(int position) {
        String country = mCountries.get(position);
        String ISOcode = getCountryCode(country);
        String callingCode = mCodes.get(position);

        return country + " <><><> " + ISOcode + " <><><> " + callingCode;
    }

    public RecyclerViewAdapter_Countries(Context mCtx, RecyclerViewClickListener clickListener) {
        this.mCtx = mCtx;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calling_code, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String ISOcode = getCountryCode(mCountries.get(position));

        holder.heading.setText(mCountries.get(position));
        holder.subheading.setText(String.format("+%s", mCodes.get(position)) + "   \u2022   " + ISOcode);

        if (ISOcode.isEmpty()) {
            holder.image.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.flag_placeholder));
        } else {
            String flagUrl = "https://www.countryflags.io/" + ISOcode + "/shiny/64.png";

            Glide.with(mCtx).load(flagUrl).error(R.drawable.flag_placeholder).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mCountries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        MyTextViewBold heading;
        MyTextView subheading;

        RecyclerViewClickListener clickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.imgV);
            heading =  itemView.findViewById(R.id.txtVHeading);
            subheading =  itemView.findViewById(R.id.txtVSubheading);

            clickListener = mClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onRecyclerViewListClicked(getAdapterPosition(), false,"");
        }
    }
}