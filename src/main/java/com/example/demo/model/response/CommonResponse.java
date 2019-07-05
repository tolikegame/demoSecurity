package com.example.demo.model.response;

public class CommonResponse{
    private int status;
    private String msg;
    private boolean result;

    public CommonResponse(){}

    public CommonResponse(boolean resul,int status,String msg){
        this.result=resul;
        this.status=status;
        this.msg=msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
