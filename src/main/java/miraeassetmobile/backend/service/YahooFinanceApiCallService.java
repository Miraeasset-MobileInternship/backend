package miraeassetmobile.backend.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import miraeassetmobile.backend.domain.dto.api.dataGoKr.Item;
import miraeassetmobile.backend.domain.dto.api.dataGoKr.StockApiResponseDto;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.Symbol;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.TrendingByRegion;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.YahooFinanceUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class YahooFinanceApiCallService {


    YahooFinanceUtils yahooFinanceUtils;
    ErrorService errorService;


    YahooFinanceApiCallService(YahooFinanceUtils yahooFinanceUtils, ErrorService errorService){
        this.yahooFinanceUtils=yahooFinanceUtils;
        this.errorService = errorService;
    }




//    public ResponseEntity useFinanceQuote(String region, String symbol) {
//
//        try {
//
//
//            StringBuilder urlBuilder = new StringBuilder(yahooFinanceUtils.getBaseUrl()+"/v6/finance/quote"); /*URL*/
//            urlBuilder.append("?" + URLEncoder.encode("region", "UTF-8") + "=" + URLEncoder.encode(region, "UTF-8")); /*한 페이지 결과 수*/
//            urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode("en", "UTF-8")); /*페이지 번호*/
//            urlBuilder.append("&" + URLEncoder.encode("symbols", "UTF-8") + "=" + URLEncoder.encode(symbol, "UTF-8")); /*결과 형식*/
//
//            URL url = new URL(urlBuilder.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("X-API-KEY", yahooFinanceUtils.getApiKey());
//
//            BufferedReader rd;
//            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                errorService.errorFromExternalServer();
//            }
//            StringBuffer sb = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//
//            System.out.println(sb.toString());
//
//            return null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.badRequest().body(null);
//
//    }



    //트랜딩한 종목 리스트 가져오는 API사용
    public ResponseEntity<TrendingByRegion> trendingByRegion() {

        try {


            StringBuilder urlBuilder = new StringBuilder(yahooFinanceUtils.getBaseUrl()+"/v1/finance/trending"); /*URL*/
            urlBuilder.append("/" +URLEncoder.encode("US", "UTF-8")); /* 미국 시장으로 고정 */


            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-API-KEY", yahooFinanceUtils.getApiKey());

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                errorService.errorFromExternalServer();
            }
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();



            //string to JSON
            JSONObject jsonObject = new JSONObject(sb.toString());

            JSONObject financeObject = jsonObject.getJSONObject("finance"); //finance 제이슨 가져오기

            if(financeObject.get("error") == null){
                //에러
                errorService.errorFromExternalServer();
            }

            JSONArray trendingByRegion = financeObject.getJSONArray("result");

            JSONObject trendingObject = trendingByRegion.getJSONObject(0);

            System.out.println(trendingByRegion);


            Long startInterval = (Long) trendingObject.get("startInterval");
            Long jobTimestamp = (Long) trendingObject.get("jobTimestamp");
            int count = (Integer) trendingObject.get("count");


            System.out.println(startInterval);
            System.out.println(jobTimestamp);
            System.out.println(count);


            //내부에 array로 구성된 결과값 가져오기
            JSONArray quotesArrays = trendingObject.getJSONArray("quotes");


            List<Symbol> quotes = new ArrayList<>();

            for (int i=0; i<quotesArrays.length(); i++) {


                JSONObject q = quotesArrays.getJSONObject(i);

                quotes.add(Symbol.builder()
                         .symbol(q.get("symbol").toString())
                        .build());

            }


            return ResponseEntity.ok().body(
                    TrendingByRegion.builder()
                    .count(count)
                    .jobTimestamp(jobTimestamp)
                    .startInterval(startInterval)
                    .quotes(quotes)
                    .build()
            );


        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);

    }

//
//    public ResponseEntity getRealtimePrice(String symbol) {
//
//        try {
//
//
//            StringBuilder urlBuilder = new StringBuilder(yahooFinanceUtils.getAlphaUrl()+"/market/get-realtime-prices"); /*URL*/
//            urlBuilder.append("?" + URLEncoder.encode("symbols", "UTF-8") + "=" + URLEncoder.encode(symbol, "UTF-8")); /*한 페이지 결과 수*/
//
//
//            URL url = new URL(urlBuilder.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("X-API-KEY", yahooFinanceUtils.getApiKey());
//
//            BufferedReader rd;
//            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                errorService.errorFromExternalServer();
//            }
//            StringBuffer sb = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//
//            System.out.println(sb.toString());
//
//            return null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.badRequest().body(null);
//
//    }
//
//
//
//    public ResponseEntity getChart(String period, String symbol) {
//
//        try {
//
//
//            StringBuilder urlBuilder = new StringBuilder(yahooFinanceUtils.getAlphaUrl()+"/symbol/get-chart"); /*URL*/
//            urlBuilder.append("?" + URLEncoder.encode("period", "UTF-8") + "=" + URLEncoder.encode(period, "UTF-8")); /*한 페이지 결과 수*/
//            urlBuilder.append("&" + URLEncoder.encode("symbol", "UTF-8") + "=" + URLEncoder.encode(symbol, "UTF-8")); /*페이지 번호*/
//
//
//            URL url = new URL(urlBuilder.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("X-API-KEY", yahooFinanceUtils.getApiKey());
//
//            BufferedReader rd;
//            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                errorService.errorFromExternalServer();
//            }
//            StringBuffer sb = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//
//            System.out.println(sb.toString());
//
//            return null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.badRequest().body(null);
//
//    }
//
//
//
//    public ResponseEntity getAutocomplete(String region, String lang, String query){
//
//
//        try {
//
//
//            StringBuilder urlBuilder = new StringBuilder(yahooFinanceUtils.getBaseUrl()+"/v6/finance/autocomplete"); /*URL*/
//            urlBuilder.append("?" + URLEncoder.encode("region", "UTF-8") + "=" + URLEncoder.encode(region, "UTF-8")); /*한 페이지 결과 수*/
//            urlBuilder.append("&" + URLEncoder.encode("lang", "UTF-8") + "=" + URLEncoder.encode(lang, "UTF-8")); /*페이지 번호*/
//            urlBuilder.append("&" + URLEncoder.encode("query", "UTF-8") + "=" + URLEncoder.encode(query, "UTF-8")); /*페이지 번호*/
//
//
//            URL url = new URL(urlBuilder.toString());
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//            conn.setRequestProperty("X-API-KEY", yahooFinanceUtils.getApiKey());
//
//            BufferedReader rd;
//            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            } else {
//                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                errorService.errorFromExternalServer();
//            }
//            StringBuffer sb = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//            rd.close();
//            conn.disconnect();
//
//            System.out.println(sb.toString());
//
//            return null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.badRequest().body(null);
//
//    }


}
