package de.uniks.stp24.constants;

import java.util.Map;


public class ResponseConstants {


    static final public Map<Integer, String> respLogin = Map.of(400,"A pirate who forgot his password walk the plank!",
            401, "Invalid username or password",
            409, "time",
            -1 , "you forgot to give your name / password pirate!!",
            200, "... boarding in ...");

    static final public Map<Integer, String> respSignup = Map.of(400,"validation failed",
            401, "Invalid username or password",
            409, "unkwon",
            -1 , "please put in name or/and password");
    static final public Map<String, Map<Integer,String>> responses = Map.of("login", respLogin,
    "signup", respSignup);


}
