#####################################################################
class SentenceData (object):
	def __init__ (self):
		self.words = None      # dict{'word' : # count}
		self.sentence = None   # 'string sentence'
		self.percentage = 0.0

	def calc_percentage (self, total_wordc, bag_of_words):
		wordc = word_count(self.words)

		for word in self.words:
			if word in bag_of_words.keys():
				percent = self.words[word] / wordc
				percent = percent * (bag_of_words[word] / total_wordc)
				self.percentage += percent

#####################################################################
def word_count (word_bag):
	totalc = 0
	for word in word_bag:
		totalc += word_bag[word]

	return totalc
