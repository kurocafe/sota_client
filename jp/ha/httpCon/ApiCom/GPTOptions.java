package jp.ha.httpCon.ApiCom;

public class GPTOptions {
    private int max_tokens;
    private boolean stream;

    public final int getMax_tokens() {
        return max_tokens;
    }

    public final void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public final boolean isStream() {
        return stream;
    }

    public final void setStream(boolean stream) {
        this.stream = stream;
    }

}
