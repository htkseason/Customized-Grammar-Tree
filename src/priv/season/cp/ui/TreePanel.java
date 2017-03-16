package priv.season.cp.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;

import javax.swing.JPanel;

import priv.season.cp.grammar.GramNode;

public class TreePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	static class PaintInfo {
		float x, y;
		float alpha;
	}

	private String defaultFont;
	private int defaultFontSizeBase;
	private int defaultFontStype;
	private int defaultFontMaxLimit;
	private int defaultVisibleLevels;
	private int defaultHeightGapBase;
	private int defaultRedWordFontSize;

	private int defaultInterval = 6;

	private float defaultWidthOffset = 0.05f;
	private Color defaultBackColor = Color.white;
	private Color defaultLeafColor = Color.black;
	private Color defaultNodeColor = new Color(140, 140, 140);
	private Color defaultRedWordColor = Color.red;

	private boolean isSimpleStyle = false;
	private boolean endFlag;

	private GramNode tree;

	private HashMap<GramNode, PaintInfo> paintMap;

	public boolean getStyle() {
		return isSimpleStyle;
	}

	public void setSimpleStyle(boolean simple) {
		if (simple) {
			isSimpleStyle = true;
			defaultFont = "Arial";
			defaultFontSizeBase = 12;
			defaultFontStype = Font.PLAIN;
			defaultFontMaxLimit = 20;
			defaultVisibleLevels = 10;
			defaultHeightGapBase = 50;
			defaultRedWordFontSize = 12;
		} else {
			isSimpleStyle = false;
			defaultFont = "Arial";
			defaultFontSizeBase = 30;
			defaultFontStype = Font.BOLD;
			defaultFontMaxLimit = 20;
			defaultVisibleLevels = 20;
			defaultHeightGapBase = 80;
			defaultRedWordFontSize = 15;
		}

	}

	public void goUp() {
		if (tree == null)
			return;
		if (tree.getParent() != null) {
			tree = tree.getParent();
		}
	}

	public void goDown(int number) {
		if (tree == null)
			return;
		for (GramNode gn : tree.getChildren()) {
			if (gn.isGrammar()) {
				number--;
				if (number == -1) {
					tree = gn;
					break;
				}
			}
		}

	}

	public TreePanel() {
		paintMap = new HashMap<GramNode, PaintInfo>();
		setSimpleStyle(false);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
				while (!endFlag) {
					repaint();
					try {
						Thread.sleep(defaultInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(defaultInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
			}
		}).start();
	}

	public void setTree(GramNode tree) {
		this.tree = tree;
	}

	public GramNode getTree() {
		return tree;
	}

	public void stop() {
		endFlag = true;
	}

	public void start() {
		endFlag = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).addRenderingHints(rh);
		g.setColor(defaultBackColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(defaultNodeColor);
		g.drawString("press <1~9> ¡ú go forward, <~> ¡ú go back, <q> ¡ú quit, <tab> ¡ú change style", g.getFont().getSize(),
				getHeight() - g.getFont().getSize());
		if (tree == null)
			return;
		if (tree.getParent() == null) {
			drawTree(g, tree, getWidth() * defaultWidthOffset, getWidth() * (1 - defaultWidthOffset * 2),
					defaultFontSizeBase, defaultHeightGapBase, defaultVisibleLevels, 0, 0);
		} else {
			int childNumber = tree.getParent().getChildren().indexOf(tree);
			float[] position = getChildPosition(tree.getParent(), childNumber, 2);
			float percent = 1 / position[1];
			drawTree(g, tree.getParent(),
					getWidth() * defaultWidthOffset - getWidth() * (1 - defaultWidthOffset * 2) * percent * position[0],
					getWidth() * (1 - defaultWidthOffset * 2) * percent, defaultFontSizeBase - defaultHeightGapBase,
					defaultHeightGapBase, defaultVisibleLevels, -1, 0);
		}
	}

	private void drawTree(Graphics g, GramNode node, float left, float width, float y, float heightGap, int maxLevel,
			int level, int redNumber) {

		if (level > maxLevel) {
			paintMap.remove(node);
			return;
		}
		String str = node.getContent();

		float strWidth = getYFactor(defaultFontSizeBase, y) * (str.length() * 0.5f);
		if (level == 1 && node.isGrammar()) {
			g.setFont(new Font(defaultFont, defaultFontStype, defaultRedWordFontSize));
			g.setColor(defaultRedWordColor);
			g.drawString("(" + redNumber + ")",
					(int) (left + width / 2 - strWidth / 2 - g.getFont().getSize() * 0.5 * 3), (int) y);
		}

		if (node.isGrammar()) {
			g.setColor(defaultNodeColor);
		} else {
			g.setColor(defaultLeafColor);
		}
		drawNode(g, node, str, left + width / 2, y);

		for (int i = 0; i < node.getChildrenCount(); i++) {

			float[] position = getChildPosition(node, i, 2);

			g.setColor(Color.gray);
			if (level == 0 && node.getChild(i).isGrammar())
				redNumber++;
			drawTree(g, node.getChild(i), left + position[0] * width, position[1] * width, y + getYFactor(heightGap, y),
					heightGap, maxLevel, level + 1, redNumber);

			if (level != maxLevel) {
				drawNodeLink(g, node.getChild(i), node);
			} else if (isSimpleStyle) {
				PaintInfo pi = paintMap.get(node);
				g.drawLine((int) pi.x, (int) pi.y, (int) pi.x, (int) (pi.y + defaultHeightGapBase * 0.5));
			}

		}

	}

	private float[] getChildPosition(GramNode parent, int childNumber, int levels) {
		float totalWeight = parent.getWeight(levels + 1) - 1;
		float width = parent.getChild(childNumber).getWeight(levels) / totalWeight;
		float left = 0;
		for (int i = 0; i < parent.getChildren().size(); i++) {
			if (i == childNumber)
				return new float[] { left, width };
			else
				left += parent.getChild(i).getWeight(levels) / totalWeight;
		}
		return null;
	}

	private void drawNodeLink(Graphics g, GramNode node1, GramNode node2) {
		PaintInfo p1 = paintMap.get(node1);
		PaintInfo p2 = paintMap.get(node2);
		if (p1 == null || p2 == null)
			return;
		g.drawLine((int) p1.x, (int) (p1.y - getYFactor(defaultFontSizeBase, p1.y)), (int) p2.x,
				(int) (p2.y + getYFactor(defaultFontSizeBase, p2.y) / 5));
	}

	private PaintInfo drawNode(Graphics g, GramNode node, String str, float x, float y) {
		PaintInfo pi;
		if (!paintMap.containsKey(node)) {
			pi = new PaintInfo();
			if (node.getParent() != null) {
				pi.x = paintMap.get(node.getParent()).x;
				pi.y = paintMap.get(node.getParent()).y;
			} else {
				pi.x = x;
				pi.y = y;
			}
			paintMap.put(node, pi);
		} else {
			pi = paintMap.get(node);
		}
		pi.x += (x - pi.x) / 10;
		pi.y += (y - pi.y) / 10;
		int fontSize = (int) (getYFactor(defaultFontSizeBase, pi.y) > defaultFontMaxLimit ? defaultFontMaxLimit
				: getYFactor(defaultFontSizeBase, pi.y));
		g.setFont(new Font(defaultFont, defaultFontStype, fontSize));
		float strWidth = g.getFont().getSize() * (str.length() * 0.5f);
		g.drawString(str, (int) (pi.x - strWidth / 2), (int) pi.y);
		return pi;
	}

	private float getYFactor(float base, float y) {
		if (isSimpleStyle)
			return base;
		return (getHeight() - y) / getHeight() * base;
	}

}