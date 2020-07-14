package com.kochetkova.api.response;

public class AddedCommentError {
    private String result;
    private ErrorComment errors;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ErrorComment getErrors() {
        return errors;
    }

    public void setErrors(ErrorComment errors) {
        this.errors = errors;
    }

    private class ErrorComment {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
