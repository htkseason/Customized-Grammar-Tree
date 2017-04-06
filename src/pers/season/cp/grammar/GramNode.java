package pers.season.cp.grammar;

import java.util.ArrayList;
import java.util.List;

import pers.season.cp.reader.WordBean;

public class GramNode {

	private WordBean wb;
	private ArrayList<GramNode> children;
	private GramNode parent;
	private String content;

	public GramNode(String grammar) {
		this.content = grammar;
		children = new ArrayList<GramNode>();
	}

	public GramNode(WordBean wb) {
		this.wb = wb;
		this.content = wb.getContent();
		children = new ArrayList<GramNode>();
	}

	public String getContent() {
		if (wb != null && wb.getPattern() != null)
			return wb.getPattern() + " " + content;
		else
			return content;
	}

	public String toString() {
		return getContent();
	}

	public WordBean getWordBean() {
		return wb;
	}

	public boolean isGrammar() {
		return wb == null;
	}

	public void pushChild(GramNode child) {
		children.add(child);
		child.parent = this;
	}

	public int getChildrenCount() {
		return children.size();
	}

	public int getWeight(int levels) {

		if (levels == 0)
			return 1;
		if (levels > 0) {
			int result = 1;
			for (GramNode child : children)
				result += child.getWeight(levels - 1);
			return result;
		}
		return 0;
	}

	public List<GramNode> getChildren() {
		return children;
	}

	public GramNode getChild(int i) {
		return children.get(i);
	}

	public void clearChildren() {
		children.clear();
	}

	public GramNode getParent() {
		return parent;
	}

	public GramNode getRoot() {
		if (this.parent != null)
			return this.parent.getRoot();
		else
			return this;
	}

}