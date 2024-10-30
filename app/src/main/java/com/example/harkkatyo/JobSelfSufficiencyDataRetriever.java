package com.example.harkkatyo;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class JobSelfSufficiencyDataRetriever {

    public ArrayList<JobSelfSufficiencyData> getData(Context context, String municipality) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode areas = null;

        try {
            areas = objectMapper.readTree(new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_125s.px"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        for (JsonNode node : areas.get("variables").get(1).get("values")) {
            values.add(node.asText());
        }
        for (JsonNode node : areas.get("variables").get(1).get("valueTexts")) {
            keys.add(node.asText());
        }

        HashMap<String, String> municipalityCodes = new HashMap<>();

        for (int i = 0; i < keys.size(); i++) {
            municipalityCodes.put(keys.get(i), values.get(i));
        }
        Log.d("HashMapSelfSuff", String.valueOf(municipalityCodes));

        String code = null;


        code = null;
        code = municipalityCodes.get(municipality);


        HttpURLConnection con = null;
        try {
            URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_125s.px");

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JsonNode jsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query3));

            ((ObjectNode) jsonInputString.get("query").get(1).get("selection")).putArray("values").add(code);
            String jsonString = jsonInputString.toString();
            Log.d("JSON Input String", jsonString);

            byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
            OutputStream os = con.getOutputStream();
            os.write(input, 0, input.length);


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            JsonNode jobSelfSufficiencyData = objectMapper.readTree(response.toString());

            ArrayList<String> years = new ArrayList<>();
            ArrayList<String> sufficiencies = new ArrayList<>();

            for (JsonNode node : jobSelfSufficiencyData.get("dimension").get("Vuosi").get("category").get("label")) {
                years.add(node.asText());
            }

            for (JsonNode node : jobSelfSufficiencyData.get("value")) {
                sufficiencies.add(node.asText());
            }

            ArrayList<JobSelfSufficiencyData> selfSufficiencyData = new ArrayList<>();

            for (int i = 0; i < years.size(); i++) {
                selfSufficiencyData.add(new JobSelfSufficiencyData(Integer.valueOf(years.get(i)), Float.valueOf(sufficiencies.get(i))));
            }

            return selfSufficiencyData;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (con != null) {
            try {
                int responseCode = con.getResponseCode();
                String responseMessage = con.getResponseMessage();
                Log.e("JobSelfSuffDataRetriever", "Response code: " + responseCode + ", Message: " + responseMessage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;

    }

}