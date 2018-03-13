/*
 * Copyright 2018 Yahoo Japan Corporation.
 * Licensed under the terms of the Apache 2.0 license.
 * See LICENSE in the project root.
 */
package jp.co.yahoo.vespa.language.lib.kuromoji;

import com.yahoo.language.process.Token;
import com.yahoo.language.process.TokenScript;
import com.yahoo.language.process.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Token for Kuromoji tokenizer.
 */
public class KuromojiToken implements Token {

  private List<Token> components = new ArrayList<>();

  private final String orig;
  private String tokenString = null;

  private TokenType type = TokenType.UNKNOWN;
  private TokenScript script = TokenScript.UNKNOWN;

  private boolean specialToken = false;
  private long offset = 0;

  public static class Builder {
    private KuromojiToken token;

    public Builder(String orig) {
      token = new KuromojiToken(orig);
    }

    public KuromojiToken build() {
      return token;
    }

    public Builder component(Token component) {
      token.components.add(component);
      return this;
    }

    public Builder tokenString(String tokenString) {
      token.tokenString = tokenString;
      return this;
    }

    public Builder type(TokenType type) {
      token.type = type;
      return this;
    }

    public Builder script(TokenScript script) {
      token.script = script;
      return this;
    }

    public Builder specialToken(boolean specialToken) {
      token.specialToken = specialToken;
      return this;
    }

    public Builder offset(long offset) {
      token.offset = offset;
      return this;
    }
  }

  private KuromojiToken(String orig) {
    this.orig = orig;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TokenType getType() {
    return type;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getOrig() {
    return orig;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumStems() {
    return tokenString != null ? 1 : 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStem(int i) {
    return tokenString;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumComponents() {
    return components.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getComponent(int i) {
    return components.get(i);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getOffset() {
    return offset;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TokenScript getScript() {
    return script;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTokenString() {
    return tokenString;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSpecialToken() {
    return specialToken;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isIndexable() {
    return getType().isIndexable() && getOrig().length() > 0;
  }


  /*
   * rest of this class is same as SimpleTokenType
   *
   * @see com.yahoo.language.simple.SimpleTokenType
   */

  @Override
  public int hashCode() {
    return orig.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Token)) {
      return false;
    }
    Token rhs = (Token) obj;
    if (!getType().equals(rhs.getType())) {
      return false;
    }
    if (!equalsOpt(getOrig(), rhs.getOrig())) {
      return false;
    }
    if (getOffset() != rhs.getOffset()) {
      return false;
    }
    if (!equalsOpt(getScript(), rhs.getScript())) {
      return false;
    }
    if (!equalsOpt(getTokenString(), rhs.getTokenString())) {
      return false;
    }
    if (isSpecialToken() != rhs.isSpecialToken()) {
      return false;
    }
    if (getNumComponents() != rhs.getNumComponents()) {
      return false;
    }
    for (int i = 0, len = getNumComponents(); i < len; ++i) {
      if (!equalsOpt(getComponent(i), rhs.getComponent(i))) {
        return false;
      }
    }
    return true;
  }

  private static boolean equalsOpt(Object lhs, Object rhs) {
    if (lhs == null || rhs == null) {
      return lhs == rhs;
    }
    return lhs.equals(rhs);
  }

  @Override
  public String toString() {
    return "token : " + getClass().getSimpleName() + " {\n" + toString(this, "    ") + "}";
  }

  private static String toString(Token token, String indent) {
    StringBuilder builder = new StringBuilder();
    builder.append(indent).append("components : {\n");
    for (int i = 0, len = token.getNumComponents(); i < len; ++i) {
      Token comp = token.getComponent(i);
      builder.append(indent).append("    [").append(i).append("] : ").append(comp.getClass().getSimpleName());
      builder.append(" {\n").append(toString(comp, indent + "        "));
      builder.append(indent).append("    }\n");
    }
    builder.append(indent).append("}\n");
    builder.append(indent).append("offset : ").append(token.getOffset()).append("\n");
    builder.append(indent).append("orig : ").append(quoteString(token.getOrig())).append("\n");
    builder.append(indent).append("script : ").append(token.getScript()).append("\n");
    builder.append(indent).append("special : ").append(token.isSpecialToken()).append("\n");
    builder.append(indent).append("token string : ").append(quoteString(token.getTokenString())).append("\n");
    builder.append(indent).append("type : ").append(token.getType()).append("\n");
    return builder.toString();
  }

  private static String quoteString(String str) {
    return str != null ? "'" + str + "'" : null;
  }
}
