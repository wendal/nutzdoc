package org.nutz.doc;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.nutz.doc.plain.PlainParserTest; //import org.nutz.doc.plain.TokenWalkerTest;
import org.nutz.doc.style.ColorTest;
import org.nutz.doc.zdoc.BlockMakerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { BlockMakerTest.class, PlainParserTest.class, ColorTest.class })
public class ZDocTestAll {}
