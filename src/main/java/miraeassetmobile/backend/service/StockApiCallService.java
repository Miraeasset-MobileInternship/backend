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


//    직전날(가장 최신의 종목정보 API 콜하기 )
//직전날(가장 최신의 종목정보 API 콜하기 )
public ResponseEntity<StockApiResponseDto> getStockInfoByCode(String code) {


    Map<String, Object> resultMap = new HashMap<>();

    try {







        StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") +"="+ URLEncoder.encode(stockApiServiceUtils.getSecretKey(),"UTF-8")); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*결과 형식*/
//        urlBuilder.append("&" + URLEncoder.encode("isinCd", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8")); /*결과 형식*/

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        System.out.println("Response code: " + conn.getResponseCode());

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

        System.out.println(sb);

        String s = sb.toString().substring(12, sb.length()-1); //response 부분 없애기
        JSONObject jsonObject = new JSONObject(s.toString());

        JSONObject bodyObject = jsonObject.getJSONObject("body");


        int pageNo = (Integer) bodyObject.get("pageNo");
        int totalCount = (Integer) bodyObject.get("totalCount");
        int numOfRows = (Integer) bodyObject.get("numOfRows");

        System.out.println(pageNo);
        System.out.println(totalCount);
        System.out.println(numOfRows);

        JSONObject itemObject = bodyObject.getJSONObject("items");

        System.out.println("아이템 오브젝트");
        System.out.println(itemObject.toString());

        JSONArray itemArrays = itemObject.getJSONArray("item");

        System.out.println("아이템 어레이 1번");
        System.out.println(itemArrays.getJSONObject(0).toString());

        System.out.println(itemArrays.length());


        List<Item> items = new ArrayList<>();

        for(int i=0;i<itemArrays.length();i++){

                        // ObjectMapper를 통해 String to Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

            objectMapper.setSerializationInclusion(
                    JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)

            System.out.println(itemArrays.getJSONObject(i).toString());

            Item item = objectMapper.readValue(
                    itemArrays.getJSONObject(i).toString(), new TypeReference<Item>() {
                    });

            items.add(item);


        }


        StockApiResponseDto stockApiResponseDto = StockApiResponseDto.builder()
                .pageNo(pageNo)
                .numOfRows(numOfRows)
                .totalCount(totalCount)
                .items(items)
                .build();


        System.out.println("드디어 됐다 ");

        System.out.println(stockApiResponseDto.getNumOfRows());
        System.out.println(stockApiResponseDto.getTotalCount());
        System.out.println(stockApiResponseDto.getPageNo());

        for (Item i : stockApiResponseDto.getItems()) {

            System.out.println(i.getItmsNm());

        }







//
//        String s = sb.toString().substring(12, sb.length()-1); //response 부분 없애기
//        JSONParser jsonParser = new JSONParser();
//        Object result = jsonParser.parse(s.toString());
//        JSONObject jsonObj = new JSONObject(result.toString());
//
//
//
//
//        System.out.println(jsonObj.get("body"));
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
//
//        objectMapper.setSerializationInclusion(
//                    JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
//        StockApiResponseDto stockApiResponse = objectMapper.readValue(jsonObj.get("body").toString(), new TypeReference<StockApiResponseDto>() {});
//
//        System.out.println(stockApiResponse.getItems());
//        System.out.println(stockApiResponse.getNumOfRows());
//        System.out.println(stockApiResponse.getTotalCount());
//
//
//        Gson gson = new Gson();
//        Type listType = new TypeToken<ArrayList<Item>>(){}.getType();
//        List<Item> list = gson.fromJson(stockApiResponse.getItems().toString(), listType);
////        List<Item> item = objectMapper.readValue(stockApiResponse.getItems().toString(), new TypeReference<List<Item>>(){});
//
//        System.out.println(list.get(0).getBasDt());


//
//                    // ObjectMapper를 통해 String to Object로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
//
//            objectMapper.setSerializationInclusion(
//                    JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
//
//            StockApiResponseDto stockApiResponse = objectMapper.readValue(
//                    s.getBody(), new TypeReference<StockApiResponseDto>() {
//                    });
//
//
//
//        HashMap<String,Object> jsonMap = new ObjectMapper().readValue(s, HashMap.class);
//
//        System.out.println(jsonMap.get("body"));
//
//
//        JSONParser jsonParser = new JSONParser();
//        Object result = jsonParser.parse(jsonMap.get("body").toString());
//
//        System.out.println(result);

//        HashMap<String,Object> jsonMap3 = new ObjectMapper().readValue(jsonMap.get("body").toString(), HashMap.class);



//        StockApiResponseDto stockApiResponse = new ObjectMapper().readValue(jsonMap3.toString(), new TypeReference<StockApiResponseDto>(){});


//        System.out.println(jsonMap);
//        System.out.println(stockApiResponse);

//        JSONParser jsonParser = new JSONParser();
//        Object result = jsonParser.parse(sb.toString());
//        System.out.println();


//            try {
//
//            System.out.println("1");
//
//            RestTemplate rt = new RestTemplate();
////            rt.setRequestFactory( new HttpComponentsClientHttpRequestFactory()); //error message type및 description확인 가능
//
//
//            System.out.println("2");
//
//            // 해더 만들기
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Accept", "text/html");
//
//            System.out.println("3");
//
//            System.out.println(stockApiServiceUtils.getSecretKey());
//
//            String key = URLEncoder.encode(stockApiServiceUtils.getSecretKey(), "UTF-8");
//
//            System.out.println("3");
//            // 바디 만들기 (HashMap 사용 불가!)
//            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
//            params.add("serviceKey", key);
//            params.add("numOfRows", 1);
//            params.add("pageNo", 1);
//            params.add("resultType", "json");
//            params.add("isinCd", code);
//
//
//
//
//            // 해더와 바디를 하나의 오브젝트로 만들기
//            HttpEntity<MultiValueMap<String, Object>> stockApiRequest =
//                    new HttpEntity<>(params, headers);
//
//            System.out.println("5");
//            System.out.println(stockApiRequest);
//
//            // Http 요청하고 리턴값을 response 변수로 받기
//            ResponseEntity<String> apiResponseJson = rt.exchange(
//                    "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo", // Host
//                    HttpMethod.GET, // Request Method
//                    stockApiRequest,    // RequestBody
//                    String.class
//            );    // return Object
//
//
//            System.out.println(apiResponseJson);
//
//            System.out.println("6");
//
//            // ObjectMapper를 통해 String to Object로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//
//            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
//
//            objectMapper.setSerializationInclusion(
//                    JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
//
//            StockApiResponseDto stockApiResponse = objectMapper.readValue(
//                    apiResponseJson.getBody(), new TypeReference<StockApiResponseDto>() {
//                    });
//
//            return ResponseEntity.ok().body(stockApiResponse);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.badRequest().body(null);
//
//
//    } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        } catch (ProtocolException e) {
//            throw new RuntimeException(e);
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


    } catch (ProtocolException e) {
        throw new RuntimeException(e);
    } catch (MalformedURLException e) {
        throw new RuntimeException(e);
    } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    return ResponseEntity.badRequest().body(null);

}



    public ResponseEntity<StockApiResponseDto> getStockInfoByCodee(String code) {


        try {

            System.out.println("1");

            RestTemplate rt = new RestTemplate();
//            rt.setRequestFactory(
//                    new HttpComponentsClientHttpRequestFactory()); //error message type및 description확인 가능


            // 해더 만들기
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");

            // 바디 만들기 (HashMap 사용 불가!)
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
            params.add(URLEncoder.encode("ServiceKey", "UTF-8"), URLEncoder.encode(stockApiServiceUtils.getSecretKey(),"UTF-8"));
            params.add(URLEncoder.encode("numOfRows", "UTF-8"),URLEncoder.encode("1", "UTF-8"));
            params.add(URLEncoder.encode("pageNo", "UTF-8"), URLEncoder.encode("1", "UTF-8"));
            params.add(URLEncoder.encode("resultType", "UTF-8"),URLEncoder.encode("json", "UTF-8"));
            params.add(URLEncoder.encode("isinCd", "UTF-8"), URLEncoder.encode(code, "UTF-8"));

            // 해더와 바디를 하나의 오브젝트로 만들기
            HttpEntity<MultiValueMap<String, Object>> stockApiRequest =
                    new HttpEntity<>(params, headers);

            System.out.println(stockApiRequest);

            // Http 요청하고 리턴값을 response 변수로 받기
            ResponseEntity<String> apiResponseJson = rt.exchange(
                    "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo", // Host
                    HttpMethod.GET, // Request Method
                    stockApiRequest,    // RequestBody
                    String.class
            );    // return Object

            // ObjectMapper를 통해 String to Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

            objectMapper.setSerializationInclusion(
                    JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)

            StockApiResponseDto stockInfoResponse = objectMapper.readValue(
                    apiResponseJson.getBody(), new TypeReference<StockApiResponseDto>() {
                    });

            return ResponseEntity.ok().body(stockInfoResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);

    }




}
