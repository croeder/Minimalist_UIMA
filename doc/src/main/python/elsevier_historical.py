#!/Library/Frameworks/Python.framework/Versions/3.3/bin/python
import os
import re
import psycopg2



### patterns and examples ###

# <ce:pii>S0109-5641(13)00127-9</ce:pii>
piiPattern = re.compile("\\s*<ce:pii>(.*?)</ce:pii>")

# <ce:doi>10.1016/j.dental.2013.04.025</ce:doi>
#		<ce:doi>10.1016/S1474-4422(13)70233-3</ce:doi>
doiPattern = re.compile("\\s*<ce:doi>(.*?)</ce:doi>")

#<ce:title id="tit0005">Mechanical properties of contemporary composite resins and their interrelations</ce:title>
titlePattern = re.compile("\\s*<ce:title.*?>(.*?)</ce:title>")

#<ce:author id="aut0005"><ce:given-name>Socratis</ce:given-name><ce:surname>Thomaidis</ce:surname><ce:cross-ref id="crf0030" refid="aff0005"><ce:sup>a</ce:sup></ce:cross-ref></ce:author>
authorPattern = re.compile("\\s*<ce:author id=\".*?\">.*?<ce:surname>(\w+)</ce:surname>")

#net/amc-colfax/RAID1/data/fulltext/elsevier/untar/UNC00000000001005/18788750/v78i5/S1878875012007796/main.xml
pathPiiPattern = re.compile(".*?/UNC0+\d+/.*?/.*?/(.*?)/main.xml")

#<?xml version="1.0" encoding="utf-8"?><!DOCTYPE converted-article PUBLIC "-//ES//DTD journal article DTD version 4.5.2//EN//XML" "art452.dtd" [
dtdPattern = re.compile("DOCTYPE.*?PUBLIC.*?\".*?version (.*?)//.*?\"\\s*\"(.*?)\"")

#<ce:abstract>
abstractPattern = re.compile("<ce:abstract>")

#<ce:sections>
sectionPattern = re.compile("<ce:sections>")


#elsevier_base = "/net/amc-colfax/RAID1/data/fulltext/elsevier/untar"
elsevier_base = "/net/amc-colfax/RAID1/data/fulltext/elsevier/historical"
doiDict = {}
# hits postgres for pmid ids
DBNAME="medline"
USER="postgres"
PASSWORD="P0stgr3s!"
HOST="140.226.123.80"
batchSize=100
DELAY=10


def createDoiTable(conn):
	cur = conn.cursor();
	cur.execute("drop table if exists elsevier_records;")
	# cascaded, not needed
	# cur.execute("drop sequence elsevier_records_id_seq;");
	cur.execute("create table elsevier_records ( path_pii varchar(150), doii varchar(150), pii varchar(150), path varchar(150), title varchar(250), authors varchar(250), dtd_version varchar(20), dtd_file varchar(20), abstract_only boolean, has_abstract boolean, id serial, primary key(id));")
	conn.commit()
	cur.close()

def readElsevierArticle(path):
	attrDict = {}

	with open(path, 'r') as f: ## or die?
		attrDict['path'] = ''
		attrDict['path_pii'] = ''
		attrDict['pii'] = 	''		
		attrDict['title'] = ''		
		attrDict['doi'] = 	''		
		attrDict['dtd_version'] = ''
		attrDict['dtd_file'] = ''
		attrDict['authorList'] = ''
		attrDict['abstract_only'] = 't'
		attrDict['has_abstract'] = 'f'
		for line in f:
			line = line.rstrip()

			pathPiiResult = re.findall(pathPiiPattern, path)
			if (len(pathPiiResult) > 0):
				attrDict['path_pii'] = pathPiiResult[0]			
		
			titleResult = re.findall(titlePattern, line)
			if (len(titleResult) > 0):
				attrDict['title'] = titleResult[0]

			piiResult = re.findall(piiPattern, line)
			if (len(piiResult) > 0):
				# don't pick up pii from later in the document (references)
				if (attrDict['pii'] == ''):
					if (len(piiResult) > 0):
						attrDict['pii'] = piiResult[0]			

			doiResult = re.findall(doiPattern, line)
			if (len(doiResult) > 0):
				# don't pick up doi from later in the document (references)
				if (attrDict['doi'] == ''):
					if (len(doiResult) > 0):
						attrDict['doi'] = doiResult[0]			

			dtdResult = re.findall(dtdPattern, line)
			if (len(dtdResult) > 0):
				attrDict['dtd_version'] = dtdResult[0][0]
				attrDict['dtd_file'] = dtdResult[0][1]


			authorResult = re.findall(authorPattern, line)
			if (len(authorResult) > 0):
				# don't pick up doi from later in the document (references)
				if (attrDict['authorList'] == ''):
					attrDict['authorList'] = authorResult

			attrDict['path']=path

			abstractOnlyResult = re.findall(sectionPattern, line)
			if (len(abstractOnlyResult) > 0):
				attrDict['abstract_only'] = 'f'

			hasAbstractResult = re.findall(abstractPattern, line)
			if (len(hasAbstractResult) > 0):
				attrDict['has_abstract'] = 't'


	return attrDict

def insertElsevierArticle(attrDict, conn):
	cursor = conn.cursor()
	authors = str(attrDict['authorList'])[:249]
	try:
		#cur.execute("create table elsevier_records ( path_pii varchar(150), doii varchar(150), pii varchar(150), path varchar(150), 
		#title varchar(250), authors varchar(250), dtd_version varchar(20), dtd_file varchar(20), abstract_only boolean, has_abstract boolean, primary key(path_pii) );")
		cursor.execute("insert into elsevier_records values (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s);", 
					(attrDict['path_pii'], attrDict['doi'], attrDict['pii'], attrDict['path'][:249], 
					attrDict['title'][:248], authors[:249], attrDict['dtd_version'], attrDict['dtd_file'], 
					attrDict['abstract_only'], attrDict['has_abstract'] )) 	
	except Exception as e:
		print('XXX Exception'  + str(e) + "\n" + str(attrDict)   )
	conn.commit()
		

def walkFileTree(conn):
	fileCount=0
	for root, dirs, files in os.walk(elsevier_base):
		for filename in files :
			if (len(filename) > 7):
				if (filename[-8:] == "main.xml") :
					fileCount += 1
					if (fileCount % 1000 == 0):
						print(root + "/" + filename + " " + str(fileCount))
					dict = readElsevierArticle(os.path.join(root, filename))
					#print(dict)
					insertElsevierArticle(dict, conn)

## main ##
conn = psycopg2.connect(database=DBNAME, host=HOST, user=USER, password=PASSWORD)
#####createDoiTable(conn)
walkFileTree(conn)
