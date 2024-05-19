package de.uniks.stp24.utils;

import javax.inject.Inject;
import javax.inject.Singleton;
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
@Singleton
public class ResponseConstants {

    @Inject
    public ResponseConstants() {}


    final public Map<Integer, String> respLogin = Map.of(400,"pirate.general.invalid.password",
            401, "pirate.general.invalid.password",
            429, "pirate.general.rate.limit.reached",
            -1 , "pirate.login.no.username.or.password",
            201, "try.login");

    final public Map<Integer, String> respSignup = Map.of(400,"validation failed",
            409, "name already exits",
            -1 , "please put in name or/and password");

//    static final public Map<String, Map<Integer,String>> responses = Map.of("login", respLogin,
//    "signup", respSignup);


}
