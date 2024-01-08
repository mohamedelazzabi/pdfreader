package com.elazzabimohamed.pdfreader;

import android.net.Uri;

public class PdfFile {
    String displayName;
    Uri fileUri;
    Long fileid;
    String fileSizeInMB;
    String fileDate;

    public PdfFile(String displayName, Uri fileUri,long fileid,String fileSizeInMB,String fileDate ) {
        this.displayName = displayName;
        this.fileUri = fileUri;
        this.fileid=fileid;
       this.fileSizeInMB =fileSizeInMB;
       this.fileDate=fileDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public Long getFileid() {
        return fileid;
    }

    public String getFileSizeInMB() {
        return fileSizeInMB;
    }

    public String getFileDate() {
        return fileDate;
    }
}
