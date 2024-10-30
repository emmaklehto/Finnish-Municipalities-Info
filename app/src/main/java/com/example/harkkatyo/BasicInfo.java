package com.example.harkkatyo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlinx.coroutines.Job;

public class BasicInfo extends AppCompatActivity {
    private TextView txtPopulationHeader, txtJobrateHeader, txtSelfSuffHeader, txtMunicipality, txtPopulationData, txtJobRateData, txtJobSelfSufficiency, txtWeatherData;
    private ImageView imgWeatherIcon;
    private WeatherDataRetriever weatherDataRetriever;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basicinfo);

        txtPopulationHeader = findViewById(R.id.txtPopulationHeader);
        txtJobrateHeader = findViewById(R.id.txtJobrateHeader);
        txtSelfSuffHeader = findViewById(R.id.txtSelfSuffHeader);
        txtMunicipality = findViewById(R.id.txtMunicipality);
        txtPopulationData = findViewById(R.id.txtPop);
        txtJobRateData = findViewById(R.id.txtJobRate);
        txtJobSelfSufficiency = findViewById(R.id.txtJobSelfSufficiency);
        txtWeatherData = findViewById(R.id.txtWeather);
        imgWeatherIcon = findViewById(R.id.ivWeatherImage);
        weatherDataRetriever = new WeatherDataRetriever();

        String location = getIntent().getStringExtra("location");
        if (location != null) {
            fetchData(location);
        }
    }

    private void fetchData(String location) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                MunicipalityDataRetriever mr = new MunicipalityDataRetriever();
                ArrayList<MunicipalityData> populationData = mr.getData(BasicInfo.this, location);

                JobRateDataRetriever jrr = new JobRateDataRetriever();
                ArrayList<JobRateData> rateData = jrr.getData(BasicInfo.this, location);

                JobSelfSufficiencyDataRetriever jsr = new JobSelfSufficiencyDataRetriever();
                ArrayList<JobSelfSufficiencyData> selfSufficiencyData = jsr.getData(BasicInfo.this, location);

                WeatherData weatherData = weatherDataRetriever.getWeatherData(location);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (populationData != null && weatherData != null && rateData != null && selfSufficiencyData != null) {
                            displayData(populationData, weatherData, rateData, selfSufficiencyData);
                        }
                    }
                });
            }
        });
    }
    private void displayData(ArrayList<MunicipalityData> populationData, WeatherData weatherData, ArrayList<JobRateData> rateData, ArrayList<JobSelfSufficiencyData> selfSufficiencyData) {
        txtMunicipality.setText(weatherData.getName());
        txtPopulationHeader.setText("Väestökehitys:");
        txtJobrateHeader.setText("Työllisyysaste:");
        txtSelfSuffHeader.setText("Työpaikkaomavaraisuus:");

        StringBuilder populationStringBuilder = new StringBuilder();
        for(MunicipalityData data : populationData) {
            populationStringBuilder.append(data.getYear()).append(": ").append(data.getPopulation()).append("\n");
        }
        txtPopulationData.setText(populationStringBuilder.toString());

        StringBuilder jrStringBuilder = new StringBuilder();
        for(JobRateData data : rateData) {
            jrStringBuilder.append(data.getYearjr()).append(": ").append(data.getJobRate()).append(" % \n");
        }
        txtJobRateData.setText(jrStringBuilder.toString());

        StringBuilder jssStringBuilder = new StringBuilder();
        for(JobSelfSufficiencyData data : selfSufficiencyData) {
            jssStringBuilder.append(data.getYearjs()).append(": ").append(data.getSelfsufficiency()).append(" %\n");
        }
        txtJobSelfSufficiency.setText(jssStringBuilder.toString());

        txtWeatherData.setText(
                        "Sää nyt: " + weatherData.getDescription() + "\n" +
                        "Lämpötila: " + weatherData.getTemperature() + " °C\n" +
                        "Tuuli: " + weatherData.getWindSpeed() + " m/s\n"
        );

        int weatherIconResource = weatherDataRetriever.getWeatherImageResource(weatherData.getMain());
        imgWeatherIcon.setImageResource(weatherIconResource);
    }
}
