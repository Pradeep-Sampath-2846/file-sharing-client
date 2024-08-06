package lk.dc.dfs.filesharingapplication.domain.service.core;



import lk.dc.dfs.filesharingapplication.domain.handlers.QueryHitHandler;
import lk.dc.dfs.filesharingapplication.domain.util.ConsoleTable;
import lk.dc.dfs.filesharingapplication.domain.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SearchManager {

    private MessageBroker messageBroker;

    private Map<String, SearchResult> fileDownloadOptions;

    SearchManager(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    Map<String, SearchResult> doSearch(String keyword) {

        Map<String, SearchResult> searchResults
                = new HashMap<String, SearchResult>();

        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();
        queryHitHandler.setSearchResutls(searchResults);
        queryHitHandler.setSearchInitiatedTime(System.currentTimeMillis());

        this.messageBroker.doSearch(keyword);

        System.out.println("Please be patient till the file results are returned ...");

        try {
            Thread.sleep(Constants.SEARCH_TIMEOUT);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //printSearchResults(searchResults);
        this.clearSearchResults();
        fileDownloadOptions = Map.copyOf(searchResults);
        //return fileDownloadOptions.size();
        return searchResults;
    }


    private void clearSearchResults() {
        QueryHitHandler queryHitHandler = QueryHitHandler.getInstance();

        queryHitHandler.setSearchResutls(null);
    }

//    private void printSearchResults(Map<String, SearchResult> searchResults) {
//
//        System.out.println("\nFile search results : ");
//
//        ArrayList<String> headers = new ArrayList<String>();
//        headers.add("Option No");
//        headers.add("FileName");
//        headers.add("Source");
//        headers.add("QueryHit time (ms)");
//        headers.add("Hop count");
//
//        ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();
//
//        int fileIndex = 1;
//
//        this.fileDownloadOptions = new HashMap<Integer, SearchResult>();
//
//        for (String s : searchResults.keySet()) {
//            SearchResult searchResult = searchResults.get(s);
//            this.fileDownloadOptions.put(fileIndex, searchResult);
//
//            ArrayList<String> row1 = new ArrayList<String>();
//            row1.add("" + fileIndex);
//            row1.add(searchResult.getFileName());
//            row1.add(searchResult.getAddress() + ":" + searchResult.getPort());
//            row1.add("" + searchResult.getTimeElapsed());
//            row1.add("" + searchResult.getHops());
//
//            content.add(row1);
//
//            fileIndex++;
//        }
//
//        if (fileDownloadOptions.size() == 0) {
//            System.out.println("Sorry. No files are found!!!");
//
//            return;
//        }
//
//        ConsoleTable ct = new ConsoleTable(headers, content);
//        ct.printTable();
//
//    }

    public SearchResult getFileDetails(String fileName) {
        return this.fileDownloadOptions.get(fileName);
    }
}
