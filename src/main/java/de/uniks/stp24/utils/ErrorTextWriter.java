package de.uniks.stp24.utils;

import java.util.Map;

public class ErrorTextWriter {

    private final String text;


    public ErrorTextWriter (Map<Integer,String> map, int code) {
        this.text = map.get(code) ;
    }

    public String getErrorText() {
        return text;
    }
}
