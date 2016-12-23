TLDR Program

Author: Raymond Weiming Luo

----------------------------------------------------------------------------------------------
                                            FILES
----------------------------------------------------------------------------------------------
TLDR.java         - Main java files to create summary from given news article website.
WordData.java     - Object to hold information about the words for keyword analysis.
SentenceData.java - Object to hold sentence and ranking to determine relevant sentences.
jsoup-1.10.1.jar  - Jar file for the Jsoup library used to parse website to string.
Makefile          - Compiles, runs and cleans java, class, and output files. 
tldr_output.txt   - Text file that displays the output of the original article and new 
                    summary. Also shows the size reduction by comparing both documents.

----------------------------------------------------------------------------------------------
                                           OVERVIEW
----------------------------------------------------------------------------------------------
Program used JSoup (version 1.10.1), a Java string parser library to retrieve the article from
and website provided. Program will take the input data, parse the string by splitting it into
sentences and formulate a summary of the whole article by compressing relevant information.
The TLDR program will be able to differentiate between actual sentences and titles (e.g., Mr.,
Ms., Mrs., Dr., PhD., etc...). It will calculate the words that are relevant and added to an
ArrayList to find relevant words for sentence combinations. A text file named 'tldr_output.txt'
will be made to log the original news article and the new summary.
