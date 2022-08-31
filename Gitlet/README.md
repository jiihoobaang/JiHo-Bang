## 1. How is your .gitlet directory structured? What files / classes are you using to represent your repository state, and what information is contained in these files / classes?

In our .gitlet directory, we are storing all current and old copies of files and metadata.

The .gitlet directory will be created by calling method init which will check if a .gitlet directory is already been made and if one hasn't been 
created, then it will create one. In the .gitlet directory, we will have 4 seperate directories dedicated to storing commits, blobs, 
branches of seperate versions of commits, and the staging area.

First the Commit class is made to represent a gitlet commit object. A commit can be considered a snapshot
of all selected files at any given point in time. In this 'commit' data such as log messages written by the author,
commit dates, the author, a reference point of the commit tree, and a reference to the parent commit that came before
it in that version of the file. Additionally, each file in a commit object will point to a blob data type that holds 
the contents of its file.Two important concepts of commit objects is that at any time we can 
restore old versions of files and different versions of files as well. To do this, we will save commit objects in a 
tree data type where each seperate version of the file is a branch of the tree.

Next we have the Repository class which stores each method the user can call (e.g. add, init, status)


## 2. What is the process of adding a file then committing? How does your .gitlet directory change after adding; then after committing?

To add a file, we will first check if the file exists in the directory. We will do this by checking if the name of the file we want to add is in the list of all the files in the directory using Utils.plainFilenamesIn. If the file does not exist, an error message will be printed and the program will exit without changing anything. If the file does exist, the file will be ready to be added. We will add the name of the file and the string inside the file in the form of a map. Since maps only allow for unique keys, staging a file that's already there will overwrite the file with its new contents. 

For commit, we will have a node that has the commit message, the names of the files and their contents (the file:string map we have in add), and the parent nodes. This only happens if the staging area is not empty since we clear the staging area after every commit. Commit will ignore any changes made to files after staging. After the commit command we will add this node to the commit tree and direct our head pointer to this newest commit node. The previous head node will automatically be assigned as the current head's parent. Each commit is identified by its SHA-1 id, which will be saved in a commits folder that can be later used for checkout and to see the log.

The staging area is somewhere in the .gitlet directory so adding files into the staging area will change the directory. Moreover, commit does not change any files in the working directory but will change files in the .gitlet directory by clearing the staging area after the commit command. Additionally, commits will be stored in another folder.

## 3. Provide three (3) distinct sequences of commands. 

## Command 1:
java gitlet.Main init
This will create a Gitlet system in the current directory. The command will not change or create any files in the system. It will have an initial empty commit with a simple message.
Given that this will likely be the first command to get all the other commands to even work, this command doesn't need any persisted information.
Algorithm:
First, we will check if the gitlet exists in the current directory. If it does, we will print an error message. If it doesn't we will create all the folders we need to get our program to work: staging, logs, commits, objects, etc. Then, we will create the initial commit, set up the commit tree, and point the head pointer to this commit. 
After the command finishes, we need to make sure that the current directory recognizes the gitlet version control system so that no other Gitlets can be run in that directory. Also, we need to make sure that the initial commit is stores properly since all repositories will share this initial commit.

## Command 2:
java gitlet.Main add [file name]
This will add files into the staging area so that they are ready to be committed. If a file with the same name is already in the staging area, the new version will overwrite the old version.
We will need a list of files in the directory and a list of files and its contents in the staging area. 
Algorithm:
To add a file, we will first check if the file exists in the directory. We will do this by checking if the name of the file we want to add is in the list of all the files in the directory using Utils.plainFilenamesIn. If the file does not exist, an error message will be printed and the program will exit without changing anything. If the file does exist, the file will be ready to be added. We will add the name of the file and the string inside the file in the form of a map. We will check if the file to be added is identical to the version in the current commit. If it is, we will not stage it to be added, and remove it from the staging area if it is already there.
We will need to properly add all appropriate files and their appropriate version to the staging area so that the next command will run properly. E.g. commit can clear the staging area, another add can properly access content files, etc.

## Command 3:
java gitlet.Main commit [message]
This command saves a snapshot of tracked files in the current commit and staging area so they can be restored at a later time. After each commit, the staging area will be cleared. 
To execute this command, we need add to properly put each file and its correct version in the staging area. Obviously, we will need the staging area and its contents to commit.
Algorithm:
First we will check if the staging area has something or if there is a commit message. If those two conditions are false (empty), we will output an error message. We will then create a new commit node, that includes the message and identification. We will add this node to the commit tree and point the head pointer to this newest commit node. The previous node will automatically be assigned as the parent node. We will then store all commits to the commit folder. And, clear the staging area so that new adds won't interfere with commit.
