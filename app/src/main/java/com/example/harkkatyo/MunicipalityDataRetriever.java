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

public class MunicipalityDataRetriever {
    public boolean municipalityExists(String municipality, HashMap<String, String> municipalityCodes) {
        return municipalityCodes != null && municipalityCodes.containsKey(municipality);
    }
    public interface MunicipalityCodesListener {
        void onMunicipalityCodesFetched(HashMap<String, String> municipalityCodes);
        void onError(String errorMessage);
    }


    public void fetchMunicipalityCodes(MunicipalityCodesListener listener) {
        new Thread(() -> {
            HashMap<String, String> codes = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode areas = objectMapper.readTree(new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px"));

                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();

                for (JsonNode node : areas.get("variables").get(1).get("values")) {
                    values.add(node.asText());
                }
                for (JsonNode node : areas.get("variables").get(1).get("valueTexts")) {
                    keys.add(node.asText());
                }

                for (int i = 0; i < keys.size(); i++) {
                    codes.put(keys.get(i), values.get(i));
                }
                Log.d("HashMapkeys", String.valueOf(keys));

                listener.onMunicipalityCodesFetched(codes);
            } catch (IOException e) {
                e.printStackTrace();
                listener.onError("Failed to fetch municipality codes");
            }
        }).start();
    }

    public ArrayList<MunicipalityData> getData(Context context, String municipality) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode areas = null;

        try {
            areas = objectMapper.readTree(new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px"));
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

        for(int i = 0; i < keys.size(); i++) {
            municipalityCodes.put(keys.get(i), values.get(i));
        }
        Log.d("HashMapmunici", String.valueOf(municipalityCodes));

        String code = null;


        code = null;
        code = municipalityCodes.get(municipality);

        try {
            URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            JsonNode jsonInputString = objectMapper.readTree(context.getResources().openRawResource(R.raw.query));

            ((ObjectNode) jsonInputString.get("query").get(0).get("selection")).putArray("values").add(code);

            byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
            OutputStream os = con.getOutputStream();
            os.write(input, 0, input.length);


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }

            JsonNode municipalityData = objectMapper.readTree(response.toString());

            ArrayList<String> years = new ArrayList<>();
            ArrayList<String> populations = new ArrayList<>();

            for (JsonNode node : municipalityData.get("dimension").get("Vuosi").get("category").get("label")) {
                years.add(node.asText());
            }

            for (JsonNode node : municipalityData.get("value")) {
                populations.add(node.asText());
            }

            ArrayList<MunicipalityData> populationData = new ArrayList<>();

            for(int i = 0; i < years.size(); i++) {
                populationData.add(new MunicipalityData(Integer.valueOf(years.get(i)), Integer.valueOf(populations.get(i))));
            }

            return populationData;

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

}