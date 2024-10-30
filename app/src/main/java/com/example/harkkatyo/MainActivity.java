package com.example.harkkatyo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SearchListAdapter.OnItemClickListener {
    private EditText editTextLocation;
    private SearchListAdapter adapter;
    private static final int REQUEST_CODE_COMPARE_SEARCH = 1;
    private MunicipalityDataRetriever retriever;
    private HashMap<String, String> municipalityCodes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextLocation = findViewById(R.id.editTextMunicipality);

        RecyclerView recyclerView = findViewById(R.id.rvSearchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchListAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        retriever = new MunicipalityDataRetriever();

        String location1 = getIntent().getStringExtra("location1");
        String location2 = getIntent().getStringExtra("location2");
        if (location1 != null && location2 != null) {
            updateSearchHistory(location1);
            updateSearchHistory(location2);
        }
        retriever.fetchMunicipalityCodes(new MunicipalityDataRetriever.MunicipalityCodesListener() {
            @Override
            public void onMunicipalityCodesFetched(HashMap<String, String> municipalityCodes) {
                MainActivity.this.municipalityCodes = municipalityCodes;
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MunicipalityCodes", errorMessage);
            }
        });
    }

    private void updateSearchHistory(String location) {
        SearchHistory.getInstance().addSearch(location);
        adapter.notifyDataSetChanged();
    }


    public void onFindBtnClick(View view) {
        String location = editTextLocation.getText().toString().trim();
        if (!location.isEmpty()) {
            if (retriever.municipalityExists(location, municipalityCodes)) {
                performSearch(location);
            } else {
                Toast.makeText(this, "Tarkista syöte", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tarkista syöte", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCompareButtonClick(View view) {
        Intent intent = new Intent(MainActivity.this, CompareSearch.class);
        startActivityForResult(intent, REQUEST_CODE_COMPARE_SEARCH);
    }

    private void performSearch(String municipality) {
        Intent intent = new Intent(MainActivity.this, BasicInfo.class);
        intent.putExtra("location", municipality);
        startActivity(intent);
        updateSearchHistory(municipality);

    }

    @Override
    public void onItemClick(String search) {
        performSearch(search);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_COMPARE_SEARCH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> updatedSearchHistory = data.getStringArrayListExtra("updatedSearchHistory");
            if (updatedSearchHistory != null) {
                SearchHistory.getInstance().setSearchHistoryList(updatedSearchHistory);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
