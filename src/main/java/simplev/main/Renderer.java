package simplev.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JPanel;
import javax.swing.Timer;

import simplev.common.Highlight;
import simplev.common.SimpleLogger;

public class Renderer extends JPanel {
    private static final SimpleLogger LOGGER = new SimpleLogger(Renderer.class.getName());
    public int[] array;
    public int len;
    public volatile Highlight highlight;
    private final Font font;

    private Font getFont(int size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, Renderer.class.getResourceAsStream("/SNPro-Medium.ttf")).deriveFont((float)size);
        } catch (NullPointerException | FontFormatException | IOException e) {
            LOGGER.log(Level.WARNING, "Unable to load custom font, defaulting to Times New Roman", e);
            return new Font("Times New Roman", Font.PLAIN, size);
        }
    }

    private void outlineText(Graphics2D g2d, String text, Font font, int x, int y) {
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout textLayout = new TextLayout(text, font, frc);
        AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
        Shape textShape = textLayout.getOutline(transform);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(textShape);
        g2d.setColor(Color.WHITE);
        g2d.fill(textShape);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(0.0f));
        int last = 0;
        float scale = getWidth()/((float)len);
        int pixSize = (int)Math.ceil(len/getWidth());
        for (int i = 0; i < len; i++) { 
            if (Math.abs(highlight.highlight.get()-i) <= pixSize*2) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.WHITE);
            }
            int w = (int)(scale*(i+1))-last;
            int y = (int)(((float)array[i]/len)*getHeight());
            g2d.fillRect(last, getHeight()-y, w, y);
            last += w;
        }
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        int lineHeight = g2d.getFontMetrics(font).getHeight();
        outlineText(g2d, highlight.title, font, 10, 30);
        outlineText(g2d, "Values: "+Integer.toString(len), font, 10, 30+lineHeight*2);
        outlineText(g2d, "Reads: "+Long.toString(highlight.reads.get()), font, 10, 30+lineHeight*3);
        outlineText(g2d, "Writes: "+Long.toString(highlight.writes.get()), font, 10, 30+lineHeight*4);
    }

    public Renderer(int[] array, int len, Highlight highlight) {
        this.array = array;
        this.len = len;
        this.highlight = highlight;
        this.font = getFont(30);
        setBackground(Color.BLACK);
        Timer timer = new Timer(4, e -> {
            repaint();
        });
        timer.start();
    }
}