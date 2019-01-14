echo off
rem assumes you are in folder D:\IJGradle\gppDemos\batFiles
rem from which you invoke runDemo or any of the other bat files
rem typical invokation
rem D:\IJGradle\gppDemos\batFiles>runDemo MCpi/RunSkelMCpi piTest2 4 1024 10000
rem where %1 is the script to run including the folder that contains it and
rem %2 is the file to which the csv style output will be written in the folder csvFiles
cd ..
for %%A in (1 2 3 4 5 6 7 8 9 10 ) do (
echo run %%A
java -jar .\out\artifacts\gppDemos_main_jar\gppDemos_main.jar .\src\main\groovy\gppDemos\%1.groovy %3 %4 %5 %6 %7 >> .\csvFiles\%2.csv)
cd batFiles