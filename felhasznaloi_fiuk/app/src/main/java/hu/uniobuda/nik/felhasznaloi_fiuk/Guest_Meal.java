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
    CustomAdapter adapter; //A termékek megjelenítéséhez szükséges adapter
    ListView listView;
    ArrayList<NameValuePair> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_meal);

        products = new ArrayList<NameValuePair>();

        ///test
        arr = new ArrayList<Product>();
        arr = Communication.products;

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
    protected void onPause() { super.onPause(); }

    @Override
    protected void onResume() {
        super.onResume();
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
        String temp = "";

        if (!Communication.table_id.equals("")) { //ha az azonosított asztal nem üres, akkor küldhetjük el a megrendelést
            if (Communication.testMode) { //teszt mód-e, ekkor nem a szerverre küldi az adatot
                for (int i = 0; i < arr.size(); i++) { //végigmegyünk a termékek listáján
                    if (Integer.parseInt(arr.get(i).getQuantity()) != 0) {//amiből nem 0db van, azt hozzáfűzzük a stringhez
                        temp += arr.get(i).getName();
                        temp += ", ";
                        temp += Integer.parseInt(arr.get(i).getQuantity());
                        temp += "\n";
                    }
                }
            }
            else {      //ha nem teszt mód
                Table order = new Table();
                order.setName(Communication.table_id);
                order.setState("foglalt"); //a szerveren foglaltra állítjuk az asztal állapotát

                for (int i = 0; i < arr.size(); i++) {//végigmegyünk a termékek listáján
                    if (Integer.parseInt(arr.get(i).getQuantity()) != 0) {
                        order.addProduct(arr.get(i));   //amiből nem 0db van, azt hozzáadjuk a rendeléshez
                    }
                }
                Communication.SendOrderToServer(order); //az összegyűjtött rendelést elküldjük a szervernek
                temp = "Rendelését elküldtük!";
            }
        } else {
            temp = "Kérjük azonosítsa az asztalt!";
        }
        Toast.makeText(Guest_Meal.this, temp, Toast.LENGTH_LONG).show(); // megjelenítjük, a beállított üzenetet (rendelések,sikeres rendelés vagy azonosítani kell az asztalt)
    }

    private class CustomAdapter extends BaseAdapter { //a listához való adapter belső osztálya

        @Override
        public int getCount() { //lista nagyságának visszaadása
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
            if (convertView == null) { //ha még nincs view, létre kell hozni
                holder = new ViewHolder();
                LayoutInflater inflater = Guest_Meal.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.meal_item, null);

                //beállítjuk a viewholder értékeit
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.price = (TextView) convertView.findViewById(R.id.price);
                holder.quantity = (EditText) convertView.findViewById(R.id.quantity);
                holder.db = (TextView) convertView.findViewById(R.id.db);

                //beállítjuk a holder tagjét
                convertView.setTag(holder);
            } else { //ha már van view, akkor azt használjuk
                holder = (ViewHolder) convertView.getTag();
            }

            //adatok beállítása
            holder.ref = position; //a ref adattag az edittextekbe írt mennyiségek azonosításához kell

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

        public class ViewHolder {  // costume view a termékek listájának megjelenítéséhez

            public TextView name;
            public TextView price;
            public EditText quantity;
            public TextView db;
            int ref;
        }
    }
}
