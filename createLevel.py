

f = open('testLevel.csv','r')
start=0
end=0
leveldata=[]
for line in f:
    line=line.split(' ')
    if line[0]=='start:':
        start=line[1].strip()
        print('startCell = '+start+';')
    elif line[0]=='end:':
        end=line[1].strip()
        print('endCell = '+end+';')
    else:
        line=line[0].split(',')
        for data in line:
            leveldata.append(data.strip())

s='int level[] = {'
for data in leveldata:
    s=s+data+','
s=s+'};'
print(s)
