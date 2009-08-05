package org.nutz.doc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.nutz.doc.plain.PlainParserTest;
import org.nutz.doc.style.ColorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { PlainParserTest.class, ColorTest.class })
public class ZDocTestAll {}
