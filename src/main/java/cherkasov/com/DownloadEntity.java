package cherkasov.com;

class DownloadEntity{
    private String url;
    private String fileName;

    public DownloadEntity(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }
}
