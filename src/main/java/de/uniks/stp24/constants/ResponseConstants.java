package de.uniks.stp24.constants;

import java.util.Map;

/* maps with text outputs for different server responses depending on controller/screen
there are on /api/v1 these 19 codes:
CODE -> STRING
400 -> Bad Request
401 -> Unauthorized
403 -> Forbidden
404 -> Not Found
405 -> Method Not Allowed
406 -> Not Acceptable
408 -> Request Timeout
409 -> Conflict
410 -> Gone
412 -> Precondition Failed
413 -> Payload Too Large
415 -> Unsupported Media Type
418 -> I Am A Teapot
422 -> Unprocessable Entity
500 -> Internal Server Error
501 -> Not Implemented
502 -> Bad Gateway
503 -> Service Unavailable
504 -> Gateway Timeout
 */
public class ResponseConstants {

    static final public Map<Integer, String> respLogin = Map.of(400,"A pirate who forgot his password walk the plank!",
            401, "Invalid username or password",
            429, "rate limit reached",
            -1 , "you forgot to give your name / password pirate!!",
            201, "... boarding in ...");

    static final public Map<Integer, String> respSignup = Map.of(400,"validation failed",
            409, "name already exits",
            -1 , "please put in name or/and password");

    static final public Map<String, Map<Integer,String>> responses = Map.of("login", respLogin,
    "signup", respSignup);


}
