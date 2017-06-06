import nltk.data
import urllib.request
from bs4 import BeautifulSoup

def main ():
	data = get_html()
	content = parse_html(data)
	#print(content)
	sentences = split_sentences(content)

	for s in sentences:
		print(s,'\n')

def split_sentences (content):
	sent_detector = nltk.data.load('tokenizers/punkt/english.pickle')
	return sent_detector.tokenize(content.strip())

def get_html ():
	url = input('Website URL: ')
	html_page = urllib.request.urlopen(url)
	data = html_page.read().decode('utf-8')
	html_page.close()
	return data

def parse_html (data):
	content = []
	parser = BeautifulSoup(data, 'html.parser')
	
	for tag in parser.find_all('p'):
		content.append(tag.text)
		content.append(' ')

	return ''.join(content)

if __name__ == '__main__':
	main()
