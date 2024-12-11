package jp.ha.httpCon.ApiCom;

public class PostGenerateReq {
    protected String msg;
    protected GPTOptions options;

    public final String getMsg() {
        return msg;
    }

    public final void setMsg(String msg) {
        this.msg = msg;
    }

    public final GPTOptions getOptions() {
        return options;
    }

    public final void setOptions(GPTOptions options) {
        this.options = options;
    }
}
