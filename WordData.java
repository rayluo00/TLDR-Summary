/**
 * Created by Ray Luo on 7/30/2016.
 */
public class WordData {

    private String word;
    private int wordCount;

    public WordData (String inputWord, int inputWordCount) {
        this.word = inputWord;
        this.wordCount = inputWordCount;
    }

    public String getWord () {
        return this.word;
    }

    public int getWordCount () {
        return this.wordCount;
    }

}
