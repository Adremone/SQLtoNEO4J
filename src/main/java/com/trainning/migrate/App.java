package com.trainning.migrate;

public class App {
    public static void main(String[] args) throws Exception {
        String directory = "src/main/resources/entities";
        // add gui here
        QueryFetcher.makeQueries(directory);
        QueryFetcher.fetchCSV();
    }
}
