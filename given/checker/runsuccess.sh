#!/usr/bin/bash
# usage: runsuccess initfile goalfile type, where there is a solution
#   type is either "hard" or "medium"
echo "Checking a puzzle in 80 seconds; last script change = March 26, 2011"
ulimit -t 80
testdir=~cs61bl/code/proj3/$3
j=/usr/sww/opt/jdk-1.7.0_25/bin
/bin/rm -f /tmp/out$$
echo $1 " " $2
$j/java -classpath ~/cs61bl-proj3/bin Solver $testdir/$1 $testdir/$2 > /tmp/out$$
if test "$?" -ne 0
then
    echo "*** Wrong exit status"
fi
$j/java -classpath ~/cs61bl-proj3/given/checker Checker $testdir/$1 $testdir/$2 < /tmp/out$$
if test "$?" -ne 0
then
    echo "*** Incorrect solver output"
fi
/bin/rm -f /tmp/out$$
echo " "
