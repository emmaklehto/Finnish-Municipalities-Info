package com.example.harkkatyo;

import java.util.ArrayList;

public class SearchHistory {
    private static final SearchHistory instance = new SearchHistory();
    private ArrayList<String> searchHistoryList = new ArrayList<>();

    private SearchHistory() {
    }

    public static SearchHistory getInstance() {

        return instance;
    }

    public ArrayList<String> getSearchHistoryList() {

        return searchHistoryList;
    }

    public void addSearch(String search) {
        if (!searchHistoryList.contains(search)) {
            searchHistoryList.add(0, search);
        }
        while (searchHistoryList.size() > 5) {
            searchHistoryList.remove(searchHistoryList.size() - 1);
        }

    }
    public void setSearchHistoryList(ArrayList<String> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }
}

