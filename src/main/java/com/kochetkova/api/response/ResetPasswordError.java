package com.kochetkova.api.response;

public class ResetPasswordError {
    private boolean result;
    private Error errors;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public Error getErrors() {
        return errors;
    }

    public void setErrors(Error errors) {
        this.errors = errors;
    }

    private class Error {
        private String code;
        private String password;
        private String captcha;


        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }
    }
}
