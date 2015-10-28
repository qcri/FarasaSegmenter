FARASA
=============

ABOUT
--------------------------
QCRI Farasa package for tokenizing Arabic text.


Download
---------

QCRI FARASA. Latest release
You may check-out the latest version from the githup repository: https://github.com/Qatar-Computing-Research-Institute/Farasa
Data directory is not included. Download the data from http://alt.qcri.org/tools/farasaData.tgz 


CONTENTS
--------------------------
Package content, files, folders

 
How to run the software
------------------------
1 - Command line:

Limux/Mac OS
	set FarasaDataDir=<FarasaData>\
	java -Dfile.encoding=UTF-8 -jar dist/Farasa.jar -i InputFile -o OutputFile

WINDOWS
	set FarasaDataDir=<FARASADATADIR>/
	java -Dfile.encoding=UTF-8 -jar dist/Farasa.jar -i InputFile -o OutputFile


Parameters:

	QataraLib <--help|-h> [--input|-i inputFile] [--output|-o outputFile]
	* options: 
 	*  --help		display help information
 	*  --input		inputfile
 	*  --output     outputfile
 	* 

Example:

	FarasaDataDir=<FarasaDataDirectory>/ java -Dfile.encoding=UTF-8 -jar dist/Farasa.jar < testfile.txt
	
For Windows Environment: You may require to explicitly specify the library path:

	set FarasaDataDir=<FARASADATADIR>/
	java -Dfile.encoding=UTF-8 -jar dist/Farasa.jar < testfile.txt


How to compile the software
----------------------------
Build the jar:
 
	ant jar
	
Deploy the package to other direcotory:

	ant deploy -Do=<Dest Dir>


CONTACT
--------------------------
If you have any problem, question please contact kdarwish@qf.org.qa or aabdelali@qf.org.qa.

WEB SITE
---------------------------
URL for the project  and the latest news  and downloads
	http://alt.qcri.org/tools/farasa

GITHUB
---------------------------
Where to download the latest version, 
	https://github.com/Qatar-Computing-Research-Institute/Farasa


LICENSE
------------
    QCRI FARASA package for tokenizing Arabic text is being made 
    public for research purpose only. 
    For non-research use, please contact:
    
        Kareem M. Darwish <kdarwish@qf.org.qa>
        Ahmed Abdelali <aabdelali@qf.org.qa>
    
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  


COPYRIGHT
----------------------------
Copyright 2015 QCRI
