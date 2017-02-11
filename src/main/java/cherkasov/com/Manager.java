package cherkasov.com;

public class Manager {
    private final String[] args;

    public Manager(String[] args) {
        this.args = args;
    }

    public long execute() {

        //parsing parameters
        ParserParameters parserParameters = new ParserParameters(args);

        //parsing links file
        ParserLinks parserLinks = new ParserLinks(parserParameters.getFileNameWithLinks());

        //create concurrency queue for tasks, from which threads will take url for download
        Downloader downloader = new Downloader(parserLinks.getQueueTasks(), parserParameters);

        downloader.start();

        return downloader.getDownloadedBytes().get();
    }

}
