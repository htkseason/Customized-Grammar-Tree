package priv.season.cp.grammar;

import java.util.HashMap;
import java.util.List;
import priv.season.cp.YamlInfoBean;

public final class Grammar {
	public static HashMap<String, List<List<String>>> grammarMap;

	public static void init(YamlInfoBean ib) {
		grammarMap = new HashMap<String, List<List<String>>>(ib.grammar);
	}

	public static String[][] getTemplates(String grammar) {
		List<List<String>> lst = grammarMap.get(grammar);
		if (lst == null)
			return null;
		String[][] result = new String[lst.size()][];
		for (int i = 0; i < result.length; i++)
			result[i] = lst.get(i).toArray(new String[0]);
		return result;
	}
}
