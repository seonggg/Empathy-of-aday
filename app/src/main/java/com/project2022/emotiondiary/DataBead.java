package com.project2022.emotiondiary;

public class DataBead {
    int image;
    String docid;

    public DataBead(int image, String docid){

        this.image=image;
        this.docid=docid;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
