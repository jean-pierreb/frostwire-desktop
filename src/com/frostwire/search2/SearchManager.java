package com.frostwire.search2;

public final class SearchManager {

    public void perform(SearchPerformer p) {
        p.perform();
    }

    public static void main(String[] args) {

        SearchManager m = new SearchManager();
        m.perform(new SearchPerformer() {
            @Override
            public void perform() {
                System.out.println("New framework");
            }
        });
    }
}
