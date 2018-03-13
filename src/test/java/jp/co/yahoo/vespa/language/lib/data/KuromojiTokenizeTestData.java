/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.yahoo.language.Language;
import com.yahoo.language.lib.kuromoji.KuromojiConfig;
import com.yahoo.language.process.StemMode;
import com.yahoo.language.process.TokenScript;
import com.yahoo.language.process.TokenType;
import com.yahoo.vespa.configdefinition.SpecialtokensConfig;

import jp.co.yahoo.vespa.language.lib.kuromoji.KuromojiToken;

public class KuromojiTokenizeTestData {

    public static final String TESTDATA_DIR = "testdata";
    
    public static class InputArgs {
        public final String input;
        public final Language language;
        public final StemMode stemMode;
        public final boolean removeAccents;
        
        public InputArgs(String input, Language language, StemMode stemMode, boolean removeAccents) {
            this.input = input;
            this.language = language;
            this.stemMode = stemMode;
            this.removeAccents = removeAccents;
        }
    }

    private final KuromojiConfig kuromojiConfig;
    private final SpecialtokensConfig specialtokensConfig;
    private final InputArgs inputArgs;
    private final List<KuromojiToken> expectTokens;
    
    private KuromojiTokenizeTestData(KuromojiConfig kuromojiConfig,
                                     SpecialtokensConfig specialtokensConfig,
                                     InputArgs inputArgs,
                                     List<KuromojiToken> expectTokens) {
        this.kuromojiConfig = kuromojiConfig;
        this.specialtokensConfig = specialtokensConfig;
        this.inputArgs = inputArgs;
        this.expectTokens = expectTokens;
    }
    
    public static KuromojiTokenizeTestData loadTestData() throws Exception {
        String[] classPathes = Thread.currentThread().getStackTrace()[2].getClassName().split("\\.");
        
        String callerClass = classPathes[classPathes.length - 1];
        String callerMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
        
        return loadTestData(String.format("%s/%s_%s.csv", TESTDATA_DIR, callerClass, callerMethod));
    }
    
    public static KuromojiTokenizeTestData loadTestData(String csvFile) throws Exception {
        KuromojiConfig.Builder krmjBuilder = new KuromojiConfig.Builder();
        SpecialtokensConfig.Builder stBuilder = new SpecialtokensConfig.Builder();
        InputArgs inputArgs = null;
        List<KuromojiToken> expectTokens = new ArrayList<>();
        
        File file = new File(KuromojiTokenizeTestData.class.getClassLoader().getResource(csvFile).getPath());
        try (Scanner scanner = new Scanner(file, "UTF-8")) {
            while (scanner.hasNext()) {
                String[] elems = scanner.nextLine().split(",");
                if (elems[0].equals("kuromoji")) {
                    KuromojiConfig.Kanji.Builder kBuilder = new KuromojiConfig.Kanji.Builder()
                                                            .length_threshold(Integer.parseInt(elems[2]))
                                                            .penalty(Integer.parseInt(elems[3]));
                    KuromojiConfig.Other.Builder oBuilder = new KuromojiConfig.Other.Builder()
                                                            .length_threshold(Integer.parseInt(elems[4]))
                                                            .penalty(Integer.parseInt(elems[5]));
                    krmjBuilder = krmjBuilder.mode(elems[1])
                                             .kanji(kBuilder)
                                             .other(oBuilder)
                                             .nakaguro_split(Boolean.parseBoolean(elems[6]))
                                             .user_dict(elems[7])
                                             .tokenlist_name(elems[8])
                                             .all_language(Boolean.parseBoolean(elems[9]))
                                             .ignore_case(Boolean.parseBoolean(elems[10]));
                }
                else if (elems[0].equals("specialtokens")) {
                    SpecialtokensConfig.Tokenlist.Builder tklBuilder = new SpecialtokensConfig.Tokenlist.Builder()
                                                                       .name(elems[1]);
                    for (int i=2; i<elems.length; i+=2) {
                        tklBuilder = tklBuilder.tokens(new SpecialtokensConfig.Tokenlist.Tokens.Builder()
                                                       .token(elems[i])
                                                       .replace(elems[i+1]));
                    }
                    stBuilder = stBuilder.tokenlist(tklBuilder);
                }
                else if (elems[0].equals("input")) {
                    inputArgs = new InputArgs(elems[1],
                                              Language.valueOf(elems[2]),
                                              StemMode.valueOf(elems[3]),
                                              Boolean.parseBoolean(elems[4])); 
                }
                else if (elems[0].equals("expect")) {
                    expectTokens.add(new KuromojiToken.Builder(elems[1])
                                .tokenString(elems[2])
                                .type(TokenType.valueOf(elems[3]))
                                .script(TokenScript.valueOf(elems[4]))
                                .specialToken(Boolean.parseBoolean(elems[5]))
                                .offset(Long.parseLong(elems[6]))
                                .build());
                }
            }
        }
        
        return new KuromojiTokenizeTestData(new KuromojiConfig(krmjBuilder),
                                            new SpecialtokensConfig(stBuilder),
                                            inputArgs,
                                            expectTokens);
    }
    
    public KuromojiConfig getKuromojiConfig() {
        return kuromojiConfig;
    }
    
    public SpecialtokensConfig getSpecialtokensConfig() {
        return specialtokensConfig;
    }
    
    public InputArgs getInitArgs() {
        return inputArgs;
    }
    
    public List<KuromojiToken> getExpectTokens() {
        return expectTokens;
    }
}
