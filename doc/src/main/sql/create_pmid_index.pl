#!/usr/bin/env perl
use warnings;
use strict;

use File::Find;
use Cwd;

#
# create_pii_index.pl <dir>
#
# This script pulls the pmid out of each of the PMC OA
# files and builds an index file out of it for use with the
# getpmids/pmidlib code. 
# Output is STDOUT
#
 
my $root = $ARGV[0];
my @dir_list = ($root);
my $cwd = getcwd();

sub process_file() {
	if ($File::Find::name =~ /.*\.nxml/) {

		my $pmid="";
		my $doi="";
		my $file =  "$cwd/$File::Find::dir/$_";
		if (-e $file) {
			open(FILE, "$file") or die "couldn't open $file $!";
			while (<FILE>) {
				if ($_ =~ /<article-id pub-id-type="pmid">(.*?)<\/article-id>/) {
					$pmid = $1;
					print "\"$file\",$pmid\n";
				}	
			}
		}
		else {
			print STDERR "couldn't open $file, skipped \n";
		}
		close FILE;
	}
}

find (\&process_file, @dir_list);
