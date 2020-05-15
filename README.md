# Localization-of-Bug-to-Commit

Graph Diff ---> Computes the difference between Original.java and Revised.java files which are in the local system and tokenizes the difference file using tokenize.java

DiffFilesInCommit file:
1. Clones a remote repository.
2. Iterates through all the commits of the repository and for each commit we list all the modified files and for each 
   modified file we output the current version and previous version of the modified file into Original.java and Revised.java.
3. Now we call GraphDiff in order to tokenize the difference between the two files. 
