/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.yahoo.language.process.TokenType;

import jp.co.yahoo.vespa.language.lib.util.JapaneseTokenType;

public class JapaneseTokenTypeTest {

    @Test
    public void testAscii() {
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("a", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("Z", 0)));
        assertEquals(TokenType.SYMBOL, JapaneseTokenType.valueOf(Character.codePointAt("+", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt(".", 0)));
        assertEquals(TokenType.NUMERIC, JapaneseTokenType.valueOf(Character.codePointAt("5", 0)));
        assertEquals(TokenType.SPACE, JapaneseTokenType.valueOf(Character.codePointAt(" ", 0)));
    }
    
    @Test
    public void testCjkSymbol() {
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("、", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("。", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("【", 0)));
        assertEquals(TokenType.SYMBOL, JapaneseTokenType.valueOf(Character.codePointAt("〄", 0)));
        assertEquals(TokenType.SYMBOL, JapaneseTokenType.valueOf(Character.codePointAt("★", 0)));
        assertEquals(TokenType.SYMBOL, JapaneseTokenType.valueOf(Character.codePointAt("♪", 0)));
        assertEquals(TokenType.SYMBOL, JapaneseTokenType.valueOf(Character.codePointAt("〒", 0)));
    }
    
    @Test
    public void testHalfWidth() {
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt(",", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt(".", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("ｶ", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("ｦ", 0)));
    }

    @Test
    public void testFullWidth() {
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("，", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("．", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("！", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("？", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("＆", 0)));
        assertEquals(TokenType.PUNCTUATION, JapaneseTokenType.valueOf(Character.codePointAt("％", 0)));
        assertEquals(TokenType.NUMERIC, JapaneseTokenType.valueOf(Character.codePointAt("０", 0)));
        assertEquals(TokenType.NUMERIC, JapaneseTokenType.valueOf(Character.codePointAt("９", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("A", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("Z", 0)));
    }
    
    @Test
    public void testHiragana() {
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("あ", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("ぺ", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("が", 0)));
    }
    
    @Test
    public void testKatakana() {
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("ヤ", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("ペ", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("ガ", 0)));
    }
    
    @Test
    public void testKanji() {
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("夏", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("暑", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("麦", 0)));
        assertEquals(TokenType.ALPHABETIC, JapaneseTokenType.valueOf(Character.codePointAt("酒", 0)));
    }
}
