package com.example.mehme.smartroom;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.constraint.solver.widgets.Helper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ListView cihazlarList;
    String info ;
    String address;
    TextView txYapilacaklar;
    private BluetoothAdapter myBluetooth=null;
    private Set<BluetoothDevice> eslenmisCihazlar;
    public static String EXTRA_DEVICE_ADDRESS="device_addres";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cihazlarList=(ListView)findViewById(R.id.cihazlar);
        txYapilacaklar=(TextView)findViewById(R.id.yapilacakTx);
        Calendar calendar=Calendar.getInstance();
        String gun=calendar.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault());
        if(gun.equals("Pazar")){
            txYapilacaklar.setText("PAZARTESİ\n09:00 Fonksiyonel programlama\n11:45 Girişimcilik kültürü\n13:30 İletişim ve sunum teknikleri");
        }else if(gun.equals("Pazartesi")){
            txYapilacaklar.setText("SALI\nDers Yok");
        }else if(gun.equals("Salı")){
            txYapilacaklar.setText("ÇARŞAMBA\n08:05 İş Sağlığı ve Güvenliği");
        }else if(gun.equals("Çarşamba")){
            txYapilacaklar.setText("PEŞEMBE\n09:00 Programlama Dilleri ve Kavramları\n13:30 Mimari-Grafikler(çakışıyor)");
        }else if(gun.equals("Perşembe")){
            txYapilacaklar.setText("CUMA\n09:00 İşletim Sistemleri\n13:30 Yapay Zekaya Giriş");
        }else if(gun.equals("Cuma")){
            txYapilacaklar.setText("CUMARTESİ\nBugün odayı temizle ve ders çalış");
        }else if(gun.equals("Cumartesi")){
            txYapilacaklar.setText("PAZAR\nDers çalış");
        }
        myBluetooth = BluetoothAdapter.getDefaultAdapter ();
        if(myBluetooth == null){
            Toast.makeText(getApplicationContext(),"Bluetooht aygıtı mevcut değil..",Toast.LENGTH_LONG).show();
            finish();
        }else{
            if(myBluetooth.isEnabled()){}
            else{
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }
        eslenmisCihazList();

    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            Intent i = new Intent(MainActivity.this,odaLampActivity.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
        }
    };
    private void eslenmisCihazList(){
        eslenmisCihazlar=myBluetooth.getBondedDevices();
        ArrayList list=new ArrayList();
        if(eslenmisCihazlar.size()>0){
            for(BluetoothDevice bt : eslenmisCihazlar){
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }else
        {
            Toast.makeText(getApplicationContext(), "Eşleştirilmiş bluetooth cihazı bulunamadı.", Toast.LENGTH_LONG).show();
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        cihazlarList.setAdapter(adapter);
        cihazlarList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
    }

}
