package miraeassetmobile.backend.service.comparator;



import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNewsTime;

import java.util.Comparator;


public class StockNewsListComparator implements Comparator { //일부의 method만 override하려면 implements
    @Override
    public int compare(Object o1, Object o2) {
        Long date1 = ((StockNewsTime) o1).getPubDate();
        Long date2 = ((StockNewsTime) o2).getPubDate();

        return date1.compareTo(date2);
    }
}
