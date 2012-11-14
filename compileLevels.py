

f = open('levels.txt','r')
starts='static final int starts[] = {'
ends='static final int ends[] = {'
pars='int pars[] = {'
levels='int levels[][] = {'
dirs='int startDirs[][]={'
numsOfButtons='int numsOfButtons[] = {'
for line in f:
    line=line.split(' ')
    if line[0]=='start:':
        starts=starts+line[1]+','
    elif line[0]=='end:':
        ends=ends+line[1]+','
    elif line[0]=='par:':
        pars=pars+line[1]+','
    elif line[0]=='numOfButtons:':
        numsOfButtons=numsOfButtons+line[1]+','
    elif line[0]=='dir:':
        dirs=dirs+line[1]+','
    elif line[0][0] == '{':
        levels = levels + line[0] + ','

starts = starts[0:-1] + '};'
ends = ends[0:-1] + '};'
pars = pars[0:-1] + '};'
numsOfButtons = numsOfButtons[0:-1] + '};'
dirs = dirs[0:-1] + '};'
levels = levels[0:-1] + '};'

print(levels)
print(starts)
print(ends)
print(pars)
print(numsOfButtons)
print(dirs)

###run length encode the level data (doesn't help)
##l1=levels[0:-1].split('{')
##l2 = l1[2:]
##for i in range(0,len(l2)):
##    l=l2[i]
##    l2[i]=l[0:-4].split(',')
##
##leveldata=[]
##zeroCount=0
##temp=[]
##for i in range(0,len(l2)):
##    zeroCount=0
##    for j in range(0,len(l2[i])):
##        if (l2[i][j]=='0'):
##            zeroCount+=1
##        else:
##            if zeroCount>0:
##                temp.append(zeroCount)
##                zeroCount=0
##            temp.append(-int(l2[i][j]))
##    leveldata.append(temp)
##    temp=[]
##
##leveldatatext='static final int levelData[][] = {'
##for i in range(0,len(leveldata)):
##    leveldatatext=str(leveldatatext)+'{'
##    for j in range(0,len(leveldata[i])):
##        leveldatatext=leveldatatext+str(leveldata[i][j])+','
##    leveldatatext=str(leveldatatext[0:-1])+'},'
##leveldatatext=str(leveldatatext[0:-1])+'};'
##print(leveldatatext)
