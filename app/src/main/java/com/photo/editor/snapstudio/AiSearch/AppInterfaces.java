package com.photo.editor.snapstudio.AiSearch;

import org.jsoup.select.Elements;

public class AppInterfaces {

    public interface SearchResult {
        void getImageElements(Elements scrapedElementsList);
        void getSingleElementAsString(String singleElementAsString);
    }

    public interface SearchResultOnClick{

        void getImageUrl(String url);

        void downloadImage(String url);

        void editImage(String url);

    }

    public interface MoreBtnOnClick{

        void getMoreBtn(String btn);

    }
}
