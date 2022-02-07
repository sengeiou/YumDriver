package com.yum_driver.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.yum_driver.Activities.BaseActivity;
import com.yum_driver.R;
import com.yum_driver.utils.SharedPreferenceManager;


public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.ViewHolder>
{
    BaseActivity context;
    String[] arrayLanguage = {"English", "French"};
    int[] arrayLanguageImages = {R.drawable.english,R.drawable.french};
    private SharedPreferenceManager prefManager;
    private String lang;
    public LanguageListAdapter(BaseActivity context, String lang)
    {
        this.context=context;
        prefManager = new SharedPreferenceManager(context);
        this.lang = lang;
        System.out.println("lang :"+lang);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(context).inflate(R.layout.row_language_list,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtLanguage.setText(arrayLanguage[position]);
        holder.imgCountry.setImageResource(arrayLanguageImages[position]);

        System.out.println("lang :"+lang);

        if(lang.equals("en") && position == 0)
        {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }
        else if(lang .equals("fr") && position == 1)
        {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgCheck.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lang = "";
                if(position == 0)
                {
                   lang = "en";
                }
                else if(position == 1)
                {
                    lang = "fr";
                }

                else {
                    lang = "en";
                }

                prefManager.connectDB();
                System.out.println("language_selected:"+lang);
                prefManager.setString("language", lang);
                prefManager.closeDB();

                notifyDataSetChanged();

                context.recreate();

            }
        });
    }

    @Override
    public long getItemId(int position) {
            return position;
    }

    @Override
    public int getItemCount() {
            return arrayLanguage.length;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLanguage;
        ImageView imgCountry,imgCheck;

        public ViewHolder(View itemView) {
            super(itemView);

            txtLanguage = itemView.findViewById(R.id.txtLanguage);
            imgCountry =  itemView.findViewById(R.id.imgCountry);
            imgCheck =  itemView.findViewById(R.id.imgCheck);
        }
    }





}
