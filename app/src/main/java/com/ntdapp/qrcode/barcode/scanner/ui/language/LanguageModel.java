package com.ntdapp.qrcode.barcode.scanner.ui.language;

public class LanguageModel {
    String languageName;
    String isoLanguage;
    boolean isCheck;
    int image;

    public LanguageModel(){

    }

    public LanguageModel(String languageName, String isoLanguage, boolean isCheck) {
        this.languageName = languageName;
        this.isoLanguage = isoLanguage;
        this.isCheck = isCheck;
    }

    public LanguageModel(String languageName, String isoLanguage, boolean isCheck, int image) {
        this.languageName = languageName;
        this.isoLanguage = isoLanguage;
        this.isCheck = isCheck;
        this.image = image;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getIsoLanguage() {
        return isoLanguage;
    }

    public void setIsoLanguage(String isoLanguage) {
        this.isoLanguage = isoLanguage;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
