#!/Library/Frameworks/Python.framework/Versions/3.3/bin/python
import os
import re
import psycopg2
import subprocess


elsevier_base = "/net/amc-colfax/RAID1/data/fulltext/elsevier/untar"
# hits postgres for pmid ids
DBNAME="medline"
USER="postgres"
PASSWORD="P0stgr3s!"
HOST="140.226.123.80"

rowCount=0
dupeCount=0

def selectDuplicates(conn):
	global rowCount
	global dupeCount
	duphash = {};
	cursor = conn.cursor()
	cursor.execute("select pii, path, id from elsevier_records where pii in  (select pii from elsevier_records group by pii having count(pii) > 1)");
	for thing in cursor.fetchall():
		# ('S1053-2498(12)00340-3', '/net/amc-colfax/RAID1/data/fulltext/elsevier/daily/UNC00000000000795/10532498/v31i4sS/S1053249812003403/main.xml', 'S1053-2498(12)00340-3')
		pii = thing[0]
		try:
			(duphash[pii]).append(thing)
			dupeCount += 1
		except KeyError:
			print("first one of these I've seen" + pii )
			duphash[pii] = [thing]
		rowCount += 1
	return duphash

def analyze1(duphash):
	batchSet=[]
	for pii in duphash.keys():
		if (len(duphash[pii]) > 1):
			print( pii + " length: " + str(len(duphash[pii])))
			paths=[]
			for  row in  duphash[pii]:
				# compare files
				file = row[1]
				paths.append(file)
			for file in paths[1:]:
				error = subprocess.call(['diff',paths[0], file], stdin=None, stdout=subprocess.DEVNULL)
				if (error) :
					print("diff: " + str(paths[0]) + ", " + str(file) + "...." + str(row))


## main ##
conn = psycopg2.connect(database=DBNAME, host=HOST, user=USER, password=PASSWORD)
duphash = selectDuplicates(conn)
analyze1(duphash)

print(str(dupeCount) + " of " + str(rowCount))

