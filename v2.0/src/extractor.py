import re
import requests
import unidecode
import nltk.data
from bs4 import BeautifulSoup

class HTMLExtractor():

    def __init__(self, content_url=''):
        self.content_url = content_url
        self.html_content = requests.get(self.content_url).text
        self.content = self.parse_raw_html()
        self.sentences = self.create_sentence_data()

    def parse_raw_html(self):
        soup = BeautifulSoup(self.html_content, 'html.parser')
        content = soup.find_all('div', {'class': re.compile(r'.*(?<=content).*')})[0]
        content = ' '.join([c.text for c in content.find_all('p') if len(c.text.split()) > 3])

        return unidecode.unidecode(content)

    def create_bag_of_words(self, content):
        

    def create_sentence_data(self):
        sentences = []
        sent_detector = nltk.data.load('tokenizers/punkt/english.pickle')
        sent_tokens = sent_detector.tokenize(self.content.strip())

        for sentence in sent_tokens:
            sent_data = {'Sentence': sentence,
                         'Words': None}
            sentences.append(sent_data)

        return sentences
