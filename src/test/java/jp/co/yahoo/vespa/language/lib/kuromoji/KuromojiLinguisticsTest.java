/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.kuromoji;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import com.yahoo.collections.Tuple2;
import com.yahoo.component.Version;
import com.yahoo.language.Language;
import com.yahoo.language.Linguistics;
import com.yahoo.language.detect.Detection;
import com.yahoo.language.detect.Detector;
import com.yahoo.language.lib.kuromoji.KuromojiConfig;
import com.yahoo.language.process.CharacterClasses;
import com.yahoo.language.process.GramSplitter;
import com.yahoo.language.process.GramSplitter.Gram;
import com.yahoo.language.process.GramSplitter.GramSplitterIterator;
import com.yahoo.language.process.Normalizer;
import com.yahoo.language.process.Segmenter;
import com.yahoo.language.process.StemList;
import com.yahoo.language.process.StemMode;
import com.yahoo.language.process.Stemmer;
import com.yahoo.language.process.Token;
import com.yahoo.language.process.Tokenizer;
import com.yahoo.language.process.Transformer;
import com.yahoo.vespa.configdefinition.SpecialtokensConfig;


public class KuromojiLinguisticsTest {
    
    private static final KuromojiConfig DEFAULT_KUROMOJI_CONFIG = new KuromojiConfig(new KuromojiConfig.Builder());
    private static final SpecialtokensConfig DEFAULT_STOKEN_CONFIG = new SpecialtokensConfig(new SpecialtokensConfig.Builder());

    @Test
    public void testFallback() throws Exception {
        Optional<Linguistics> nullOptional = KuromojiLinguistics.create(null, null);
        assertFalse(nullOptional.isPresent());
    }
    
    @Test
    public void testStemmer() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Stemmer stemmer = linguistics.getStemmer();
        String input = "お寿司が食べたい。";
        String[] expecteds = new String[]{"お", "寿司", "が", "食べる", "たい"};
        List<StemList> actuals = stemmer.stem(input, StemMode.ALL, Language.JAPANESE);
        
        assertEquals(expecteds.length, actuals.size());
        for (int i=0; i<actuals.size(); ++i) {
            assertEquals(1, actuals.get(i).size());
            assertEquals(expecteds[i], actuals.get(i).get(0));
        }
    }
    
    @Test
    public void testTokenizer() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Tokenizer tokenizer = linguistics.getTokenizer();
        String input = "お寿司が食べたい。";
        String[] expecteds = new String[]{"お", "寿司", "が", "食べる", "たい", "。"};
        List<Token> actuals = (List<Token>) tokenizer.tokenize(input, Language.JAPANESE, StemMode.ALL, false);

        assertEquals(expecteds.length, actuals.size());
        for (int i=0; i<actuals.size(); ++i) {
            assertEquals(expecteds[i], actuals.get(i).getTokenString());
        }
    }

    @Test
    public void testSegmenter() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Segmenter segmenter = linguistics.getSegmenter();
        String input = "お寿司が食べたい。";
        String[] expecteds = new String[]{"お", "寿司", "が", "食べ", "たい"};
        List<String> actuals = segmenter.segment(input, Language.JAPANESE);

        assertEquals(expecteds.length, actuals.size());
        for (int i=0; i<actuals.size(); ++i) {
            assertEquals(expecteds[i], actuals.get(i));
        }
    }
    
    @Test
    public void testNormalizer() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Normalizer normalizer = linguistics.getNormalizer();
        String input = "ｷﾞﾛｯﾎﾟﾝでﾙｰﾋﾞｰ";
        String expected = "ギロッポンでルービー";
        String actual = normalizer.normalize(input);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testTransformer() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Transformer transformer = linguistics.getTransformer();
        String input = "お寿司が食べたい。";
        String expected = "お寿司か\u3099食へ\u3099たい。";
        String actual = transformer.accentDrop(input, Language.JAPANESE);
        
        assertEquals(expected, actual);
    }
    
    @Test
    public void testDetector() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Detector detector = linguistics.getDetector();
        
        Detection d = detector.detect("ほげ", null);
        assertEquals(Language.JAPANESE, d.getLanguage());

        // XXX: because using default impl
        d = detector.detect("ｶﾞｶﾞｶﾞ", null);
        assertEquals(Language.UNKNOWN, d.getLanguage());
        
        d = detector.detect(java.text.Normalizer.normalize("ｶﾞｶﾞｶﾞ", java.text.Normalizer.Form.NFKC), null);
        assertEquals(Language.JAPANESE, d.getLanguage());
    }
    
    @Test
    public void testGramSplitter() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        GramSplitter gramSplitter = linguistics.getGramSplitter();
        
        String input = "お寿司が食べたい。";
        String[] expecteds = new String[]{"お寿", "寿司", "司が", "が食", "食べ", "べた", "たい"};
        GramSplitterIterator actualIter = gramSplitter.split(input, 2);
        
        for (String expected : expecteds) {
            Gram gram = actualIter.next();
            assertEquals(expected, input.substring(gram.getStart(), gram.getStart() + gram.getLength()));
        }
        assertFalse(actualIter.hasNext());
    }
    
    @Test
    public void testCharacterClasses() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        CharacterClasses characterClasses = linguistics.getCharacterClasses();
        
        assertTrue(characterClasses.isLetter(Character.codePointAt("ほげ", 0)));
        
        // XXX: because using default impl
        assertTrue(characterClasses.isLetter(Character.codePointAt("１２３", 0))); // ?
        assertFalse(characterClasses.isLetter(Character.codePointAt(java.text.Normalizer.normalize("１２３", java.text.Normalizer.Form.NFKC), 0)));
        
        assertTrue(characterClasses.isDigit(Character.codePointAt("１２３", 0)));
        assertTrue(characterClasses.isDigit(Character.codePointAt(java.text.Normalizer.normalize("１２３", java.text.Normalizer.Form.NFKC), 0)));
    }
    
    @Test
    public void testVersion() throws Exception {
        Linguistics linguistics = KuromojiLinguistics.create(DEFAULT_KUROMOJI_CONFIG, DEFAULT_STOKEN_CONFIG).get();
        
        Tuple2<String, Version> krmjVersion = linguistics.getVersion(Linguistics.Component.TOKENIZER);
        assertEquals("kuromoji", krmjVersion.first);
        assertEquals("0.9", krmjVersion.second.toString()); // XXX: change if you changed version!
        
        Tuple2<String, Version> simpleVersion = linguistics.getVersion(Linguistics.Component.GRAM_SPLITTER);
        assertEquals("yahoo", simpleVersion.first);
        assertEquals("1", simpleVersion.second.toString()); // XXX: change if you changed version!
    }
}
