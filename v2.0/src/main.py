from __future__ import print_function
import nltk.data
import string
from nltk.corpus import stopwords
from nltk import FreqDist
from nltk import tokenize
import urllib.request
from bs4 import BeautifulSoup
from sentence_data import SentenceData
from sentence_data import word_count
from flask import Flask, request, render_template, jsonify, json, Response
import sys
from flask_cors import CORS, cross_origin

app = Flask(__name__)
CORS(app)

summary = ''

#####################################################################
def main (jsdata):
	content = get_html(jsdata)
	sentences = split_sentences(content)
	bag_of_words = create_word_bag(content)

	total_wordc = word_count(bag_of_words)
	print(sorted(bag_of_words.items(), key=lambda x: x[1], reverse=True), file=sys.stderr)

	for sent in sentences:
		sent.calc_percentage(total_wordc, bag_of_words)
	
	sorted_sent = sorted(sentences, key=lambda x: x.percentage, 
							reverse=True)

	summary = ''
	i = 0
	for s in sorted_sent:
		if i > 7:
			break
		#print(s.sentence,'\n\n', file=sys.stderr)
		summary += (s.sentence + ' ')
		i += 1

	return summary

#####################################################################
@app.route('/', methods=['POST'])
def get_post_data ():
	global summary

	if request.method == 'POST':
		jsdata = request.get_json()
		print('\nPOST: ',jsdata['url'],'\n', file=sys.stderr)
		summary = main(jsdata)

	return 'done'

@app.route('/getmethod', methods=['GET'])
def ajax ():
	global summary
	print('\nGET: SEND\n', file=sys.stderr)
	return Response(json.dumps(summary))

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
def get_html (jsdata):
	content = []

	#url = input('Website URL: ')
	url = jsdata['url']
	html_page = urllib.request.urlopen(url)
	data = html_page.read().decode('utf-8')
	html_page.close()

	parser = BeautifulSoup(data, 'html.parser')
	
	for tag in parser.find_all('p'):
		content.append(tag.text)
		content.append(' ')

	return ''.join(content)

if __name__ == '__main__':
	#main()
	app.run(host='0.0.0.0')
