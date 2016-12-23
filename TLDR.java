/* TLDR.java
 *
 * TLDR - Summarize Article program
 * Author: Raymond Weiming Luo
 *
 * This program receives the document from a news article website using JSoup.
 * Then summarizes the relevant information into a  about a paragraph or two
 * depending on the original length of the original document. The program has
 * a regex to split words and determines the relevant keywords for the summary.
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class TLDR {
 
    /***********************************************************************************************
     * Return boolean to check if there is only a singular quote in the sentence.
     */
    public static boolean CheckMissingQuotes (String sentence) {
        int sentenceLength = sentence.length();
        boolean completeQuote = true;

        for (int i = 0; i < sentenceLength; i++) {
            if (sentence.charAt(i) == '"') {
                if (completeQuote) {
                    completeQuote = false;
                } else {
                    completeQuote = true;
                }
            }
        }
        return completeQuote;
    }

    /***********************************************************************************************
     * Return boolean to check if there's a title at the end.
     */
    public static boolean CheckSentenceWithTitles (String currentSentence, int sentenceLength) {
        int substringStart;
        String titleCheck;
        String[] titleArray= {"Mr", " Mr", "Ms", " Ms", "Mrs", "Dr", " Dr", " Lt", "Lt"};

        if (sentenceLength < 3) {
            substringStart = 2;
        } else {
            substringStart = 3;
        }

        if ((sentenceLength - substringStart) > 0) {
            titleCheck = currentSentence.substring((sentenceLength - substringStart), (sentenceLength));
            for (String title : titleArray) {
                if (titleCheck.equals(title)) {
                    return false;
                }
            }
        }
        return true;
    }

    /***********************************************************************************************
     * Form sentences using a regex to split at every period. Check and match any
     * sentences that should match a quote. A quote can have multiple sentences,
     * but should be one relevant sentence. Then match error splits, (ie. Mr. or Mrs.).
     */
    public static void FormSentences (String webData, ArrayList<String> sentences) {
        int sentenceCount;
        int sentenceLength;
        char period_or_quote;
        String sentence = "";
        String titleSentence = "";
        String currentSentence;
        boolean inQuote = false;
        boolean fullQuote = true;
        boolean hasEndingTitle = false;

        ArrayList<String> currSentences = new ArrayList<>(Arrays.asList
			(webData.split("((?<=[a-z])|(?<=[0-9]))\\.\\s+")));

        sentenceCount = currSentences.size();

        for (int i = 0; i < sentenceCount; i++) {
            currentSentence = currSentences.get(i);
            sentenceLength = currentSentence.length();

            if (sentenceLength == 0) {
                //period_or_quote = '\u0000';
                continue;
            } else {
                period_or_quote = currentSentence.charAt(sentenceLength - 1);
            }

            if (period_or_quote != '.' & period_or_quote != '"') {
                currSentences.set(i, (currentSentence+="."));
            }

            // Combine split quotes.
            if (!CheckMissingQuotes(currentSentence)) {
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

            // Combine sentences ending with titles. Ex. Mr. Smith, Dr. Smith, Mrs. Smith
            if (!CheckSentenceWithTitles(currentSentence, sentenceLength)) {
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
                if (CheckSentenceWithTitles(titleSentence, titleSentence.length())) {
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

    /***********************************************************************************************
     * Split the sentences in an article to get the words used and how often the words are used.
     */
    public static void SplitSentence (String sentence, ArrayList<String> article,
                                      HashMap<String, Integer> articleData) {

        ArrayList<String> sentenceList = new ArrayList<>(Arrays.asList(
                sentence.replaceAll("&|\"|,|\\.|:|\'|/|\\\\|\\|", "").split("\\s")));

        sentenceList.add("\n");

        for (String word : sentenceList) {
            if (!word.equals("")) {
                article.add(word);

                if (articleData.containsKey(word)) {
                    articleData.replace(word, (articleData.get(word) + 1));
                } else {
                    articleData.put(word, 1);
                }
            }
        }
    }

    /***********************************************************************************************
     * Swap the WordData object in the array from the OLD position to the NEW position.
     */
    private static void Swap (WordData[] wordArray, int OLD, int NEW) {
        WordData temp;

        temp = wordArray[OLD];
        wordArray[OLD] = wordArray[NEW];
        wordArray[NEW] = temp;
    }

    /***********************************************************************************************
     * Partition the array for quicksort and swap the pivot (i) with the current index (j) if
     * the current index is less than the pivot.
     */
    private static int Partition (WordData[] wordArray, int low, int high) {
        int i = low;
        int pivot = wordArray[high].getWordCount();

        for (int j = low; j <= high-1; j++) {
            if (wordArray[j].getWordCount() <= pivot) {
                Swap(wordArray, i, j);
                i++;
            }
        }
        Swap(wordArray, i, high);
        return i;
    }

    /***********************************************************************************************
     * Perform quicksort algorithm to sort the words by increasing reference to the word.
     */
    private static void Quicksort (WordData[] wordArray, int low, int high) {
        int pivot;

        if (low < high) {
            pivot = Partition(wordArray, low, high);
            Quicksort(wordArray, low, pivot-1);
            Quicksort(wordArray, pivot+1, high);
        }
    }

    /***********************************************************************************************
     * Retrieve relevant data from the wordArray which contains all words used in the
     * article using slope to remove words that doesn't specify the topic of the article.
     */
    public static WordData[] ComputeSlopeRelevance (WordData[] wordArray, int arraySize) {
        int slope;
        int index = 0;
        boolean relevantDataFlag = false;
        WordData[] newWordArray = new WordData[arraySize];

        for (int i = 0; i < arraySize-1; i++) {
            slope = (wordArray[i+1].getWordCount() - wordArray[i].getWordCount()) / ((i+2)-(i+1));

            if (slope > 0 && slope < 2 && !relevantDataFlag) {
                relevantDataFlag = true;
            } else if (relevantDataFlag && slope > 2) {
                relevantDataFlag = false;
            }

            if (relevantDataFlag) {
                newWordArray[index] = wordArray[i];
                index++;
            }
        }
        return newWordArray;
    }

    /***********************************************************************************************
     * Remove the commonly used English words from the wordArray, common words are used
     * very frequently and will interfere with the relevance comparisons with other commonly
     * used words from the article that can help summarize the given article.
     */
    public static WordData[] RemoveCommonWords (WordData[] wordArray, int arraySize) {
        int index = 0;
        int commonWordCount;
        String word;
        WordData[] newWordArray = new WordData[arraySize];
        String[] commonWords = {
                "the","be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for",
                "not", "on", "with", "he", "as", "you", "do", "at", "this", "but", "his",
                "by", "from", "they", "we", "say", "her", "she", "or", "an", "my", "all",
                "would", "there", "their", "what", "so", "if", "about", "who", "get",
                "which", "go", "me" , "when", "make", "can", "like", "no", "just", "lt",
                "him", "know", "take", "into", "your", "some", "could", "them", "see",
                "other", "than", "then", "now", "look", "only", "come", "its", "over",
                "also", "after", "use", "how", "our", "because", "any", "these", "us",
                "was", "been", "has", "did", "many", "mrs", "mr", "said", "had", "you're",
                "while", "ms", "dr", "went", "\n", "\t", "is"};

        commonWordCount = commonWords.length;
        for (int i = 0; i < arraySize; i++) {
            if (wordArray[i] == null) {
                break;
            }

            word = wordArray[i].getWord();
            for (int j = 0; j < commonWordCount; j++) {
                if (commonWords[j].equals(word.toLowerCase())) {
                    break;
                }
                if ((j+1) == commonWordCount) {
                    newWordArray[index++] = wordArray[i];
                }
            }
        }
        return newWordArray;
    }

    /**********************************************************************************************
     * Write the document input into the given file. The file logs all the output from the console.
     */
    private static void WriteToFile (String document) {
    	try {
		PrintWriter docWriter = new PrintWriter(new FileWriter("tldr_output.txt", true));
		docWriter.append(document);
		docWriter.close();
	
	} catch (IOException e) {
		System.out.println("ERROR: WriteToFile() unable to write document into file.");
	}
    }

    /**********************************************************************************************
     * Formats the given document to have at most 65 characters per line, wrap around the next line
     * if the line is over 65 characters.
     */
    private static StringBuilder FormatDocument (String document) {
	int textPerLine = 0;
    	StringBuilder documentBuilder = new StringBuilder(document);

	while (textPerLine + 85 < documentBuilder.length() && 
		(textPerLine = documentBuilder.lastIndexOf(" ", textPerLine + 85)) != -1) {

		documentBuilder.replace(textPerLine, textPerLine + 1, "\r\n");	
	}

	return documentBuilder;
    }

    /**********************************************************************************************
     * Using the most relevant words in the wordArray, choose the top words that are the most
     * relevant and rank each sentence based on the relevance of the word to retrieve sentences
     * that gives main ideas of the article.
     */
    private static void FindRelevantSentences (ArrayList<String> sentences, WordData[] wordArray) {
        int i;
        int avgRef;
        int maxRef = 0;
        int totalRef = 0;
        int wordCount = 0;
        int currentRef;
        int summarySize;
        int sentenceCount = sentences.size();
        int tempRelevantRef;
        int currRelevantRef;
        float decreasePercentage;
	String stats;
	String summaryText = "";
        String tempSentence;
        String currRelevantSentence;
	StringBuilder summaryBuilder;
        WordData[] keyWords = new WordData[8];
        ArrayList<SentenceData> relevantSentence = new ArrayList<>();
        ArrayList<String> summary = new ArrayList<>();

        while (wordArray[wordCount] != null) {
            wordCount++;
        }

        // Sort key words by decreasing word references.
        wordCount--;
        for (i = 0; i < 8; i++) {
            if (wordArray[wordCount] != null) {
                keyWords[i] = wordArray[wordCount--];
                //System.out.println(keyWords[i].getWordCount()+" | "+keyWords[i].getWord());
            }
        }

        // Provide a rank to each sentence by increasing the rank based off
        // the amount of key words the sentence contains.
        for (i = 0; i < sentenceCount; i++) {
            SentenceData sentenceData = new SentenceData();
            sentenceData.initSentence(sentences.get(i));

            for (int j = 0; j < 8; j++) {
                if (keyWords[j] == null) {
                    break;
                } else {
                    if (sentenceData.getSentence().contains(keyWords[j].getWord())) {
                        currentRef = sentenceData.getReference()+1;
                        totalRef += currentRef;

                        if (currentRef > maxRef) {
                            maxRef = currentRef;
                        }

                        sentenceData.setReference(currentRef);
                    }
                }
            }
            relevantSentence.add(sentenceData);
        }

        avgRef = totalRef / sentenceCount;
        //System.out.println("MAXREF: "+maxRef+" | AVGREF: "+avgRef);
        System.out.println("\n=========================== START SUMMARY ===========================");
	WriteToFile("\r\n=========================== START SUMMARY ===========================\r\n");

        for (i = 0; i < sentenceCount; i++) {
            currRelevantRef = relevantSentence.get(i).getReference();
            currRelevantSentence = relevantSentence.get(i).getSentence();

            // Add current sentence if it contains more keywords than the average sentences.
            /*if (currRelevantRef > avgRef) {
                System.out.println(currRelevantRef+" | "+currRelevantSentence);
                relevantSentence.get(i).setInSummaryTrue();
                summary.add(currRelevantSentence);
            }*/

            /**************************************** OR ******************************************/

            // Add the previous relevant sentence to give current sentence more context.
            if (currRelevantRef > avgRef && i > 0) {
                tempSentence = relevantSentence.get(i-1).getSentence();
                tempRelevantRef = relevantSentence.get(i-1).getReference();
                if (tempRelevantRef >= 1 && !relevantSentence.get(i-1).getInSummary()) {
                    //System.out.println(tempRelevantRef+" | "+tempSentence);
		    summaryText += (tempSentence + " ");
                    summary.add(tempSentence);
                }

                //System.out.println(currRelevantRef+" | "+currRelevantSentence);
		summaryText += (currRelevantSentence + " ");
                relevantSentence.get(i).setInSummaryTrue();
                summary.add(currRelevantSentence);
            }
        }

	summaryBuilder = FormatDocument(summaryText);

	System.out.println(summaryBuilder);
	WriteToFile(summaryBuilder.toString());
        System.out.println("============================ END SUMMARY ============================\n");
	WriteToFile("\r\n============================ END SUMMARY ============================\r\n");
        summarySize = summary.size();
        decreasePercentage = sentenceCount - summarySize;
        decreasePercentage = decreasePercentage / sentenceCount * 100;
        stats = String.format("PREV : %d sentences | NEW : %d sentences | COMPRESSION: %.2f%%\r\n", 
			sentenceCount, summarySize, decreasePercentage);
	WriteToFile("\r\n"+stats+"\r\n");
	System.out.println(stats);
    }

    /***********************************************************************************************
     * Main function that parses the website to retrieve the document as multiple strings. Calls
     * all the required functions needed to sort and compute the relevant words needed to
     * summarize the article.
     */
    public static void main (String[] args) {

        int index = 0;
        int arraySize;
	String websiteText = "";
	StringBuilder articleBuilder;
	Scanner inputScanner = new Scanner(System.in);
        ArrayList<String> article = new ArrayList<>();
        ArrayList<String> sentences = new ArrayList<>();
        HashMap<String, Integer> articleData = new HashMap<>();

	try {
	    Path deleteFilePath = Paths.get("tldr_output.txt");
	    Files.delete(deleteFilePath);

	} catch (IOException e) {
	    System.out.println("ERROR: main() file is not found.");
	}

        try {
            System.out.print("Enter website url: ");
	    String website = inputScanner.next();
	    
            URL webURL = new URL(website);
            Document webDoc = Jsoup.parse(webURL, 3000);
            String title = webDoc.title();
            Elements webData = webDoc.select("p");

            System.out.println("\n============================ START ARTICLE ===========================");
	    WriteToFile("============================ START ARTICLE ===========================\r\n");
            for (Element p : webData) {
		websiteText += p.text()+" ";
                FormSentences(p.text(), sentences);
                SplitSentence(p.text(), article, articleData);
            }

	    articleBuilder = FormatDocument(websiteText);
	    System.out.println(articleBuilder);
	    WriteToFile(articleBuilder.toString());
            System.out.println("============================= END ARTICLE ============================\n");
	    WriteToFile("\r\n============================= END ARTICLE ============================\r\n");

            arraySize = articleData.size();
            WordData[] wordArray = new WordData[arraySize];

            for (String w : articleData.keySet()) {
                WordData currentWord = new WordData(w, articleData.get(w));
                wordArray[index++] = currentWord;
            }

            Quicksort(wordArray, 0, articleData.size()-1);
            wordArray = ComputeSlopeRelevance(wordArray, arraySize);
            wordArray = RemoveCommonWords(wordArray, arraySize);

            FindRelevantSentences(sentences, wordArray);
	    WriteToFile("WEBSITE: "+website+"\r\n");
	    WriteToFile("TITLE: "+title+"\r\n");


        } catch (MalformedURLException e) {
            System.out.println("ERROR : main() invalid URL.");
        } catch (IOException e) {
            System.out.println("ERROR : main() unable to parse website.");
        }
    }
}
