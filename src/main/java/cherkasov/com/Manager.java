package cherkasov.com;

public class Manager {
    private String[] args;

    public Manager(String[] args) {
        this.args = args;
    }

    public void execute() {

        //parsing parameters
        ParserParameters parserParameters = new ParserParameters(args);

        //parsing links file
        ParserLinks parserLinks = new ParserLinks(parserParameters.getFileNameWithLinks());

        //create concurrency queue for tasks, from which threads will take url for download
        Downloader downloader = new Downloader(parserLinks.getQueueTasks(),parserParameters);
        downloader.start();

    }

}
