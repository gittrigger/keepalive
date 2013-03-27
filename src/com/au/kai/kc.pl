#!/usr/bin/perl

if( !$ARGV[0] || !-e $ARGV[0]){
	die "Usage: $0 {filepath}\n";
}

my $k = "";
my $g = "";
my $nest = 0;
my $nests = 0;
my $nestsc = "";
my @k;

my $file = $ARGV[0];
$kin = 0;

if($file){
	my $k = "";
	print "GET $file\n";

	open(K,$file);
	$k .= $_ while(<K>);
	close(K);

	print "SIZE " . (length($k))."\n";
	$k =~ s/\}/}\n/sg;
	$k =~ s/\)[\s\n]+\{/\) \{/sg;
	@k = split(/\n/,$k);
}

# Load
scanme(1,0,0);
# Count
scanme(0,1,0);
# Print
scanme(0,0,1);

#scanme(1,1,1);

sub getContent {
	#print "$kin>\n";

	$k[$kin] =~ s/^\s+//;
	my $k = $k[$kin];
	my $g = $k;

	$k =~ s/\\\{|\\\}//g;
	$k =~ s/\\\(|\\\)//g;


	$k =~ s/\/\/.*//;

	my $limitcomment = 0;

	while($k =~ /\/\*/){

		$limitcomment++;
		if($limitcomment > 5){ last; }
		$k =~ s/\/\*.*?\*\///sg;

		if( $k =~ /\/\*/ ){
			for($kin++; $kin < @k; $kin++){
				$k[$kin] =~ s/^\s+//;
				$g .= "\n" . $k[$kin];
				$k .= "\n" . $k[$kin];
				chomp($k);
				if($k =~ s/.*\*\///s){last;}
			}
		}
	}

	if(1==2){
	if($k =~ /\(/){
		$p = $k;

		$limitcomment = 0;

		while( $p =~ /\(/ ){
			$limitcomment++;
			if($limitcomment > 5){ last; }
			$p =~ s/\(.*?\)//sg;

			if( $p =~ /\(/ ){
				for($kin++; $kin < @k; $kin++){
					$k[$kin] =~ s/^\s+//;
					$g .= "\n" . $k[$kin];
					$k .= " " . $k[$kin];
					$p .= "\n" . $k[$kin];
					if( $p =~ /\)/ ){ 
						last;
					}
				}
			}
		}

	}

	$k =~ s/\\\{|\\\}//g;
	$k =~ s/\\\(|\\\)//g;

	}

	$g =~ s/\/\/.*|\/\*.*?\*\///sg;

	while($k =~ s/(\{.*?\})//){
		print ">> NOTICE PACKNEST: $1 <<\n";
	}
	

	while($k =~ s/{//){
		$nest++;
	}

	$g =~ s/^\s+//;

	if($k =~ /\\[\{\}\(\)]/){
		print ">> NOTICE bracket { } ( ) <<\n";
		print ">> $k <<\n";
	}

	return ($g, $k);
}

sub scanme {
	my $load = shift || 0;
	$global::count = shift || 0;
	my $report = shift || 0;

	$nest = 0;
	$nests = 0;

	my $k = "";

	for($kin = 0; $kin < @k; $kin++){
		#print "SCAN $kin>\n";
		($g , $k) = getContent();



		if($g){
			$nestsc = "";
			for($nests = $nest; $nests > 0; $nests--){
				if($nestclass{$nests}){
					$nestsc = $nestclass{$nests} . "> ";
					last;
				}
			}

			my @G = split(/;/,$g);
			if($report && @G > 2 ){
				print ">> !! ; NOTICE $g !! <<\n";
			}

			#foreach $h ( split(/;/,$g) ){
			foreach $h ( @G ){
				chomp($h);
				$h =~ s/\s+\(/\(/g;
				if(length($h)==0){next;}

#if($report){
#	print ">> $h\n";
#}

				$desc = "";
				$sg = 0;
				# class
				if($h =~ /class (\w.*?)[\s]{/i){
					$ab = $1;
					$nestclass{$nest} = $ab;
					$nestclass{$nest} =~ s/\s.*//;
					$ab =~ s/.* extends /extends /;
					$ab =~ s/.* implements /implements /;

					#$nestclasses{$nestclass{$nests}}{$nestclass{$nest}} = $ab;
					$oj = storeSea($nestclass{$nest},'class',"");
					$desc .= "\t\t\t(class: $nestclass{$nests}: $nestclass{$nest}: $ab *$oj)\n";
					$sg = 0;

				# define assign
				}elsif($h =~ /(\w.*) (\w.*?)[\s+]\=[\s+](.*)/){
					$ac = $1;
					$an = $2;
					$av = $3;

					my ($mab, $mdesc, $oj) = defineHere($ac,$an,$av);

					if(1==2 && $av =~ /new (.*?)\((.*)\)/){
						$ab = $1;
						$ad = $2;
						foreach $am (split(/,/,$ad)){
							$oj .= "(".storeSea($am,'','').")";
						}
					}

					
					if($av =~ /new (.*?)\((.*)\)/){
						$ab = $1;
						$ad = $2;
						#$oj .= "($ab *".storeSea($ab,'','').")";
						foreach $am (split(/,/,$ad)){
							$oj .= "($am *".storeSea($am,'','').")";
						}
					}
					if($h =~ /\{/){ $nestnew{$nest} = $an; }
					$desc .= "\t($nestclass{$nests}\.($ac)$an($av) *$oj)\n";
					$sg = 1;

				}elsif($h =~ /(.*?) \= new (.*?)\((.*)\)/){
					$an = $1;
					$ab = $2;
					$ad = $3;
					my ($mab, $mdesc, $oj) = defineHere("",$an,$ab);
					$oj .= "($ab *".storeSea($ab,'','').")";
					foreach $am (split(/,/,$ad)){
						$oj .= "($am *".storeSea($am,'','').")";
					}
					if($h =~ /\{/){ $nestnew{$nest} = $an; }

					$desc .= "\t\t\t\t\t\t(instance $nestclasses{$nestclass{$nests}}{$ab}:$nestclass{$nests}: $ab: $nestclassesc{$nestclass{$nests}}{$ab} #$of $ofp *$oj)\n";
					$sg = 0;

				# assign
				}elsif($h =~ /(\w.*?) \= (.*)/s){
					$an = $1;
					$av = $2;
					$av =~ s/[\n\r]/ /g;
					#$of = isMany($an);
					#$oj = storeSea($an,'',$av);
					#$ov = isMany($av);
					#$desc .= $mdesc;

					my ($mab, $mdesc, $oj) = defineHere("",$an,$av);
					if($h =~ /\{/){ $nestnew{$nest} = $an; }
					$desc .= "\t($nestclass{$nests}\.(_)$an($av) *$oj)\n";
					#$desc .= "\t\t\t\t\t\t(assignment: $nestclass{$nests}: $nesthere{$nestclass{$nests}}{$b}: $neston{$nestclass{$nests}}: $an(#$of) = $av(#$ov) *($oj)) \n";
					$sg = 1;
				# new instance constructor
				}elsif($h =~ /new (.*?)\((.*)\)/){
					$ab = $1;
					$ad = $2;
					$oj = "($ab *".storeSea($ab,'','').")";
					foreach $am (split(/,/,$ad)){
						$oj .= "($am *".storeSea($am,'','').")";
					}
					$desc .= "\t\t\t\t\t\t(instance $nestclasses{$nestclass{$nests}}{$ab}:$nestclass{$nests}: $ab: $nestclassesc{$nestclass{$nests}}{$ab} #$of $ofp *$oj)\n";
					$sg = 0;

					#$of = isMany($ab);
					#$ofp = "";
					#$ofp .= "($am #".isMany($am).")";
				
				# return statement
				}elsif($h =~ /return/){
					$desc .= "\t\t\t\t\t\t(return statement)\n";
					$sg = 1;


				# nest open
				}elsif( $h =~ /([(else)][\s+]if)[\s+]\((.*)\)\{/){
					$ad = $2;
					$nestnew{$nest} = $1;
					$nestnew{$nest} =~ s/\s+//g;
					$desc .= "\t(+$nestclass{$nests}.$nestnew{$nest}) ($ad)\n";


			#}elsif($h =~ /(\w+).* ([a-z]\w.*?)\(/i){
				# method
				}elsif($h =~ /(\w[\w_\.\[\] ]+) (\w.*?)\((.*)\)[\s+]{/si ){

					$ab = $1;
					$av = $2;
					$ad = $3;

					$av =~ s/(^\s+|\s+$)//g;
					#$desc .= "\t\tFEEDBACK($av, $ab, $ad)\n";

					#$nesthere{$nestclass{$nests}}{$av}{'.cast'} = $ab;


					$oj = storeSea($av,"method",$ab);
					$neston{$nestclass{$nests}} = $av;
					$neston{$nestclass{$nest}} = $av;
					$nestmethod{$nest} = $av;

				
					#$ef = isMany($av);
					$ofb = "";
					foreach my $b ( split(/,/,$ad) ){
						chomp($b);
						$b =~ s/\[.*?\]//;
						$b =~ s/(^\s+|\s+$)//g;
						if(!$b){next;}
						my ($ab,$av) = split(/ /,$b);
						#$nesthere{$nestclass{$nests}}{$neston{$nestclass{$nests}}}{$av}{'.cast'} = $ab;
						#$ofb .= "(".isMany($av).")";
						$oj .= " (".storeSea($av,$ab,"").")";
					}

					$desc .= "\t\t\t\t\t(method: $nestclass{$nests}: $av) *($oj)\n";
					$sg = 0;


				# press
				#}elsif($h =~ /([\w_\.\[\]]+)\(/){
				}elsif($h =~ /^([\w_\.\[\]]+)\((.*)\)/ ){
					$press = $1;
					$parts = $2;
					$parts =~ s/\n/ /gs;
					my $partsbin = $parts;
					$parts =~ s/(,|\&\&|\|\|| OR | AND |\!\=|\=\=|<|>|\=)/\n/gis;
					$parts =~ s/\)/\)\n/gs;

					$oj = storeSea($press,'method',"");
					foreach my $b ( split(/\n/,$parts) ){
						chomp($b);
						$b =~ s/\[.*?\]//;
						$b =~ s/(^\s+|\s+$)//g;
						if(!$b){next;}
						if($b =~ / / && $b !~ /"/ ){
							my ($d, $e) = split(/ /,$b);
							$oj .= " ($b *".storeSea($e,$d,'').")";
						}else{
							$oj .= " ($b *".storeSea($b,'','').")";
						}
					}
					$parts =~ s/\n/, /sg;
#import
					if($h =~ /\{/){
						$nestnew{$nest} = $press;
						$desc .= "\t\t\t\t(pressnest: $nestclass{$nests}($nests): $nest: $press: $partsbin: *($oj))\n";
					}else{
						$desc .= "\t\t\t\t(press: $nestclass{$nests}($nests): $neston{$nestclass{$nests}}: $press: $partsbin: *($oj))\n";
					}


					$sg = 0;



				# nest open
				}elsif( $h =~ /else[\s+]\{/){
					$nestnew{$nest} = "else";
					$desc .= "\t(+$nestclass{$nests}.$nestnew{$nest})\n";


				# nest open
				}elsif( $h =~ /^\{/){
					$nestnew{$nest} = "block";
					$desc .= "\t(+$nestclass{$nests}.$nestnew{$nest})\n";

				# nest close
				}elsif( $h =~ /\}/){

					if($storenest{$nest}){$storenest{$nest} .= $g;$g = "";}

					if($nestmethod{$nest}){
						$desc .= "\t(-$nestclass{$nests}.$nestmethod{$nest} ".(length($storenest{$nest})).")\n\n$storenest{$nest}\n\n";
						#delete($storenest{$nest});
					}elsif($nestnew{$nest}){
						$desc .= "\t(-$nestclass{$nests}.$neston{$nestclass{$nests}} $nestnew{$nest} ".(length($storenest{$nest})).")\n\n$storenest{$nest}\n\n";
					}elsif($nestclass{$nest}){
						$desc .= "\t(-$nestclass{$nest} ".(length($storenest{$nest})).")\n\n$storenest{$nest}\n\n";
					}else{
						$desc .= "(} // ($h) $nest, $nestclass{$nest}, $nestmethod{$nest} ".(length($storenest{$nest})).")\n\n$storenest{$nest}\n\n";
					}

				# package {class}|import {class}|declare content[, n]
				#}elsif($h =~ /([\w_\-.\[\]]+) (\w[\w_\-,\[\]\. ]+)$/){
				}elsif($h =~ /([\w\.\[\]]+) (\w+.*?)$/){
					$ab = $1;
					$av = $2;

					$of = "";

					if($ab eq "package"){
						$nesthere{"super"}{$av}{'.cast'} = $ab;
						$nesthere{"super"}{'.cast'} = "package";
						$nestclass{"0"} = "super";
						$nesthere{"super"}{'.count'} = 1;
						$nesthere{"super"}{$av}{'.count'} = 1;
						$desc .= "\t\t\t\t\t\t\t(package: $av)\n";
					}elsif($ab eq "import"){
						$ai = $av;
						$av =~ s/(^\s+|\s+$)//g;
						$oj = storeSea($av,"import",$av);#sub storeSea
						$desc .= "\t(import: $av *$oj)\n";
					}else{
						my ($mab, $mdesc, $oj) = defineHere($ab,$av,"");
						#$desc .= $mdesc;
						if($h =~ /\{/){ $nestnew{$nest} = $ab; }
						$desc .= "\t($nestclass{$nests}\.($ab)$av(_) *$oj)\n";
					}

					$sg = 1;

				}else{
					$desc .= "\t\t\t\t\t\t\t(undefined: $nestclass{$nests}: $h)\n";
				}





				$sg = 0;
				if( $report && !$sg && $nest <= 24){

# pressnest
if($nestnew{$nest} || $nestmethod{$nest} || $nestclass{$nest} ){
	#print $g;
	if($g){
		$storenest{$nest} .= $g . "\n";
		$g = "";
	}
}

{
					print "$nests:".($neston{$nestclass{$nests}}?'*':'_').":$nest> ";
					for($sat = $nests; $sat > 0; $sat--){print "\t";}

					#print $h . "\n";
					#print $h . "\n" . $desc;
					$desc =~ s/^\s+//;

					if($desc){
						if($desc =~ /UND/){
							print $h;
						}
						print $desc;
					}else{
						print "\n";
					}

}

					#if($desc){
						#print "$nest> ";
						#$desc =~ s/^\s+//;
						#for($sat = $nest; $sat > 0; $sat--){print "\t";}
						#print $h . "\n;
					#}else{
						#print "$nestsc$nest: $h\n$desc";
					#}

				}

			}

		}

		while( $k =~ s/}// ){
			if( $neston{$nestclass{$nests}} && $nestmethod{$nest} ){ delete($neston{$nestclass{$nests}}); }
			if( $nestnew{$nest} ){ delete($nestnew{$nest}); }
			if( $storenest{$nest} ){ delete($storenest{$nest}); }
			if( $nestclass{$nest} ){ delete($nestclass{$nest}); }
			if( $nestmethod{$nest} ){ delete($nestmethod{$nest}); }
			$nest--;
		}


	}
	#close(K);
	
	if($report){
		print "\n\n#####################################\n";

		foreach my $pc ( sort {$a cmp $b} keys(%nesthere) ){
			print "## $pc\n";
			my $n2 = $nesthere{$pc};
			foreach my $ac ( sort {$a cmp $b} keys(%$n2) ){
				$n3 = $$n2{$ac};
				if($$n3{'.count'}){
					print "#### $pc #### $ac ($$n3{'.count'} $$n3{'.cast'})\n";
				}else{

					foreach my $ad ( sort {$a cmp $b} keys(%$n3) ){
						if($ad =~ /^\./){
							next;
						}
						print "#### $pc #### $ac #### $ad ($$n3{$ad}{'.count'} $$n3{$ad}{'.cast'})\n";
					}
				}
			}
		}

		print "\n\n#####################################\n";
		foreach my $pc ( keys(%nestclasses) ){
			print "## $pc\n";
			my $n2 = $nestclasses{$pc};
			foreach my $ac ( keys(%$n2) ){
				$n3 = $$n2{$ac};
				if($nesthere{$pc}{$ac}){
					foreach my $ad ( keys(%$n3) ){
						$num = "";
						$num = "LOCAL $nestclassesc{$pc}{$ac} $nesthere{$pc}{$ac}{'.count'} $nestclassesc{$pc}{$ac}";
						print "###### $pc: $ac: $ad ($$n3{$ad}) ($num)\n";
				#}elsif($nesthere{$pc}{$ac}){
				#	$num = "GLOBAL $nestclassesc{$pc}{$ac}";
				#}else{
				#	$num = "UNA ";
				#}
					}
				}else{
					print "#### $pc: $ac ($$n2{$ac}) ($nestclassesc{$pc}{$ac})\n";
				}
			}
		}

		print "\n#####################################\n\n";
	}

}

sub defineHere {
	my $ab = shift;
	my $av = shift;
	my $setto = shift;
	$ab =~ s/^(private|public)[\s+]//i;

	for(my $nests = $nest; $nests > 0; $nests--){
		if($nestclass{$nests}){
			last;
		}
	}

	my $desc = "";
	my $of = "";
	foreach my $b ( split(/,/,$av) ){
		chomp($b);
		$b =~ s/\[.*?\]//;
		$b =~ s/(^\s+|\s+$)//g;
		if(!$b){next;}

		$desc .= "(".storeSea($b,$ab,$setto).")\n";
		#$desc .= "($b,$ab,$setto ** ".storeSea($b,$ab,$setto).")\n";

		#$of .= "(".isMany($b).")";

#		if($neston{$nestclass{$nests}}){
#			$desc .= "(LOCAL $nest: $nests: $nestclass{$nests}: $neston{$nestclass{$nests}}: $b)\n";
#			$nesthere{$nestclass{$nests}}{$neston{$nestclass{$nests}}}{$b}{'.cast'} = $ab;
#		}else{
#			$desc .= "(GLOBAL $nest: $nests: $nestclass{$nests}: $neston{$nestclass{$nests}}: $b)\n";
#			$nesthere{$nestclass{$nests}}{$b}{'.cast'} = $ab;
#		}
	}

	$of = $desc;
	$of =~ s/\n+/ /gs;

	return($ab, $desc, $of);
}

sub storeMachine {
	my $baseclass = shift;
	my $scopev = shift;
	my $storevalue = shift;

	if($storevalue){
		if($neston{$baseclass} && $nesthere{$baseclass}{$scopev}{$neston{$baseclass}}{'.count'} ){
			$nesthere{$baseclass}{$scopev}{$neston{$baseclass}}{'.value'} = $storevalue;
			$nesthere{$baseclass}{$scopev}{$neston{$baseclass}}{'.values'}++;
		}elsif($nesthere{$baseclass}{$scopev}{'.count'}){
			$nesthere{$baseclass}{$scopev}{'.value'} = $storevalue;
			$nesthere{$baseclass}{$scopev}{'.values'}++;
		}
	}
}

sub storeSea {
	my $b = shift;
	my $e = shift;
	my $storevalue = shift || "";

	$b =~ s/\n/ /g;
	$b =~ s/\[.*?\]//g;
	$b =~ s/\(.*?\)//g;
	$b =~ s/(^\s+|\s+$)//g;
	$b =~ s/\s+//g;

	my $bin = $b;
	#$b =~ s/\..*//;

	$e =~ s/^(private|public)[\s+]//i;
	$e =~ s/(^\s+|\s+$)//g;
	#$e =~ s/\s+//g;
	

	if($b =~ /^(null)$/i ){
		return "NULL";
	}elsif($b =~ /^"(.*)"$/){
		return "STRING $1";
	}elsif($b =~ /^(\d+)$/){
		return "INT $b";
	}elsif($b =~ /^([\-]\d+)$/){
		return "INT $b";
	}elsif($b =~ /^([\-]\d+\.\d+)$/){
		return "DOUBLE $b";
	}elsif($b =~ /^([\-]\d+f)$/){
		return "FLOAT $b";
	}elsif($b =~ /^(false|true)$/i){
		return "BOOLEAN $b";
#UND
	}else{

		if($e =~ /(import|method)/ && $b =~ /.*\.(\w.*)$/){
			$b = $1;
		}elsif($b =~ /(.*?)\..*/){
			$b = $1;
		}

		if($b =~ /\=/){
			print ">> NOTICE $b <<\n";
		}

		my $satb = $nest;
		for($satb; $satb >= 0; $satb--){
			if($nestclass{$satb}){

				if($neston{$nestclass{$satb}}){
					if($nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.cast'} && $e !~ /(method|import|package)/) {last;}
				}
				if($nesthere{$nestclass{$satb}}{$b}{'.cast'} || ($e && $e !~ /(method|import|package)/) ){last;}
				#last;
			}
		}

#		foreach $bc ( sort keys %nesthere){
#			if($neston{$bc}){
#				if($nesthere{$bc}{$b}{$neston{$bc}}{'.cast'} && $e && $e !~ /(method|import|package)/) {last;}
#			}
#			if($nesthere{$bc}{$b}{'.cast'} || ($e && $e !~ /(method|import|package)/) ){last;}
#		}

		if($satb <= 0){
			$satb = 0;
			$nestclass{$satb} = "super";
		}

		if($neston{$nestclass{$satb}} && $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'} ){
			# Local, active class's active method
			if($global::count){
				$nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'}++;
			}
			storeMachine($nestclass{$satb},$b,$storevalue);
			return "LOCAL$satb $nestclass{$satb}: $b: $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.cast'}: $neston{$nestclass{$satb}} #$nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'}";# $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.value'}";
		}elsif($neston{$nestclass{$satb}} && $e && $e !~ /(method|import|package)/){
			if($global::count){
				$nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'} = 1;
			}
			$nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.cast'} = $e;
			#return "LOCAL+ $nestclass{$satb}: $b: $neston{$nestclass{$satb}}";
			storeMachine($nestclass{$satb},$b,$storevalue);
			return "LOCAL+$satb $nestclass{$satb}: $b: $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.cast'}: $neston{$nestclass{$satb}} #$nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'}";# $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.value'}";
		}


		if($nesthere{$nestclass{$satb}}{$b}{'.count'}){
			if($global::count){
				$nesthere{$nestclass{$satb}}{$b}{'.count'}++;
			}
			storeMachine($nestclass{$satb},$b,$storevalue);
			return "GLOBAL$satb $nestclass{$satb}: $b: $nesthere{$nestclass{$satb}}{$b}{'.cast'}: #$nesthere{$nestclass{$satb}}{$b}{'.count'}";# $nesthere{$nestclass{$satb}}{$b}{'.value'}";
		}elsif($e){
			if($global::count){
				$nesthere{$nestclass{$satb}}{$b}{'.count'} = 1;
			}

			if($e =~ /(method|import|package)/){
				if(!$storevalue){$storevalue = $b;}
				$nesthere{$nestclass{$satb}}{$b}{'.cast'} = $storevalue;
			}else{
				$nesthere{$nestclass{$satb}}{$b}{'.cast'} = $e;
			}
			storeMachine($nestclass{$satb},$b,$storevalue);
			return "GLOBAL+$satb $nestclass{$satb}: $b: $nesthere{$nestclass{$satb}}{$b}{'.cast'} #$nesthere{$nestclass{$satb}}{$b}{'.count'}";# $nesthere{$nestclass{$satb}}{$b}{'.value'}";
		}else{
			return "UND ($b) ($e) ($storevalue) ($nestclass{$satb} $satb $nest)";
		}

	}

#return "UND $nestclass{$satb}: $b";

}

sub isManyX {
	my $b = shift;

	$b =~ s/\n/ /g;
	$b =~ s/\[.*?\]//g;
	$b =~ s/\(.*?\)//g;
	$b =~ s/(^\s+|\s+$)//g;
	$b =~ s/\..*//;
	$b =~ s/\s+//g;

	#$satb = 0;
	#for($satb = $nests; $satb > 0; $satb--){
	#if($nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}){last;}
	#if($nesthere{$nestclass{$satb}}{$b}){last;}
	#}

	my $satb = $nest;
	for($satb; $satb >= 0; $satb--){
		if($nestclass{$satb}){last;}
	}


	if($nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}){
		if($global::count){
			$nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'}++;
		}
		return "$nestclass{$satb} LOCAL $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.cast'} $neston{$nestclass{$satb}} $b " . $nesthere{$nestclass{$satb}}{$b}{$neston{$nestclass{$satb}}}{'.count'};
	}elsif($nesthere{$nestclass{$satb}}{$b}){
		if($global::count){
			$nesthere{$nestclass{$satb}}{$b}{'.count'}++;
		}
		return "$nestclass{$satb} GLOBAL $nesthere{$nestclass{$satb}}{$b}{'.cast'} $b " . $nesthere{$nestclass{$satb}}{$b}{'.count'};
	}elsif($b =~ /^(null)$/i ){
		return "NULL";
	}elsif($b =~ /^"(.*)"/){
		return "STRING $b $1";
	}elsif($b =~ /^([\-|]\d+)$/){
		return "INT $b $1";
	}elsif($b =~ /^([\-|]\d+\.\d+)$/){
		return "DOUBLE $b $1";
	}else{
		return "UNK $b -1";
	}

}





