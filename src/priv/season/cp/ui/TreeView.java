package priv.season.cp.ui;

import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import priv.season.cp.grammar.GramNode;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class TreeView extends JFrame {

	private static final long serialVersionUID = 1L;
	private TreePanel treePanel;

	public TreeView(EntranceUI entranceui) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				if (isVisible() == false) {
					entranceui.setBig();
					hideTreeView();
				}
			}
		});

		this.setTitle("TreeView bySeason");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		int sWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int sHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		this.setBounds(sWidth / 2 - 400, sHeight / 2 - 300, 800, 600);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// this.setAlwaysOnTop(true);
		this.setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());

		treePanel = new TreePanel();
		getContentPane().add(treePanel);
		treePanel.setLayout(null);
		treePanel.setVisible(true);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == 9) {
					treePanel.setSimpleStyle(!treePanel.getStyle());
				}
				switch (arg0.getKeyChar()) {
				case '`':
					treePanel.goUp();
					break;
				case 'q':
					hideTreeView();
					break;
				default:
					if (arg0.getKeyChar() >= '1' && arg0.getKeyChar() <= '9') {
						treePanel.goDown(arg0.getKeyChar() - '1');
					}
					break;
				}

			}

		});

		this.setVisible(false);
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.setDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());

	}

	public void showTreeView() {
		treePanel.start();
		this.setVisible(true);
	}

	public void hideTreeView() {
		this.setVisible(false);
		treePanel.stop();

	}

	public void setTree(GramNode tree) {
		treePanel.setTree(tree);
	}

	public GramNode getTree() {
		return treePanel.getTree();
	}

	public void gotoLine(int line) {
		if (treePanel.getTree() == null)
			return;
		GramNode root = treePanel.getTree().getRoot();

		GramNode gn = gotoLine(root, line);
		if (gn != null) {
			treePanel.setTree(gn.getParent() == null ? gn : gn.getParent());
		}
	}

	private GramNode gotoLine(GramNode node, int line) {
		if (node.getWordBean() != null && node.getWordBean().getLine() == line) {
			return node;
		} else {
			for (GramNode child : node.getChildren()) {
				GramNode result = gotoLine(child, line);
				if (result != null)
					return result;
			}
			return null;
		}

	}

}
