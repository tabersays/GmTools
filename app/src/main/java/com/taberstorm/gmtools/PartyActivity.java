package com.taberstorm.gmtools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class PartyActivity extends AppCompatActivity {
    PartyDB partyDB;
    ArrayList<String> characterList;
    ArrayAdapter<String> partyListAdapter;
    Button addCharacterButton;
    EditText characterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        ListView characterListView = (ListView) findViewById(R.id.listViewCharacterList);
        SharedPreferences preferences = getSharedPreferences("PartyChoice", MODE_PRIVATE);
        Toast.makeText(getApplicationContext(), preferences.getString("party", "Just Kidding"), Toast.LENGTH_LONG);
        partyDB = new PartyDB(getApplicationContext());
        Cursor characters = partyDB.selectCharacterRecord(preferences.getString("party", ""));
        characterList = new ArrayList<>();
        while(!characters.isAfterLast()) {
            characterList.add(characters.getString(characters.getColumnIndex("name")));
            characters.moveToNext();
        }
        partyListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, characterList);
        addCharacterButton = (Button) findViewById(R.id.buttonAddCharacter);
        addCharacterButton.setOnClickListener(addCharacter);
        characterListView.setAdapter(partyListAdapter);
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

    public class Character {
        String name;
        String party;
        int initiative;
        Character(String name, String party, int initiative) {
            this.name = name;
            this.party = party;
            this.initiative = initiative;
        }

        public String getName() {
            return name;
        }

        public String getParty() {
            return party;
        }

        public int getInitiative() {
            return initiative;
        }
    }

    private Button.OnClickListener addCharacter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addCharacterBox();
        }
    };

    public void addCharacterBox(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Name Character");

        characterName = new EditText(PartyActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        characterName.setLayoutParams(lp);
        alertDialogBuilder.setView(characterName);

        alertDialogBuilder.setPositiveButton("OK", okCreateButton);

        alertDialogBuilder.setNegativeButton("Cancel", cancelButton);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private DialogInterface.OnClickListener okCreateButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            partyDB = new PartyDB(getApplicationContext());
            SharedPreferences preferences = getSharedPreferences("PartyChoice", MODE_PRIVATE);
            partyDB.createCharacterRecord(characterName.getText().toString(), preferences.getString("party", ""), 0);
            characterList.add(characterName.getText().toString());
            partyListAdapter.notifyDataSetChanged();
        }
    };

    private DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //should do nothing
        }
    };
}
