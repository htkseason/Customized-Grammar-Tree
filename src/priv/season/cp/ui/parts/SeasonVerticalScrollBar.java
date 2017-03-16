package priv.season.cp.ui.parts;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class SeasonVerticalScrollBar extends BasicScrollBarUI {

	private Color backColor, foreColor;

	public SeasonVerticalScrollBar(Color backColor, Color foreColor) {
		super();
		this.backColor = backColor;
		this.foreColor = foreColor;
	}

	@Override
	protected void configureScrollBarColors() {
		trackColor = backColor;
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		super.paintTrack(g, c, trackBounds);
	}

	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		g.translate(thumbBounds.x, thumbBounds.y);
		g.setColor(foreColor);
		g.drawRoundRect(5, 0, 6, thumbBounds.height - 1, 5, 5);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2.fillRoundRect(5, 0, 6, thumbBounds.height - 1, 5, 5);
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		JButton button = new JButton(getDownButtonImage());
		button.setBorder(null);
		return button;
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		JButton button = new JButton(getUpButtonImage());
		button.setBorder(null);
		return button;
	}

	private ImageIcon getUpButtonImage() {
		BufferedImage bi = new BufferedImage(17, 30, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.addRenderingHints(rh);
		g.setColor(backColor);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());

		g.setColor(foreColor);
		Polygon t1 = new Polygon(new int[] { 4, 8, 12, 8 }, new int[] { 23, 8, 23, 20 }, 4);
		g.drawPolygon(t1);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.fillPolygon(t1);

		return new ImageIcon(bi);

	}

	private ImageIcon getDownButtonImage() {
		BufferedImage bi = new BufferedImage(17, 30, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.addRenderingHints(rh);
		g.setColor(backColor);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());

		g.setColor(foreColor);
		Polygon t1 = new Polygon(new int[] { 4, 8, 12, 8 }, new int[] { 8, 23, 8, 11 }, 4);
		g.drawPolygon(t1);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.fillPolygon(t1);

		return new ImageIcon(bi);

	}

}
