package com.example.transfer.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by sergey on 13.10.2018.
 */
public class ErrorResponse {
    private List<String> errMessage;

    public ErrorResponse() {
    }

    public ErrorResponse(List<String> errMessage) {
        this.errMessage = errMessage;
    }

    public ErrorResponse(String errMessage) {
        this.errMessage = Collections.singletonList(errMessage);
    }

    public List<String> getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(List<String> errMessage) {
        this.errMessage = errMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorResponse that = (ErrorResponse) o;

        return errMessage != null ? errMessage.equals(that.errMessage) : that.errMessage == null;
    }

    @Override
    public int hashCode() {
        return errMessage != null ? errMessage.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errMessage=" + errMessage +
                '}';
    }
}
