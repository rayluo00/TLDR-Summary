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
