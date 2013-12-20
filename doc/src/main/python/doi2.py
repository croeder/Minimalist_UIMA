#!/Library/Frameworks/Python.framework/Versions/3.3/bin/python
import urllib.request
import psycopg2
import sys
import time
import json
from pprint import pprint

# hits pmid2doi.org  for converting pmids to dois
PMID="http://www.pmid2doi.org/rest/json/batch/doi"
OPTIONS=""

# uses the medline database for source pmids
DBNAME="medline"
USER="postgres"
PASSWORD="P0stgr3s!"
HOST="140.226.123.80"
batchSize=200
conn = psycopg2.connect(database=DBNAME, host=HOST, user=USER, password=PASSWORD)

#/net/amc-colfax/RAID1/data/fulltext/elsevier/untar/UNC00000000001026/00029394/v138i6/S0002939404009365/main.xml
# does not run
def createDoiTable():
	cur = conn.cursor();
	cur.execute("drop table if exists medline_doi;")
	cur.execute("create table medline_doi ( pmid varchar(10), doi varchar(100), path varchar(200) , primary key(pmid) );")
	cur.close()

def insertDict(pmidToDoiDict):
	cur = conn.cursor();
	for pmid in pmidToDoiDict:
		doi=pmidToDoiDict[pmid]
		cur.execute("INSERT into medline_doi VALUES (%s, %s);", (pmid,doi)) 	
		conn.commit();
	cur.close()

def convertPmidBatch(IDS):
	url = PMID + "?pmids=[" + IDS + "]"
	h = urllib.request.urlopen(url)

	pmidToDoiDict = {}	
	response = h.read()
	responseStr = str(response, encoding='utf8')
	for line in responseStr.splitlines() :
		dataDict = json.loads(line)
		for entry in dataDict:
			pmidToDoiDict[str(entry['pmid'])] =  entry['doi'] 
	h.close()
	return pmidToDoiDict


def fetchPmidBatches(batchNum):
	idStringList = []
	cursor = conn.cursor("query")
	cursor.execute("select pmid from medline_batches where id = " + str(batchNum)) 	
	batchNumber=0
	batch = cursor.fetchmany(batchSize)
	while batch:
		idString=""
		for row in batch:
			idString= idString + "," + str(row[0])
		idStringList.append(idString[1:])
		batch  = cursor.fetchmany(batchSize)
		batchNumber += 1
	cursor.close()
	return idStringList


def doRange(start, end):
	print("doing range start:" + str(start) + " end:" + str(end))
	for batchNum in range(start, end):
		for idString in fetchPmidBatches(batchNum):
			sys.stderr.write(str(batchNum) + "\n")
			dict = convertPmidBatch(idString)
			insertDict(dict)
			#time.sleep(1)

#createDoiTable()
#doRange(8000,8316)
#doRange(7000,7999)
#doRange(6000,6999)
#doRange(5000,5999)
##doRange(4000,4999)
doRange(4880,4999)
doRange(3000,3999)
doRange(2000,2999)
doRange(1000,1999)
doRange(0,999)
conn.close()
