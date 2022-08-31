package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import byow.TileEngine.Tileset;

import java.awt.*;
import java.io.*;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 40;

    private World world;
    private String seed;
    private TETile[][] save;
    private World worldSave;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File SAVEDWORLD = Utils.join(CWD, "world.txt");

    public Engine() {
        //world = new World(WIDTH, HEIGHT, "");
        seed = "";
        save = null;
        if (!SAVEDWORLD.exists()) {
            Utils.writeObject(SAVEDWORLD, new World(true));
        }
    }



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        StdDraw.setCanvasSize(this.WIDTH * 16, this.HEIGHT * 16);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        int place = this.HEIGHT / 4;
        StdDraw.text(this.WIDTH / 2, place, "Quit (Q)");
        StdDraw.text(this.WIDTH / 2, place * 2, "Load (L)");
        StdDraw.text(this.WIDTH / 2, place * 3, "New World (N)");
        StdDraw.show();
        int count = 1;
        char user;

        while (count > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                user = StdDraw.nextKeyTyped();
                if (user == 'n') {
                    //World wor = new World("");
                    StdDraw.clear(Color.BLACK);
                    StdDraw.setPenColor(Color.WHITE);
                    StdDraw.setFont(fontBig);
                    StdDraw.setXscale(0, this.WIDTH);
                    StdDraw.setYscale(0, this.HEIGHT);
                    StdDraw.text(this.WIDTH / 2, place * 3, "Enter seed:");
                    String inputSeed = "";
                    while (count > 0) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char in = StdDraw.nextKeyTyped();
                            if (in == 's') {
                                break;
                            }
                            inputSeed = inputSeed + in;
                            StdDraw.clear(Color.BLACK);
                            StdDraw.setPenColor(Color.WHITE);
                            StdDraw.setFont(fontBig);
                            StdDraw.setXscale(0, this.WIDTH);
                            StdDraw.setYscale(0, this.HEIGHT);
                            StdDraw.text(this.WIDTH / 2, place * 3, "Enter seed:");
                            StdDraw.text(this.WIDTH / 2, place * 2, inputSeed);
                            StdDraw.show();
                        }
                    }
                    TETile app = appearanceMenu();
                    String name = nameMenu();

                    World wor = new World(inputSeed, app, name);
                    wor.startGame(app, name);

                } else if (user == 'q' || user == 'Q') {
                    System.exit(0);
                } else if (user == 'l' || user == 'L') {
                    if (Utils.readObject(SAVEDWORLD, World.class).isEmpty()) {
                        System.exit(0);
                    } else {
                        World wor = Utils.readObject(SAVEDWORLD, World.class);
                        //TETile app = appearanceMenu();
                        wor.continueGame(wor);
                    }
                }
            }
        }
    }

    public TETile appearanceMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        int place = this.HEIGHT / 6;
        StdDraw.text(this.WIDTH / 2, place * 5, "Would you like to play "
                + "with a different appearance?");
        StdDraw.text(this.WIDTH / 2, place * 4, "Avatar $ (1)");
        StdDraw.text(this.WIDTH / 2, place * 3, "Avatar ? (2)");
        StdDraw.text(this.WIDTH / 2, place * 2, "Avatar % (3)");
        StdDraw.text(this.WIDTH / 2, place, "Play Default (4)");
        String content = "";
        TETile result = Tileset.AVATAR;
        int count = 1;
        while (count > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                content = content + c;
                if (c == '1' || c == '1') {
                    result = Tileset.AVATARTWO;
                } else if (c == '2' || c == '2') {
                    result = Tileset.AVATARTHREE;
                } else if (c == '3' || c == '3') {
                    result = Tileset.AVATARFOUR;
                } else if (c == '4' || c == '4') {
                    result = Tileset.AVATAR;
                }
                count--;
            }
        }
        return result;
    }

    public String nameMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.setXscale(0, this.WIDTH);
        StdDraw.setYscale(0, this.HEIGHT);
        int place = this.HEIGHT / 6;
        StdDraw.text(this.WIDTH / 2, place * 5, "Would you like to name your avatar?");
        StdDraw.text(this.WIDTH / 2, place * 4, "If no, press 0");
        StdDraw.text(this.WIDTH / 2, place * 3, "If yes, type your desired name, then press 1.");
        StdDraw.text(this.WIDTH / 2, place, "Caution: Clicking the delete key will "
                + "cause the program to exit so be careful!");
        String content = "";
        int count = 1;
        while (count > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == '1') {
                    count--;
                } else if (c == '0') {
                    content = "";
                    return content;
                }
                content = content + c;
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(fontBig);
                StdDraw.setXscale(0, this.WIDTH);
                StdDraw.setYscale(0, this.HEIGHT);
                StdDraw.text(this.WIDTH / 2, place * 5, "Would you like to name your avatar?");
                StdDraw.text(this.WIDTH / 2, place * 4, "If no, press 0");
                StdDraw.text(this.WIDTH / 2, place * 3,
                        "If yes, type your desired name, then press 1.");
                StdDraw.text(this.WIDTH / 2, place * 2, content);
                StdDraw.text(this.WIDTH / 2, place,
                        "Caution: Clicking the delete key will cause"
                                + " the program to exit so be careful!");
                StdDraw.show();
            }
        }
        String result = "";
        for (int i = 0; i < content.toCharArray().length; i++) {
            char x = content.charAt(i);
            if (x != '1') {
                result = result + x;
            }
        }
        content = result;
        return content;
    }


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TERenderer t = new TERenderer();
        ter.initialize(90, 40);
        String copy = input;
        String numb;
        if (input.isEmpty()) {
            numb = "";
        } else {
            numb = copy.replaceAll("[^0-9]", "");
        }
        if (input.length() >= 1 && input.charAt(0) == 'l') {
            //World wor = new World(save, numb);
            World wor = Utils.readObject(SAVEDWORLD, World.class);

            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == ':' && !(i + 1 >= input.length())
                        && input.charAt(i + 1) == 'q') {
                    Utils.writeObject(SAVEDWORLD, wor);
                    t.renderFrame(wor.getWorld());
                    return wor.getWorld();
                } else if (input.charAt(i) == 'w' || input.charAt(i) == 's'
                        || input.charAt(i) == 'a' || input.charAt(i) == 'd') {
                    wor.moveAvatar(input.charAt(i));
                }
            }
            t.renderFrame(wor.getWorld());
            return wor.getWorld();
        } else {
            World wor = new World(90, 40, numb, Tileset.AVATARTWO, "");

            wor.generateRooms();
            wor.connect();
            wor.createPlayer();
            wor.generateFlowers();
            //Main.startGame(numb, wor);
            for (int i = 1; i < input.length(); i++) {
                if (input.charAt(i) == ':' && !(i + 1 >= input.length())
                        && input.charAt(i + 1) == 'q') {
                    save = wor.getWorld();
                    worldSave = wor;
                    t.renderFrame(wor.getWorld());
                    Utils.writeObject(SAVEDWORLD, wor);
                    return wor.getWorld();
                } else if (input.charAt(i) == 'w' || input.charAt(i) == 's'
                        || input.charAt(i) == 'a' || input.charAt(i) == 'd') {
                    wor.moveAvatar(input.charAt(i));
                }
            }

            TETile[][] finalWorldFrame = wor.getWorld();
            save = finalWorldFrame;
            worldSave = wor;
            t.renderFrame(wor.getWorld());
            return finalWorldFrame;
        }
    }

    public String getSeed() {
        return this.seed;
    }
}
