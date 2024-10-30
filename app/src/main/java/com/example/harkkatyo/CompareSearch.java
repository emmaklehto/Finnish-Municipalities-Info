package com.example.harkkatyo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class CompareSearch extends AppCompatActivity implements MunicipalityDataRetriever.MunicipalityCodesListener {
    private EditText editFirstMunicipalityName, editSecondMunicipalityName;
    private MunicipalityDataRetriever retriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparesearch);

        editFirstMunicipalityName = findViewById(R.id.editFirstMunicipalityName);
        editSecondMunicipalityName = findViewById(R.id.editSecondMunicipalityName);
        retriever = new MunicipalityDataRetriever();
    }

    public void onSecondSearchButtonClick(View view) {
        String location1 = editFirstMunicipalityName.getText().toString().trim();
        String location2 = editSecondMunicipalityName.getText().toString().trim();

        if (!location1.isEmpty() && !location2.isEmpty()) {
            if (location1.equals(location2)) {
                Toast.makeText(this, "Syötä kaksi eri kuntaa", Toast.LENGTH_SHORT).show();
            } else {
                retriever.fetchMunicipalityCodes(this);
            }
        } else {
            Toast.makeText(this, "Syötä molemmat kunnat", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSearchHistory(String location) {
        SearchHistory.getInstance().addSearch(location);
    }

    private void performSearch(String municipality1, String municipality2, HashMap<String, String> municipalityCodes) {
        if (municipalityCodes.containsKey(municipality1) && municipalityCodes.containsKey(municipality2)) {
            Intent intent = new Intent(CompareSearch.this, Compare.class);
            intent.putExtra("location1", municipality1);
            intent.putExtra("location2", municipality2);

            updateSearchHistory(municipality1);
            updateSearchHistory(municipality2);
            ArrayList<String> updatedSearchHistory = SearchHistory.getInstance().getSearchHistoryList();
            intent.putStringArrayListExtra("updatedSearchHistory", updatedSearchHistory);

            setResult(RESULT_OK, intent);
            finish();
            startActivity(intent);
        } else {
            Toast.makeText(this, "Tarkista syöte", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMunicipalityCodesFetched(HashMap<String, String> municipalityCodes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String location1 = editFirstMunicipalityName.getText().toString().trim();
                String location2 = editSecondMunicipalityName.getText().toString().trim();
                performSearch(location1, location2, municipalityCodes);
            }
        });
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
