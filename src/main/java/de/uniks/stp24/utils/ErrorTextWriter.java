package de.uniks.stp24.utils;

import java.util.Map;

public class ErrorTextWriter {

    private final String text;

    // seek the correct text in the map
    public ErrorTextWriter (Map<Integer,String> map, int code) {
        this.text = map.get(code) ;
    }

    public String getErrorText() {
        return text;
    }
}
