package miraeassetmobile.backend.service.comparator;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.MarketNews;
import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;

import java.util.Comparator;

public class ListComparator implements Comparator {

    //신문기사 최신순으로 정렬
    @Override
    public int compare(Object o1, Object o2) {
        String date1 = ((MarketNews) o1).getPubDate();
        String date2 = ((MarketNews) o2).getPubDate();

        return date2.compareTo(date1);
    }
}
