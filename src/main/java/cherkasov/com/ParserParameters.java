package cherkasov.com;

import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

//parsing command line parameters
public class ParserParameters {
    private final String[] args;

    //command line keys
    private final String NUMBER_OF_THREADS_KEY = "-n";
    private final String MAX_DOWNLOAD_SPEED_KEY = "-l";
    private final String FILE_NAME_WITH_LINKS_KEY = "-f";
    private final String OUTPUT_FOLDER_KEY = "-o";
    private final String DEBUG_KEY = "-d";


    public ParserParameters(String[] args) {
        this.args = args;
    }

    public ParserParameters() {
        this.args = new String[0];
    }

    public Parameters parseArgs() {

        int numberOfThreads = 0;
        int maxDownloadSpeed = 0;
        String fileNameWithLinks = "";
        String outputFolder = "";
        boolean debug = false;

        for (int i = 0; i < args.length; i++) {
            try {
                switch (args[i]) {
                    case NUMBER_OF_THREADS_KEY:
                        numberOfThreads = parseIntoNumber(args[i + 1]);
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

                    case DEBUG_KEY:
                        debug = Boolean.valueOf(args[i + 1]);
                        break;
                    default:
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.log(Level.WARNING, "Index of parameter out of array bounds");
            }
        }

        LOG.log(Level.INFO, "Parsing command line parameters done");

        return new Parameters(numberOfThreads, maxDownloadSpeed, fileNameWithLinks, outputFolder, debug);
    }

    //todo check for wrong strings
    private int parseIntoNumber(String arg) {

        if (arg == null || arg.length() == 0 || arg.charAt(0) == '-') {
            return 0;
        }

        StringBuilder result = new StringBuilder("0");

        int scale = 1;

        for (int i = 0; i < arg.length(); i++) {

            char c = arg.charAt(i);

            if (c >= '0' && c <= '9') {
                result.append(c);
            }

            switch (c) {
                case 'k':
                case 'K':
                    scale = 1024;
                    break;
                case 'm':
                case 'M':
                    scale = 1024 * 1024;
                    break;
                default:
            }
        }
        return Integer.parseInt(result.toString()) * scale;
    }
}


