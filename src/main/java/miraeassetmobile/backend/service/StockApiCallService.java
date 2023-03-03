package miraeassetmobile.backend.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import miraeassetmobile.backend.domain.dto.api.StockApiResponseDto;
import miraeassetmobile.backend.domain.dto.api.StockApiServiceUtils;
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
import java.net.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockApiCallService {


    private StockApiServiceUtils stockApiServiceUtils;


    StockApiCallService(StockApiServiceUtils stockApiServiceUtils) {
        this.stockApiServiceUtils = stockApiServiceUtils;
    }


    //직전날(가장 최신의 종목정보 API 콜하기 )
    public ResponseEntity<StockApiResponseDto> getStockInfoByCode(String code) {


        Map<String, Object> resultMap = new HashMap<>();

        try {







            StringBuilder urlBuilder = new StringBuilder("https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") +"="+ URLEncoder.encode(stockApiServiceUtils.getSecretKey(),"UTF-8")); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("resultType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*결과 형식*/
            urlBuilder.append("&" + URLEncoder.encode("isinCd", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8")); /*결과 형식*/

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
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            System.out.println(sb);


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



}
