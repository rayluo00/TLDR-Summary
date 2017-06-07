import nltk.data
import string
from nltk.corpus import stopwords
from nltk import FreqDist
from nltk import tokenize
import urllib.request
from bs4 import BeautifulSoup
from sentence_data import SentenceData
from sentence_data import word_count

#####################################################################
def main ():
	content = get_html()
	sentences = split_sentences(content)
	bag_of_words = create_word_bag(content)

	total_wordc = word_count(bag_of_words)
	#print(total_wordc)

	for sent in sentences:
		sent.calc_percentage(total_wordc, bag_of_words)
		#print(sent.percentage,'|',sent.sentence,'\n\n')
	
	sorted_sent = sorted(sentences, key=lambda x: x.percentage, 
							reverse=True)

	for s in sorted_sent:
		print(s.sentence,'\n\n')

#####################################################################
def split_sentences (content):
	sentences = []
	sent_detector = nltk.data.load('tokenizers/punkt/english.pickle')
	sent_tokens = sent_detector.tokenize(content.strip())

	for sent in sent_tokens:
		data = SentenceData()
		data.sentence = sent
		data.words = create_word_bag(sent)
		sentences.append(data)

	return sentences

#####################################################################
def create_word_bag (sentence):
	stopword_list = stopwords.words('english')

	translator = str.maketrans('', '', string.punctuation)
	sentence = sentence.translate(translator)
	all_words = tokenize.word_tokenize(sentence)
	word_bag = FreqDist(word.lower() for word in all_words 
						if word.lower() not in stopword_list)
	return word_bag

#####################################################################
def get_html ():
	content = []

	url = input('Website URL: ')
	html_page = urllib.request.urlopen(url)
	data = html_page.read().decode('utf-8')
	html_page.close()

	parser = BeautifulSoup(data, 'html.parser')
	
	for tag in parser.find_all('p'):
		content.append(tag.text)
		content.append(' ')

	return ''.join(content)

if __name__ == '__main__':
	main()
