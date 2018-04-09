import numpy as np
t = range(1,10) # set of years to calculate
max_ed = 13.0 # max level of education
ed = range(1,12) # list of possible years of education

# calculate q value given a k value
# return a vector of q for all years in t
def q_func(k):
	r = []
	for x in t:
		r.append( float(format( 1 / (1 + np.exp(-k*x)),'.2f' )))
	return r

# calculate list of possible k values given maximum education and possble education range	
def k_func(ed, max_ed):
	k = []
	for e in ed:
		k.append( float(format( 2.95/(max_ed - e),'.2f' )))
	return k

## calculate all possible k values and corresponding distributions	
k = k_func(ed,max_ed)
std = [] # standard deviation of values

for kk in k:
	r = q_func(kk)
	std.append( float(format(np.std(r),'.2f') ) )
		
i = 0
for kk in k:
	print "education " + str(i+1) + ", k value: " + str(kk) + ", std: " + str(std[i])+ " : " + str(q_func((kk)))
	i +=1
