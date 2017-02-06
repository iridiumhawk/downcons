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


        //create thread pool
//        LOG.log(Level.INFO, "thread pool start");


        //start download
//        LOG.log(Level.INFO, "download start");

//        LOG.log(Level.INFO, "download done");
    }

}
