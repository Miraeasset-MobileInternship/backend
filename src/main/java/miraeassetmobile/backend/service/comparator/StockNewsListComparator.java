package miraeassetmobile.backend.service.comparator;


import miraeassetmobile.backend.domain.dto.api.rapidApiYhFinance.StockNews;

import java.util.Comparator;


public class StockNewsListComparator implements Comparator { //일부의 method만 override하려면 implements
    @Override
    public int compare(Object o1, Object o2) {
        String date1 = ((StockNews) o1).getPubDate();
        String date2 = ((StockNews) o2).getPubDate();

        return date2.compareTo(date1);
    }
}
