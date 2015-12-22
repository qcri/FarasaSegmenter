#!/bin/bash -e

: <<=cut
=pod

=head1 NAME

Farasa - Arabic tokenizer

=head1 SYNOPSIS
  
farasa.sh <InputFile> <OutputFile>
farasa.sh < InputFile
  
  
 arguments:
 	InputFile         Text Input file encoded in UTF-8
        OutputFile        Output file

=head1 DESCRIPTION

This script will run the QCRI Farasa package for tokenizing Arabic text.

=head1 AUTHOR

<Kareem Darwish> (kdarwish@qf.org.qa)

=head1 COPYRIGHT

Copyright (C) <2015>, QCRI.

=cut

function VERBOSE {
  echo $@ > /dev/null
}

SHOWHELP=false 

while [[ $# -gt 0 ]]; do
  case "$1" in
   "--option1")
   #set something
    shift
    ;;
   "-h")
    shift
    SHOWHELP=true;
    ;;
   "--help")
    shift
    SHOWHELP=true;
    ;;
     *)
      break;
  esac
done

if $SHOWHELP;
then
  pod2text $0
  exit 0
fi


#
BASEDIR=$(dirname $0)
# 

if [[ $# -eq 0 ]]; then
   FarasaDataDir=$BASEDIR/../FarasaData/ java -Dfile.encoding=UTF-8 -jar $BASEDIR/dist/Farasa.jar -c atb -n true 
fi

if [[ $# -eq 1 ]]; then
    FarasaDataDir=$BASEDIR/../FarasaData/ java -Dfile.encoding=UTF-8 -jar $BASEDIR/dist/Farasa.jar -c atb -n true < $1
fi

if [[ $# -eq 2 ]]; then
    FarasaDataDir=$BASEDIR/../FarasaData/ java -Dfile.encoding=UTF-8 -jar $BASEDIR/dist/Farasa.jar -c atb -n true -i $1 -o $2
fi

exit 0

