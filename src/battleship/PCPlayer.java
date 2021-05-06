package battleship;

import java.util.ArrayList;

public class PCPlayer {
    private final ArrayList<Integer> positionsUsed = new ArrayList<>();
    private final ArrayList<Integer> lengths = new ArrayList<>();
    private final ArrayList<Integer> hitPosUsed = new ArrayList<>();
    private final ArrayList<Ship> sunkShips = new ArrayList<>();
    public int x = 0, y = 0;
    private int targetedX = -1, targetedY = -1, times = 0, stage = 0;
    private boolean hit = false;

    public PCPlayer() {
        lengths.add(5);
        lengths.add(4);
        lengths.add(3);
        lengths.add(3);
        lengths.add(2);
    }

    public void submitResults(Ship ship, boolean hit) {
        this.hit = hit;
        if (hit)
            hitPosUsed.add(Main.size * x + y);
        if (ship != null) {
            sunkShips.add(ship);
            int length = ship.getLength();
            int smallestLength = 5;
            for (int i = 0; i < lengths.size(); i++)
                if (lengths.get(i) == length) {
                    lengths.remove(i);
                    break;
                }
            for (Integer value : lengths)
                if (value < smallestLength)
                    smallestLength = value;
            times = times - length + 1;
            stage = 0;
            if (times > 0) {
                boolean foundAnother;
                for (Integer integer : hitPosUsed) {
                    foundAnother = newTargets(integer);
                    if (foundAnother)
                        break;
                }
            } else {
                targetedX = -1;
                targetedY = -1;
            }
        } else if (hit) {
            times++;
            if (targetedX == -1) {
                targetedX = x;
                targetedY = y;
            }
        }
    }

    private boolean isUsed() {
        return positionsUsed.contains(x * Main.size + y);
    }

    public void go() {
        if (targetedX == -1) {
            do {
                x = (int) (Math.random() * Main.size);
                y = (int) (Math.random() * Main.size);
            }
            while (isUsed());
        } else if (targetedX > -1) {
            if (stage == 0 || !hit)
                changeDirection();
            else
                setDirection();
            while (isUsed())
                changeDirection();
        }
        positionsUsed.add(x * Main.size + y);
    }

    private void setDirection() {
        switch (stage) {
            case 1 -> x++;
            case 2 -> x--;
            case 3 -> y++;
            case 4 -> y--;
        }
        if (x >= Main.size || x <= -1 || y >= Main.size || y <= -1)
            changeDirection();
    }

    private void changeDirection() {
        x = targetedX;
        y = targetedY;
        stage++;
        setDirection();
    }

    private boolean newTargets(int hitPos) {
        boolean result = true;
        for (Ship sunkShip : sunkShips) {
            if (!sunkShip.isOnGivenCoordinates(hitPos / Main.size, hitPos % Main.size)) {
                targetedX = hitPos / Main.size;
                targetedY = hitPos % Main.size;
            } else if (sunkShip.isOnGivenCoordinates(hitPos / Main.size, hitPos % Main.size)) {
                result = false;
                break;
            }
        }
        return result;
    }

}
