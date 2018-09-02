UC Berekeley Summer 2013 CS61BL Project 3

- Tsion Behailu
- Hayden Na
- Michael Rowe
- YJ Yang

<h1>How to Test the Project Remotely</h1>

Hey guys,

Over the next few steps, I would like to discuss how this project can be tested
on a remote machine in 275 Soda. I have made some modifications to the Shell
scripts given by the course staff (e.g. runsuccess.sh, run.easy, run.medium,
run.hard), so that it is easier to test the project.
Because of these modifications, the Shell scripts in this project must be run
in the same <i>exact</i> order as follows. Please read carefully.

<h3>Before We Start</h3>

The following procedure makes a lot of remote Git calls.
During these Git commands, you might get an error message from Git
saying "SSL certificate problem." This error occurs because the machines
in 275 Soda use an outdated version of Git. Fortunately, there is a
workaround to this issue. When you get that error, just type in the following command:

<b>export GIT_SSL_NO_VERIFY=true</b>

Then that error will not happen again.

<h3>Step 1: Clone the Git Repository</h3>

You must first clone the GitHub repository into your Berkeley account.
Log in to any Berkeley CS SSH server. At the <b>home directory</b>
(the first directory you're in right after you login) - nowhere else -
type in the following command:

<b>git clone https://github.com/chcokr/cs61bl-proj3.git</b>

Your CS account now has a clone of the project on the home directory,
in a folder named 'cs61bl-proj3'. Do not change the name of this folder.

Now exit from your SSH terminal.

<h3>Step 2: Choosing the Remote Machine</h3>

Now recall that your CS account is shared across all of the machines in
275 Soda. So on any of the machines, you can find the 'cs61bl-proj3' folder
on the home directory. This means you can choose any machine to run our project on.

So here is the list of the machines in 275 Soda:

- Kearsarge
- KittyHawk
- Oriskany
- Princeton
- Randolph
- Ranger
- Saipan
- SanJacinto
- Saratoga
- Tarawa

Pick any one of these. Then SSH into (server-name).cs.berkeley.edu.
You are ready to move on.

A student has claimed on Piazza that different machines give out different
runtime results. So you might want to try as many of the above machines
as possible.

<h3>Step 3: Compiling the Codes</h3>

If you look at our GitHub repository, you will see that the <b>bin</b> folder
on your local repository, where Eclipse saves all the .class files,
is not part of the remote repository. This is because Git deliberately
ignores the bin folder while committing, pushing, pulling, merging, etc.
(this behavior is specified in the file named '.gitignore', which is
part of our repository). So now you have to create a <b>bin</b>
directory in your newly cloned repository on your CS account.

Go into the 'cs61bl-proj3' directory, and create a bin folder inside.
Then move into the <b>src</b> folder, and there run:

<b>javac -d ../bin Solver.java</b>

Now your bin folder must contain a number of .class files.

Every time you pull from GitHub into your CS repository,
don't forget to compile the new codes this way. Remember, we're not on
Eclipse - the .class files are <i>not</i> automatically generated.

<h3>Step 4: Run the Easy, Medium, Hard Tests</h3>

Now you are ready to perform the tests. Move back out to the 
'cs61bl-proj3' directory. There just type in one of the following:

- <b>given/easy/run.easy</b>
- <b>given/medium/run.medium</b>
- <b>given/hard/run.hard</b>

Then the corresponding test will begin, <i>using the resources of the
remote Soda machine you chose in Step 2</i>. So our Solver will run
more slowly than on your nice Macbook.

<i><b>Note</b>: If you are getting a "Permission denied" error at this step,
run the following commands, all four of them, on the 'cs61bl-proj3' directory</i>:

- <b>chmod +x given/checker/runsuccess.sh</b>
- <b>chmod +x given/easy/run.easy</b>
- <b>chmod +x given/medium/run.medium</b>
- <b>chmod +x given/hard/run.hard</b>

Each test contains about 30 challenges. If you get 'verified' after
a challenge, than our Solver succesfully finishes that challenge.
So our goal is to get 'verified' on every challenge.

<b>That's it! Good luck! :)</b>