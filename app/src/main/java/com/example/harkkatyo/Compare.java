package com.example.harkkatyo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Compare extends AppCompatActivity{
    private TextView txtPopHeader1, txtPopHeader2, txtJobRateHeader1, txtJobRateHeader2, txtSelfSuffHeader1, txtSelfSuffHeader2, txtMunicipality1, txtMunicipality2, txtPop1, txtPop2, txtWeather1, txtWeather2, txtJobRate1, txtJobRate2, txtSelfSuff1, txtSelfSuff2;
    private ImageView imgWeather1, imgWeather2;
    private RecyclerView recyclerView1, recyclerView2;
    private PopulationAdapter adapter1, adapter2;
    private WeatherDataRetriever weatherDataRetriever;
    

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        txtPopHeader1 = findViewById(R.id.txtPopHeader1);
        txtPopHeader2 = findViewById(R.id.txtPopHeader2);

        txtJobRateHeader1 = findViewById(R.id.txtJobRateHeader1);
        txtJobRateHeader2 = findViewById(R.id.txtJobRateHeader2);

        txtSelfSuffHeader1 = findViewById(R.id.txtSelfSuffHeader1);
        txtSelfSuffHeader2 = findViewById(R.id.txtSelfSuffHeader2);

        txtMunicipality1 = findViewById(R.id.txtMunicipality1);
        txtMunicipality2 = findViewById(R.id.txtMunicipality2);

        txtJobRate1 = findViewById(R.id.txtJobRate1);
        txtJobRate2 = findViewById(R.id.txtJobRate2);

        txtSelfSuff1 = findViewById(R.id.txtSelfSuff1);
        txtSelfSuff2 = findViewById(R.id.txtSelfSuff2);

        txtWeather1 = findViewById(R.id.txtWeather1);
        txtWeather2 = findViewById(R.id.txtWeather2);

        imgWeather1 = findViewById(R.id.imgWeather1);
        imgWeather2 = findViewById(R.id.imgWeather2);

        recyclerView1 = findViewById(R.id.rvPop1);
        recyclerView2 = findViewById(R.id.rvPop2);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        adapter1 = new PopulationAdapter(this);
        adapter2 = new PopulationAdapter(this);
        recyclerView1.setAdapter(adapter1);
        recyclerView2.setAdapter(adapter2);

        weatherDataRetriever = new WeatherDataRetriever();

        String location1 = getIntent().getStringExtra("location1");
        String location2 = getIntent().getStringExtra("location2");
        if (location1 != null && location2 != null) {
            fetchData(location1, location2);
        }
    }

    private void fetchData(String location1, String location2) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                MunicipalityDataRetriever mr1 = new MunicipalityDataRetriever();
                ArrayList<MunicipalityData> populationData1 = mr1.getData(Compare.this, location1);

                MunicipalityDataRetriever mr2 = new MunicipalityDataRetriever();
                ArrayList<MunicipalityData> populationData2 = mr2.getData(Compare.this, location2);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter1.setPopulationData(populationData1);
                        adapter2.setPopulationData(populationData2);
                    }
                });

                JobRateDataRetriever jrr1 = new JobRateDataRetriever();
                ArrayList<JobRateData> rateData1 = jrr1.getData(Compare.this, location1);
                JobRateDataRetriever jrr2 = new JobRateDataRetriever();
                ArrayList<JobRateData> rateData2 = jrr2.getData(Compare.this, location2);

                JobSelfSufficiencyDataRetriever jsr1 = new JobSelfSufficiencyDataRetriever();
                ArrayList<JobSelfSufficiencyData> selfSufficiencyData1 = jsr1.getData(Compare.this, location1);
                JobSelfSufficiencyDataRetriever jsr2 = new JobSelfSufficiencyDataRetriever();
                ArrayList<JobSelfSufficiencyData> selfSufficiencyData2 = jsr2.getData(Compare.this, location2);

                WeatherData weatherData1 = weatherDataRetriever.getWeatherData(location1);
                WeatherData weatherData2 = weatherDataRetriever.getWeatherData(location2);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SearchHistory.getInstance().addSearch(location1);
                        SearchHistory.getInstance().addSearch(location2);
                        if (weatherData1 != null && weatherData2 != null && rateData1 != null && rateData2 != null && selfSufficiencyData1 != null && selfSufficiencyData2 != null) {
                            displayData(weatherData1, weatherData2, rateData1, rateData2, selfSufficiencyData1, selfSufficiencyData2);
                        }
                    }
                });
            }
        });


    }
    private void displayData(WeatherData weatherData1, WeatherData weatherData2,  ArrayList<JobRateData> rateData1, ArrayList<JobRateData> rateData2, ArrayList<JobSelfSufficiencyData> selfSufficiencyData1, ArrayList<JobSelfSufficiencyData> selfSufficiencyData2) {
        txtMunicipality1.setText(weatherData1.getName());
        txtMunicipality2.setText(weatherData2.getName());

        txtPopHeader1.setText("Väestökehitys: ");
        txtPopHeader2.setText("Väestökehitys: ");

        txtJobRateHeader1.setText("Työllisyysaste: ");
        txtJobRateHeader2.setText("Työllisyysaste: ");

        txtSelfSuffHeader1.setText("Työpaikkaomavaraisuus: ");
        txtSelfSuffHeader2.setText("Työpaikkaomavaraisuus: ");

        StringBuilder jrStringBuilder1 = new StringBuilder();
        for(JobRateData data : rateData1) {
            jrStringBuilder1.append(data.getYearjr()).append(": ").append(data.getJobRate()).append(" % \n");
        }
        txtJobRate1.setText(jrStringBuilder1.toString());

        StringBuilder jrStringBuilder2 = new StringBuilder();
        for(JobRateData data : rateData2) {
            jrStringBuilder2.append(data.getYearjr()).append(": ").append(data.getJobRate()).append(" % \n");
        }
        txtJobRate2.setText(jrStringBuilder2.toString());

        StringBuilder jssStringBuilder1 = new StringBuilder();
        for(JobSelfSufficiencyData data : selfSufficiencyData1) {
            jssStringBuilder1.append(data.getYearjs()).append(": ").append(data.getSelfsufficiency()).append(" %\n");
        }
        txtSelfSuff1.setText(jssStringBuilder1.toString());

        StringBuilder jssStringBuilder2 = new StringBuilder();
        for(JobSelfSufficiencyData data : selfSufficiencyData2) {
            jssStringBuilder2.append(data.getYearjs()).append(": ").append(data.getSelfsufficiency()).append(" %\n");
        }
        txtSelfSuff2.setText(jssStringBuilder2.toString());

        txtWeather1.setText(
                        "Sää nyt: " + weatherData1.getDescription() + "\n" +
                        "Lämpötila: " + weatherData1.getTemperature() + " °C\n" +
                        "Tuuli: " + weatherData1.getWindSpeed() + " m/s\n"
        );
        txtWeather2.setText(
                        "Sää nyt: " + weatherData2.getDescription() + "\n" +
                        "Lämpötila: " + weatherData2.getTemperature() + " °C\n" +
                        "Tuuli: " + weatherData2.getWindSpeed() + " m/s\n"
        );

        int weatherIconResource1 = weatherDataRetriever.getWeatherImageResource(weatherData1.getMain());
        imgWeather1.setImageResource(weatherIconResource1);

        int weatherIconResource2 = weatherDataRetriever.getWeatherImageResource(weatherData2.getMain());
        imgWeather2.setImageResource(weatherIconResource2);

    }

}
