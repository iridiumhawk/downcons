package cherkasov.com;

//task with url and file
class TaskEntity {
    private final String url;
    private final String fileName;

    public TaskEntity(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    //for unit testing
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        if (!getUrl().equals(that.getUrl())) return false;
        return getFileName().equals(that.getFileName());
    }
}
