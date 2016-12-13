/* SentenceData.java
 *
 * TLDR - Summarize Article program
 * Author: Raymond Weiming Luo
 *
 * Object to hold the sentence and the keyword references in the sentence.
 * The greater the reference count means the more relevant the sentence is
 * to summarizing the article.
 */

public class SentenceData {

    private String sentence;
    private int reference;

    public void initSentence (String inputSentence) {
        this.sentence = inputSentence;
        this.reference = 0;
    }

    public String getSentence () { return this.sentence; }

    public void setReference (int inputReference) { this.reference = inputReference; }

    public int getReference () { return this.reference; }
}
