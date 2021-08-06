# README.md - GitHub Web Scraper
### Author - Spencer Warezak
### Description:
This program scrapes profiles from GitHub based on command line inputs made by the user. It assumes that all command line
syntax is input correctly, though it does minor error checking to ensure that certain inputs (or lackthereof) do not crash
the program. After scraping for a certain search query, the program will generate a `.csv` file with profile information
that will allow the user to easily assess and reach out to their desired profiles. The filters are:
- Number of Repositories
- Number of Followers
- School Attended/Attending
- Degree Level (Bachelor's, Master's, PhD)
- Programming Language (C, C++, Java, Python, JavaScript, PHP, etc.)

For large searches, this program may take extended periods of time to finish running, as GitHub will flag too many requests
from a given IP address to its servers. Thus, for every profile viewed, there is a 1 second delay placed on the next search,
and for every search page viewed, there is a 7 second delay placed.

### Usage
>__"all"__ for [language] or [degree] denotes no filter on coding language or degree level

The command line syntax is as follows:
`$: java WebScraper.java ["query"] [path/schoolsFile.txt] [language] [numRepositories] [numFollowers] [degree] [initalPageNumber] [pageCrawlDepth] 
[path/outputFile.csv]`

#### Examples
1) `java WebScraper.java "Wang" targetSchools.txt all 10 50 all 1 30 scrapeOutput1.csv`
2) `java WebScraper.java "Ye" targetSchools.txt C++ 10 200 PhD 1 15 scrapeOutput2.csv`
3) `java WebScraper.java "Mao" targetSchools.txt Python 75 50 Bachelor's 25 50 scrapeOutputMao.csv`
4) `java WebScraper.java "John Smith" targetSchools.txt JavaScript 35 500 Master's 10 10 scrapeOutput3.csv`
5) `java WebScraper.java "John Smith C++" targetSchools.txt all 10 25 PhD 20 40 scrapeOutput4.csv`
6) `java WebScraper.java "Spencer" targetSchools.txt C 10 20 all 1 50 scrapeOutput5.csv`

Make sure you run this program in your terminal with all the files located in the same directory.
Happy Sourcing!