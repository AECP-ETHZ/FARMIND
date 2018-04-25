import numpy as np

# calculate q value given a k value for all years of experience
def q_func(k,s):
	r = []
	for t in experience:
		r.append( float(format( 1 / (1 + np.exp(-k*t*s)),'.2f' )))
	return r


def find_k(memory_limit):
	s = 1
	m1_ratio = 1/2.0
	m2_ratio = 1/8.0

	upper_q = 0.9
	lower_q = 0.65
	delta = 0.005
	k_upper = 1
	k_lower = 0.1

	while (k_upper > k_lower):
		ln_ratio = -np.log((1-upper_q)/upper_q)
		k_upper = float(format( ln_ratio/( round(memory_limit * m1_ratio) * s),'.2f' ))	
		ln_ratio = -np.log((1-lower_q)/lower_q)
		k_lower = float(format( ln_ratio/( round(memory_limit * m2_ratio) * s),'.2f' ))
		upper_q = upper_q - delta
		lower_q = lower_q + delta

	print(k_upper, k_lower, upper_q, lower_q)
	avg = (k_upper + k_lower) / 2.0
	print(avg)
	print(q_func(avg,1), np.std(q_func(avg,1)))
	print

memory_limit = range(4,10)

for limit in memory_limit:
	experience = range(1,int(limit + 1)) # set of years to calculate
	print("memory limit is: " + str(limit))
	find_k(limit)	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
