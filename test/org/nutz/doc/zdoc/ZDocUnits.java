package org.nutz.doc.zdoc;

import java.io.BufferedReader;

import org.nutz.lang.Lang;

public abstract class ZDocUnits {

	public static ScanResult sr2(String s) {
		return new Scanning(2).scan(new BufferedReader(Lang.inr(s)));
	}

	public static ScanResult sr4(String s) {
		return new Scanning(4).scan(new BufferedReader(Lang.inr(s)));
	}

	public static Line scan4(String s) {
		return sr4(s).root();
	}

	public static Line scan2(String s) {
		return sr2(s).root();
	}

}
