#!/bin/bash
#
# download_pmc_oa.sh
# 
# This script downloads a fresh copy of files from
# PubMed Central for the Open Access subset. It 
# also expands them into usable files, and emails
# at the end.
#
# paths to mail, wget and tar are hard-coded to linux locations
#
# uses these scripts:
#    $BASE/../build-db.pl
#    $BASE/create_pmid_index.pl 
#
# Chris Roeder, Jan 2012

NOTIFY="chris.roeder@ucdenver.edu"
BASE=/RAID1/data/fulltext/pmc
LOG=$BASE/download_pmc_oa.log


email_error() {
	echo "seem to have failed \
	see log at $BASE/download_pmc_oa.log on amc-colfax\
	(automatic message sent by  /home/hadoop/bin/download_pmc_oa.sh )"\
 	| /bin/mail -s "FAILURE: pmc downloads on colfax status" $NOTIFY
	
}

mv $LOG $LOG.old
echo "\n\nstarting:" >> $LOG
date >> $LOG

# -- get the meta-data
rm -rf  $BASE/metadata.old
mv $BASE/metadata $BASE/metadata.old
mkdir  $BASE/metadata
cd $BASE/metadata
/usr/bin/wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/PMC-ids.csv.gz
if (($? >  0)) 
then 
	echo "problem $?  getting PMC-ids.csv.gz" >> $LOG 2>> $LOG
	email_error
	exit 1
fi

gunzip /PMC-ids.csv.gz
/usr/bin/wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/file_list.csv
if (($? >  0)) 
then 
	echo "problem getting file_list.csv" >> $LOG 2>> $LOG
	email_error
	exit 1
fi


# -- get the zip files --
rm -rf  $BASE/downloads.old
# save the previous files
mv $BASE/downloads $BASE/downloads.old
mkdir  $BASE/downloads
cd $BASE/downloads
/usr/bin/wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/articles.A-B.tar.gz >> $LOG 2>> $LOG
status_a=$?
/usr/bin/wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/articles.C-H.tar.gz >> $LOG 2>> $LOG
status_c=$?
/usr/bin/wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/articles.I-N.tar.gz >> $LOG 2>> $LOG
status_i=$?
/usr/bin/wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/articles.O-Z.tar.gz >> $LOG 2>> $LOG
status_o=$?

if (($status_a > 0 || $status_c > 0 || $status_i > 0 | $status_o > 0)) 
then
	echo "one of the files didn't download" >> $LOG
	email_error
	exit 1
fi


# -- expand the zip files into tar files --
rm -rf $BASE/files.old
mkdir  $BASE/files.new
cd  $BASE/files.new
for file in ../downloads/*.tar.gz
do 
	echo "untarring $file" >> $LOG
	/bin/tar xvzf $file >> $LOG 2>> $LOG
	if (( $? > 0 )) 
	then
		echo "error untarring: $file " >> $LOG
		email_error
		exit 1	
	fi
done

mv $BASE/files $BASE/files.old
mv $BASE/files.new $BASE/files

# -- create a new index file for it
cd $BASE
./create_pmid_index.pl files > pmc_pmid_index.new
if (( $? > 0 )) 
then
	echo "error creating index " >> $LOG
	email_error
	exit 1	
fi
mv pmc_pmid_index pmc_pmid_index.old
mv pmc_pmid_index.new pmc_pmid_index

# -- convert the index file to a dbm file
cd $BASE
mv $BASE/pmc_pmid_index.dbm $BASE/pmc_pmid_index.dbm.old
$BASE/../build-db.pl pmc_pmid_index
if (( $? > 0 )) 
then
	echo "error creating index dbm file  " >> $LOG
	email_error
	exit 1	
fi

# -- email success --
echo "seem to have worked \
(automatic message sent by  /home/hadoop/bin/download_pmc_oa.sh )"\
 | /bin/mail -s "pmc downloads on colfax status" $NOTIFY

exit 0

