import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WebScraper {
    /*
     * main
     *      args are as follows:
     *          1) "Search Query" -- must be in quotes
     *          2) path to target schools list wanted
     *          3) language
     *          4) number of repos
     *          5) followers
     *          6) degree level
     *          7) initial page depth
     *          8) max depth of crawl -- avoid too many requests to github
     *          9) output file path
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        // program runtime
        long startTime = System.nanoTime();

        // grab the search query, path to target schools, etc.
        String query = (args[0] != null) ? args[0] : null;
        String collegeList = (args[1] != null) ? args[1] : null;
        String language = (args[2] != null) ? args[2] : null;
        int numRepos = (args[3] != null && Integer.parseInt(args[3]) >= 0) ? Integer.parseInt(args[3]) : -1;
        int followers = (args[4] != null && Integer.parseInt(args[4]) >= 0) ? Integer.parseInt(args[4]) : -1;
        String degree = (args[5] != null) ? args[5] : null;
        int initPage = (args[6] != null && Integer.parseInt(args[6]) > 0) ? Integer.parseInt(args[6]) : -1;
        int depth = (args[7] != null && Integer.parseInt(args[7]) > 0) ? Integer.parseInt(args[7]) : -1;
        String outputFile = (args[8] != null) ? args[8] : null;

        // error checking
        assert query != null : "must input a query and inside of quotations \" \" as the first argument";
        assert collegeList != null : "must input a proper path to a list of target schools as the second argument";
        assert language != null : "must input a proper language as the third argument. Input \"all\" for all languages";
        assert numRepos != -1 : "must input a minimum number of repos as the fourth argument";
        assert followers != -1 : "must input a minimum number of followers as the fifth argument";
        assert degree != null : "must input a proper degree level (Bachelor's, Master's, PhD) or \"all\" as the sixth " +
                "argument";
        assert initPage != -1 : "must input an initial depth as the seventh argument";
        assert depth != -1 : "must input a max depth of crawl as the eighth argument";
        assert outputFile != null : "must input an appropriate output file name as the ninth argument";

        // initialize the search and grab the first page, profiles list, output file writer
        FileWriter out = new FileWriter(outputFile, true);
        Webpage currPage = new Webpage(GithubSearch.getUrl(query, language, initPage));

        out.write("Username, Number of Repositories, Followers, School, Degree, Language, Link\n");

        // loop through all of pages
        int i = initPage;
        int j = 0;
        while (currPage != null && i < (initPage + depth)) {
            ArrayList<String> pageUsers = GithubSearch.getPageUsers(currPage);
            // check all of the users on the page to see if they are valid
            for (String pageUser : pageUsers) {
                System.out.println("Visiting " + pageUser + "!");
                GithubProfile userProfile = new GithubProfile(pageUser, collegeList);

                // validate the user
                if (userProfile.getNumRepositories() >= numRepos && userProfile.getNumFollowers() >= followers &&
                        userProfile.getSchool() != null) {
                    if (!degree.equals("all")) {
                        if (userProfile.getDegree() != null) if (userProfile.getDegree().equals(degree)) {
                            out.write(userProfile + ", " + language + ", " + "https://github.com/" + userProfile.getUsername() + "\n");
                            j++;
                            System.out.println("Profile " + j + " Added!");
                        }
                    }
                    else {
                        out.write(userProfile + ", " + language + ", " + "https://github.com/" + userProfile.getUsername() + "\n");
                        j++;
                        System.out.println("Profile " + j + " Added!");
                    }
                }
                System.out.println("Left " + pageUser + "!");
                System.out.println("");
                Thread.sleep(1500);
            }
            System.out.println("Page " + i + " Scraped!");
            System.out.println("");
            i++;
            Thread.sleep(10000);
            currPage = GithubSearch.getNextPage(currPage, query, i, language);
        }
        out.close();

        double secondsElapsed = (double) ((System.nanoTime() - startTime) / 1_000_000_000);
        System.out.println("Done! Scraping completed in " + secondsElapsed / 60 + " minutes!");
    }
}
