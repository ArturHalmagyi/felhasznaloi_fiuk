package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class Guest_Meal extends ActionBarActivity { //A vendégek által lekérdezhető menü

    ///Adattagok
    ArrayList<Product> arr; //A termékek tömbje
    int productCount; //A termékek száma
    CustomAdapter adapter; //A termékek megjelenítéséhez szükséges adapter
    boolean can_submit; //Azonosítva van-e az asztal



    ListView listView;

    ArrayList<NameValuePair> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_meal);

        products = new ArrayList<NameValuePair>();
        Populate_List(products); //TODO ez később a szerver lesz

        arr = new ArrayList<Product>();
        productCount = products.size();
        for (int i = 0; i < productCount; i++){
            Product temp = new Product();
            temp.setName(products.get(i).getName());
            temp.setPrice(products.get(i).getValue());
            temp.setQuantity("0");
            temp.setDb("db");
            arr.add(temp);
        }

        can_submit = false;

        adapter = new CustomAdapter(); //Adapter a listához
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guest_meal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Az actionbaron szereplő rendelés gombra koppintva lehet rendelni
        if (item.getItemId() == R.id.submit){
            Submit_Click();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    void Submit_Click(){
        String s = "";
        if (can_submit) {
            for (int i = 0; i < productCount; i++) {
                if (Integer.parseInt(arr.get(i).getQuantity()) != 0) {
                    s += arr.get(i).getName();
                    s += ", ";
                    s += Integer.parseInt(arr.get(i).getQuantity());
                    s += "\n";
                }
            }
        } else {
            s = "Kérjük azonosítsa az asztalt!";
        }
        Toast.makeText(Guest_Meal.this, s, Toast.LENGTH_LONG).show();
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (arr != null && arr.size() != 0){
                return arr.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return arr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = Guest_Meal.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.meal_item, null);

                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.quantity = (EditText) convertView.findViewById(R.id.quantity);
                holder.db = (TextView) convertView.findViewById(R.id.db);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            holder.name.setText(arr.get(position).getName());
            holder.price.setText(arr.get(position).getPrice());
            holder.quantity.setText(arr.get(position).getQuantity());
            holder.quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    arr.get(holder.ref).setQuantity(s.toString());
                }
            });
            return convertView;
        }

        public class ViewHolder {

            public TextView name;
            public TextView price;
            public EditText quantity;
            public TextView db;
            int ref;

        }
    }

    void Populate_List(List<NameValuePair> list){
        list.add(new BasicNameValuePair("Kávé", "420"));
        list.add(new BasicNameValuePair("Kávéka", "421"));
        list.add(new BasicNameValuePair("Wrapper", "999"));
        list.add(new BasicNameValuePair("Sütika", "20"));
        list.add(new BasicNameValuePair("Répa", "119"));
        list.add(new BasicNameValuePair("Billog", "1420"));
        list.add(new BasicNameValuePair("Bálint", "1"));
        list.add(new BasicNameValuePair("STM32F4Discovery mikrokontroller", "32"));
        list.add(new BasicNameValuePair("Razer", "11"));
        list.add(new BasicNameValuePair("Send which", "131"));
        list.add(new BasicNameValuePair("Fickó", "980"));
        list.add(new BasicNameValuePair("Alan", "40"));
        list.add(new BasicNameValuePair("Cigike", "15"));
        list.add(new BasicNameValuePair("Büffet", "000"));
        list.add(new BasicNameValuePair("Versle", "129"));
        list.add(new BasicNameValuePair("Sejci", "1000"));
        list.add(new BasicNameValuePair("Kvanter", "960"));
        list.add(new BasicNameValuePair("Kalun", "113"));
        list.add(new BasicNameValuePair("Sántáné", "666"));
        list.add(new BasicNameValuePair("Jackie Csink", "90"));
    }

}
