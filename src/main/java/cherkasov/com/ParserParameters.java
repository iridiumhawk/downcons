package cherkasov.com;

import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

//parsing command line parameters
public class ParserParameters {

    //default values
    private int numberOfThreads = 1;
    private int maxDownloadSpeed = 1000000; //bytes in second
    private String fileNameWithLinks = "links.txt";
    private String outputFolder = "download";
    private boolean debug = true;

    //command line keys
    private final String NUMBER_OF_THREADS_KEY = "-n";
    private final String MAX_DOWNLOAD_SPEED_KEY = "-l";
    private final String FILE_NAME_WITH_LINKS_KEY = "-f";
    private final String OUTPUT_FOLDER_KEY = "-o";
//    private final String DEBUG_KEY = "-d";

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

    public ParserParameters(String[] args) {
        parseArgs(args);
    }

    public void parseArgs(String[] args) {

        for (int i = 0; i < args.length; i++) {
            try {
                switch (args[i]) {
                    case NUMBER_OF_THREADS_KEY:
                        numberOfThreads = Integer.parseInt(args[i + 1]);
                        break;

                    case MAX_DOWNLOAD_SPEED_KEY:
                        maxDownloadSpeed = parseIntoNumber(args[i + 1]);
                        break;

                    case FILE_NAME_WITH_LINKS_KEY:
                        fileNameWithLinks = args[i + 1];
                        break;

                    case OUTPUT_FOLDER_KEY:
                        outputFolder = args[i + 1];
                        break;

                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.log(Level.WARNING, "Index of parameter out of array bounds");
            }
        }
        LOG.log(Level.INFO, "Parsing command line parameters done");
    }

    //todo check for wrong strings
    private int parseIntoNumber(String arg) {

        if (arg == null || arg.length() == 0 || arg.charAt(0) == '-') {
            return 0;
        }

        StringBuilder result = new StringBuilder();

        int scale = 1;

        for (int i = 0; i < arg.length(); i++) {

            char c = arg.charAt(i);

            if (c >= '0' && c <= '9') {
                result.append(c);
            }

            switch (c){
                case 'k':
                case 'K':  scale = 1024;
                break;
                case 'm':
                case 'M':  scale = 1024 * 1024;
                break;
                default:
            }
        }
        return Integer.parseInt(result.toString()) * scale;
    }
}


