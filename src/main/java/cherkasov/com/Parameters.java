package cherkasov.com;

public class Parameters {
    private int numberOfThreads ;
    private int maxDownloadSpeed ; //bytes in second
    private String fileNameWithLinks;
    private String outputFolder ;
    private boolean debug = false;

    public Parameters(int numberOfThreads, int maxDownloadSpeed, String fileNameWithLinks, String outputFolder, boolean debug) {

        //check and set default values if necessary
        this.numberOfThreads = numberOfThreads > 0 ? numberOfThreads : 1;
        this.maxDownloadSpeed = maxDownloadSpeed > 0 ? maxDownloadSpeed : 1000000;
        this.fileNameWithLinks = (fileNameWithLinks == null || "".equals(fileNameWithLinks)) ? "links.txt" : fileNameWithLinks;
        this.outputFolder = (outputFolder == null || "".equals(outputFolder)) ? "download" : outputFolder;
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

    @Override
    public int hashCode() {
        int result = getNumberOfThreads();
        result = 31 * result + getMaxDownloadSpeed();
        result = 31 * result + getFileNameWithLinks().hashCode();
        result = 31 * result + getOutputFolder().hashCode();
        result = 31 * result + (isDebug() ? 1 : 0);
        return result;
    }
}