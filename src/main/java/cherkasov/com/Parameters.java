package cherkasov.com;

public class Parameters {
    //default values
    private int numberOfThreads = 1;
    private int maxDownloadSpeed = 1000000; //bytes in second
    private String fileNameWithLinks = "links.txt";
    private String outputFolder = "download";
    private boolean debug = false;

    public Parameters(int numberOfThreads, int maxDownloadSpeed, String fileNameWithLinks, String outputFolder, boolean debug) {
        this.numberOfThreads = numberOfThreads > 0 ? numberOfThreads : this.numberOfThreads;
        this.maxDownloadSpeed = maxDownloadSpeed > 0 ? maxDownloadSpeed : this.maxDownloadSpeed;
        this.fileNameWithLinks = "".equals(fileNameWithLinks) ? this.fileNameWithLinks : fileNameWithLinks;
        this.outputFolder = "".equals(outputFolder) ? this.outputFolder : outputFolder;
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public int getMaxDownloadSpeed() {
        return maxDownloadSpeed;
    }

    public String getFileNameWithLinks() {
        return fileNameWithLinks;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameters that = (Parameters) o;

        if (getNumberOfThreads() != that.getNumberOfThreads()) return false;
        if (getMaxDownloadSpeed() != that.getMaxDownloadSpeed()) return false;
        if (isDebug() != that.isDebug()) return false;
        if (!getFileNameWithLinks().equals(that.getFileNameWithLinks())) return false;
        return getOutputFolder().equals(that.getOutputFolder());
    }

/*    @Override
    public int hashCode() {
        int result = getNumberOfThreads();
        result = 31 * result + getMaxDownloadSpeed();
        result = 31 * result + getFileNameWithLinks().hashCode();
        result = 31 * result + getOutputFolder().hashCode();
        result = 31 * result + (isDebug() ? 1 : 0);
        return result;
    }*/
}