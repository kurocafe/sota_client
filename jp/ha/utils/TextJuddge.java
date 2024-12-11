package jp.ha.utils;

final public class TextJuddge {
    public static boolean isCallSota(String spResult) {
        return spResult.contains("そうか") || spResult.contains("そうた") || spResult.contains("そっか")
                || spResult.contains("壮太") || spResult.contains("そうだ") || spResult.contains("そた");
    }

    public static boolean isSayEnd(String spResult) {
        return spResult.contains("おわり") || spResult.contains("終わり") || spResult.contains("おわ");
    }

    public static boolean isGreeding(String spResult) {
        return spResult.contains("おはよう") || spResult.contains("こんにちは") || spResult.contains("こんばんは");
    }
}
