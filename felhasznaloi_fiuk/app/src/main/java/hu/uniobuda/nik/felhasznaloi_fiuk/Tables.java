package hu.uniobuda.nik.felhasznaloi_fiuk;

import android.app.ListActivity; //
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Tables extends ActionBarActivity { //Az asztalok állapotát lekérdező activity

    ///Adattagok
    ArrayList<Table> arr_tables; //Az asztalok tömbje
    TableAdapter t_adapt; //A lista adaptere

    String textview; //Az asztal által rendelt termékek

    ListView tables; //Az asztalok ListView-ja
    TextView products; //A rendelt termékek TextView-ja

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);

        textview = "";
        products = (TextView) findViewById(R.id.products);

        arr_tables = Communication.tables; //Az activity létrehozásánál már létrejött az asztallista(szervertől vagy belül), így itt elég csak lekérdezni

        t_adapt = new TableAdapter();
        tables = (ListView) findViewById(R.id.tables);
        tables.setAdapter(t_adapt);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tables, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //A frissítés gombra lekérdezi újra az asztalok listáját
        int id = item.getItemId();

        if (id == R.id.refresh) {
            try {
                ProgressDialog pd = new ProgressDialog(Tables.this);
                pd.setMessage(getResources().getString(R.string.layout_activity_guest_main_menu_dolgozom));
                pd.show();
                Communication.GetTables();
                if (!Communication.testMode) {
                    Communication.getServerCom().get(1000, TimeUnit.MILLISECONDS);
                }
                pd.dismiss();

                arr_tables = Communication.tables;
                t_adapt.notifyDataSetChanged(); //Frissíteni kell az adaptert, hiszen a lekérdezés után megváltozhat az asztalok állapota

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TableAdapter extends BaseAdapter { //Lista adapter
        @Override
        public int getCount() { //Visszaadja a lista hosszát
            if (arr_tables != null && arr_tables.size() != 0){
                return arr_tables.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return arr_tables.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //final volt itt, nem kell
             final TableHolder holder;// = null; //ViewHolder a listához
            if (convertView == null){ //Ha még nincs meg a View, létrehozom
                holder = new TableHolder();
                LayoutInflater inflater = Tables.this.getLayoutInflater();

                convertView = inflater.inflate(R.layout.tables_item, null); //Layout beállítása

                holder.name = (Button) convertView.findViewById(R.id.name); //Asztalok hozzáadása
                holder.state = (Button) convertView.findViewById(R.id.state);
                convertView.setTag(holder);
            } else { //Ha már van View, lekérdezzük azt
                holder = (TableHolder) convertView.getTag();
            }

            holder.name.setText(arr_tables.get(position).getName());
            holder.name.setBackgroundColor(Color.LTGRAY);
            switch (arr_tables.get(position).getState()){ //Az asztal állapota alapján a gomb hátterének beállítása
                case "szabad":
                    holder.state.setBackgroundColor(Color.GREEN);
                    break;

                case "foglalt":
                    holder.state.setBackgroundColor(Color.YELLOW);
                    break;

                case "fizet":
                    holder.state.setBackgroundColor(Color.RED);
                    break;
            }

            holder.name.setOnClickListener(new View.OnClickListener() { //Egy asztalra rákoppintva látható az asztalnál rendelt termékeket
                @Override
                public void onClick(View v) {
                    textview = "";
                    int cost = 0;
                    List<Product> temp = arr_tables.get(position).getProducts();
                    for (int i = 0; i < temp.size(); i++){ //A Textview összefűzése, a fizetendő összeg kiszámítása
                        textview += temp.get(i).getName();
                        textview += ": ";
                        textview += temp.get(i).getQuantity();
                        textview += "db ";
                        int q =Integer.parseInt(temp.get(i).getQuantity());
                        int p =Integer.parseInt(temp.get(i).getPrice());
                        textview += String.valueOf(q*p);
                        textview += "Ft ";
                        cost += q*p;
                        textview += "\n";
                    }
                    textview += "\n";
                    textview += "Összesen " + String.valueOf(cost) + "Ft";
                    products.setText(textview);
                }
            });


            return convertView;
        }

        public class TableHolder {
            public Button name;
            public Button state;
        }

    }
}
