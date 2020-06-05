package pl.michaloruba.kontakty;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.HashSet;
import java.util.Set;

public class CustomJavaScriptInterface {
    private static final String TAG = "CustomJavaScriptI";
    private Context context;
    private Set<String> phoneNumbers;
    private Set<String> emails;
    private String note;
    private String organization;
    private StringBuilder sb;

    CustomJavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public String getContacts(){
        if (sb == null) {
            Log.d(TAG, "getContacts: Generating all contacts");
            sb = new StringBuilder();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, ContactsContract.Contacts.DISPLAY_NAME);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    sb.append("<tr><td><p class=\"mt-2 mb-0\">");
                    sb.append(name);
                    sb.append("</p></td><td><a class=\"btn btn-sm btn-info\" href='details.html?id=");
                    sb.append(id);
                    sb.append("&name=");
                    sb.append(name);
                    sb.append("'>Szczegóły</a></td></tr>");
                }
            }
            cur.close();
        }
        return sb.toString();
    }

    @JavascriptInterface
    public void getContactDetails(String id){
        Log.d(TAG, "getContactDetails: getting contact details");
        phoneNumbers = new HashSet<>();
        emails = new HashSet<>();
            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                    new String[]{id}, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        // get the phone number
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            phoneNumbers.add(pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        }
                        pCur.close();

                        // get email
                        Cursor emailCur = cr.query(
                                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (emailCur.moveToNext()) {
                            // This would allow you get several email addresses
                            // if the email addresses were stored in an array
                            emails.add(emailCur.getString(
                                    emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                        }
                        emailCur.close();

                        // Get note.......
                        String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] noteWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                        Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                        if (noteCur.moveToFirst()) {
                            note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        }
                        noteCur.close();

                        // Get Organizations.........
                        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] orgWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                        Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, orgWhere, orgWhereParams, null);
                        if (orgCur.moveToFirst()) {
                            organization = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        }
                        orgCur.close();
                    }
                }
            }
        }

    @JavascriptInterface
    public String getPhoneNumbers(){
        Log.d(TAG, "getPhoneNumbers: getting phone numbers");
        StringBuilder sb =  new StringBuilder();
        for (String number : phoneNumbers) {
            sb.append("<li>");
            sb.append(number);
            sb.append("</li>");
            sb.append("\n");
        }
        return sb.toString();
    }

    @JavascriptInterface
    public String getEmails(){
        Log.d(TAG, "getEmails: getting emails");
        StringBuilder sb =  new StringBuilder();
        for (String email : emails){
            sb.append("<li>");
            sb.append(email);
            sb.append("</li>");
            sb.append("\n");
        }
        return sb.toString();
    }

    @JavascriptInterface
    public String getOrganization(){
        Log.d(TAG, "getOrganization: getting organization");
        return organization;
    }

    @JavascriptInterface
    public String getNote(){
        Log.d(TAG, "getNote: getting note");
        return note;
    }
}
