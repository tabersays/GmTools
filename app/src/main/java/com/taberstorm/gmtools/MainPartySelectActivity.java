package com.taberstorm.gmtools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainPartySelectActivity extends AppCompatActivity {
    Button addParty;
    ArrayList<String> partyList;
    ArrayAdapter<String> partyListAdapter;
    EditText partyName;
    PartyDB partyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Todo: Get items from sqlDB if exist
         */
        PartyDB partyDB = new PartyDB(getApplicationContext());

        ListView partyListView = (ListView) findViewById(R.id.viewPartyList);
        partyList = new ArrayList<String>();
        Cursor party = partyDB.selectPartyRecord();
        while(!party.isAfterLast()){
            partyList.add(party.getString(party.getColumnIndex("name")));
            party.moveToNext();
        }
        partyListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, partyList);
        partyListView.setOnItemClickListener(selectParty);
        partyListView.setOnItemLongClickListener(deleteParty);
        addParty = (Button) findViewById(R.id.buttonNewParty);
        addParty.setOnClickListener(createPartyBox);
        partyListView.setAdapter(partyListAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addParty(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Name Party");

        partyName = new EditText(MainPartySelectActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        partyName.setLayoutParams(lp);
        alertDialogBuilder.setView(partyName);

        alertDialogBuilder.setPositiveButton("OK", okCreateButton);

        alertDialogBuilder.setNegativeButton("Cancel", cancelButton);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    
    public void deleteParty(int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete Party?");

        alertDialogBuilder.setPositiveButton("Yes", new DeletePartyOnClickListener(position));

        alertDialogBuilder.setNegativeButton("Cancel", cancelButton);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private DialogInterface.OnClickListener okCreateButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            partyDB = new PartyDB(getApplicationContext());
            partyDB.createPartyRecord(partyName.getText().toString());
            partyList.add(partyName.getText().toString());
            partyListAdapter.notifyDataSetChanged();
        }
    };

    private DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //should do nothing
        }
    };

    public class DeletePartyOnClickListener implements DialogInterface.OnClickListener
    {

        int position;
        public DeletePartyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which)
        {
            partyDB = new PartyDB(getApplicationContext());
            partyDB.deletePartyRecord(partyList.get(position).toString());
            partyList.remove(position);
            partyListAdapter.notifyDataSetChanged();
        }

    };

    private Button.OnClickListener createPartyBox = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addParty();
        }
    };

    private AdapterView.OnItemLongClickListener deleteParty = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView parent, View v, int position, long id) {
            deleteParty(position);
            return true;
        }
    };

    private AdapterView.OnItemClickListener selectParty = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            SharedPreferences.Editor editor = getSharedPreferences("PartyChoice", MODE_PRIVATE).edit();
            editor.putString("party", partyList.get(position).toString());
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), PartyActivity.class);
            startActivity(intent);
        }
    };
}
