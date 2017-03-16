package priv.season.cp.reader;

import java.util.LinkedList;

public class CodeReader {
	private LinkedList<WordBean> wbList;
	private boolean underAnno ;

	public WordBean[] getWordBeanList() {
		return wbList.toArray(new WordBean[0]);
	}

	public CodeReader() {
		clear();
	}

	public void clear() {
		wbList = new LinkedList<WordBean>();
		underAnno = false;
	}

	public void readNextLine(String input, int lineCount) {
		input = deleteAnno(input);
		input = input.replace('\t', ' ');
		String[] strs = input.toString().split(" ");

		for (String str : strs) {
			StringBuffer sb = new StringBuffer(str);
			int pos = 0;
			while (pos < sb.length()) {
				WordBean wb = pickFirstWord(sb.subSequence(pos, sb.length()));
				wb.setLine(lineCount);
				wbList.addLast(wb);
				pos += wb.getContent().length();
			}
		}
	}

	private static WordBean pickFirstWord(CharSequence str) {
		// separate the string with operator

		// if start with an operator
		int headOptLen = Words.getHeadOptLength(str);
		if (headOptLen > 0) {
			return new WordBean(str.subSequence(0, headOptLen).toString());
		}

		// slice the string before the first operator
		String slice;
		int optIndex = -1;
		for (int i = 0; i < str.length(); i++) {
			if (Words.getHeadOptLength(str.subSequence(i, str.length())) > 0) {
				optIndex = i;
				break;
			}
		}

		if (optIndex != -1) {
			slice = str.subSequence(0, optIndex).toString();
		} else {
			slice = str.toString();
		}
		
		return new WordBean(slice);


	}

	private String deleteAnno(String str) {
		if (!underAnno) {
			int lineAnnoIndex = str.indexOf(Words.anno_line);
			int leftAnnoIndex = str.indexOf(Words.anno_left);

			if (lineAnnoIndex == -1 && leftAnnoIndex == -1) {
				return str;
			}

			if (lineAnnoIndex != -1 && leftAnnoIndex != -1) {
				if (Math.min(lineAnnoIndex, leftAnnoIndex) == leftAnnoIndex) {
					lineAnnoIndex = -1;
				} else {
					leftAnnoIndex = -1;
				}
			}

			if (lineAnnoIndex != -1) {
				str = str.substring(0, lineAnnoIndex);
				return str;
			}

			if (leftAnnoIndex != -1) {
				int rightAnnoIndex = str.indexOf(Words.anno_right, leftAnnoIndex);
				if (rightAnnoIndex == -1) {
					str = str.substring(0, leftAnnoIndex);
					underAnno = true;
					return str;
				} else {
					str = str.substring(0, leftAnnoIndex) + " " + str.substring(rightAnnoIndex + Words.anno_right.length());
				}
				return deleteAnno(str);
			}

			return str;
		} else {
			int rightAnnoIndex = str.indexOf(Words.anno_right);
			if (rightAnnoIndex != -1) {
				str = str.substring(rightAnnoIndex + Words.anno_right.length());
				underAnno = false;
				return deleteAnno(str);
			} else
				return "";
		}
	}
}
