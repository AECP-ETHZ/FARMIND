

def parseFile(f):
        region = dict()
        f.readline()
        f.readline()
        key_list = []

        for line in f:
                reg = line[0:5]
                act_list = line[6:]
                two = act_list.split()
                act = two[0]
                if("/;" in two[1]):
                        amount = float(two[1][:-2])
                else:
                        amount = float(two[1])

                if(reg in region):
                        val = []
                        val = region[reg]
                        if (amount > 0):
                                val.append(act)
                                region[reg] = val
                else:
                        
                        val = []
                        if (amount > 0):
                                key_list.append(reg)
                                val.append(act)
                                region[reg] = val
        return region, key_list

f = open('data_PFLLANDKAPBJ.gms', 'r')
plantAct,key_list = parseFile(f)

f = open('data_ANIMALKAP_BJ.gms', 'r')
animalAct,keys = parseFile(f)
final = dict()
print(animalAct)


for key in plantAct:
        print(key)
        val = plantAct[key]
        if (key in animalAct):
                val_f = animalAct[key]
                print(val_f)
                print(val)
                val_f.extend(val)
                print(val_f)
                print
                final[key] = val_f
        else:
                final[key] = val


f =  open('initial_activities.csv', 'w')
f.write(",v1,v2,\n")
for key in key_list:
    m = key + ","
    for val in final[key]:
            m = m + val + ","
    m = m + "\n"
    f.write(m)
    
