#!/usr/bin/perl

if( !$ARGV[0] || !-e $ARGV[0]){
	die "Usage: $0 {filepath}\n";
}

my $k = "";
open(K,$ARGV[0]);
while($k = <K>){
	
}
close(K);


