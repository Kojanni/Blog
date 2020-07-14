package com.kochetkova.api.response;

public class EditProfileError {
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
        private String photo;
        private String name;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
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
    }
}
