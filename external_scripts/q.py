import numpy as np
experience = range(1,12) # set of years to calculate
memory = range(3,12) # list of possible years of education


ln_ratio = -np.log((1-0.9)/0.9)

# calculate list of possible k values given memory length and education ratio (s)
def k_func(memory, s, memory_ratio):
	k = []
	for m in memory:
		k.append( float(format( ln_ratio/(m * s * memory_ratio),'.2f' )))
	return k

# calculate q value given a k value for all years of experience
def q_func(k,s):
	r = []
	for t in experience:
		r.append( float(format( 1 / (1 + np.exp(-k*t*s)),'.2f' )))
	return r

s_list = [0.5,1] # education/max
for s in s_list:
	i = 0
	if s > 0.5:
		memory_ratio = 0.5
	else:
		memory_ratio = 1

	k_list = k_func(memory, s, memory_ratio)	
	print("s value: " + str(s) + " and memory ratio: " + str(memory_ratio)) 
	for k in k_list:
		print("years of memory: " + str(i+3) + ", years to reach 0.9: " + str( (i+3)*memory_ratio ) + ", k value: " + str(k) + ", q values for years of experience " + str(q_func(k,s)))
		i += 1
	print



#std = [] # standard deviation of values

#for kk in k:
#	r = q_func(kk,1)
#	std.append( float(format(np.std(r),'.2f') ) )
		
#i = 0
#for kk in k:
	#print "years of experience: " + str(i+3) + ", k value: " + str(kk) + ", std: " + str(std[i])+ " : " + str(q_func(kk,1))
	#i +=1
	
	

