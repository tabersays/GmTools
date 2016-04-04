package com.taberstorm.gmtools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PartyActivity extends AppCompatActivity {
    PartyDB partyDB;
    ArrayList<Character> characterList;
    ArrayAdapter<Character> partyListAdapter;
    Button addCharacterButton;
    Button nextCharacterButton;
    Button previousCharacterButton;
    EditText characterName;
    EditText characterInitiative;
    Integer position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
        ListView characterListView = (ListView) findViewById(R.id.listViewCharacterList);
        SharedPreferences preferences = getSharedPreferences("PartyChoice", MODE_PRIVATE);
        partyDB = new PartyDB(getApplicationContext());
        Cursor characters = partyDB.selectCharacterRecord(preferences.getString("party", ""));
        characterList = new ArrayList<>();
        while(!characters.isAfterLast()) {
            Character character = new Character(characters.getString(characters.getColumnIndex("name")), null, Integer.parseInt(characters.getString(characters.getColumnIndex("initiative"))));
            characterList.add(character);
            characters.moveToNext();
        }
        Collections.sort(characterList, new Comparator<Character> () {
            @Override
            public int compare(Character character1, Character character2) {
                return character2.getInitiative().compareTo(character1.getInitiative());
            }
        });
        partyListAdapter = new ArrayAdapter<Character>(this, android.R.layout.simple_list_item_1, characterList);
        characterListView.setOnItemLongClickListener(deleteCharacter);
        characterListView.setOnItemClickListener(editCharacter);
        addCharacterButton = (Button) findViewById(R.id.buttonAddCharacter);
        nextCharacterButton = (Button) findViewById(R.id.buttonNext);
        previousCharacterButton = (Button) findViewById(R.id.buttonPrevious);

        addCharacterButton.setOnClickListener(addCharacter);
        nextCharacterButton.setOnClickListener(nextCharacter);
        previousCharacterButton.setOnClickListener(previousCharacter);
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
        private String name;
        private String party;
        private Integer initiative;
        Character(String name, String party, Integer initiative) {
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

        public Integer getInitiative() {
            return initiative;
        }

        public void setInitiative(Integer initiative) {
            this.initiative = initiative;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setParty(String party) {
            this.party = party;
        }

        public String toString() {
            return name + ": " + initiative;
        }
    }

    private Button.OnClickListener addCharacter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addCharacterBox();
        }
    };

    private Button.OnClickListener nextCharacter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Character character =  characterList.remove(0);
            characterList.add(character);
            partyListAdapter.notifyDataSetChanged();
        }
    };

    private Button.OnClickListener previousCharacter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Character character =  characterList.remove(characterList.size() -1);
            characterList.add(0, character);
            partyListAdapter.notifyDataSetChanged();
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
            Character character = new Character(characterName.getText().toString(), preferences.getString("party", ""), 0);
            characterList.add(character);
            partyListAdapter.notifyDataSetChanged();

        }
    };

    private DialogInterface.OnClickListener cancelButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //should do nothing
        }
    };

    private DialogInterface.OnClickListener okEditButton = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            String originalName;
            int initiative;

            Character character = characterList.get(position);
            originalName = character.getName();
            character.setName(characterName.getText().toString());
            character.setInitiative(Integer.parseInt(characterInitiative.getText().toString()));
            initiative = character.getInitiative();
            partyDB.updateCharacterRecord(originalName, character.getName(), initiative);

            character = characterList.get(0);
            characterList.remove(0);
            Collections.sort(characterList, new Comparator<Character>() {
                @Override
                public int compare(Character character1, Character character2) {
                    return character2.getInitiative().compareTo(character1.getInitiative());
                }
            });
            characterList.add(0, character);
        }
    };

    public void deleteCharacter(int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Delete Character?");

        alertDialogBuilder.setPositiveButton("Yes", new DeleteCharacterOnClickListener(position));

        alertDialogBuilder.setNegativeButton("Cancel", cancelButton);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void editCharacter(int position) {
        this.position = position;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Edit");
        Character character = characterList.get(position);

        characterName = new EditText(PartyActivity.this);
        characterName.setText(character.getName());
        characterInitiative = new EditText(PartyActivity.this);
        characterInitiative.setText(character.getInitiative().toString());
        characterInitiative.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout layout = new LinearLayout(PartyActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout nameLayout = new LinearLayout(PartyActivity.this);
        nameLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView nameText = new TextView(PartyActivity.this);
        nameText.setText("Name: ");
        nameLayout.addView(nameText);
        nameLayout.addView(characterName);
        LinearLayout initiativeLayout = new LinearLayout(PartyActivity.this);
        initiativeLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView initiativeText = new TextView(PartyActivity.this);
        initiativeText.setText("Initiative: ");
        initiativeLayout.addView(initiativeText);
        initiativeLayout.addView(characterInitiative);
        layout.addView(nameLayout);
        layout.addView(initiativeLayout);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("OK", okEditButton);

        alertDialogBuilder.setNegativeButton("Cancel", cancelButton);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public class DeleteCharacterOnClickListener implements DialogInterface.OnClickListener
    {

        int position;
        public DeleteCharacterOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which)
        {
            partyDB = new PartyDB(getApplicationContext());
            partyDB.deleteCharacterRecord(characterList.get(position).getName());
            characterList.remove(position);
            partyListAdapter.notifyDataSetChanged();
        }

    };

    private AdapterView.OnItemLongClickListener deleteCharacter = new AdapterView.OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView parent, View v, int position, long id) {
            deleteCharacter(position);
            return true;
        }
    };

    private AdapterView.OnItemClickListener editCharacter = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            editCharacter(position);
        }
    };
}
