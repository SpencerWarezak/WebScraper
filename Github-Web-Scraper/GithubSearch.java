import java.io.IOException;
import java.util.ArrayList;

public class GithubSearch {
    /* getUrl() - grabs the URL created from the search query */
    public static String getUrl(String search, String language, int pageDepth) {
        String[] searchList = search.split(" ");
        StringBuilder searchString = new StringBuilder();

        for (String input : searchList) {
            searchString.append(input).append("+");
        }
        searchString.replace(searchString.length()-1, searchString.length(), "");

        if (language.equals("C++")) language = "C%2B%2B";
        if (language.equals("all")) return "https://github.com/search?p=" + pageDepth + "&q=" + searchString + "&type=Users";

        return "https://github.com/search?p=" + pageDepth + "&q=" + searchString + "&type=Users" + "&" + language;
    }

    /* grabs the next webpage linked */
    public static Webpage getNextPage(Webpage currPage, String search, int pageDepth, String language) throws IOException {
        /*
         * go to the tag w next_page in it and grab the href if it's there
         * if there is no href, return null
         */
        for (String tag : currPage.getTags(currPage.getHtml())) {
            // disabled next page
            if (tag.contains("class=\"next_page disabled\"")) return null;
        }
        // contains tag
        return new Webpage(getUrl(search, language, pageDepth));
    }

    /* getPageUsers - gets all of the users on a page */
    public static ArrayList<String> getPageUsers(Webpage currPage) {
        // initialize the list to be returned
        ArrayList<String> userURLs = new ArrayList<String>();
        for (String tag : currPage.getTags(currPage.getBody())) {
            if (tag.contains("<a class=\"color-text-secondary\" data-hydro-click")) {
                userURLs.add("https://github.com/" + GithubSearch.grabHref(tag));
            }
        }
        return userURLs;
    }

    public static String grabHref(String tag) {
        int i=0,j;
        while (!tag.startsWith("href=\"/", i)) i++;
        i+="href=\"/".length();
        j=i;
        while (!tag.startsWith("\"", j)) j++;
        return tag.substring(i, j);
    }
}
