package com.photo.editor.snapstudio.AiSearch;

import android.app.Activity;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class AppAsyncTask {

    private static final String IMAGE_SEARCH = "https://www.google.com/search?tbm=isch&tbs=isz:m&q=";
    private static final String YandexBase = "https://yandex.com/images/search?text=";

    public static class SearchResult extends AsyncTask {

        Activity activity;
        AppInterfaces.SearchResult searchResult;
        String search;

        Elements selectedDiv = new Elements();

        public SearchResult(Activity activity, AppInterfaces.SearchResult searchResult, String search) {
            this.activity = activity;
            this.searchResult = searchResult;
            this.search = search;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document document = Jsoup.connect(YandexBase + search).get();
                selectedDiv = document.select("div[class= serp-controller__content]").select("div[class=serp-item__preview]");
                System.out.println("image link>>>>>>>>> " + selectedDiv);
            } catch (IOException e) {
                System.out.println("scrapping crashed" + e.getMessage());
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            searchResult.getImageElements(selectedDiv);
        }

    }

    public static class SearchResultG extends AsyncTask {

        Activity activity;
        AppInterfaces.SearchResult searchResult;
        String search;

        Elements selectedDiv = new Elements();

        public SearchResultG(Activity activity, AppInterfaces.SearchResult searchResult, String search) {
            this.activity = activity;
            this.searchResult = searchResult;
            this.search = search;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Document document = Jsoup.connect(IMAGE_SEARCH + search).get();
                selectedDiv = document.select("div[class=bRMDJf islir]");
                System.out.println("image link>>>>>>>>> " + selectedDiv);
            } catch (IOException e) {
                System.out.println("scrapping crashed" + e.getMessage());
            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            searchResult.getImageElements(selectedDiv);
        }

    }


   /* public static List<String> getImageSearchResult(String search) {
        List<String> list = new ArrayList<>();

        try {
            String url = IMAGE_SEARCH + search;
            System.out.println("\n\nRequest: " + url);

            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:110.0) Gecko/20100101 Firefox/110.0");

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            Document doc = Jsoup.parse(response.toString());
            Elements q = doc.select("img");

            System.out.println("\n" + q.size());

            for (Element i : q) {
                String link = i.attr("data-src");
                if (link == null || link.isEmpty()) {
                    link = i.attr("src");
                }
                if (link != null &&
                        link.contains("http") &&
                        !link.contains("favicon") &&
                        !link.contains("ui/v1") &&
                        !link.contains("fonts.gstatic")) {
                    System.out.println("\nimageUrl: " + link);
                    list.add(link);
                }
            }
            System.out.println("List Element>>>>>"+list);

        } catch (IOException e) {
            System.err.println("\ngetImageSearchResultE: " + e.getMessage());
        }

        return list;
    }
*/
}
