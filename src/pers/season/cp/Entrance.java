package pers.season.cp;

import pers.season.cp.ui.EntranceUI;

public final class Entrance {
	public static void main(String[] args) {
		if (!YamlInfoBean.init())
			return;
		new EntranceUI();

	}
}
