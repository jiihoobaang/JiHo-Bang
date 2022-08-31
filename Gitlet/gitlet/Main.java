<<<<<<< HEAD
package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Adithya Sagar, JiHo Bang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        Repository repo = new Repository();
        if (args.length == 0) {
            exitWithError("Please enter a command.");
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                repo.init();
                break;
            case "add":
                repo.add(args[1]);
                break;
            case "commit":
                repo.commit(args[1]);
                break;
            case "checkout":
                repo.checkout(args);
                break;
            case "log":
                repo.log();
                break;
            case "rm":
                repo.rm(args[1]);
                break;
            case "find":
                repo.find(args[1]);
                break;
            case "branch":
                repo.branch(args[1]);
                break;
            case "global-log":
                repo.globalLog();
                break;
            case "status":
                repo.status();
                break;
            case "reset":
                repo.reset(args[1]);
                break;
            case "rm-branch":
                repo.rmBranch(args[1]);
                break;
            case "merge":
                repo.merge(args[1]);
                break;
            default:
                Main.exitWithError("No command with that name exists.");

        }
    }

    public static void exitWithError(String message) {
        if (message != null && !message.equals("")) {
            System.out.println(message);
        }
        System.exit(0);
    }
}
=======
package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
}
>>>>>>> 25b4d4593f721c34e8d02038e24c1a213feffb63
