package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Random;

public class World implements Serializable {
    private int width;
    private int height;
    private TETile[][] world;
    private ArrayList<String> allRooms;
    private ArrayList<Point> allRoomPoints;
    private ArrayList<Point> allWallPoints;
    private ArrayList<Room> allCenterRooms;
    private ArrayList<Point> allCenterPoints;
    private ArrayList<Point> currPos;
    private String seed;
    private boolean gameOver = false;
    private ArrayList<Point> allFloorPoints;
    private boolean empty;

    private TETile avatar;
    private String avatarName;
    private String curDescription;

    public World(int width, int height, String seed, TETile avatar, String name) {
        this.empty = false;
        this.width = width;
        this.height = height;
        this.world = new TETile[width][height];
        this.allRooms = new ArrayList<String>();
        this.allRoomPoints = new ArrayList<Point>();
        this.allFloorPoints = new ArrayList<Point>();
        this.allWallPoints = new ArrayList<Point>();
        this.currPos = new ArrayList<Point>();
        this.allCenterPoints = new ArrayList<Point>();
        allCenterRooms = new ArrayList<Room>();
        this.seed = seed;
        this.avatar = avatar;
        this.avatarName = name;
        curDescription = "";
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        //StdDraw.setXscale(0, this.width);
        //StdDraw.setYscale(0, this.height);
    }
    public World(String seed, TETile av, String name) {
        this.empty = false;
        this.width = 90;
        this.height = 40;
        this.world = new TETile[width][height];
        this.allRooms = new ArrayList<String>();
        this.allRoomPoints = new ArrayList<Point>();
        this.allFloorPoints = new ArrayList<Point>();
        this.allWallPoints = new ArrayList<Point>();
        this.currPos = new ArrayList<Point>();
        this.allCenterPoints = new ArrayList<Point>();
        this.avatar = av;
        this.avatarName = name;
        allCenterRooms = new ArrayList<Room>();
        this.seed = seed;
        curDescription = "";
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
    }

    public World(boolean empty) {
        this.empty = empty;
    }


    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }
    public void startGame(TETile av, String name) {
        TERenderer ter = new TERenderer();
        ter.initialize(90, 40);

        World wor = new World(90, 40, this.seed, av, name);

        wor.generateRooms();
        wor.connect();
        wor.createPlayer();
        wor.generateFlowers();
        ter.renderFrame(wor.getWorld());


        char user;
        char qCheck;
        while (!gameOver) {
            wor.drawDisplay();

            if (StdDraw.hasNextKeyTyped()) {
                user = StdDraw.nextKeyTyped();
                if (user == ':') {
                    int count = 0;
                    while (count == 0) {
                        if (StdDraw.hasNextKeyTyped()) {
                            qCheck = StdDraw.nextKeyTyped();
                            if (qCheck == 'q' || qCheck == 'Q') {
                                Utils.writeObject(Engine.SAVEDWORLD, wor);
                                System.exit(0);
                            } else {
                                user = qCheck;
                            }
                            count++;
                        }
                    }
                }
                wor.moveAvatar(user);
                ter.renderFrame(wor.getWorld());
            }
        }
    }

    public void continueGame(World wor) {
        TERenderer ter = new TERenderer();
        ter.initialize(90, 40);
        ter.renderFrame(wor.getWorld());

        char qCheck;
        char user;
        while (!gameOver) {
            wor.drawDisplay();
            if (StdDraw.hasNextKeyTyped()) {
                user = StdDraw.nextKeyTyped();
                if (user == ':') {
                    int count = 0;
                    while (count == 0) {
                        if (StdDraw.hasNextKeyTyped()) {
                            qCheck = StdDraw.nextKeyTyped();
                            if (qCheck == 'q' || qCheck == 'Q') {
                                Utils.writeObject(Engine.SAVEDWORLD, wor);
                                System.exit(0);
                            } else {
                                user = qCheck;
                            }
                            count++;
                        }
                    }
                }
                wor.moveAvatar(user);
                ter.renderFrame(wor.getWorld());
            }
        }
    }


    public String getSeed() {
        return seed;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public void drawDisplay() {
        StdDraw.setPenColor(Color.WHITE);
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX >= world.length) {
            mouseX = world.length - 1;
        } else if (mouseX < 0) {
            mouseX = 0;
        }

        if (mouseY >= world[0].length) {
            mouseY = world[0].length - 1;
        } else if (mouseY < 0) {
            mouseY = 0;
        }

        TETile mouseTile = world[mouseX][mouseY];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm: z");
        Date currTime = new Date(System.currentTimeMillis());
        StdDraw.text(10, 38, "Current Time: " + formatter.format(currTime));
        StdDraw.text(5, 37, "Avatar Name:" + avatarName);
        StdDraw.text(5, 36, "Current Tile: " + mouseTile.description());
        curDescription = mouseTile.description();


        StdDraw.show();
        StdDraw.pause(50);
    }

    public ArrayList<String> roomData(Point topL, Point bottomL, Point bottomR) {
        int roomW = Math.abs(topL.getY() - bottomL.getY());
        int roomL = Math.abs(bottomL.getX() - bottomR.getX());
        ArrayList<String> pointData = new ArrayList<String>();
        for (int x = bottomL.getX(); x < bottomR.getX(); x++) {
            for (int y = bottomL.getY(); y < topL.getY(); y++) {
                Point point = new Point(x, y);
                pointData.add(point.getPointAsString());
            }
        }
        for (int x = bottomL.getX(); x < bottomR.getX(); x++) {
            int y = bottomL.getY();
            Point point = new Point(x, y + roomW);
            pointData.add(point.getPointAsString());
        }
        for (int y = bottomL.getY(); y < topL.getY(); y++) {
            int x = bottomL.getX();
            Point point = new Point(x + roomL, y);
            pointData.add(point.getPointAsString());
        }
        int topY = topL.getY();
        int topX = bottomR.getX();
        Point point = new Point(topX, topY);
        pointData.add(point.getPointAsString());
        return pointData;
    }
    public void drawRectRoom(Point topL, Point bottomL, Point bottomR) {
        int roomW = Math.abs(topL.getY() - bottomL.getY());
        int roomL = Math.abs(bottomL.getX() - bottomR.getX());
        int centerX = bottomL.getX() + (roomL / 2);
        int centerY = bottomL.getY() + (roomW / 2);
        ArrayList<String> currRoom = roomData(topL, bottomL, bottomR);

        for (int i = 0; i < currRoom.size(); i++) {
            if (allRooms.contains(currRoom.get(i))) {
                return;
            }
        }
        for (int x = bottomL.getX(); x < bottomR.getX(); x++) {
            for (int y = bottomL.getY(); y < topL.getY(); y++) {
                Point point = new Point(x, y);
                allRoomPoints.add(point);
                world[x][y] = Tileset.FLOOR;
            }
        }
        for (int x = bottomL.getX(); x < bottomR.getX(); x++) {
            int y = bottomL.getY();
            world[x][y] = Tileset.WALL;
            world[x][y + roomW] = Tileset.WALL;
            if (x == bottomR.getX() - 1) {
                Point point = new Point(x, y);
                allWallPoints.add(point);
            }
        }
        for (int y = bottomL.getY(); y < topL.getY(); y++) {
            int x = bottomL.getX();
            world[x][y] = Tileset.WALL;
            world[x + roomL][y] = Tileset.WALL;
            if (y == topL.getY() - 1) {
                Point point = new Point(x, y);
                allWallPoints.add(point);
            }
        }
        int topY = topL.getY();
        int topX = bottomR.getX();
        world[topX][topY] = Tileset.WALL;

        Point center = new Point(centerX, centerY);
        allCenterRooms.add(new Room(center));
        allCenterPoints.add(center);

        allRooms.addAll(currRoom);
    }

    public void generateRooms() {
        int max = 40;
        int min = 30;
        Random random;
        if (!seed.equals("")) {
            String num = "";
            String cur = seed;
            Long seedNumber = Long.parseLong(seed);
            random = new Random(seedNumber);
        } else {
            random = new Random();
        }
        int maxH = 29;
        int minH = 2;
        int maxW = 79;
        int minW = 2;
        int roomNumbers = random.nextInt(max + 1 - min) + min;
        for (int i = 0; i < roomNumbers; i++) {
            int randX = random.nextInt(maxW + 1 - minW) + minW;
            int randY = random.nextInt(maxH + 1 - minH) + minH;
            int randW = random.nextInt(5 + 1 - 2) + 2;
            int randH = randW * 2;
            int randRect = (int) (random.nextInt(2) % 2 + 1);
            if (randRect == 1) {
                Point roomPos = new Point(randX, randY);
                Point topL = new Point(randX, randY + randW);
                Point bottomR = new Point(randX + randH, randY);
                drawRectRoom(topL, roomPos, bottomR);
            } else if (randRect == 2) {
                Point roomPos = new Point(randX, randY);
                Point topL = new Point(randX, randY + randH);
                Point bottomR = new Point(randX + randW, randY);
                drawRectRoom(topL, roomPos, bottomR);
            }
        }
        for (int i = 0; i < this.world.length; i++) {
            for (int j = 0; j < this.world[0].length; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    allFloorPoints.add(new Point(i, j));
                }
            }
        }
    }

    public void connect() {
        while (allCenterRooms.size() > 1) {
            Room first = allCenterRooms.get(0);
            allCenterRooms.remove(first);
            double min = 999999.0;
            Point one = first.getCenter();
            int index = -1;
            for (int i = 0; i < allCenterRooms.size(); i++) {
                Point two = allCenterRooms.get(i).getCenter();
                double currDist = distance(one, two);
                if (currDist < min) {
                    min = currDist;
                    index = i;
                }
            }
            Point two = allCenterRooms.get(index).getCenter();
            if (one.getX() < two.getX()) {
                for (int x = one.getX(); x <= two.getX(); x++) {
                    int y = one.getY();
                    world[x][y] = Tileset.FLOOR;
                    if (world[x][y + 1] == Tileset.NOTHING) {
                        world[x][y + 1] = Tileset.WALL;
                    }
                    if (world[x][y - 1] == Tileset.NOTHING) {
                        world[x][y - 1] = Tileset.WALL;
                    }
                }
            } else if (one.getX() > two.getX()) {
                for (int x = one.getX(); x >= two.getX(); x--) {
                    int y = one.getY();
                    world[x][y] = Tileset.FLOOR;
                    if (world[x][y + 1] == Tileset.NOTHING) {
                        world[x][y + 1] = Tileset.WALL;
                    }
                    if (world[x][y - 1] == Tileset.NOTHING) {
                        world[x][y - 1] = Tileset.WALL;
                    }
                }
            }
            one = new Point(two.getX(), one.getY());
            if (one.getY() < two.getY()) {
                for (int y = one.getY(); y <= two.getY(); y++) {
                    int x = one.getX();
                    world[x][y] = Tileset.FLOOR;
                    if (world[x + 1][y] == Tileset.NOTHING) {
                        world[x + 1][y] = Tileset.WALL;
                    }
                    if (world[x - 1][y] == Tileset.NOTHING) {
                        world[x - 1][y] = Tileset.WALL;
                    }
                }
            } else if (one.getY() > two.getY()) {
                for (int y = one.getY(); y >= two.getY(); y--) {
                    int x = one.getX();
                    world[x][y] = Tileset.FLOOR;
                    if (world[x + 1][y] == Tileset.NOTHING) {
                        world[x + 1][y] = Tileset.WALL;
                    }
                    if (world[x - 1][y] == Tileset.NOTHING) {
                        world[x - 1][y] = Tileset.WALL;
                    }
                }
            }
        }
    }

    private double distance(Point one, Point two) {
        double oneX = one.getX();
        double oneY = one.getY();
        double twoX = two.getX();
        double twoY = two.getY();
        return Math.sqrt((twoY - oneY) * (twoY - oneY) + (twoX - oneX) * (twoX - oneX));
    }

    public void createPlayer() {

        Random random;
        if (!seed.equals("")) {
            Long se = Long.parseLong(seed);
            random = new Random(se);

        } else {
            random = new Random();
        }
        int randomIndex = random.nextInt((allFloorPoints.size() - 1));
        Point startPos = allFloorPoints.get(randomIndex);
        currPos.add(startPos);
        world[startPos.getX()][startPos.getY()] = avatar;

    }

    public void moveAvatar(char key) {
        switch (key) {
            case 'w' -> move("up");
            case 'a' -> move("left");
            case 's' -> move("down");
            case 'd' -> move("right");
            default -> move("Default");
        }
    }

    public void move(String direction) {
        Point curr = currPos.get(currPos.size() - 1);
        Point newPos;
        switch (direction) {
            case "up" -> newPos = new Point(curr.getX(), curr.getY() + 1);
            case "down" -> newPos = new Point(curr.getX(), curr.getY() - 1);
            case "right" -> newPos = new Point(curr.getX() + 1, curr.getY());
            case "left" -> newPos = new Point(curr.getX() - 1, curr.getY());
            default -> newPos = new Point(-5, -5);
        }
        if (validatePoint(newPos)) {
            if (world[newPos.getX()][newPos.getY()] == Tileset.FLOWER
                    || world[newPos.getX()][newPos.getY()].equals(Tileset.FLOWER)) {
                Random random = new Random();
                int num = random.nextInt(3);
                switch (num) {
                    case 0: AltWorld one = new AltWorld();
                        world[curr.getX()][curr.getY()] = Tileset.FLOOR;
                        world[newPos.getX()][newPos.getY()] = avatar;
                        curr = newPos;
                        currPos.add(curr);
                        one.startAltWorld(this.avatar);
                        continueGame(this);
                        break;
                    case 1: SecondAltWorld two = new SecondAltWorld();
                        world[curr.getX()][curr.getY()] = Tileset.FLOOR;
                        world[newPos.getX()][newPos.getY()] = avatar;
                        curr = newPos;
                        currPos.add(curr);
                        two.startSecondAlt(this.avatar);
                        continueGame(this);
                        break;
                    case 2: ThirdAltWorld three = new ThirdAltWorld();
                        world[curr.getX()][curr.getY()] = Tileset.FLOOR;
                        world[newPos.getX()][newPos.getY()] = avatar;
                        curr = newPos;
                        currPos.add(curr);
                        three.startThirdAlt();
                        continueGame(this);
                        break;
                    default: System.out.println("x");
                }
            }
            world[curr.getX()][curr.getY()] = Tileset.FLOOR;
            world[newPos.getX()][newPos.getY()] = avatar;
            curr = newPos;
            currPos.add(curr);
        }
    }

    public boolean validatePoint(Point point) {
        if (point.getX() > world.length || point.getX() < 0) {
            return false;
        } else if (point.getY() > world[0].length || point.getY() < 0) {
            return false;
        } else if (world[point.getX()][point.getY()] == Tileset.WALL
                || world[point.getX()][point.getY()].equals(Tileset.WALL)) {
            return false;
        }
        return true;
    }

    public void generateFlowers() {
        for (int i = 0; i < allCenterPoints.size() / 2; i++) {
            Point curr = allCenterPoints.get(i);
            world[curr.getX()][curr.getY()] = Tileset.FLOWER;
        }
    }

    public boolean isEmpty() {
        return this.empty;
    }
}
