package miraeassetmobile.backend.service.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import miraeassetmobile.backend.domain.dto.api.naver.NaverApiServiceUtils;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.yahooFinance.FinanceQuote;
import miraeassetmobile.backend.error.exception.ErrorCode;
import miraeassetmobile.backend.error.exception.ServiceException;
import miraeassetmobile.backend.service.ResponseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

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
public class NaverTranslatorApiService {


    NaverApiServiceUtils naverApiServiceUtils;
    ResponseService responseService;



    NaverTranslatorApiService(NaverApiServiceUtils naverApiServiceUtils, ResponseService responseService){
        this.naverApiServiceUtils = naverApiServiceUtils;
        this.responseService= responseService;
    }



    public String translateToKo(String text) {

        try {


            StringBuilder urlBuilder = new StringBuilder(naverApiServiceUtils.getBaseUrl()+"/v1/papago/n2mt"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("source", "UTF-8") + "=" + URLEncoder.encode("en", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("target", "UTF-8") + "=" + URLEncoder.encode("ko", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("text", "UTF-8") + "=" + URLEncoder.encode(text, "UTF-8")); /*결과 형식*/


            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("X-Naver-Client-Id", naverApiServiceUtils.getClientId());
            conn.setRequestProperty("X-Naver-Client-Secret", naverApiServiceUtils.getClientSecret());


            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                throw new ServiceException(ErrorCode.API_SEVER_ERROR_NAVER);
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


            JSONObject resultObject = jsonObject.getJSONObject("message"); //finance 제이슨 가져오기

            // 실 데이터 부분 추출
            JSONObject bodyObject = resultObject.getJSONObject("result");

            // 한 종목 씩 검색할 것이므로 list 여도 결과는 1개
            String translatedText = bodyObject.get("translatedText").toString();


            return translatedText;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(ErrorCode.API_SEVER_ERROR_NAVER);
        }


    }







}
