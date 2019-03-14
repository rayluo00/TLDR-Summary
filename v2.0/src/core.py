from extractor import HTMLExtractor


class Summarizer():

    def __init__(self, url=''):
        self.html_extractor = HTMLExtractor(url)

if __name__ == '__main__':
    url = 'https://decisiondata.org/news/53-of-people-shown-an-anti-vaccination-website-left-believing-new-misconceptions/'
    #url = 'https://www.theguardian.com/world/2011/may/25/china-prisoners-internet-gaming-scam'
    
    smry = Summarizer(url)
    #print(smry.html_extractor.content)
    print(smry.html_extractor.sentences)
