package com.example.blackbox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    ListView itemViewList;                 // ListView of all Item views
    ArrayAdapter<Item> inventoryAdapter;   // customized array adapter
    ArrayList<Item> itemList;              // list of Item objects
    Button addButton;                      // add item button

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize a list of items
//        itemViewList = findViewById(R.id.item_list);
//        itemList = new ArrayList<Item>();
//        itemList.add(new Item("Laptop", 345.23, "this is good bro"));
//        inventoryAdapter = new InventoryListAdapter(this, itemList);
//        itemViewList.setAdapter(inventoryAdapter);

    }
}