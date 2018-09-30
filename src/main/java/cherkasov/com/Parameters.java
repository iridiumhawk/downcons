package cherkasov.com;

/**
 * Stores the command line parameters
 */
public class Parameters {
    private final int numberOfThreads ;
    private final long maxDownloadSpeed ; //bytes in second
    private final String fileNameWithLinks;
    private final String outputFolder ;
    private boolean debug = false;

    public Parameters(int numberOfThreads, long maxDownloadSpeed, String fileNameWithLinks, String outputFolder, boolean debug) {

        //check and set default values if necessary
        // TODO: 22.06.2018 change defaults on exception
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

    public long getMaxDownloadSpeed() {
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
        result = 31 * result + Long.hashCode(getMaxDownloadSpeed());
        result = 31 * result + getFileNameWithLinks().hashCode();
        result = 31 * result + getOutputFolder().hashCode();
        result = 31 * result + (isDebug() ? 1 : 0);
        return result;
    }
}