package gitlet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
import java.util.List;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  Has the implementation for all Gitlet command including
 *  add, commit, remove, log, branch, etc.
 *
 *  @author Adithya Sagar, JiHo Bang
 */
public class Repository implements Serializable {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The directory storing blobs. */
    public static final File BLOB = join(GITLET_DIR, "blob");
    /** The directory that stores branches. */
    public static final File BRANCHES = join(GITLET_DIR, "branches");
    /** The data for the main branch */
    public static final File MAINBRANCH = join(BRANCHES, "main");
    /** The data the current branch. */
    public static final File CURRENTBRANCH = join(GITLET_DIR, "currentbranch");
    /** The directory for the staging area. Stores file names and SHA1. */
    public static final File STAGE = join(GITLET_DIR, "stagingarea");
    public static final File STAGING_AREA = join(STAGE, "stage");
    /** The directory the head commit. */
    public static final File HEAD = join(GITLET_DIR, "head");
    /** The directory that stores all commits. */
    public static final File COMMIT_DIR = Utils.join(GITLET_DIR, "commit");
    /** Staging area */
    private StagingArea stage = new StagingArea();


    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            BLOB.mkdir();
            BRANCHES.mkdir();
            STAGE.mkdir();
            COMMIT_DIR.mkdir();
            Utils.writeContents(HEAD, "");
            Utils.writeContents(STAGING_AREA, "");
            StagingArea stage = new StagingArea();
            Utils.writeObject(STAGING_AREA, stage);

            Commit initial = new Commit("initial commit", new HashMap<>(), null);
            String initialUID = initial.getUID();
            File commitUID = Utils.join(COMMIT_DIR, initialUID);
            commitUID.mkdir();
            File commitObject = Utils.join(commitUID, "object");
            Utils.writeObject(commitObject, initial);

            Utils.writeContents(CURRENTBRANCH, "main");
            Utils.writeContents(HEAD, initial.getUID());
            Utils.writeContents(MAINBRANCH, initial.getUID());
        }
    }

    public void add(String fileName) {
        List<String> checkFiles = Utils.plainFilenamesIn("./");
        boolean found = false;
        for (String file : checkFiles) {
            if (file.equals(fileName)) {
                found = true;
            }
        }
        if (!found) {
            Main.exitWithError("File does not exist.");
        }

        byte[] blob = Utils.readContents(Utils.join(".", fileName));
        String blobSHA1 = Utils.sha1(blob);
        String branch = Utils.readContentsAsString(CURRENTBRANCH);
        File bran = Utils.join(BRANCHES, branch);
        branch = Utils.readContentsAsString(bran);
        Commit currCommit = commitFromSHA(branch);
        File blobFile = Utils.join(BLOB, blobSHA1);
        Utils.writeContents(blobFile, blob);
        StagingArea current = Utils.readObject(STAGING_AREA, StagingArea.class);
        if (current.getRemoved().containsKey(fileName)) {
            current.getRemoved().remove(fileName);
            Utils.writeObject(STAGING_AREA, current);
            return;
        }

        for (String key : currCommit.getBlobs().keySet()) {
            if (key.equals(fileName)) {
                if (currCommit.getBlobs().get(key).equals(blobSHA1)) {
                    return;
                }
            }
        }

        if (current.addToAdded(fileName, blobSHA1)) {
            for (String key : current.getAdded().keySet()) {
                current.addToAdded(key, current.getAdded().get(key));
            }
            Utils.writeObject(STAGING_AREA, current);
        }
    }

    public void commit(String message) {
        StagingArea stageArea = Utils.readObject(STAGING_AREA, StagingArea.class);
        if (stageArea.isEmpty()) {
            Main.exitWithError("No changes added to the commit.");
        } else if (message.isEmpty()) {
            Main.exitWithError("Please enter a commit message.");
        }
        String branch = Utils.readContentsAsString(CURRENTBRANCH);
        File branchy = Utils.join(BRANCHES, branch);
        String parent = Utils.readContentsAsString(branchy);
        File lastCom = Utils.join(COMMIT_DIR, parent);
        lastCom = Utils.join(lastCom, "object");
        Commit last = Utils.readObject(lastCom, Commit.class);
        HashMap<String, String> track = last.getBlobs();
        StagingArea current = Utils.readObject(STAGING_AREA, StagingArea.class);
        for (String key : current.getAdded().keySet()) {
            track.put(key, current.getAdded().get(key));
        }

        for (String key : current.getRemoved().keySet()) {
            track.remove(key);
        }

        Commit com = new Commit(message, track, parent);
        String comUID = com.getUID();
        File commitUID = Utils.join(COMMIT_DIR, comUID);

        commitUID.mkdir();
        File commitObject = Utils.join(commitUID, "object");
        Utils.writeObject(commitObject, com);
        for (String key : track.keySet()) {
            File keyFile = Utils.join(commitUID, key);
            Utils.writeContents(keyFile, track.get(key));
        }
        Utils.writeContents(HEAD, comUID);
        Utils.writeContents(branchy, comUID);
        stageArea.reset();
        Utils.writeObject(STAGING_AREA, stageArea);
    }

    public void commitMerge(String message, String mergeParent) {
        StagingArea stageArea = Utils.readObject(STAGING_AREA, StagingArea.class);
        if (stageArea.isEmpty()) {
            Main.exitWithError("No changes added to the commit.");
        } else if (message.isEmpty()) {
            Main.exitWithError("Please enter a commit message.");
        }
        String branch = Utils.readContentsAsString(CURRENTBRANCH);
        File branchy = Utils.join(BRANCHES, branch);
        String parent = Utils.readContentsAsString(branchy);
        File lastCom = Utils.join(COMMIT_DIR, parent);
        lastCom = Utils.join(lastCom, "object");
        Commit last = Utils.readObject(lastCom, Commit.class);
        HashMap<String, String> track = last.getBlobs();
        StagingArea current = Utils.readObject(STAGING_AREA, StagingArea.class);
        for (String key : current.getAdded().keySet()) {
            track.put(key, current.getAdded().get(key));
        }

        for (String key : current.getRemoved().keySet()) {
            track.remove(key);
        }

        Commit com = new Commit(message, track, parent, mergeParent);
        String comUID = com.getUID();
        File commitUID = Utils.join(COMMIT_DIR, comUID);

        commitUID.mkdir();
        File commitObject = Utils.join(commitUID, "object");
        Utils.writeObject(commitObject, com);
        for (String key : track.keySet()) {
            File keyFile = Utils.join(commitUID, key);
            Utils.writeContents(keyFile, track.get(key));
        }
        Utils.writeContents(HEAD, comUID);
        Utils.writeContents(branchy, comUID);
        stageArea.reset();
        Utils.writeObject(STAGING_AREA, stageArea);
    }

    public Commit getLastCommit() {
        String sha1 = Utils.readContentsAsString(HEAD);

        File sha1File = Utils.join(COMMIT_DIR, sha1);
        File commitObject = Utils.join(sha1File, "object");
        Commit current = Utils.readObject(commitObject, Commit.class);
        return current;
    }

    public void checkout(String[] args) {
        if (args.length == 2) {
            checkoutBranch(args[1]);
        } else if (args[1].equals("--") && args.length == 3) {
            checkoutPrev(args[2]);
        } else if (args[2].equals("--") && args.length == 4) {
            checkoutIdFile(args[1], args[3]);
        } else {
            Main.exitWithError("Incorrect operands.");
        }
    }

    public void checkoutPrev(String file) {
        Commit prevCommit = getLastCommit();
        if (!prevCommit.getBlobs().containsKey(file)) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            Path path = Paths.get(file);
            String way = path.toAbsolutePath().toString();
            Utils.restrictedDelete(way);
            File newSave = new File(way);
            File fromBlob = Utils.join(COMMIT_DIR, prevCommit.getUID());
            fromBlob = Utils.join(fromBlob, file);
            String sha1 = Utils.readContentsAsString(fromBlob);
            fromBlob = Utils.join(BLOB, sha1);
            byte[] contents = Utils.readContents(fromBlob);
            Utils.writeContents(newSave, contents);
        }
    }

    public void checkoutIdFile(String id, String file) {
        File specificFile = Utils.join(COMMIT_DIR, id);
        specificFile = Utils.join(specificFile, "object");

        for (File abbr : COMMIT_DIR.listFiles()) {
            if (abbr.getName().startsWith(id)) {
                specificFile = Utils.join(COMMIT_DIR, abbr.getName());
                specificFile = Utils.join(specificFile, "object");
            }
        }

        Commit specificCommit = null;
        try {
            specificCommit = Utils.readObject(specificFile, Commit.class);
        } catch (IllegalArgumentException e) {
            Main.exitWithError("No commit with that id exists.");
        }
        if (specificCommit.getBlobs().containsKey(file)) {
            Path path = Paths.get(file);
            String way = path.toAbsolutePath().toString();
            Utils.restrictedDelete(way);
            File newSave = new File(way);
            File fromBlob = Utils.join(COMMIT_DIR, specificCommit.getUID());
            fromBlob = Utils.join(fromBlob, file);
            String sha1 = Utils.readContentsAsString(fromBlob);
            fromBlob = Utils.join(BLOB, sha1);
            byte[] contents = Utils.readContents(fromBlob);
            Utils.writeContents(newSave, contents);
        } else {
            Main.exitWithError("File does not exist in that commit.");
        }
    }

    public void checkoutBranch(String branch) {
        File check = Utils.join(BRANCHES, branch);

        if (!check.exists()) {
            System.out.println("No such branch exists.");
            return;
        } else if (branch.equals(Utils.readContentsAsString(CURRENTBRANCH))) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        String tarComSHA = Utils.readContentsAsString(check);
        File tarCom = Utils.join(COMMIT_DIR, tarComSHA);
        tarCom = Utils.join(tarCom, "object");
        Commit target = Utils.readObject(tarCom, Commit.class);
        String lastBranch = Utils.readContentsAsString(CURRENTBRANCH);
        String shaLast = Utils.readContentsAsString(Utils.join(BRANCHES, lastBranch));
        File lastCom = Utils.join(COMMIT_DIR, shaLast);
        lastCom = Utils.join(lastCom, "object");
        Commit last = Utils.readObject(lastCom, Commit.class);
        for (String file : Utils.plainFilenamesIn(CWD)) {

            if (target.getBlobs().containsKey(file) && !last.getBlobs().containsKey(file)) {
                Main.exitWithError("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.");
            }
        }

        for (String key : last.getBlobs().keySet()) {
            if (!target.getBlobs().containsKey(key)) {
                Path path = Paths.get(key);
                String way = path.toAbsolutePath().toString();
                Utils.restrictedDelete(way);
            }
        }
        for (String key : target.getBlobs().keySet()) {
            Path path = Paths.get(key);
            String way = path.toAbsolutePath().toString();
            File change = new File(way);
            File saved = Utils.join(BLOB, target.getBlobs().get(key));
            byte[] contents = Utils.readContents(saved);
            Utils.writeContents(change, contents);
        }
        Utils.writeContents(CURRENTBRANCH, branch);
        stage.reset();
        Utils.writeObject(STAGING_AREA, stage);
    }

    public void log() {
        String lastBranch = Utils.readContentsAsString(CURRENTBRANCH);
        String shaLast = Utils.readContentsAsString(Utils.join(BRANCHES, lastBranch));
        File lastCom = Utils.join(COMMIT_DIR, shaLast);
        lastCom = Utils.join(lastCom, "object");
        Commit iterate = Utils.readObject(lastCom, Commit.class);
        while (iterate.getParentUID() != null) {
            System.out.println("===");
            System.out.println("commit " + iterate.getUID());
            System.out.println("Date: " + iterate.getTime());
            System.out.println(iterate.getMessage());
            System.out.println();
            String iterateParent = iterate.getParentUID();
            File parentFile = Utils.join(COMMIT_DIR, iterateParent);
            parentFile = Utils.join(parentFile, "object");
            iterate = Utils.readObject(parentFile, Commit.class);
        }

        System.out.println("===");
        System.out.println("commit " + iterate.getUID());
        System.out.println("Date: " + iterate.getTime());
        System.out.println(iterate.getMessage());
        System.out.println();
    }

    public void rm(String file) {
        StagingArea currentStage = Utils.readObject(STAGING_AREA, StagingArea.class);
        String branch = Utils.readContentsAsString(CURRENTBRANCH);
        File bran = Utils.join(BRANCHES, branch);
        branch = Utils.readContentsAsString(bran);
        Commit currentCommit = commitFromSHA(branch);
        int counter = 0;
        if (currentStage.getAdded().containsKey(file)) {
            currentStage.getAdded().remove(file);
            counter++;
        }
        if (currentCommit.getBlobs().containsKey(file)) {
            Path path = Paths.get(file);
            String way = path.toAbsolutePath().toString();
            currentStage.getRemoved().put(file, currentCommit.getBlobs().get(file));
            Utils.restrictedDelete(way);
            counter++;
        }
        Utils.writeObject(STAGING_AREA, currentStage);

        if (counter == 0) {
            System.out.println("No reason to remove the file.");
            return;
        }

    }

    public void find(String message) {
        int checker = 0;
        String[] files = COMMIT_DIR.list();
        for (String sha1 : files) {
            File comObject = Utils.join(COMMIT_DIR, sha1);
            comObject = Utils.join(comObject, "object");
            Commit com = Utils.readObject(comObject, Commit.class);
            if (com.getMessage().equals(message)) {
                System.out.println(sha1);
                checker++;
            }
        }
        if (checker == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void globalLog() {
        String[] files = COMMIT_DIR.list();
        for (String sha1 : files) {
            File comObject = Utils.join(COMMIT_DIR, sha1);
            comObject = Utils.join(comObject, "object");
            Commit com = Utils.readObject(comObject, Commit.class);

            System.out.println("===");
            System.out.println("commit " + com.getUID());
            System.out.println("Date: " + com.getTime());
            System.out.println(com.getMessage());
            System.out.println("");
        }
    }

    public void branch(String name) {
        File newBranch = Utils.join(BRANCHES, name);
        if (!newBranch.exists()) {
            String sha1 = Utils.readContentsAsString(HEAD);
            Utils.writeContents(newBranch, sha1);
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    public void status() {
        if (!GITLET_DIR.exists()) {
            Main.exitWithError("Not in an initialized Gitlet directory.");
        }

        StagingArea current = Utils.readObject(STAGING_AREA, StagingArea.class);
        String head = Utils.readContentsAsString(CURRENTBRANCH);

        System.out.println("=== Branches ===");
        ArrayList<String> branches = new ArrayList<>(Utils.plainFilenamesIn(BRANCHES));
        for (String file : branches) {
            if (file.equals(head)) {
                System.out.print("*");
            }
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        List<String> addOrdered = new ArrayList<>(current.getAdded().keySet());
        java.util.Collections.sort(addOrdered);
        for (String file : addOrdered) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        List<String> removeOrdered = new ArrayList<>(current.getRemoved().keySet());
        java.util.Collections.sort(removeOrdered);
        for (String file : removeOrdered) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public void reset(String commitID) {
        File specificFile = Utils.join(COMMIT_DIR, commitID);
        specificFile = Utils.join(specificFile, "object");
        String lastBranch = Utils.readContentsAsString(CURRENTBRANCH);
        String lastSHA = Utils.readContentsAsString(Utils.join(BRANCHES, lastBranch));
        File last = Utils.join(COMMIT_DIR, lastSHA);
        last = Utils.join(last, "object");
        Commit lastCommit = Utils.readObject(last, Commit.class);
        StagingArea currentStage = Utils.readObject(STAGING_AREA, StagingArea.class);

        if (!specificFile.exists()) {
            Main.exitWithError("No commit with that id exists.");
        }
        Commit specificCommit = Utils.readObject(specificFile, Commit.class);
        for (String file : Utils.plainFilenamesIn(CWD)) {
            if (specificCommit.getBlobs().containsKey(file)
                    && !lastCommit.getBlobs().containsKey(file)) {
                Main.exitWithError("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.");
            }

            if (!specificCommit.getBlobs().containsKey(file)
                    && lastCommit.getBlobs().containsKey(file)) {
                if (currentStage.getAdded().containsKey(file)) {
                    currentStage.getAdded().remove(file);
                    Utils.writeObject(STAGING_AREA, currentStage);
                }
                if (lastCommit.getBlobs().containsKey(file)) {
                    Path path = Paths.get(file);
                    String way = path.toAbsolutePath().toString();
                    currentStage.getRemoved().put(file, lastCommit.getBlobs().get(file));
                    currentStage.reset();
                    Utils.restrictedDelete(way);
                    Utils.writeObject(STAGING_AREA, currentStage);
                }
            }
        }

        for (String file: specificCommit.getBlobs().keySet()) {
            checkoutIdFile(commitID, file);
        }
        currentStage.reset();
        String currentBranch = Utils.readContentsAsString(CURRENTBRANCH);
        File branch = Utils.join(BRANCHES, currentBranch);
        Utils.writeContents(branch, commitID);
    }

    public void rmBranch(String branchName) {
        String currBranch = Utils.readContentsAsString(CURRENTBRANCH);

        if (branchName.equals(currBranch)) {
            Main.exitWithError("Cannot remove the current branch.");
        }

        List<String> allBranches = Utils.plainFilenamesIn(BRANCHES);
        if (allBranches.contains(branchName)) {
            File branchFile = Utils.join(BRANCHES, branchName);
            branchFile.delete();
        } else {
            Main.exitWithError("A branch with that name does not exist.");
        }
    }

    private boolean mergeHelper1(String branch) {
        StagingArea stageArea = Utils.readObject(STAGING_AREA, StagingArea.class);
        File branchSHA = Utils.join(BRANCHES, branch);
        int counter = 0;
        if (!stageArea.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return true;
        } else if (!Utils.join(BRANCHES, branch).exists()) {
            System.out.println("A branch with that name does not exist.");
            return true;
        } else if (Utils.readContentsAsString(CURRENTBRANCH).equals(branch)) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        }
        return false;
    }

    public void mergeHelper2(Commit lastCommit, Commit specificCommit, Commit split) {
        int counter = 0;
        for (String key : specificCommit.getBlobs().keySet()) {
            if (!split.getBlobs().keySet().contains(key)) {
                checkoutIdFile(specificCommit.getUID(), key);
                StagingArea current = Utils.readObject(STAGING_AREA, StagingArea.class);
                current.addToAdded(key, specificCommit.getBlobs().get(key));
                Utils.writeObject(STAGING_AREA, current);
            } else if ((!specificCommit.getBlobs().get(key).equals(split.getBlobs().get(key)))
                    && lastCommit.getBlobs().containsKey(key)
                    && lastCommit.getBlobs().get(key).equals(split.getBlobs().get(key))) {
                checkoutIdFile(specificCommit.getUID(), key);
                StagingArea current = Utils.readObject(STAGING_AREA, StagingArea.class);
                current.addToAdded(key, specificCommit.getBlobs().get(key));
                Utils.writeObject(STAGING_AREA, current);
            }
        }

        for (String key : split.getBlobs().keySet()) {
            if (lastCommit.getBlobs().containsKey(key)
                    && lastCommit.getBlobs().get(key).equals(split.getBlobs().get(key))
                    && !specificCommit.getBlobs().containsKey(key)) {

                rm(key);
            } else if (lastCommit.getBlobs().containsKey(key)
                    && !specificCommit.getBlobs().containsKey(key)
                    && !lastCommit.getBlobs().get(key).equals(split.getBlobs().get(key))) {
                counter++;
                String contents = "<<<<<<< HEAD" + "\n";
                File blob = Utils.join(BLOB, lastCommit.getBlobs().get(key));
                contents = contents + Utils.readContentsAsString(blob)
                        + "=======" + "\n" + ">>>>>>>\n";
                Path path = Paths.get(key);
                String way = path.toAbsolutePath().toString();
                File w = new File(way);
                Utils.writeContents(w, contents);
            } else if (!lastCommit.getBlobs().containsKey(key)
                    && specificCommit.getBlobs().containsKey(key)
                    && !specificCommit.getBlobs().get(key).equals(split.getBlobs().get(key))) {
                counter++;
                String contents = "<<<<<<< HEAD" + "\n";
                contents = contents + "=======" + "\n";
                File blob = Utils.join(BLOB, specificCommit.getBlobs().get(key));
                contents = contents + Utils.readContentsAsString(blob) + ">>>>>>>\n";
                Path path = Paths.get(key);
                String way = path.toAbsolutePath().toString();
                File w = new File(way);
                Utils.writeContents(w, contents);

            } else if (lastCommit.getBlobs().containsKey(key)
                    && specificCommit.getBlobs().containsKey(key)
                    && !specificCommit.getBlobs().get(key).equals(split.getBlobs().get(key))
                    && !lastCommit.getBlobs().get(key).equals(split.getBlobs().get(key))
                    && !specificCommit.getBlobs().get(key).equals(lastCommit.getBlobs().get(key))) {

                counter++;
                String contents = "<<<<<<< HEAD" + "\n";
                File blob = Utils.join(BLOB, lastCommit.getBlobs().get(key));
                contents = contents + Utils.readContentsAsString(blob);
                contents = contents + "=======" + "\n";
                blob = Utils.join(BLOB, specificCommit.getBlobs().get(key));
                contents = contents + Utils.readContentsAsString(blob) + ">>>>>>>\n";

                Path path = Paths.get(key);
                String way = path.toAbsolutePath().toString();
                File w = new File(way);
                Utils.writeContents(w, contents);
                add(key);
            }
        }
        if (counter != 0) {
            System.out.println("Encountered a merge conflict.");

        }
    }
    public void merge(String branch) {
        if (mergeHelper1(branch)) {
            return;
        }
        int counter = 0;
        File branchSHA = Utils.join(BRANCHES, branch);
        String commitID = Utils.readContentsAsString(branchSHA);
        File specificFile = Utils.join(COMMIT_DIR, commitID);
        specificFile = Utils.join(specificFile, "object");
        String lastBranch = Utils.readContentsAsString(CURRENTBRANCH);
        String lastSHA = Utils.readContentsAsString(Utils.join(BRANCHES, lastBranch));
        File last = Utils.join(COMMIT_DIR, lastSHA);
        last = Utils.join(last, "object");
        Commit lastCommit = Utils.readObject(last, Commit.class);
        Commit specificCommit = Utils.readObject(specificFile, Commit.class);
        for (String file : Utils.plainFilenamesIn(CWD)) {
            if (specificCommit.getBlobs().containsKey(file)
                    && !lastCommit.getBlobs().containsKey(file)) {
                Main.exitWithError("There is an untracked file in the way; delete it, "
                        + "or add and commit it first.");
            }
        }
        ArrayList<String> lastB = new ArrayList<>();
        ArrayList<String> specB = new ArrayList<>();

        while (lastCommit.getParentUID() != null) {
            lastB.add(lastCommit.getUID());
            String parent = lastCommit.getParentUID();
            File paren = Utils.join(COMMIT_DIR, parent);
            paren = Utils.join(paren, "object");
            if (lastCommit.getMergeParent() != null) {
                lastB.add(lastCommit.getMergeParent());
            }
            lastCommit = Utils.readObject(paren, Commit.class);

        }
        lastB.add(lastCommit.getUID());

        while (specificCommit.getParentUID() != null) {
            specB.add(specificCommit.getUID());
            String parent = specificCommit.getParentUID();
            File paren = Utils.join(COMMIT_DIR, parent);
            paren = Utils.join(paren, "object");
            if (specificCommit.getMergeParent() != null) {
                specB.add(lastCommit.getMergeParent());
            }
            specificCommit = Utils.readObject(paren, Commit.class);

        }
        specB.add(specificCommit.getUID());

        String ancestorSHA = null;
        int index = -1;
        for (int i = 0; i < specB.size(); i++) {
            if (lastB.contains(specB.get(i))) {
                ancestorSHA = specB.get(i);
                index = i;
                break;
            }
        }
        if (ancestorSHA == null) {
            return;
        }
        if (lastSHA.equals(ancestorSHA)) {
            System.out.println("Current branch fast-forwarded.");
            checkoutBranch(branch);
            return;
        }
        if (lastB.contains(commitID)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        specificCommit = commitFromSHA(commitID);
        lastCommit = commitFromSHA(lastSHA);
        Commit split = commitFromSHA(ancestorSHA);
        mergeHelper2(lastCommit, specificCommit, split);
        commitMerge("Merged " + branch + " into " + lastBranch + ".", specificCommit.getUID());
    }

    private Commit commitFromSHA(String sha) {
        File com = Utils.join(COMMIT_DIR, sha);
        com = Utils.join(com, "object");
        return Utils.readObject(com, Commit.class);
    }
}
