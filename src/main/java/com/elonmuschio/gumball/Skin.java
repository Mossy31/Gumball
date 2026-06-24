package com.elonmuschio.gumball;

import java.util.concurrent.ThreadLocalRandom;

public enum Skin {
    TOP("f5132d454e3f092c8af1c5c24c5a537d9d91ee7250d33edd43d76dc19df330da"),
    GUMBALL_PURPLE("7502b66bc4ae7684d10f0898d31ac8239b3b4febd49ec45e73e6687cdbc1a4b8"),
    GUMBALL_BLUE("821ed98b8126cca8b251320ae775104e0ce2402ae5a86fbd224265b2aeac4eb6"),
    GUMBALL_GREEN("3299ec4d18a080034328b667efb95d7093d19b5be1ac91b7000df0f15eddf80a"),
    GUMBALL_YELLOW("bdd4cdceba98d4efd5816d31847edf35bc6fd1ce542635e30d99259edfc56090"),
    GUMBALL_ORANGE("7b767e38a5f1d3c9c010431898192e1743ca3eedb65646549f39f4ce42d3d6df"),
    GUMBALL_RED("901504a878c8618d00415d54e5a0412ea769888ca4d287f0b945c67a8c6e591b"),
    GUMBALL_PINK("f274a8a5ca66afeb2c7f162253a6c461d56b8511427a55857882a1f77aac8");

    private final String skin;

    Skin(String skin) {
        this.skin = skin;
    }

    public String getSkin() {
        return skin;
    }

    public static Skin randomGumball() {
        Skin[] values = values();
        return values[ThreadLocalRandom.current().nextInt(1, values.length)];
    }
}
