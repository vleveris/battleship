package battleship;

public class Ship {
    private String name, fName;
    private final int length;
    private final int position;
    private int strikes = 0;
    private boolean sunk = false;
    private final int[][] posArray;
    private final int type;

    public Ship(int type, int length, int x, int y, int position) {
        this.length = length;
        this.type = type;
        switch (type) {
            case 0 -> {
                name = "Aircraft carrier";
                fName = "carrier";
            }
            case 1 -> {
                name = "Destroyer";
                fName = "destroyer";
            }
            case 2 -> {
                name = "Submarine";
                fName = "submarine";
            }
            case 3 -> {
                name = "Cruiser";
                fName = "cruiser";
            }
            case 4 -> {
                name = "Patrol boat";
                fName = "boat";
            }
        }
        posArray = new int[length][2];
        this.position = position;
        for (int i = 0; i < length; i++) {
            posArray[i][0] = x;
            posArray[i][1] = y;
            if (position == 0)
                x++;
            else
                y++;
        }
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public boolean isOnGivenCoordinates(int x, int y) {
        for (int i = 0; i < length; i++)
            if (x == posArray[i][0] && y == posArray[i][1])
                return true;
        return false;
    }

    public void shoot() {
        strikes++;
        sunk = (length - strikes) == 0;
    }

    public String getFName() {
        return fName;
    }

    public int[][] getPosArray() {
        return posArray;
    }

    public int getPosition() {
        return position;
    }

    public boolean isSunk() {
        return sunk;
    }

    public int getType() {
        return type;
    }
}
