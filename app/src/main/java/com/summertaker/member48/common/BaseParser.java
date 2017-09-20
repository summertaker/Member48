package com.summertaker.member48.common;

public class BaseParser {

    private String mTag;

    public BaseParser() {
        mTag = "== " + this.getClass().getSimpleName();
    }
}