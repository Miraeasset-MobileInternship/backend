package miraeassetmobile.backend.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import miraeassetmobile.backend.domain.dto.api.Item;
import miraeassetmobile.backend.domain.dto.api.StockApiResponseDto;
import miraeassetmobile.backend.domain.dto.api.StockApiServiceUtils;

import net.bytebuddy.description.method.MethodDescription;
import org.hibernate.mapping.Any;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.*;

@Service
public class StockApiCallService {


    private StockApiServiceUtils stockApiServiceUtils;


    StockApiCallService(StockApiServiceUtils stockApiServiceUtils) {
        this.stockApiServiceUtils = stockApiServiceUtils;
    }


    // 제공하는 모든 주식정보를 20개씩 반환해줌 (페이지별로) -> 가공되지 않은 entity
    public ResponseEntity<StockApiResponseDto> getAllStockInfo(int pageNo) {

        try {

            String page = Integer.toString(pageNo);

            StringBuilder urlBuilder = new StringBuilder(stockApiServiceUtils.getHostUrl()+"/getStockPriceInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") +"="+ URLEncoder.encode(stockApiServiceUtils.getSecretKey(),"UTF-8")); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("20", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(page, "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*결과 형식*/
    //        urlBuilder.append("&" + URLEncoder.encode("isinCd", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8")); /*결과 형식*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();


            //감싸진 response 부분 제거
            String s = sb.toString().substring(12, sb.length()-1); //response 부분 없애기

            //string to JSON
            JSONObject jsonObject = new JSONObject(s.toString());

            //header , body 중 body부분 가져오기
            JSONObject bodyObject = jsonObject.getJSONObject("body");


            //body 부분 중 추출할 수 있는 정보
            int pageNum = (Integer) bodyObject.get("pageNo");
            int totalCount = (Integer) bodyObject.get("totalCount");
            int rowNum = (Integer) bodyObject.get("numOfRows");


            //items로 감싸진 JSONObject 부분 추출
            JSONObject itemObject = bodyObject.getJSONObject("items");

            // 그 내부 array로 구성된 결과값 가져오기
            JSONArray itemArrays = itemObject.getJSONArray("item");


            // 아이템 리스트
            List<Item> items = new ArrayList<>();

            for(int i=0;i<itemArrays.length();i++){

                // ObjectMapper를 통해 String to Object로 변환
                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

                objectMapper.setSerializationInclusion(
                        JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)


                Item item = objectMapper.readValue(
                        itemArrays.getJSONObject(i).toString(), new TypeReference<Item>() {
                        });

                items.add(item);


            }


            StockApiResponseDto stockApiResponseDto = StockApiResponseDto.builder()
                    .pageNo(pageNum)
                    .numOfRows(rowNum)
                    .totalCount(totalCount)
                    .items(items)
                    .build();


            return ResponseEntity.ok().body(stockApiResponseDto);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);

    }



    // 제공하는 모든 주식정보를 20개씩 반환해줌 (페이지별로) -> 가공되지 않은 entity
    public ResponseEntity<StockApiResponseDto> getStockInfoByCode(String code) {

        try {


            StringBuilder urlBuilder = new StringBuilder(stockApiServiceUtils.getHostUrl()+"/getStockPriceInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") +"="+ URLEncoder.encode(stockApiServiceUtils.getSecretKey(),"UTF-8")); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*결과 형식*/
            urlBuilder.append("&" + URLEncoder.encode("isinCd", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8")); /*결과 형식*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");


            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();


            //감싸진 response 부분 제거
            String s = sb.toString().substring(12, sb.length()-1); //response 부분 없애기

            //string to JSON
            JSONObject jsonObject = new JSONObject(s.toString());

            //header , body 중 body부분 가져오기
            JSONObject bodyObject = jsonObject.getJSONObject("body");


            //body 부분 중 추출할 수 있는 정보
            int pageNum = (Integer) bodyObject.get("pageNo");
            int totalCount = (Integer) bodyObject.get("totalCount");
            int rowNum = (Integer) bodyObject.get("numOfRows");


            //items로 감싸진 JSONObject 부분 추출
            JSONObject itemObject = bodyObject.getJSONObject("items");

            // 그 내부 array로 구성된 결과값 가져오기
            JSONArray itemArrays = itemObject.getJSONArray("item");


            // 아이템 리스트
            List<Item> items = new ArrayList<>();

            for(int i=0;i<itemArrays.length();i++){

                // ObjectMapper를 통해 String to Object로 변환
                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

                objectMapper.setSerializationInclusion(
                        JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)


                Item item = objectMapper.readValue(
                        itemArrays.getJSONObject(i).toString(), new TypeReference<Item>() {
                        });

                items.add(item);


            }


            StockApiResponseDto stockApiResponseDto = StockApiResponseDto.builder()
                    .pageNo(pageNum)
                    .numOfRows(rowNum)
                    .totalCount(totalCount)
                    .items(items)
                    .build();


            return ResponseEntity.ok().body(stockApiResponseDto);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);

    }



}
