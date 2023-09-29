import java.util.*;

public class LineBattle {
    int playerSoldiers = 25, enemySoldiers = 25;
    int playerFirePower = 100, enemyFirePower = 100;
    int initialPlayerPosition = 11;
    int initialEnemyPosition = -11;
    int playerPosition = initialPlayerPosition;
    int enemyPosition = initialEnemyPosition;
    int playerBombs = 1, enemyBombs = 1;
    int noBombPosition = 30;
    int playerBombPosition = noBombPosition;
    int enemyBombPosition = noBombPosition;
    boolean surrender = false;
    String player = "player", enemy = "enemy";
    Random random = new Random();
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new LineBattle().run();
    }

    private void run() {
        System.out.println();
        System.out.println("WELCOME TO LINE-BATTLE! PREPARE YOUR TROOPS!");
        System.out.println();
        initializeStartValues();
        runGame();
        System.out.println("\nGameOver!");

    }

    private void runGame() {
        String playerID = player;
        while (!checkWin() && !surrender) {
            showBattlefield();
            String move = nextTurn(playerID);
            makeMove(move, playerID);
            System.out.println();
            playerID = (playerID.equals(player)) ? enemy : player;
        }
    }

    public void initializeStartValues() {
        playerPosition = initialPlayerPosition - rollDice();
        enemyPosition = initialEnemyPosition + rollDice();
    }

    public int evaluateRange(int position1, int position2) {
        return Math.abs(position1 - position2);
    }

    public String nextTurn(String playerID) {
        if (playerID.equals("player")) {
            if (evaluateRange(playerPosition, enemyPosition) < 3) {
                callScout();
            }
            return playerTurn();
        } else {
            return enemyTurn(enemyOptions());
        }
    }

    public String playerTurn() {
        System.out.println("\nAwaiting orders, sir!");
        if (playerPosition != -10) {
            System.out.println("F: Forward!");
        }
        if (playerPosition != 10) {
            System.out.println("R: Retreat");
        }
        System.out.println("A: Attack");
        if (playerBombs > 0 && playerPosition < 0) {
            System.out.println("B: Place Bomb");
        }
        if (playerBombPosition != noBombPosition && evaluateRange(playerPosition, playerBombPosition) > 6 && playerPosition > 0) {
            System.out.println("D: Detonate Bomb");
        }
        System.out.println("S: Status\nG: Give up");
        return scanner.nextLine();
    }

    public List<String> enemyOptions() {
        List<String> availableMoves = new ArrayList<>();
        if (enemyPosition != 10) {
            availableMoves.add("f");
        }
        if (enemyPosition != -10) {
            availableMoves.add("r");
        }
        availableMoves.add("a");
        if (enemyBombs > 0 && enemyPosition > 0) {
            availableMoves.add("b");
        }
        if (enemyBombPosition != noBombPosition && evaluateRange(enemyPosition, enemyBombPosition) > 6 && enemyPosition < 0) {
            availableMoves.add("d");
        }
        availableMoves.add("s");
        availableMoves.add("g");

        return availableMoves;
    }

    public String enemyTurn(List<String> options) {
        int playerDistance = evaluateRange(playerPosition, enemyPosition);
        if (playerDistance <= 3 && enemyFirePower < 50) {
            return "r"; // Retreat if firepower is low and the player is close.
        } else if(options.contains("b") && playerDistance <=5 || options.contains("b") &&  enemyPosition ==10){
            return "b"; // place bomb is enemy is close OR it has reached player camp.
        } else if(options.contains("d") && playerDistance <=5 ||options.contains("d")&& enemyBombPosition ==10){
            return "d"; //detonate bomb if player is close OR bomb is placed in player camp
        } else if (playerDistance <= 5 && enemyFirePower > 40) {
            return "a"; // Attack if close to the player.
        } else if (options.contains("f")) {
            return "f"; // Move forward if possible.
        } else if (enemySoldiers < 3 && enemyFirePower <40){
            return "q"; //Surrenders if only 3 soldiers left and limited firepower.
        } else {
            return "r"; // If all else fails, retreat.
        }
    }

    public void reachedCamp(String playerID) {
        if (playerID.equals("player")) {
            if (playerPosition == 10 && playerBombs == 0) {
                retrieveBomb(player);
            }
        } else if (enemyPosition == -10 && enemyBombs == 0) {
            retrieveBomb(enemy);
        }
    }

    public void makeMove(String move, String playerID) {
        switch (move.toLowerCase()) {
            case "f" -> moveForward(playerID, rollDice());
            case "a" -> attack(playerID);
            case "r" -> moveBack(playerID);
            case "b" -> placeBomb(playerID);
            case "d" -> detonateBomb(playerID);
            case "s" -> showStats();
            case "g" -> surrender(playerID);
            default -> System.out.println("This is not the time for jokes, Commander! We are at war! LOOK OUT!");
        }
    }

    public int rollDice() {
        return random.nextInt(1, 7);
    }

    public void moveForward(String playerID, int dice) {
        int move = (dice % 2 == 0) ? 2 : 1;
        if (playerID.equals("player")) {
            int newPosition = playerPosition - move;
            playerPosition = Math.max(newPosition, -10);
            printPosition(player);
        } else if (playerID.equals("enemy")) {
            int newPosition = enemyPosition + move;
            enemyPosition = Math.min(newPosition, 10);
            printPosition(enemy);
        }
    }

    public void printPosition(String playerID) {
        if (playerID.equals(player)) {
            System.out.println("\nYou have moved to field " + playerPosition);
        } else if (playerID.equals(enemy)) {
            System.out.println("\nEnemy have moved to field " + enemyPosition);
        }
    }

    public int diceValue(int dice) {
        if (dice == 1 || dice == 2) {
            dice = 1;
        } else if (dice == 3 || dice == 4) {
            dice = 2;
        } else {
            dice = 3;
        }
        return dice;
    }

    public void moveBack(String playerID) {
        int move = diceValue(rollDice());
        if (playerID.equals("player")) {
            int newPosition = playerPosition + move;
            playerPosition = Math.min(newPosition, 10);
            playerFirePower += 10;
            printPosition(player);
            reachedCamp(playerID);
        } else if (playerID.equals("enemy")) {
            int newPosition = enemyPosition - move;
            enemyPosition = Math.max(newPosition, -10);
            enemyFirePower += 10;
            printPosition(enemy);
            reachedCamp(playerID);
        }
    }

    public int killRange(int position1, int position2) {
        int killRange = evaluateRange(position1, position2);
        int possibleSoldiersInRange = 6;
        if (killRange == 0) { //if 0 then kill return 6
            return possibleSoldiersInRange;
        } else return (possibleSoldiersInRange - killRange); //if range = 8 then = -2
    }

    public void successfulAttack(String playerID, int power, int soldiersInRange) {
        if (playerID.equals(player)) {
            playerFirePower -= power;
            enemySoldiers -= soldiersInRange;
            System.out.printf("\nENEMY HIT! %d Soldier(s) dead!", soldiersInRange);
        } else if (playerID.equals(enemy)) {
            enemyFirePower -= power;
            playerSoldiers -= soldiersInRange;
            System.out.printf("\nWE ARE HIT! %d SOLDIER(S) DEAD!", soldiersInRange);
        }
    }

    public void attack(String playerID) {
        int soldiersInRange = killRange(playerPosition, enemyPosition);
        if (soldiersInRange > 0) {
            int power = 10 * rollDice();
            if (playerID.equals("player")) {
                System.out.println("WE ATTACK!");
                if (playerFirePower >= power) {
                    successfulAttack(player, power, soldiersInRange);
                } else System.out.println("Out of firepower! Retreat to gather more amo!");
            } //Split in 2?
            else if (playerID.equals("enemy")) {
                System.out.println("ENEMY ATTACKS!");
                if (enemyFirePower >= power) {
                    successfulAttack(enemy, power, soldiersInRange);
                } else System.out.println("but they are out amo :(");
            }
        } else System.out.println(playerID.toUpperCase() + " ATTACKS! MISS! Out of range!");
    }

    public void placeBomb(String playerID) {
        if (playerID.equals("player")) {
            playerBombPosition = playerPosition;
            System.out.println("Bomb placed at " + playerBombPosition);
            playerBombs -= 1;
        } else if (playerID.equals("enemy")) {
            enemyBombPosition = enemyPosition;
            System.out.println("Bomb placed at " + enemyBombPosition);
            enemyBombs -= 1;
        }
    }

    public void detonateBomb(String playerID) {
        int soldiersInRange;
        if (playerID.equals("player")) {
            if (playerBombPosition == -10) {
                enemySoldiers = 0;
                System.out.println("Enemy camp destroyed!");
            } else {
                soldiersInRange = killRange(playerBombPosition, enemyPosition);
                System.out.println("Bomb detonated! " + (soldiersInRange + 10) + " enemies slayed!");
                enemySoldiers = (enemySoldiers - (soldiersInRange + 10));
            }
            playerBombPosition = noBombPosition; //resets bomb position
        } else if (playerID.equals("enemy")) {
            if (enemyBombPosition == 10) {
                playerSoldiers = 0;
                System.out.println("Our camp is destroyed!");
            } else {
                soldiersInRange = killRange(playerPosition, enemyBombPosition);
                System.out.println("Enemy bomb detonated! " + (soldiersInRange + 10) + " soldiers killed!");
                playerSoldiers = playerSoldiers - (soldiersInRange + 10);
            }
            enemyBombPosition = noBombPosition;
        }
    }

    public void retrieveBomb(String playerID) {
        if (playerID.equals("player") && playerBombPosition == noBombPosition) {
            playerBombs++;
            System.out.println("You have received a bomb!");
        } else if (playerID.equals("enemy") && enemyBombPosition == noBombPosition) {
            enemyBombs++;
            System.out.println("Enemy have received a bomb!");
        }
    }

    public void callScout() {
        System.out.println("\nEnemy is " + evaluateRange(playerPosition, enemyPosition) + " field(s) away!");
    }

    public void showStats() {
        System.out.printf("\n%22s %12s", "Player", "Enemy");
        System.out.printf("\nPosition %10d %13d ", playerPosition, enemyPosition);
        System.out.printf("\nFirepower %9d %13d ", playerFirePower, enemyFirePower);
        System.out.printf("\nSoldiers %10d %13d ", playerSoldiers, enemySoldiers);
        System.out.printf("\nBombs %13d %13d ", playerBombs, enemyBombs);
        if (playerBombPosition != noBombPosition || enemyBombPosition != noBombPosition) {
            System.out.printf("\nBomb positions %4d %13d ", playerBombPosition, enemyBombPosition);
        }
        System.out.println();
    }

    public void surrender(String playerID) {
        if (playerID.equals("player")) {
            System.out.println("We surrender!");
            surrender = true;
        } else if (playerID.equals("enemy")) {
            System.out.println("Enemy surrenders!");
            surrender = true;
        }
    }

    public boolean checkWin() {
        if (playerSoldiers <= 0) {
            System.out.println("Enemy has won!\nDEFEAT");
            return true;
        } else if (enemySoldiers <= 0) {
            System.out.println("Enemy slaughtered! We have won the battle, Commander! Time to drink!!\nVICTORY");
            return true;
        } else return false;
    }

    public void showBattlefield() {
        for (int i = -10; i < 11; i++) {
            if (i == playerPosition) {
                System.out.print("\u001B[32m" + i + "\u001B[0m "); // \u001B[32m sets green color, \u001B[0m resets the color
            } else if (i == enemyPosition) {
                System.out.print("\u001B[31m" + i + "\u001B[0m "); // \u001B[31m sets red color, \u001B[0m resets the color
            } else {
                // Print other elements in default color
                System.out.print(i + " ");
            }
        }
        System.out.println();
    }
}
