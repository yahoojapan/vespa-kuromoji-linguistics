/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.yahoo.language.process.TokenScript;

import jp.co.yahoo.vespa.language.lib.util.JapaneseTokenScript;

public class JapaneseTokenScriptTest {

    @Test
    public void testAscii() {
        assertEquals(TokenScript.ASCII, JapaneseTokenScript.valueOf(Character.codePointAt("a", 0)));
        assertEquals(TokenScript.ASCII, JapaneseTokenScript.valueOf(Character.codePointAt("Z", 0)));
        assertEquals(TokenScript.ASCII, JapaneseTokenScript.valueOf(Character.codePointAt("+", 0)));
        assertEquals(TokenScript.ASCII, JapaneseTokenScript.valueOf(Character.codePointAt(".", 0)));
        assertEquals(TokenScript.ASCII, JapaneseTokenScript.valueOf(Character.codePointAt("5", 0)));
    }
    
    @Test
    public void testHiragana() {
        assertEquals(TokenScript.HIRAGANA, JapaneseTokenScript.valueOf(Character.codePointAt("あ", 0)));
        assertEquals(TokenScript.HIRAGANA, JapaneseTokenScript.valueOf(Character.codePointAt("ぺ", 0)));
        assertEquals(TokenScript.HIRAGANA, JapaneseTokenScript.valueOf(Character.codePointAt("が", 0)));
    }
    
    @Test
    public void testKatakana() {
        assertEquals(TokenScript.KATAKANA, JapaneseTokenScript.valueOf(Character.codePointAt("ヤ", 0)));
        assertEquals(TokenScript.KATAKANA, JapaneseTokenScript.valueOf(Character.codePointAt("ペ", 0)));
        assertEquals(TokenScript.KATAKANA, JapaneseTokenScript.valueOf(Character.codePointAt("ガ", 0)));
    }
    
    @Test
    public void testKanji() {
        assertEquals(TokenScript.UNKNOWN, JapaneseTokenScript.valueOf(Character.codePointAt("夏", 0)));
        assertEquals(TokenScript.UNKNOWN, JapaneseTokenScript.valueOf(Character.codePointAt("暑", 0)));
        assertEquals(TokenScript.UNKNOWN, JapaneseTokenScript.valueOf(Character.codePointAt("麦", 0)));
        assertEquals(TokenScript.UNKNOWN, JapaneseTokenScript.valueOf(Character.codePointAt("酒", 0)));
    }
}
