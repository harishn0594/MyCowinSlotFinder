package com.example.cowinprobe;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FindSlot {
    int age = 21;
//    int[] districts = {265, 301, 307, 304, 306};

    final int[] flag = {0};
    List<String> availableSlots = new ArrayList<>();

    public FindSlot(List<String> cvals) throws IOException, ProtocolException{

        List<Integer> districts = new ArrayList<>();
        for (int ddd=0; ddd<cvals.size(); ddd++){
            districts.add(ddd, Integer.parseInt(cvals.get(ddd)));
        }

        LocalDate today1 = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String today = today1.format(formatter);

        for (int i : districts) {

            String urltoread = String.format("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=%d&date=%s", i, today.toString());
            StringBuilder result = new StringBuilder();
            URL url = new URL(urltoread);

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection conn = null;
                    try {
                        conn = (HttpURLConnection) url.openConnection();
                        String mes = conn.getResponseMessage();
                        conn.setRequestMethod("GET");
                        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                        BufferedReader br = new BufferedReader(isr);
                        String out = br.readLine();
                        JSONObject js = new JSONObject(out);
                        JSONArray jaa = js.getJSONArray("centers");
                        List<JSONObject> listofcenters = new ArrayList<>();

                        for (int j = 0; j < jaa.length(); j++) {
                            listofcenters.add(jaa.getJSONObject(j));
                        }

                        int counter = 0;
                        for (JSONObject kkk : listofcenters) {
                            String tempcenter = kkk.getString("name");
                            String tempdist = kkk.getString("district_name");
                            String tempblock = kkk.getString("block_name");

                            JSONArray temp = kkk.getJSONArray("sessions");
                            List<JSONObject> sessions = new ArrayList<>();
                            for (int ppp = 0; ppp < temp.length(); ppp++) {
                                sessions.add(temp.getJSONObject(ppp));
                            }

                            for (JSONObject session : sessions) {
                                if ( !session.get("available_capacity").equals(0) && session.get("min_age_limit").equals(18)) {
                                    counter += 1;
                                    System.out.println("gogo");
                                    String center = session.getString("date") + "\n" + tempcenter + "\n" + tempdist + "\n" + tempblock + "\n" + session.getString("vaccine");
                                    availableSlots.add(center);
                                }
                            }
                        }
                        if (counter>0){
                            flag[0] = 1;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            };
            r.run();
        }
    }

}
