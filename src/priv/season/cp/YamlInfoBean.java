package priv.season.cp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.YamlReader;
import priv.season.cp.grammar.Grammar;
import priv.season.cp.reader.Words;

public class YamlInfoBean {
	public List<String> reserves;
	public List<String> opts;
	public Map<String, String> patterns;
	public Map<String, List<List<String>>> grammar;
	public String anno_left;
	public String anno_right;
	public String anno_line;
	
	public static boolean init() {
		try {
			YamlReader in = new YamlReader(new FileReader("giligili_mind.yaml"));
			YamlInfoBean ib = (YamlInfoBean) in.read();
			in.close();
			Words.init(ib);
			Grammar.init(ib);
			return true;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "<giligili_mind.yaml> not found", "error", JOptionPane.ERROR_MESSAGE);
		} catch (YamlException | IOException e) {
			JOptionPane.showMessageDialog(null, "<giligili_mind.yaml> has error", "error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
		
	}
}
