/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import jp.co.yahoo.vespa.language.lib.util.JapaneseNormalizer.Result;

public class JapaneseNormalizerTest {

    @Test
    public void testFullWidth() throws Exception {
        String input = "AB＿C．";
        
        Result result = JapaneseNormalizer.normalizeByNFKC(input);
        assertEquals("AB_C.", result.norm);
        assertEquals(6, result.origIndexes.length);
        
        int[] expectedIndexes = new int[]{0,1,2,3,4,5};
        for (int i=0; i<6; ++i) {
            assertEquals(expectedIndexes[i], result.origIndexes[i]);
        }
    }
    
    @Test
    public void testHalfWidth() throws Exception {
        String input = "ｶﾊﾞｦﾀﾞ";
        
        Result result = JapaneseNormalizer.normalizeByNFKC(input);
        assertEquals("カバヲダ", result.norm);
        assertEquals(5, result.origIndexes.length);
        
        int[] expectedIndexes = new int[]{0,1,3,4,6};
        for (int i=0; i<5; ++i) {
            assertEquals(expectedIndexes[i], result.origIndexes[i]);
        }
    }
    
    @Test
    public void testCJKCompatibility() throws Exception {
        String input = "１㌶に30㌧の㈱";
        
        Result result = JapaneseNormalizer.normalizeByNFKC(input);
        assertEquals("1ヘクタールに30トンの(株)", result.norm);
        assertEquals(16, result.origIndexes.length);
        
        int[] expectedIndexes = new int[]{0,1,1,1,1,1,2,3,4,5,5,6,7,7,7,8};
        for (int i=0; i<16; ++i) {
            assertEquals(expectedIndexes[i], result.origIndexes[i]);
        }
    }
    
    @Test
    public void testSurrogatePair() throws Exception {
        String input = "A𐌀a𐐀";
        
        Result result = JapaneseNormalizer.normalizeByNFKC(input);
        assertEquals("A𐌀a𐐀", result.norm);
        assertEquals(7, result.origIndexes.length);
        
        int[] expectedIndexes = new int[]{0,1,2,3,4,5,6};
        for (int i=0; i<7; ++i) {
            assertEquals(expectedIndexes[i], result.origIndexes[i]);
        }
    }

    @Test
    public void testLongInput() throws Exception {
        String input = "ｷﾞﾛｯﾎﾟﾝで＄１００のSHI-SU-！";
        
        Result result = JapaneseNormalizer.normalizeByNFKC(input);
        assertEquals("ギロッポンで$100のSHI-SU-!", result.norm);
        assertEquals(20, result.origIndexes.length);
        
        int[] expectedIndexes = new int[]{0,2,3,4,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21};
        for (int i=0; i<20; ++i) {
            assertEquals(expectedIndexes[i], result.origIndexes[i]);
        }
    }
}
