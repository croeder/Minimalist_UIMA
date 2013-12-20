#!/Library/Frameworks/Python.framework/Versions/3.3/bin/python
import urllib.request
import psycopg2
import sys
import time

### CRAPS OUT after a few minutes

# hits pubmed for converting pmids to dois
PMID="http://www.pubmedcentral.nih.gov/utils/idconv/v1.0/"
OPTIONS="&versions=no&format=csv"

# hits postgres for pmid ids
DBNAME="medline"
USER="postgres"
PASSWORD="P0stgr3s!"
HOST="140.226.123.80"
batchSize=100
DELAY=10

def convertPmidBatch(IDS):
	h = urllib.request.urlopen(PMID + "?ids=" + IDS + OPTIONS)
	response = h.read()
	responseStr = str(response, encoding='utf8')
	# "PMID","PMCID","DOI","Version","MID","IsCurrent","IsLive","ReleaseDate","Msg"
	for line in responseStr.splitlines()[1:] :
		(pmid, pmcid, doi) =line.split(",")[0:3]
		if (doi.strip("\"") != ""):
			print(pmid + "," + doi)
	h.close()

def fetchPmidBatches(batchNum):
	idStringList = []
	conn = psycopg2.connect(database=DBNAME, host=HOST, user=USER, password=PASSWORD)
	cursor = conn.cursor()
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
	conn.close()
	return idStringList



##for batchNum in range(0,8316):
for batchNum in range(7000,7100):
	for idString in fetchPmidBatches(batchNum):
		sys.stderr.write(str(batchNum) + "\n")
		convertPmidBatch(idString)
		time.sleep(DELAY)

