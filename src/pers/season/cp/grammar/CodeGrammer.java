package pers.season.cp.grammar;

import java.util.ArrayList;
import java.util.List;

import pers.season.cp.reader.WordBean;

public class CodeGrammer {

	private final static String GRAMMAR_ENTRANCE = "<grammar_entrance>";
	private final static String CTRL_OPTIONAL = "<ctrl_optional>";
	private WordBean[] wbs;
	private int pos;
	private GramNode treeRoot;
	private GramError lastError;
	private ArrayList<String> stack;	//avoid looping

	public CodeGrammer() {
		this(null);
	}

	public CodeGrammer(WordBean[] wbs) {
		this.wbs = wbs;
		stack = new ArrayList<String>();
	}

	public void setWordBeans(WordBean[] wbs) {
		this.wbs = wbs;
	}

	public boolean parse() {
		pos = 0;
		lastError = new GramError();
		treeRoot = parse(GRAMMAR_ENTRANCE);
		if (treeRoot == null)
			return false;
		return true;

	}

	public GramNode getTreeRoot() {
		return treeRoot;
	}

	public GramError getLastError() {
		return lastError;
	}

	private boolean isLooping() {
		String token = stack.get(stack.size() - 1);
		List<String> sub = stack.subList(0, stack.size() - 1);

		int i1 = sub.lastIndexOf(token);
		if (i1 == -1)
			return false;

		List<String> slice = sub.subList(i1, sub.size());

		List<String> remain = sub.subList(0, i1);

		int i2 = remain.lastIndexOf(token);

		if (i2 == -1 || i1 - i2 != slice.size())
			return false;

		for (int i = 0; i < slice.size(); i++)
			if (!slice.get(i).equals(remain.get(i2 + i)))
				return false;

		// forceJumpOut = slice.size();
		return true;
	}

	public GramNode parse(String gramName) {

		String token = pos + "_" + gramName;
		stack.add(token);
		if (isLooping()) {
			stack.remove(stack.size() - 1);
			return null;
		}

		String[][] templates = Grammar.getTemplates(gramName);

		if (templates == null) {
			templates = new String[0][];
		}

		GramNode thisNode = new GramNode(gramName);

		int startPos = pos;

		for (int i = 0; i < templates.length; i++) {

			boolean fitted = true;
			boolean isOptional = false;
			for (String eleName : templates[i]) {
				if (eleName.equals(CTRL_OPTIONAL)) {
					isOptional = true;
					continue;
				}

				if (Grammar.grammarMap.containsKey(eleName)) {
					GramNode result = parse(eleName);
					if (result == null) {
						if (isOptional) {
							break;
						} else {
							fitted = false;
							break;
						}
					} else {
						thisNode.pushChild(result);
					}
				} else {
					if (pos >= wbs.length || !wbs[pos].isFittedGrammar(eleName)) {
						if (isOptional) {
							break;
						} else {
							fitted = false;
							break;
						}
					} else {
						thisNode.pushChild(new GramNode(wbs[pos]));
						pos++;
					}
				}
				isOptional = false;

			}

			if (fitted) {
				stack.remove(stack.size() - 1);
				return thisNode;
			} else {
				pos = startPos;
				thisNode.clearChildren();
			}
		}

		if (pos >= lastError.errorWbPos) {
			lastError.errorWbPos = pos;
			if (pos < wbs.length)
				lastError.errorWb = wbs[pos];
			else
				lastError.errorWb = new WordBean("");
			lastError.expectedGrammarName = gramName;
		}

		stack.remove(stack.size() - 1);
		return null;

	}

}
