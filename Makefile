make:
	javac -cp jsoup-1.10.1.jar TLDR.java WordData.java SentenceData.java
	java -cp .:jsoup-1.10.1.jar TLDR

clean:
	$(RM) *.class , *# , *~
