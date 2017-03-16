package priv.season.cp.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import priv.season.cp.YamlInfoBean;
import priv.season.cp.grammar.CodeGrammer;
import priv.season.cp.reader.CodeReader;
import priv.season.cp.reader.WordBean;
import priv.season.cp.ui.parts.SeasonHorizontalScrollBar;
import priv.season.cp.ui.parts.SeasonVerticalScrollBar;

import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowStateListener;
import java.awt.event.WindowEvent;

public class EntranceUI extends JFrame {
	private static final long serialVersionUID = 1L;
	JPanel uiPanel;
	JTextArea txt_in;
	TreeView treeView;
	JScrollPane txt_in_pane;

	final int defaultWidth = 600, defaultHeight = 650;
	final int smallWidth = 200, smallHeight = 400;
	Rectangle defaultBounds, smallBounds;

	private static void selectLine(JTextArea txt, int line) {
		try {
			txt.requestFocus();
			txt.select(txt.getLineStartOffset(line - 1), txt.getLineEndOffset(line - 1) - 1);
		} catch (BadLocationException e) {
		}
	}

	public EntranceUI() {

		setResizable(true);

		this.setTitle("CodeParser bySeason");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(defaultWidth, defaultHeight);
		this.setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		this.setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());

		treeView = new TreeView(this);

		// main panel
		uiPanel = new JPanel();
		uiPanel.setBackground(new Color(0, 153, 255));
		getContentPane().add(uiPanel);
		uiPanel.setLayout(null);

		// textarea
		txt_in = new JTextArea();
		txt_in.addMouseListener(action_goto);
		txt_in.setBackground(new Color(255, 255, 255));
		txt_in.setForeground(new Color(0, 0, 0));
		txt_in.setFont(new Font("Arial", Font.PLAIN, 15));
		txt_in_pane = new JScrollPane(txt_in);
		uiPanel.add(txt_in_pane);
		txt_in_pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		txt_in_pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		txt_in_pane.getVerticalScrollBar()
				.setUI(new SeasonVerticalScrollBar(txt_in.getBackground(), txt_in.getForeground()));
		txt_in_pane.getHorizontalScrollBar()
				.setUI(new SeasonHorizontalScrollBar(txt_in.getBackground(), txt_in.getForeground()));

		// resize event
		addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent arg0) {
				updateSize();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				updateSize();
			}
		});

		// init button
		JButton btn_reload = new JButton("Reload Yaml");
		btn_reload.setBackground(new Color(255, 255, 255));
		btn_reload.setForeground(new Color(40, 40, 40));
		btn_reload.setBounds(125, 0, 120, 23);
		btn_reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (YamlInfoBean.init())
					drawKotori();
			}
		});
		uiPanel.add(btn_reload);

		// show tree button
		JButton btn_showTree = new JButton("Show Tree (Alt+S)");
		btn_showTree.setBackground(new Color(255, 255, 255));
		btn_showTree.setForeground(new Color(40, 40, 40));
		btn_showTree.setMnemonic('s');
		btn_showTree.setBounds(255, 0, 140, 23);
		btn_showTree.addActionListener(action_showTree);
		uiPanel.add(btn_showTree);

		// sample button
		JButton btn_sample = new JButton("Sample");
		btn_sample.setBackground(Color.WHITE);
		btn_sample.setForeground(new Color(40, 40, 40));
		btn_sample.setBounds(15, 0, 100, 23);
		uiPanel.add(btn_sample);
		btn_sample.addActionListener(action_sample);

		this.setVisible(true);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.setDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
		txt_in.requestFocus();

	}

	ActionListener action_sample = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			try {
				drawKotori();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream("sample")));
				txt_in.setText("");
				String tstr;
				while ((tstr = reader.readLine()) != null)
					txt_in.append(tstr + "\n");
				reader.close();
				txt_in.setCaretPosition(0);
				txt_in.requestFocus();
				repaint();
			} catch (Exception e) {
				// println(txt_out, "cannot load sample program.");
			}
		}
	};

	MouseAdapter action_goto = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			repaint();
			int pos = txt_in.getCaretPosition();
			try {
				treeView.gotoLine(txt_in.getLineOfOffset(pos) + 1);
			} catch (BadLocationException e1) {
			}
		}
	};

	ActionListener action_showTree = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			Scanner scan = new Scanner(txt_in.getText());
			CodeReader cr = new CodeReader();

			int line = 0;
			while (scan.hasNext()) {
				String str = scan.nextLine();
				line++;
				cr.readNextLine(str, line);

			}
			scan.close();

			WordBean[] wbs = cr.getWordBeanList();
			for (WordBean wb : wbs) {
				if (wb.getCode() == WordBean.TYPE_ERROR) {
					selectLine(txt_in, wb.getLine());
				}
			}

			CodeGrammer cg = new CodeGrammer(cr.getWordBeanList());
			if (!cg.parse()) {
				selectLine(txt_in, cg.getLastError().getLine());
				drawKazumi("Error This~w");
				treeView.setTree(null);

			} else {
				drawKotori();
				treeView.setTree(cg.getTreeRoot());
			}
			if (treeView.getTree() == null) {
				return;
			}
			setSmall();
			treeView.showTreeView();
			treeView.requestFocus();
		}
	};

	public void updateSize() {
		uiPanel.setBounds(0, 0, getWidth() - 6, getHeight() - 29);
		txt_in_pane.setBounds(2, 22, uiPanel.getWidth() - 15, uiPanel.getHeight() - 34);
		repaint();
	}

	public void setSmall() {
		this.setAlwaysOnTop(true);
		if (treeView.isVisible() == false && this.getExtendedState() != JFrame.MAXIMIZED_BOTH)
			defaultBounds = this.getBounds();
		if (smallBounds != null)
			this.setBounds(smallBounds);
		else {
			int sWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int sHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			this.setBounds(sWidth - smallWidth, sHeight - smallHeight, smallWidth, smallHeight);
		}
		updateSize();

	}

	public void setBig() {
		this.setAlwaysOnTop(false);
		smallBounds = this.getBounds();
		if (defaultBounds != null)
			this.setBounds(defaultBounds);
		else {
			int sWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int sHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			this.setBounds(sWidth / 2 - 400, sHeight / 2 - 300, defaultWidth, defaultHeight);
		}
		updateSize();
	}

	private void drawRotatedImage(Graphics2D g, Image img, int x, int y, int width, int height, int degree) {
		g.rotate(Math.toRadians(degree), x + width / 2, y + height / 2);
		g.drawImage(img, x, y, width, height, null);
		g.rotate(Math.toRadians(-degree), x + width / 2, y + height / 2);
	}

	private void drawKotori() {
		try {
			Graphics2D g2 = (Graphics2D) getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Image kotori = new ImageIcon(getClass().getResource("/kotori.png")).getImage();
			int size = (int) (Math.max(getHeight(), getWidth()) * 0.5);

			for (int i = 0; i < 3; i++) {
				drawRotatedImage(g2, kotori, 0, getHeight() - size, size, size, 0);
				Thread.sleep(80);
				drawRotatedImage(g2, kotori, 0, 0, size, size, 90);
				Thread.sleep(80);
				drawRotatedImage(g2, kotori, getWidth() - size, 0, size, size, 180);
				Thread.sleep(80);
				drawRotatedImage(g2, kotori, getWidth() - size, getHeight() - size, size, size, 270);
				Thread.sleep(80);
				size *= 1.2;
			}
			Thread.sleep(150);
			repaint();
		} catch (InterruptedException e) {
		}
	}

	private void drawKazumi(String word) {
		Graphics2D g2 = (Graphics2D) getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Image kazumi = new ImageIcon(getClass().getResource("/kazumi.png")).getImage();
		int size = (int) (Math.min(getHeight(), getWidth()) * 0.5);
		g2.drawImage(kazumi, getWidth() - size - 20, getHeight() - size, size, size, null);
		g2.setColor(Color.black);
		g2.setFont(new Font(null, Font.PLAIN, 20));
		g2.drawString(word, getWidth() - size - 20, getHeight() - size);
	}
}
