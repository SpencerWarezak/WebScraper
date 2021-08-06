import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Webpage {
    private URL url;
    private String html = null;
    private String head = null;
    private String body = null;

    public Webpage(String url) throws IOException {
        // malformed url try-catch
        try { this.url = new URL(url); }
        catch (MalformedURLException e) { this.url = null; }

        // load html
        if (this.url != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.url.openStream()));
            StringBuilder htmlS = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                htmlS.append(line);
            }
            this.html = htmlS.toString();

            this.head = loadTagContents("<head>", "</head>");
            this.body = loadTagContents("<body", "</body>");
        }
    }

    // getters
    public URL getUrl() { return url; }
    public String getHtml() { return html; }
    public String getHead() { return head; }
    public String getBody() { return body; }

    // helpers
    /*
     * loadHead - skip to the <head> tag and load all info until </head>
     */
    public String loadTagContents(String tagOpen, String tagClose) {
        // parse all tags until reaching
        for (int i=0; i<html.length(); ) {
            if (html.startsWith(tagOpen, i)) {
                int j = i;
                while (!html.startsWith(tagClose, j)) {
                    j++;
                }
                j += tagClose.length();
                return html.substring(i, j);
            }
            i++;
        }
        return null;
    }

    public ArrayList<String> getTags(String s) {
        ArrayList<String> tags = new ArrayList<String>();
        for (int i=0; i<s.length(); ) {
            if (s.startsWith("<", i)) {
                int j = i;
                while (!s.startsWith(">", j)) {
                    j++;
                }
                j++;
                tags.add(s.substring(i, j));
            }
            i++;
        }
        return tags;
    }
}