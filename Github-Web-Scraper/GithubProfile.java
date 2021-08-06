import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GithubProfile extends Webpage {
    private String username;
    private int numRepositories;
    private int followers;
    private String school;
    private String degree;
    private String schoolsFilename;

    // constructor
    public GithubProfile(String url, String schoolsFilename) throws IOException {
        // load page base
        super(url);
        this.schoolsFilename = schoolsFilename;

        // helper methods to load all data
        this.username = scrapeUsername();
        this.numRepositories = scrapeRepositories();
        this.followers = scrapeFollowers();
        this.school = scrapeSchool();
        this.degree = scrapeDegree();
    }

    // getters
    public String getUsername() { return this.username; }

    public int getNumRepositories() { return this.numRepositories; }

    public int getNumFollowers() { return this.followers; }

    public String getSchool() { return this.school; }

    public String getDegree() { return this.degree; }

    /*
     * helper methods to load data
     * all return their datatypes
     *      * String returns String if there, null otherwise
     *      * int returns int if there, -1 if otherwise
     */
    /* scrapeUsername - this method scrapes the username of an individual from the metadata in their webpage */
    private String scrapeUsername() {
        /*
         * access the head and look for all of the metadata tags
         * look through each tag to see if there is a profile class
         */
        String usernameTag = null;
        for (String tag : getTags(this.getHead())) {
            if (tag.contains("meta") && tag.contains("profile:username")) usernameTag = tag;
        }

        /*
         * increment up to the content piece
         * grab what's between the quotes
         */
        int i = 0;
        while (!usernameTag.startsWith("content=", i)) {
            i++;
        }
        i += "content=\"".length();

        // return the username
        return usernameTag.substring(i).split("\"")[0];
    }

    /* scrapeRepositories - this method scrapes the number of repositories from an individuals webpage */
    private int scrapeRepositories() {
        /*
         * grab the number of repositories in the head
         * written as "___ has x repositories available"
         * split by " " and then grab string if int
         */
        String repositoryTag = null;
        for (String tag : getTags(this.getHead())) {
            if (tag.contains("meta") && tag.contains("name=\"description\"")) repositoryTag = tag;
        }
        int numRepo = -1;
        for (String word : repositoryTag.split(" ")) {
            try {
                numRepo = Integer.parseInt(word);
            } catch(NumberFormatException e) {
                continue;
            }
        }

        // did not scrape from meta data --> get by hand from body
        if (numRepo == -1) {
            // go to where " Repositories " is and step tag by tag to the number
            int i=0;
            while (!this.getBody().startsWith("Repositories", i)) i++;
            while (!this.getBody().startsWith(">", i)) i++;

            // step to the start of the next tag and convert to int
            int j=i;
            while (!this.getBody().startsWith("<", j)) j++;

            String numRepoS = this.getBody().substring(i+1, j);
            if (numRepoS.contains("k")) {
                numRepo = (int)(Double.parseDouble(numRepoS.substring(0, numRepoS.length()-1)) * 1000);
            }
            else numRepo = Integer.parseInt(this.getBody().substring(i+1, j));
        }

        // return the number of repos
        return numRepo;
    }

    /* scrapeFollowers - this method scrapes the number of followers from an individuals webpage */
    private int scrapeFollowers() {
        /*
         * grab the number of followers from their bio
         * go to the div -- <span class="text-bold color-text-primary">1.1k</span>
         * grab the information between the span info and translate it to an actual number
         */

        /*
         * find the link to followers
         * https://github.com/USERNAME?tab=followers
         * hidden in the link tag and slice from there
         */
        String href = "https://github.com/" + this.getUsername() + "?tab=followers";
        int i = 0;
        while (!this.getBody().startsWith(href, i)) i++;

        // the below grabs the correct tag block
        /*
        int j = i;
        while (!this.getHtml().startsWith("</a>", j)) j++;
        System.out.println(this.getHtml().substring(i,j));
        */
        while (!this.getBody().startsWith("<span class=\"text-bold color-text-primary\" >", i)) i++;
        i += "<span class=\"text-bold color-text-primary\">".length()+1;

        int j = i;
        while (!this.getBody().startsWith("<", j)) j++;
        String followers = this.getBody().substring(i,j);

        // parse the string to generate an int
        if (followers.contains("k")) {
            //parse up to the K and multiply by 1000
            return (int)(Double.parseDouble(followers.substring(0, followers.length()-1)) * 1000);
        }
        return Integer.parseInt(followers);
    }

    /* scrapeSchool - this method scrapes the school from an individuals webpage (if exists) */
    private String scrapeSchool() throws IOException {
        /*
         * See if there's an organization the individual works at/is currently studying at
         * If there exists such, grab it and see if it's a university/institute
         *
         * If there does not exist this tag, look to their bio for a university name
         * return the school or a null string depending on result
         * --> in our filter, we will check to see if this string is null before adding
         * a user to the list
         */
        ArrayList<String> targetSchools = populateSchoolsList();
        String schoolTag = null;
        for (String tag : getTags(this.getBody())) {
            if (tag.contains("Organization: ") && tag.contains("worksFor")) {
                schoolTag = tag;
            }
        }
        // if an organization is found
        if (schoolTag != null) {
            int i,j;
            for (i=0; !schoolTag.startsWith(":", i); ) i++;
            for (j=i; !schoolTag.startsWith("\"", j); ) j++;
            schoolTag = schoolTag.substring(i+1,j).trim();
        }
        // if no organization is found -- this is the biography part
        else {
            // grab the bio
            String bio = grabBio();

            // check if the tag is in the targetSchools list
            for (String schoolName : targetSchools) {
                if (bio.contains(schoolName)) schoolTag = schoolName;
                return schoolTag;
            }
        }

        for (String school : targetSchools) {
            if (schoolTag.contains(school)) return schoolTag;
        }

        return null;
    }

    /* scrapeDegree - grab the candidate's degree level if school != null */
    public String scrapeDegree() {
        if (school != null) {
            // grab the bio and degree array
            String bio = grabBio();
            String[] Bachelors = new String[]{"BA", "B.A.", "BS", "B.S.", "Bachelor's"};
            String[] Masters = new String[]{"MA", "M.A.", "MS", "M.S.", "Master's"};
            String[] PhD = new String[]{"PhD", "Ph.D.", "Phd"};

            // check for a degree --> BA --> MS --> PhD
            for (String degreeS : Bachelors) {
                if (bio.contains(degreeS)) return "Bachelor's";
            }

            for (String degreeS : Masters) {
                if (bio.contains(degreeS)) return "Master's";
            }

            for (String degreeS : PhD) {
                if (bio.contains(degreeS)) return "PhD";
            }
        }
        return null;
    }

    /* grabBio - grabs the bio text */
    private String grabBio() {
        // find the bio tag
        int i=0;
        while (!this.getBody().startsWith("p-note user-profile-bio", i)) i++;

        // crawl to and past the next div
        while (!this.getBody().startsWith("<div>", i)) i++;
        i+="<div>".length();

        // grab the text between the open and close tag
        int j=i;
        while (!this.getBody().startsWith("</div>", j)) j++;

        // grab and return the bio
        return this.getBody().substring(i, j);
    }

    /* populateSchoolsList - populates a list of schools for the user */
    private ArrayList<String> populateSchoolsList() throws IOException {
        ArrayList<String> schools = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader(this.schoolsFilename));

        String content;
        while ( (content = in.readLine()) != null) {
            schools.add(content);
        }

        return schools;
    }

    public String toString() {
        return username + ", " + numRepositories + ", " + followers + ", " + school + ", " + degree;
    }
}
