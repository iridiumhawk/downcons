package cherkasov.com;

class DownloadEntity{
    private final String url;
    private final String fileName;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadEntity that = (DownloadEntity) o;

        if (!getUrl().equals(that.getUrl())) return false;
        return getFileName().equals(that.getFileName());
    }
/*
    @Override
    public int hashCode() {
        int result = getUrl().hashCode();
        result = 31 * result + getFileName().hashCode();
        return result;
    }*/
}
