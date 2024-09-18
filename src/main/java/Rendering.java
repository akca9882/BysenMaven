import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Rendering extends JPanel {
    Logic logic = new Logic();
    public Rendering(){
        setPreferredSize(new Dimension(721, 687));
        setBackground(Color.white);
        setForeground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 18));
        setFocusable(true);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                logic.handleMouseClick(e);
                repaint();
            }
        });
    }
    /**
     * Ritar spelaren
     * @param g Graphics2D objektet att rita med
     */
    void drawPlayer(Graphics2D g) {
        int x = Logic.rooms[Logic.currRoom][0] + (Logic.roomSize - Logic.playerSize) / 2;
        int y = Logic.rooms[Logic.currRoom][1] + (Logic.roomSize - Logic.playerSize) - 2;

        Path2D player = new Path2D.Double();
        player.moveTo(x, y);
        player.lineTo(x + Logic.playerSize, y);
        player.lineTo(x + Logic.playerSize / 2, y - Logic.playerSize);
        player.closePath();

        g.setColor(Color.white);
        g.fill(player);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.black);
        g.draw(player);
    }

    /***
     * ritar startskärmermen
     * @param g Graphics2D objektet att rita med
     */
    void drawStartScreen(Graphics2D g) {
        g.setColor(new Color(0xDDFFFFFF, true));
        g.fillRect(0, 0, getWidth(), getHeight() - 60);

        g.setColor(Color.darkGray);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        centerText(g,"Fånga Bysen!", 240);

        g.setFont(getFont());
        centerText(g, "Vänsterklicka för att flytta, Högerklicka för att skjuta", 310);
        centerText(g, "Var försiktig väsen kan befinna sig i samma rum som du", 345);
        centerText(g, "Klicka för att starta", 380);
    }


    /**
     * beräkna fönstrets bredd för att få centrerad text
     * @param g Graphics2D objektet att rita med
     * @param text texten som ska ritas
     * @param y y kordinaten att rita texten på
     */
    void centerText(Graphics2D g, String text , int y){
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int panelWidth = getWidth();
        g.drawString(text ,(panelWidth - textWidth) / 2 , y);
    }

    /***
     * ritar rum samt sträcken mellan dem
     * @param g Graphics2D objektet att rita med
     */
    void drawRooms(Graphics2D g) {
        g.setColor(Color.darkGray);
        g.setStroke(new BasicStroke(2));

        for (int i = 0; i < Logic.links.length; i++) {
            for (int link : Logic.links[i]) {
                int x1 = Logic.rooms[i][0] + Logic.roomSize / 2;
                int y1 = Logic.rooms[i][1] + Logic.roomSize / 2;
                int x2 = Logic.rooms[link][0] + Logic.roomSize / 2;
                int y2 = Logic.rooms[link][1] + Logic.roomSize / 2;
                g.drawLine(x1, y1, x2, y2);
            }
        }

        g.setColor(Color.orange);
        for (int[] r : Logic.rooms)
            g.fillOval(r[0], r[1], Logic.roomSize, Logic.roomSize);

        if (!Logic.gameOver) {
            g.setColor(Color.magenta);
            for (int link : Logic.links[Logic.currRoom])
                g.fillOval(Logic.rooms[link][0], Logic.rooms[link][1], Logic.roomSize, Logic.roomSize);
        }

        g.setColor(Color.darkGray);
        for (int[] r : Logic.rooms)
            g.drawOval(r[0], r[1], Logic.roomSize, Logic.roomSize);
    }

    /***
     * ritar medelanden samt antalt pilar
     * @param g
     */
    void drawMessage(Graphics2D g) {
        if (!Logic.gameOver)
            g.drawString("pilar  " + Logic.numArrows, 610, 30);

        if (Logic.messages != null) {
            g.setColor(Color.black);

            // ta bort lika meddelanden
            Logic.messages = Logic.messages.stream().distinct().collect(toList());

            // slå ihop max tre
            String msg = Logic.messages.stream().limit(3).collect(joining(" & "));
            g.drawString(msg, 20, getHeight() - 40);

            // om det finns mer, skriv ut nedanför
            if (Logic.messages.size() > 3) {
                g.drawString("& " + Logic.messages.get(3), 20, getHeight() - 17);
            }

            Logic.messages.clear();
        }
    }

    /***
     * sätter renderHints och kallar på dem olika ritnings metoderna
     * @param gg the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawRooms(g);
        if (Logic.gameOver) {
            drawStartScreen(g);
        } else {
            drawPlayer(g);
        }
        drawMessage(g);
    }
}
