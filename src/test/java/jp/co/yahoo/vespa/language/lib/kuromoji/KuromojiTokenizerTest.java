/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.kuromoji;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import com.yahoo.language.Language;
import com.yahoo.language.lib.kuromoji.KuromojiConfig;
import com.yahoo.language.process.StemMode;
import com.yahoo.vespa.configdefinition.SpecialtokensConfig;
import org.junit.Test;

import com.yahoo.language.Linguistics;
import com.yahoo.language.process.Token;
import com.yahoo.language.process.Tokenizer;
import com.yahoo.language.simple.SimpleLinguistics;

import jp.co.yahoo.vespa.language.lib.data.KuromojiTokenizeTestData;
import jp.co.yahoo.vespa.language.lib.data.KuromojiTokenizeTestData.InputArgs;

/*
 * see "src/text/resources/KuromojiTokenizerTest_${testCase}.csv" for details
 */
public class KuromojiTokenizerTest {

    public static Linguistics simpleLinguistics = new SimpleLinguistics();
    
    @Test
    public void testDefaultTokenize() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testWithoutSpecialTokens() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testWithSpecialTokens() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testJapaneseNormalizer() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testCaseSensitive() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testSpaces() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testCJKCompatibility() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testLanguageEnglish() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testLanguageEnglishWithAll() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testUserDict() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testUserDict2() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }
    
    @Test
    public void testNoStem() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }

    @Test
    public void testCombingDot() throws Exception {
        KuromojiTokenizeTestData testData = KuromojiTokenizeTestData.loadTestData();
        doTest(testData);
    }

    private void doTest(KuromojiTokenizeTestData testData) throws Exception {
        InputArgs inputArgs = testData.getInitArgs();
        List<KuromojiToken> expectTokens = testData.getExpectTokens();
        KuromojiContext context = new KuromojiContext(testData.getKuromojiConfig(),
                                                      testData.getSpecialtokensConfig());
        Tokenizer tokenizer = new KuromojiTokenizer(context,
                                                    simpleLinguistics.getNormalizer(),
                                                    simpleLinguistics.getTransformer(),
                                                    simpleLinguistics.getTokenizer());
        
        Iterable<Token> tokens = tokenizer.tokenize(inputArgs.input,
                                                    inputArgs.language,
                                                    inputArgs.stemMode,
                                                    inputArgs.removeAccents);
        int idx = 0;
        for (Token token : tokens) {
            KuromojiToken expectToken = expectTokens.get(idx);
            
            assertEquals(expectToken.getOrig(), token.getOrig());
            assertEquals(expectToken.getTokenString(), token.getTokenString());
            assertEquals(expectToken.getType(), token.getType());
            assertEquals(expectToken.getScript(), token.getScript());
            assertEquals(expectToken.isSpecialToken(), token.isSpecialToken());
            assertEquals(expectToken.getOffset(), token.getOffset());

            ++idx;
        }
    }
}
