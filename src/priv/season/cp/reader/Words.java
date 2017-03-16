package priv.season.cp.reader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import priv.season.cp.YamlInfoBean;

public final class Words {
	private final static int STANDARD_OPT_LENGTH = 2;
	private static HashSet<String> reserveWords = null;
	private static HashSet<String> shortOpts = null;
	private static HashSet<String> longOpts = null;
	private static LinkedHashMap<String, Pattern> patterns = null;
	private static int OPT_MAX_LEN = STANDARD_OPT_LENGTH;

	public static String anno_left, anno_right, anno_line;

	public static void init(YamlInfoBean ib) {
		anno_left = ib.anno_left;
		anno_right = ib.anno_right;
		anno_line = ib.anno_line;
		reserveWords = new HashSet<String>(ib.reserves);
		shortOpts = new HashSet<String>();
		longOpts = new HashSet<String>();
		for (String opt : ib.opts) {
			if (opt.length() > STANDARD_OPT_LENGTH) {
				longOpts.add(opt);
				if (opt.length() > OPT_MAX_LEN)
					OPT_MAX_LEN = opt.length();
			} else
				shortOpts.add(opt);
		}

		patterns = new LinkedHashMap<String, Pattern>();
		Iterator<Entry<String, String>> iter = ib.patterns.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, String> et = iter.next();
			patterns.put(et.getKey(), Pattern.compile(et.getValue()));
		}
	}

	public static boolean isPattern(CharSequence word, String patName) {
		Pattern pat = patterns.get(patName);
		if (pat == null)
			return false;
		Matcher matcher = pat.matcher(word);
		return matcher.matches();
	}

	public static String isPattern(CharSequence word) {
		Iterator<Entry<String, Pattern>> iter = patterns.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Pattern> et = iter.next();
			Matcher matcher = et.getValue().matcher(word);
			if (matcher.matches())
				return et.getKey();
		}
		return null;
	}

	public static boolean isOpt(CharSequence word) {
		if (word.length() <= STANDARD_OPT_LENGTH)
			return shortOpts.contains(word.toString());
		else
			return longOpts.contains(word.toString());
	}

	public static int getHeadOptLength(CharSequence str) {
		for (int i = str.length() > OPT_MAX_LEN ? OPT_MAX_LEN : str.length(); i > 0; i--) {
			String ts = str.subSequence(0, i).toString();
			if (isOpt(ts)) {
				return i;
			}
		}
		return 0;
	}

	public static boolean isReserve(CharSequence word) {
		return reserveWords.contains(word.toString());
	}

}
