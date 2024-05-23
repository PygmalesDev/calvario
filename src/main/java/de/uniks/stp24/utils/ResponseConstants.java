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
            404, "404",
            429, "pirate.general.rate.limit.reached",
            -1 , "pirate.login.no.username.or.password",
            201, "try.login");

    final public Map<Integer, String> respSignup = Map.of(400,"pirate.general.invalid.password",
            404, "404",
            409, "username.in.use.by.another.user",
            429,"pirate.general.rate.limit.reached",
            201, "try.login",
            -2, "pirate.register.passwords.dont.match",
            -1 , "please put in name or/and password");

    final public Map<Integer, String> respEditAcc = Map.of(400, "pwd.8characters",
            401, "validation.failed",
            403, "attempting.to.change.someone.else.user",
            404, "404",
            409, "username.in.use.by.another.user",
            429,"pirate.general.rate.limit.reached",
            -1, "put.in.username.password",
            -2, "enter.password");

    final public Map<Integer, String> respCreateGame = Map.of(400, "invalid.password",
            401, "validation.failed",
            403, "attempting.to.change.someone.else.user",
            404, "404",
            409, "gamename.already.exists",
            429,"pirate.general.rate.limit.reached",
            -1, "put.in.username.password",
            -2, "passwords.do.not.match");

    final public Map<Integer, String> respEditGame = Map.of(400, "invalid.password",
            401, "validation.failed",
            403, "attempting.to.change.someone.else.user",
            404, "404",
            409, "gamename.already.exists",
            429,"pirate.general.rate.limit.reached",
            -1, "put.in.username.password",
            -2, "passwords.do.not.match");

    final public Map<Integer, String> respDelGame = Map.of(400 ,"invalid.password",
    401, "validation.failed",
    403, "attempting.to.change.someone.else.game",
    404, "404",
    429, "rate.limit.reached");

    final public Map<Integer, String> respGetGame = Map.of(400 ,"invalid.password",
      401, "validation.failed",
      403, "attempting.to.change.someone.else.game",
      404, "404",
      429, "rate.limit.reached");


//    static final public Map<String, Map<Integer,String>> responses = Map.of("login", respLogin,
//    "signup", respSignup);


}
