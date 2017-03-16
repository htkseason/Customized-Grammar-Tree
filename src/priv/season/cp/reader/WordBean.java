package priv.season.cp.reader;

public class WordBean {
	public final static int TYPE_ERROR = -1;
	public final static int TYPE_RESERVE = 1;
	public final static int TYPE_OPERATOR = 2;
	public final static int TYPE_PATTERN = 3;

	private String content;
	private String pattern;
	private int code;
	private int lineLoc;
	private int type;

	public String getContent() {
		return content;
	}

	public int getType() {
		return type;
	}

	public int getCode() {
		return code;
	}

	public int getLine() {
		return this.lineLoc;
	}

	public void setLine(int line) {
		this.lineLoc = line;
	}

	public String getPattern() {
		return pattern;
	}
	
	public boolean isFittedGrammar(String gram) {
		if (this.type == WordBean.TYPE_PATTERN)
			return this.pattern.equals(gram);
		else
			return this.content.equals(gram);
	}

	public WordBean(String content) {
		this.content = content;

		if (Words.isReserve(content))
			this.type = WordBean.TYPE_RESERVE;
		else if (Words.isOpt(content))
			this.type = WordBean.TYPE_OPERATOR;
		else if ((pattern = Words.isPattern(content)) != null)
			this.type = WordBean.TYPE_PATTERN;
		else
			this.type = WordBean.TYPE_ERROR;

	}

}
