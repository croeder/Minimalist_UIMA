#!/usr/bin/env perl
use strict;
use warnings;
use DB_File;

#
# buld-db.pl <input filename>
#
# takes a file of value:  key pairs and builds a dbm hash file
#

my %hash;
my $input_filename=$ARGV[0];
my $db_filename="$input_filename.db";
print "input: $input_filename, output: $db_filename\n";

dbmopen(%hash, $db_filename, 0666) || die $!;

# Example input line:
# ./UNC20020040000032/02726386/v40i3/S0272638602000690/main.xml: S0272-6386(02)00069-0

my $count=0;
open INPUT, $input_filename || die  "couldn't open input $input_filename $!";
while (<INPUT>) {
    next unless m{(.*):\s+(.*)};
	$count++;
    $hash{$2} = $1;	
	if ($count % 10000 == 0) {
		print "key: $2  value: $1\n";
	}
} 
printf "Found %d paths\n", $.;

dbmclose(%hash);

