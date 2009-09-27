package org.nutz.doc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.doc.style.ColorTest;
import org.nutz.doc.zdoc.BlockMakerTest;
import org.nutz.doc.zdoc.ZDocFileParserTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BlockMakerTest.class, ZDocFileParserTest.class, ColorTest.class })
public class ZDocTestAll {}
