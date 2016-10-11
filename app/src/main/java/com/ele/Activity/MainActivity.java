package com.ele.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.ele.Models.Contact;
import com.ele.Adapter.ContactAdapter;
import com.ele.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;
    TextView outputText;// contacts unique ID

    ArrayList<Contact> contacts = new ArrayList<>();
    Handler handler ;
    HashMap<String, String> calLogMap;

    RecyclerView rv_contact ;
    ProgressDialog progress;
    String[] permissions= new String[]{
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG
    };

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        rv_contact = (RecyclerView) findViewById(R.id.rv_contact);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");

        //fetchContacts();
        handler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ) {

                List<String> listPermissionsNeeded = new ArrayList<>();
                int result;
                for (String p:permissions) {
                    result = ContextCompat.checkSelfPermission(getBaseContext(),p);
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(p);
                    }
                }

                requestPermissions(new String[]{
                        Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_CONTACTS
                },1); // asking permission to read contacts
            }else{
               asyncRead();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(grantResults[0] != -1){
            asyncRead();

            }
    }

    public void asyncRead(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.show();
                    }
                });
                readContacts();
            }
        }).start();
    }






    public void readContacts() {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        String phone = null;
        String emailContact = null;
        String emailType = null;
        String image_uri = "";
        Bitmap bitmap = null;


        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                Contact contact = new Contact(); // create a new contact to put in contact list

                String id = cur.getString(cur .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));


                //phone number query
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    contact.setContactName(name.replaceAll("\\s+",""));
                    System.out.println("name : " + name + ", ID : " + id);

                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[] { id }, null);

                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println("phone" + phone);
                        contact.setNumber(phone.replaceAll("\\s+",""));
                    }
                    pCur.close();




                    // email query
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                    + " = ?", new String[] { id }, null);

                    while (emailCur.moveToNext()) {
                        emailContact = emailCur
                                .getString(emailCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        emailType = emailCur
                                .getString(emailCur
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        System.out.println("Email " + emailContact
                                + " Email Type : " + emailType);
                        contact.setEmail(emailContact.replaceAll("\\s+",""));
                    }
                    emailCur.close();
                }




                // Image Query
                if (image_uri != null) {
                    System.out.println(Uri.parse(image_uri));
                    try {
                        bitmap = MediaStore.Images.Media
                                .getBitmap(this.getContentResolver(),
                                        Uri.parse(image_uri));

                        System.out.println(bitmap);

                        contact.setBitmap(bitmap);

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                if(contact.getEmail() != null)
                 contacts.add(contact);

            }

            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,null, null, null);

            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);

            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            calLogMap = new HashMap<>();
            while (managedCursor.moveToNext()) {

                if(calLogMap.containsKey(managedCursor.getString(number))){

                    calLogMap.put(managedCursor.getString(number).replaceAll("\\s+",""),managedCursor.getString(date));
                    //Log.d("alreadynumber",managedCursor.getString(number));
                }else {
                    //Log.d("nothavenumber",Integer.toString(number));
                    calLogMap.put(managedCursor.getString(number).replaceAll("\\s+",""),managedCursor.getString(date));

                }

            }

          handler.post(new Runnable() {
              @Override
              public void run() {
                    drawList();
              }
          });

        }
    }



    public void drawList(){
        ContactAdapter adapter = new ContactAdapter(getBaseContext());

        for(Contact c:contacts){
            if(calLogMap.containsKey(c.getNumber())){
                c.setLastCall(calLogMap.get(c.getNumber()));
                Log.d("milgaya",calLogMap.get(c.getNumber()).toString());
            }
        }

        adapter.setContacts(contacts);

        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        rv_contact.setLayoutManager(llm);
        rv_contact.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        progress.dismiss();


    }
}