package com.kochetkova.api.response;

public class NewUserError {
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
        private String email;
        private String name;
        private String password;
        private String captcha;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
