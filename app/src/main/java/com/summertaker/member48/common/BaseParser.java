package com.summertaker.member48.common;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "== " + this.getClass().getSimpleName();
    }
}