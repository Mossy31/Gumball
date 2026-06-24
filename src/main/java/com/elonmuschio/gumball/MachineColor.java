package com.elonmuschio.gumball;

public enum MachineColor {
    YELLOW("&e", "43a33161af89d43a9fbc57e9dc3252a2eee37c48e7c24c3c9f48dca9e0b203ea"),
    WHITE("&f", "2f5b9170cd5504f5111e50563fff50c02d05536249de8d1d2998eb8c5509f9de"),
    RED("&c", "30a19501fbbc5d9d7c10e5bf0b72eb52f8b2b38d562f58be2e111680aa239f76"),
    PURPLE("&5", "960a008b8cb254b6bb0f33036ebbd434a40741ebc2dc5d5aeb2ab463c8ee7515"),
    PINK("&d", "ca73d6404a4bce270d59564a317fbda62a2e93128c69d340e0fe16c287573cb8"),
    DARK_AQUA("&3", "7901a6846b4c2cc488fb94612a905a884ed0886a5ee5562fe7680081003ed7e4"),
    ORANGE("&6", "77ff60144e849a014354f25db0bc5fd04f8c35ecb02ead0941d3f0ce1f3b270f"),
    DARK_BLUE("&1", "76cb5c31eb9665341f50661915757bf39aba16b1e6b6750c9f6d5e912419c56b"),
    MAGENTA("&d", "182d622d593cdb57226ea7436dbed7ba781386777b88b0d28ff0a1ff4647ac20"),
    GREEN("&a", "56829d6966093772afe3b32c66de51cfbfe0aaff33a8e359beb91eb289b02b86"),
    DARK_GREEN("&2", "a39f22b03f9c6d448ee98f19e936df0db04d39bdb10af824b4b7428399ac97ac"),
    GRAY("&7", "adcd30971c1b78b5151f9ce2d520b57f8aa23b2156df9134486055838205f2b"),
    BROWN("&6", "5d22831ee1d84a6e49b6856a095be92991e1a635feb5904b7a2988f13311e292"),
    LIGHT_BLUE("&b", "148322b385608cf196ab47a7ca493958da95ef3f2e73954c3355ee9ffc022173"),
    BLACK("&0", "21c89f85fa93dad77a67bc9be39a48357ee0ea2b0418f5244efa8d552bcdb6e4"),
    AQUA("&b", "11393d480c87e376adaa9af8154d759e79076229cbfedde7cf1f9866a6d0cecf");

    private final String code;
    private final String skin;

    MachineColor(String code, String skin) {
        this.code = code;
        this.skin = skin;
    }

    public String getCode() {
        return code;
    }

    public String getSkin() {
        return skin;
    }

    public static MachineColor parse(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
