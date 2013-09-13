#!/bin/sh

#add all source codes
java -Xms4096m -Xmx8192m -cp "bin:lib/*" gov.ornl.healthcare.runtime.CsvDb2Mongo_Run config/nppes_file.xml


