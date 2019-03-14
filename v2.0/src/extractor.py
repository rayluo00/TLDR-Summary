import re
import requests
import unidecode
import nltk.data
from bs4 import BeautifulSoup

class HTMLExtractor():

    def __init__(self, content_url=''):
        self.content_url = content_url
        self.html_content = requests.get(self.content_url).text
        self.content = self.parse_raw_html(self.html_content)
        self.sentences = self.create_sentence_data(self.content)

    def parse_raw_html(self, html_content):
        ''' Parse the raw HTML string.

            Args:
                html_content (string): Raw HTML as String.
            Returns:
                Cleaned string of the content from the raw HTML. Removes fillers
                and HTML elements.
        '''

        soup = BeautifulSoup(html_content, 'html.parser')
        content = soup.find_all('div', {'class': re.compile(r'.*(?<=content).*')})[0]
        content = ' '.join([c.text for c in content.find_all('p') if len(c.text.split()) > 3])

        return unidecode.unidecode(content)

    # TODO: Create bag of words function
    def create_bag_of_words(self, content):
        ''' Create bag of words from the content.

            Args:
                content (string): Main contents of the HTML extraction.
            Returns:
                Dictionary of words and the word count based off the content.
        '''

        pass

    def create_sentence_data(self, content):
        ''' Create list of sentence metadata.

            Args:
                content (string): Main contents of the HTML extraction.
            Returns:
                List of dictionaries containing the metadata of the sentences from
                the extracted document/content.
        '''

        sentences = []
        sent_detector = nltk.data.load('tokenizers/punkt/english.pickle')
        sent_tokens = sent_detector.tokenize(content.strip())

        for sentence in sent_tokens:
            sent_data = {'Sentence': sentence,
                         'Words': None}
            sentences.append(sent_data)

        return sentences
