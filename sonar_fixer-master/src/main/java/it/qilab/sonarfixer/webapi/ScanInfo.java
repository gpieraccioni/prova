package it.qilab.sonarfixer.webapi;

public class ScanInfo {
    private String scanId;
    private String status;

    public ScanInfo(String scanId, String status) {
        this.scanId = scanId;
        this.status = status;
    }

    public String getScanId() {
        return scanId;
    }

    public String getStatus() {
        return status;
    }
}


