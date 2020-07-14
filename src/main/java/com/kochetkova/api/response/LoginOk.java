package com.kochetkova.api.response;

public class LoginOk {
    private String result;
    private User user;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    private class User {
        private int id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean setting;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isModeration() {
            return moderation;
        }

        public void setModeration(boolean moderation) {
            this.moderation = moderation;
        }

        public int getModerationCount() {
            return moderationCount;
        }

        public void setModerationCount(int moderationCount) {
            this.moderationCount = moderationCount;
        }

        public boolean isSetting() {
            return setting;
        }

        public void setSetting(boolean setting) {
            this.setting = setting;
        }
    }
}
