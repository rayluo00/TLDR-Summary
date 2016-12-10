public class SentenceData {
    private String sentence;
    private int reference;

    public void initSentence (String inputSentence) {
        this.sentence = inputSentence;
        this.reference = -1;
    }

    public void setReference (int inputReference) {
        this.reference = inputReference;
    }
}
