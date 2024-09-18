import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Logic extends JPanel {
    //Dem varelser som kan finnas i rummen
    enum Creatures {
        Bysen(" hör ett mummel"),
        Troll(" ser en trollring"),
        Vittra(" känner ett kallt isande drag"),
        Vette(" hör ett irriterat mummel");

        //Varning som visas när varelsen är i närheten
        Creatures(String warning) {
            this.warning = warning;
        }
        final String warning;
    }
    String playerName;

    final String[] sounds = {" hör ett mummel liknande ljud", " hör en gren knäckas"," hör en varg yla",
                            " märker att det blir mörkare", " hör ett ondskefullt skratt", " hör vätten skratta",
                            " känner ett kallt drag"};

    static final Random rand = new Random();

    public static final int roomSize = 45;
    public static final int playerSize = 16;

    public static boolean gameOver = true;
    public static int currRoom;
    public static int numArrows;
    int creatureRoom;
    boolean isIrritated=false; // om Vätten är irriterad
    public static List<String> messages; //lista med varningar att visa
    Set<Creatures>[] creatures; //Ett set med varelser

    public void handleMouseClick(MouseEvent e){
        if (gameOver) {
            startNewGame();

        } else {
            int selectedRoom = -1;
            int ex = e.getX();
            int ey = e.getY();

            for (int link : links[currRoom]) {
                int cx = rooms[link][0];
                int cy = rooms[link][1];
                if (insideRoom(ex, ey, cx, cy)) {
                    selectedRoom = link;
                    break;
                }
            }

            if (selectedRoom == -1) {
                if (isRightMouseButton(e)) {
                    messages.add(playerName + " kan inte skjuta hit");
                    return;
                }
                messages.add(playerName + " kan inte gå hit");
                return;
            }
            if (isLeftMouseButton(e)) {
                currRoom = selectedRoom;
                situation();

            } else if (isRightMouseButton(e)) {
                throwNet(selectedRoom);
            }
        }
    }

    /***
     * Testar om musen är över ett rum
     * @param ex Klick event position x
     * @param ey Klick event position y
     * @param cx rum position x
     * @param cy rum position y
     * @return om musen är över rummet
     */
    public boolean insideRoom(int ex, int ey, int cx, int cy) {
        return ((ex > cx && ex < cx + roomSize)
                && (ey > cy && ey < cy + roomSize));
    }
    /***
     * Förbereder spelstart
     * samt skapar varelser och placerar dem i rum
     */
    public void startNewGame() {
        playerName = JOptionPane.showInputDialog(this, "Ange ditt namn:",
                                            "Välkommen till Fånga Bysen!", JOptionPane.PLAIN_MESSAGE);

        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Spelaren"; // Ange ett standardnamn om användaren inte matar in något
        }
        numArrows = 3;
        currRoom = rand.nextInt(rooms.length);
        messages = new ArrayList<>();

        creatures = new Set[rooms.length];
        for (int i = 0; i < rooms.length; i++)
            creatures[i] = EnumSet.noneOf(Creatures.class);

        // varelser kan dela rum (om de inte är identiska)
        int[] ordinals = {0, 1, 1, 1, 2, 2, 3};
        Creatures[] values = Creatures.values();
        for (int ord : ordinals) {
            int room;
            do {
                room = rand.nextInt(rooms.length);
            } while (tooClose(room) || creatures[room].contains(values[ord]));

            if (ord == 0)
                creatureRoom = room;

            creatures[room].add(values[ord]);
        }

        gameOver = false;
    }

    // placera inte varelser nära startrummet

    /***
     * testar om ett rum är för nära varelse att skapas i
     * @param room rum som ska testas
     * @return om rummet är för nära
     */
    boolean tooClose(int room) {
        if (currRoom == room)
            return true;
        for (int link : links[currRoom])
            if (room == link)
                return true;
        return false;
    }

    /***
     * interagerar med varelsen i rummet
     * och ser in i anslutna rum
     */
    void situation() {
        Set<Creatures> set = creatures[currRoom];

        if (set.contains(Creatures.Bysen)) {
            messages.add("Bysen lockar " + playerName + " att gå vilse");
            gameOver = true;

        } else if (set.contains(Creatures.Troll)) {
            messages.add(playerName + " faller ner i trollringen");
            gameOver = true;

        } else if (set.contains(Creatures.Vittra)) {
            messages.add("Vittran kör iväg " + playerName + " till ett slumpat rum");

            // förflytta, men undvik 2 förflyttningar i rad
            do {
                currRoom = rand.nextInt(rooms.length);
            } while (creatures[currRoom].contains(Creatures.Vittra));

            // flytta vittran, men inte till spelarens rum eller ett rum med vittra
            set.remove(Creatures.Vittra);
            int newRoom;
            do {
                newRoom = rand.nextInt(rooms.length);
            } while (newRoom == currRoom || creatures[newRoom].contains(Creatures.Vittra));
            creatures[newRoom].add(Creatures.Vittra);

            // omvärdera
            situation();

        } else if (set.contains(Creatures.Vette)) {
            messages.add("Vätten sände en sjukdom på " + playerName);
            gameOver = true;

        } else {

            // se sig om
            for (int link : links[currRoom]) {
                for (Creatures creature : creatures[link]) {
                    messages.add(playerName + creature.warning);
                    if(creature == Creatures.Vette){
                        if(isIrritated) {
                            messages.clear();
                            messages.add("Vätten sände en sjukdom på " + playerName);
                            gameOver = true;
                        }
                        else isIrritated=true;
                    }
                }
            }
        }
        if(messages.isEmpty()){
            messages.add(playerName + sounds[rand.nextInt(sounds.length)]);
        }
    }

    /***
     * hanterar interagering med ett anslutet rum
     * @param room rummet som ska interageras med
     */
    void throwNet(int room) {
        if (creatures[room].contains(Creatures.Bysen)) {
            messages.add(playerName + " vinner! " + playerName + " har fångat Bysen!");
            gameOver = true;

        } else {
            numArrows--;
            if (numArrows == 0) {
                messages.add("Oops! " + playerName + " har inga inga nät kvar.");
                gameOver = true;

            } else if (rand.nextInt(4) != 0) { // 75 %
                creatures[creatureRoom].remove(Creatures.Bysen);
                creatureRoom = links[creatureRoom][rand.nextInt(3)];

                if (creatureRoom == currRoom) {
                    messages.add(playerName + " väckte Bysen och han är inte glad!");
                    gameOver = true;

                } else {
                    messages.add(playerName + " råkade se Bysen och han bara försvann");
                    creatures[creatureRoom].add(Creatures.Bysen);
                }
            }
        }
    }
    //rummens koordinater
    public static final int[][] rooms = {{334, 20}, {609, 220}, {499, 540}, {169, 540}, {62, 220},
            {169, 255}, {232, 168}, {334, 136}, {435, 168}, {499, 255}, {499, 361},
            {435, 447}, {334, 480}, {232, 447}, {169, 361}, {254, 336}, {285, 238},
            {387, 238}, {418, 336}, {334, 393}};
    //anslutningarna mellan dem olika rummen
    public static final int[][] links = {{4, 7, 1}, {0, 9, 2}, {1, 11, 3}, {4, 13, 2}, {0, 5, 3},
            {4, 6, 14}, {7, 16, 5}, {6, 0, 8}, {7, 17, 9}, {8, 1, 10}, {9, 18, 11},
            {10, 2, 12}, {13, 19, 11}, {14, 3, 12}, {5, 15, 13}, {14, 16, 19},
            {6, 17, 15}, {16, 8, 18}, {19, 10, 17}, {15, 12, 18}};
}
