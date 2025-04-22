package com.mado.ap2_dbsqlite2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase myDB;
    ArrayList<String> aryMBRList;
    ArrayAdapter<String> adtMembers;
    ListView lstView;
    String strRecord = null;
    ContentValues insertValue;
    Cursor allRCD;
    Button btnInsert, btnUpdate, btnDelete, btnSearch;
    EditText edtCarType, edtCarPower;
    String strSQL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtCarType = (EditText) findViewById(R.id.editCarType);
        edtCarPower = (EditText) findViewById(R.id.editCarPower);

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(this);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        lstView = (ListView) findViewById(R.id.lstMember);

        // Create DB (DB name: CarInformation)
        myDB = MainActivity.this.openOrCreateDatabase("CarInformation", MODE_PRIVATE, null);
        myDB.execSQL("Drop table if exists Carlist");

        // Create Table (Table name: Carlist)
        myDB.execSQL("Create table Carlist (" +
                "_id integer primary key autoincrement, " +
                "CarType text not null, " +
                "CarPower text not null);");

        // Insert Data ("BMW", "3200") into Carlist table
        myDB.execSQL("Insert into Carlist " +
                "(CarType, CarPower) values ('BMW 528i', '2800');");

        // Insert Data into Carlist table
        insertValue = new ContentValues();
        insertValue.put("CarType", "Benz 320");
        insertValue.put("CarPower", "3200");
        myDB.insert("Carlist", null, insertValue);

        getDBData(null); // Get DB data from Carlist

        if (myDB != null) myDB.close(); // Close DB

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] strData = selectedItem.split("\\s+");

                edtCarType.setText(strData[0] + " " + strData[1]);
                edtCarPower.setText(strData[2]);
            }
        });
    }

    public void getDBData(String strWhere) {
        // Get DB data from Carlist table
        allRCD = myDB.query("Carlist", null, strWhere, null, null, null, null);

        // Create arrayList
        aryMBRList = new ArrayList<String>();
        if (allRCD != null) {
            if (allRCD.moveToFirst()) {
                do {
                    strRecord = allRCD.getString(1) + "\t\t" + allRCD.getString(2);
                    aryMBRList.add(strRecord);
                } while (allRCD.moveToNext());
            }
        }

        adtMembers = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_single_choice,
                aryMBRList
        );

        // Create ListView
        lstView.setAdapter(adtMembers);
        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onClick(View view) {
        myDB = this.openOrCreateDatabase("CarInformation", MODE_PRIVATE, null);

        if (view == btnInsert) {
//            insertValue = new ContentValues();
//            insertValue.put("CarType", edtCarType.getText().toString());
//            insertValue.put("CarPower", edtCarPower.getText().toString());
//            myDB.insert("Carlist", null, insertValue);
            String strSQL = "Insert into Carlist " + "(CarType, CarPower) values('" + edtCarType.getText().toString() + "', '" + edtCarPower.getText().toString() + "');";
            myDB.execSQL(strSQL);
            getDBData(null);
        } else if (view == btnUpdate) {
            int position = lstView.getCheckedItemPosition();

            if (position != ListView.INVALID_POSITION) {
                String selectedItem = (String) lstView.getItemAtPosition(position);
                String[] strData = selectedItem.split("\\s+");

                String originalCarType = strData[0] + " " + strData[1];

                String newCarType = edtCarType.getText().toString();
                String newCarPower = edtCarPower.getText().toString();

                ContentValues updateValues = new ContentValues();
                updateValues.put("CarType", newCarType);
                updateValues.put("CarPower", newCarPower);

                myDB.update("Carlist", updateValues, "CarType=?", new String[]{originalCarType});

                getDBData(null);
            }
        }

        if (myDB != null) myDB.close();
    }

}