package cherkasov.com;

public class Manager {
    private String[] args;
    ParserParameters parserParameters;
    ParserLinks parserLinks;
    //queue
//    private static volatile AtomicLong downloadedBytes = new AtomicLong(0L);


    public Manager(String[] args) {

        this.args = args;

        //parsing parameters
        parserParameters = new ParserParameters(args);

        //parsing links file
        parserLinks = new ParserLinks(parserParameters.getFileNameWithLinks());

        //create concurrency queue for tasks, from which threads will take url for download

    }

    public void execute() {


        //create thread pool executor

//        LOG.log(Level.INFO, "thread pool start");

        //start threads


//        LOG.log(Level.INFO, "download start");

        //each thread download with max speed (curl, get bytes, count time, if speed exceed - sleep for timeslot)

        //on exit thread write downloaded bytes


//        LOG.log(Level.INFO, "download done");
    }

}
