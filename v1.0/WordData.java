/* WordData.java
 *
 * TLDR - Summarize Article program
 * Author: Raymond Weiming Luo
 *
 * Object to hold the word and amount of times it was referenced
 * in the article. The greater the wordCount means the more
 * frequent the word is used in the article.
 */
public class WordData {

    private String word;
    private int wordCount;

    public WordData (String inputWord, int inputWordCount) {
        this.word = inputWord;
        this.wordCount = inputWordCount;
    }

    public String getWord () { return this.word; }

    public int getWordCount () { return this.wordCount; }

}
