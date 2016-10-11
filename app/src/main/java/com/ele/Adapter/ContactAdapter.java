package com.ele.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ele.Models.Contact;
import com.ele.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by prateekgupta on 11/10/16.
 */

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;

    ArrayList<Contact> mContacts = new ArrayList<>();


    public void setContacts(ArrayList<Contact> contacts){
        mContacts = contacts;
    }

    public ContactAdapter(Context context){
        mContext = context;
    }






    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ContactHolder ch = (ContactHolder) holder;
        ch.image.setImageBitmap(mContacts.get(position).getBitmap());
        ch.contactName.setText(mContacts.get(position).getContactName());
        ch.contactEmail.setText(mContacts.get(position).getEmail());
        ch.contactNumber.setText(mContacts.get(position).getNumber());

        if(mContacts.get(position).getLastCall() != null){

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = new Date(Long.valueOf(mContacts.get(position).getLastCall()));
            sdf.format(date);


            ch.lastCall.setText(sdf.format(date));
        }else{
            ch.lastCall.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }


    private class ContactHolder extends RecyclerView.ViewHolder{

        TextView contactName;
        TextView contactNumber;
        TextView contactEmail;
        ImageView image;
        TextView lastCall;


        public ContactHolder(View itemView) {
            super(itemView);

            contactNumber = (TextView) itemView.findViewById(R.id.contactNumber);

            contactName = (TextView) itemView.findViewById(R.id.contactName);

            contactEmail = (TextView) itemView.findViewById(R.id.contactEmail);

            lastCall = (TextView) itemView.findViewById(R.id.lastCall);

            image = (ImageView) itemView.findViewById(R.id.userImage);


        }
    }
}
