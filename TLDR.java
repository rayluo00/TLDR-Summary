/* TLDR.java
 * Author: Raymond Weiming Luo
 *
 *
 *
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class TLDR {

    /****************************************** CHECK QUOTATIONS **************************************************/
    // Return boolean to check if there is only a singular quote in the sentence.
    public static boolean checkMissingQuotes (String sentence) {
        boolean completeQuote = true;
        int sentenceLength = sentence.length();

        for (int i = 0; i < sentenceLength; i++) {
            if (sentence.charAt(i) == '"') {
                if (completeQuote)
                    completeQuote = false;
                else
                    completeQuote = true;
            }
        }
        return completeQuote;
    }

    /****************************************** CHECK FOR TITLES **************************************************/
    // Return boolean to check if there's a title at the end.
    public static boolean checkSentenceWithTitles (String currentSentence, int sentenceLength) {
        int substringStart;
        String titleCheck;
        String[] titleArray= {"Mr", " Mr", "Ms", " Ms", "Mrs", "Dr", " Dr", " Lt", "Lt"};

        if (sentenceLength < 3)
            substringStart = 2;
        else
            substringStart = 3;

        titleCheck = currentSentence.substring((sentenceLength-substringStart), (sentenceLength));
        for (String title : titleArray) {
            if (titleCheck.equals(title)) {
                return false;
            }
        }
        return true;
    }

    /******************************************* FORM SENTENCE ****************************************************/
    // Form sentences using a regex to split at every period. Check and match any
    // sentences that should match a quote. A quote can have multiple sentences,
    // but should be one relevant sentence. Then match error splits, (ie. Mr. or Mrs.).
    public static void formSentences (String webData, ArrayList<String> sentences) {
        int sentenceCount;
        int sentenceLength;
        char period_or_quote;
        boolean fullQuote = true;
        boolean inQuote = false;
        boolean hasEndingTitle = false;
        String sentence = "";
        String titleSentence = "";
        String currentSentence;
        ArrayList<String> currSentences = new ArrayList<>(Arrays.asList(webData.split("((?<=[a-z])|(?<=[0-9]))\\.\\s+")));

        sentenceCount = currSentences.size();

        for (int i = 0; i < sentenceCount; i++) {
            currentSentence = currSentences.get(i);
            sentenceLength = currentSentence.length();
            period_or_quote = currentSentence.charAt(sentenceLength-1);

            if (period_or_quote != '.' & period_or_quote != '"') {
                currSentences.set(i, (currentSentence+="."));
            }

            /* Combine split quotes. */
            if (!checkMissingQuotes(currentSentence)) {
                if (fullQuote) {
                    sentence = currentSentence;
                    currSentences.set(i, "");
                    fullQuote = false;
                    inQuote = true;
                } else {
                    sentence+=" "+currentSentence;
                    currSentences.add(sentence);
                    currSentences.set(i, "");
                    fullQuote = true;
                    inQuote = false;
                }
            }
            else if (!fullQuote && inQuote) {
                sentence+=" "+currentSentence;
                currSentences.set(i, "");
            }

            /* Combine sentences ending with titles. Ex. Mr. Smith, Dr. Smith, Mrs. Smith */
            if (!checkSentenceWithTitles(currentSentence, sentenceLength)) {
                if (!hasEndingTitle) {
                    titleSentence = currentSentence;
                    currSentences.set(i, "");
                    hasEndingTitle = true;
                } else {
                    titleSentence += " "+currentSentence;
                    currSentences.set(i, "");
                }
            }
            else if (hasEndingTitle) {
                currSentences.set(i, "");
                titleSentence += " "+currentSentence;
                if (checkSentenceWithTitles(titleSentence, titleSentence.length())) {
                    hasEndingTitle = false;
                    currSentences.add(titleSentence);
                }
            }
        }

        sentenceCount = currSentences.size();

        for (int i = 0; i < sentenceCount; i++) {
            sentence = currSentences.get(i);
            if (!sentence.equals("")) {
                sentences.add(sentence);
            }
        }
    }

    /******************************************* SPLIT SENTENCE ***************************************************/
    // Split the sentences in an article to get the words used and how often the words are used.
    public static void splitSentence (String sentence, ArrayList<String> article,
				      HashMap<String, Integer> articleData) {
        ArrayList<String> sentenceList = new ArrayList<>(Arrays.asList(sentence.replaceAll("&|\"|,|\\.|:|\'|/|\\\\|\\|", "").split("\\s")));
        sentenceList.add("\n");

        for (String word : sentenceList) {
            if (!word.equals("")) {
                article.add(word);

                if (articleData.containsKey(word))
                    articleData.replace(word, (articleData.get(word)+1));
                else
                    articleData.put(word, 1);
            }
        }
    }

    /********************************************** QUICKSORT *****************************************************/
    private static void swap (WordData[] wordArray, int OLD, int NEW) {
        WordData temp;

        temp = wordArray[OLD];
        wordArray[OLD] = wordArray[NEW];
        wordArray[NEW] = temp;
    }

    private static int partition (WordData[] wordArray, int low, int high) {
        int pivot = wordArray[high].getWordCount();
        int i = low;

        for (int j = low; j <= high-1; j++) {
            if (wordArray[j].getWordCount() <= pivot) {
                swap(wordArray, i, j);
                i++;
            }
        }
        swap(wordArray, i, high);
        return i;
    }

    // Perform quicksort algorithm to sort the words by increasing reference to the word.
    private static void quicksort (WordData[] wordArray, int low, int high) {
        int pivot;

        if (low < high) {
            pivot = partition(wordArray, low, high);
            quicksort(wordArray, low, pivot-1);
            quicksort(wordArray, pivot+1, high);
        }
    }

    /******************************************* SLOPE RELEVANT ***************************************************/
    // Retrieve relevant data from the wordArray which contains all words used in the
    // article using slope to remove words that doesn't specify the topic of the article.
    public static WordData[] computeSlopeRelevance (WordData[] wordArray, int arraySize) {
        int slope;
        int index = 0;
        boolean relevantDataFlag = false;
        WordData[] newWordArray = new WordData[arraySize];

        for (int i = 0; i < arraySize-1; i++) {
            slope = (wordArray[i+1].getWordCount() - wordArray[i].getWordCount()) / ((i+2)-(i+1));
            //System.out.println(slope);

            if (slope > 0 && slope < 2 && !relevantDataFlag)
                relevantDataFlag = true;
            else if (relevantDataFlag && slope > 2)
                relevantDataFlag = false;

            if (relevantDataFlag) {
                newWordArray[index] = wordArray[i];
                index++;
            }
        }
        return newWordArray;
    }

    /******************************************* REMOVE COMMONS ***************************************************/
    // Remove the commonly used English words from the wordArray, common words are used
    // very frequently and will interfere with the relevance comparisons with other commonly
    // used words from the article that can help summarize the given article.
    public static WordData[] removeCommonWords (WordData[] wordArray, int arraySize) {
        String word;
        int commonWordCount;
        int index = 0;
        WordData[] newWordArray = new WordData[arraySize];
        String[] commonWords = {
            "the","be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he",
	    "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or",
	    "an", "my", "all", "would", "there", "their", "what", "so", "if", "about", "who", "get", "which", "go",
	    "me" , "when", "make", "can", "like", "time", "no", "just", "him", "know", "take", "into", "your",
	    "some", "could", "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over",
	    "also", "after", "use", "how", "our", "because", "any", "these", "us", "was", "been", "has", "did",
	    "many", "mrs", "mr", "said", "had", "you're", "while"};

        commonWordCount = commonWords.length;
        for (int i = 0; i < arraySize; i++) {
            if (wordArray[i] == null)
                break;

            word = wordArray[i].getWord();
            for (int j = 0; j < commonWordCount; j++) {
                if (commonWords[j].equals(word.toLowerCase()))
                    break;
                if ((j+1) == commonWordCount)
                    newWordArray[index++] = wordArray[i];
            }
        }
        return newWordArray;
    }

    // Using the most relevant words in the wordArray, choose the top words that are the most
    // relevant and rank each sentence based on the relevance of the word to retrieve sentences
    // that gives main ideas of the article.
    private static void findRelevantSentences (ArrayList<String> sentences, WordData[] wordArray) {
        int i;
        int maxRef = 0;
        int totalRef = 0;
        int currentRef;
        int wordCount = 0;
        WordData[] keyWords = new WordData[10];
        ArrayList<SentenceData> relevantSentence = new ArrayList<>();
        ArrayList<SentenceData> summary = new ArrayList<>();

        while (wordArray[wordCount] != null) {
            wordCount++;
        }

        wordCount--;
        for (i = 0; i < 10; i++) {
            if (wordArray[wordCount] != null) {
                keyWords[i] = wordArray[wordCount--];
                System.out.println(keyWords[i].getWord());
            }
        }

        for (i = 0; i < sentences.size(); i++) {
            SentenceData sentenceData = new SentenceData();
            sentenceData.initSentence(sentences.get(i));

            for (int j = 0; j < 10; j++) {
                if (keyWords[j] == null)
                    break;
                else {
                    if (sentenceData.getSentence().contains(keyWords[j].getWord())) {
                        currentRef = sentenceData.getReference()+1;

                        totalRef += currentRef;
                        if (currentRef > maxRef)
                            maxRef = currentRef;

                        sentenceData.setReference(currentRef);
                    }
                }
            }
            relevantSentence.add(sentenceData);
        }

        System.out.println("MAXREF: "+maxRef+" | AVGREF: "+(totalRef/sentences.size())+"\n\n");
        for (SentenceData x : relevantSentence) {
            System.out.println(x.getSentence()+" | "+x.getReference()+"\n");
        }

    }

    // Main function that parses the website to retrieve the document as multiple strings. Calls
    // all the required functions needed to sort and compute the relevant words needed to
    // summarize the article.
    public static void main (String[] args) {
        int index = 0;
        int arraySize;
        ArrayList<String> article = new ArrayList<>();
        ArrayList<String> sentences = new ArrayList<>();
        HashMap<String, Integer> articleData = new HashMap<>();
        String website = "http://www.pcworld.com/article/3094797/analytics/googles-ai-is-learning-how-to-save-your-life.html";
        //String website = "http://www.nytimes.com/2016/07/22/business/media/roger-ailes-fox-news.html?_r=0";
        //String website = "http://www.nytimes.com/2016/07/23/us/politics/tim-kaine-hillary-clinton-vice-president.html";

        try {
            URL webURL = new URL(website);
            Document webDoc = Jsoup.parse(webURL, 3000);
            String title = webDoc.title();
            Elements webData = webDoc.select("p");

            for (Element p : webData) {
                formSentences(p.text(), sentences);
                splitSentence(p.text(), article, articleData);
            }

            arraySize = articleData.size();
            WordData[] wordArray = new WordData[arraySize];
            for (String w : articleData.keySet()) {
                WordData currentWord = new WordData(w, articleData.get(w));
                wordArray[index++] = currentWord;
            }

            quicksort(wordArray, 0, articleData.size()-1);
            wordArray = computeSlopeRelevance(wordArray, arraySize);
            wordArray = removeCommonWords(wordArray, arraySize);

            findRelevantSentences(sentences, wordArray);

        } catch (MalformedURLException e) {
            System.out.println("ERROR : Invalid URL.");
        } catch (IOException e) {
            System.out.println("ERROR : Unable to parse website.");
        }
    }
}
