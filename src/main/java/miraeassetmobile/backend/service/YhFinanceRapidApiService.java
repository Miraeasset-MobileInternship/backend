package miraeassetmobile.backend.service;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import miraeassetmobile.backend.domain.BanklassResponseEntity;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.YhFinanceRapidApiUtils;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.AutoComplete;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.error.Mark;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class YhFinanceRapidApiService {


    YhFinanceRapidApiUtils yhFinanceRapidApiUtils;
    ResponseService responseService;


    YhFinanceRapidApiService(YhFinanceRapidApiUtils yhFinanceRapidApiUtils, ResponseService responseService){
        this.yhFinanceRapidApiUtils = yhFinanceRapidApiUtils;
        this.responseService = responseService;
    }


    public List<MarketNews> getMarketNews(){

        try {

            String requestUrl = yhFinanceRapidApiUtils.getBaseUrl() + "/ne/news";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("X-RapidAPI-Key", yhFinanceRapidApiUtils.getApiKey())
                    .header("X-RapidAPI-Host", yhFinanceRapidApiUtils.getHost())
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JSONArray jsonArray = new JSONArray(response.body());


            List<MarketNews> marketNewsList = new ArrayList<>();


            for(int i=0; i<jsonArray.length(); i++){

                JSONObject news = jsonArray.getJSONObject(i);


// ObjectMapper를 통해 String to Object로 변환
                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);

                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)

                MarketNews newsData = objectMapper.readValue(news.toString(),
                        new TypeReference<MarketNews>() {
                        });


                marketNewsList.add(newsData);

            }

            return marketNewsList;

        } catch (Exception e) {
            e.printStackTrace();
            responseService.errorFromExternalServerNoResult();
        }

        return new ArrayList<>();



    }





}

