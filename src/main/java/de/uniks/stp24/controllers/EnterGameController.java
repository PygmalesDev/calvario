package de.uniks.stp24.controllers;

import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;

@Title("Enter Game")
@Controller
public class EnterGameController {

    @Inject
    public EnterGameController() {

    }

    public void cancel() {
        System.out.println("Canceled");
    }

    public void joinGame() {
        System.out.println("Joined");
    }
}
