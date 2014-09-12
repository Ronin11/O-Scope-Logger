import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

import javax.swing.*;
 
public class GraphingData extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int[] dataProp,dataActual;
    final int PAD = 20;
    static Graphics2D g2;
    
    GraphingData(int[] dataP, int[] dataA){
    	dataProp = dataP;
    	dataActual = dataA;
    }
    public static void setProp(int[] i){dataProp = i;}
    public static void setActual(int[] i){dataActual = i;}
    public static void paint(){};
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        // Draw labels.
        Font font = g2.getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = font.getLineMetrics("0", frc);
        float sh = lm.getAscent() + lm.getDescent();
        // Ordinate label.
        String s = "MONTO RETIRO";
        float sy = PAD + ((h - 2*PAD) - s.length()*sh)/2 + lm.getAscent();
        for(int i = 0; i < s.length(); i++) {
            String letter = String.valueOf(s.charAt(i));
            float sw = (float)font.getStringBounds(letter, frc).getWidth();
            float sx = (PAD - sw)/2;
            g2.drawString(letter, sx, sy);
            sy += sh;
        }
        // Abcissa label.
        s = "EDAD";
        sy = h - PAD + (PAD - sh)/2 + lm.getAscent();
        float sw = (float)font.getStringBounds(s, frc).getWidth();
        float sx = (w - sw)/2;
        g2.drawString(s, sx, sy);
        // Draw lines.
        double xInc = (double)(w - 2*PAD)/(dataProp.length-1);
        double scale = (double)(h - 2*PAD)/getMax();
        g2.setPaint(new Color(0,189,242));
        for(int i = 0; i < dataProp.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*dataProp[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*dataProp[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        g2.setPaint(new Color(0,71,131));
        for(int i = 0; i < dataActual.length-1; i++) {
            double x1 = PAD + i*xInc;
            double y1 = h - PAD - scale*dataActual[i];
            double x2 = PAD + (i+1)*xInc;
            double y2 = h - PAD - scale*dataActual[i+1];
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
    }
 
    private int getMax() {
        int max = -Integer.MAX_VALUE;
        for(int i = 0; i < dataProp.length; i++) {
            if(dataProp[i] > max)
                max = dataProp[i];
        }
        for(int i = 0; i < dataActual.length; i++) {
            if(dataActual[i] > max)
                max = dataActual[i];
        }
        return max;
    }
}