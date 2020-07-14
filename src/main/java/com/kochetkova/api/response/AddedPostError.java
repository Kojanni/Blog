package com.kochetkova.api.response;

public class AddedPostError {
    private boolean result;
    private ErrorPost errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorPost getErrors() {
        return errors;
    }

    public void setErrors(ErrorPost errors) {
        this.errors = errors;
    }

    private class ErrorPost {
        private String title;
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
