package cherkasov.com;

import org.apache.commons.cli.*;

import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

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
        // create Options object
        Options options = new Options();

        options.addOption("h", "help", false, "print this message");
        options.addOption("v", "version", false, "print the version information and exit");
        options.addOption("d", "debug", false, "output debugging information while working");

        options.addOption(Option.builder("n")
                .desc("number of threads")
                .hasArg()
                .argName("threads")
                .build());


        options.addOption(Option.builder("l")
                .desc("max download speed in Megabyte/second")
                .hasArg()
                .argName("speed")
                .build());


        options.addOption(Option.builder("f")
                .desc("file name with http links")
                .hasArg()
                .argName("file")
                .build());


        options.addOption(Option.builder("o")
                .desc("output folder for saving downloaded files")
                .hasArg()
                .argName("folder")
                .build());


        // create the parser
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        String helpMessage = "downcons.jar -n threads -l speed -f file -o folder [-d -h -v]";

        // parse the command line arguments
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOG.log(Level.WARNING, "Parsing failed.  Reason: " + e.getMessage());
            formatter.printHelp(helpMessage, options);
            return null;
        }

        if (cmd.hasOption("h")) {
            formatter.printHelp(helpMessage, options);
            return null;
        }

        if (cmd.hasOption("v")) {
            System.out.println("version: 1.0");
            return null;
        }

        int numberOfThreads;
        int maxDownloadSpeed;
        String fileNameWithLinks;
        String outputFolder;

        if (cmd.hasOption("n") && cmd.hasOption("l") && cmd.hasOption("f") && cmd.hasOption("o")) {
            numberOfThreads = Integer.parseInt(cmd.getOptionValue("n"));
            maxDownloadSpeed = parseIntoNumber(cmd.getOptionValue("l"));
            fileNameWithLinks = cmd.getOptionValue("f");
            outputFolder = cmd.getOptionValue("o");
        } else {
            formatter.printHelp(helpMessage, options);
            return null;
        }

        boolean debug = cmd.hasOption("d");

        if (numberOfThreads > 0 && maxDownloadSpeed > 0
                && fileNameWithLinks != null && outputFolder != null
                && !fileNameWithLinks.trim().equals("") && !outputFolder.trim().equals("")) {
            LOG.log(Level.INFO, "Parsing of command line parameters done");

            return new Parameters(numberOfThreads, maxDownloadSpeed, fileNameWithLinks, outputFolder, debug);
        } else {
            return null;
        }
    }

    public Parameters parseArgsOld() {

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
                        fileNameWithLinks = checkArg(args[i + 1]);
                        break;

                    case OUTPUT_FOLDER_KEY:
                        outputFolder = checkArg(args[i + 1]);
                        break;

                    case DEBUG_KEY:
                        debug = Boolean.valueOf(args[i + 1]);
                        break;
                    default:
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.log(Level.INFO, "Index of parsing parameter out of array bounds");
            }
        }

        LOG.log(Level.INFO, "Parsing command line parameters done");

        return new Parameters(numberOfThreads, maxDownloadSpeed, fileNameWithLinks, outputFolder, debug);
    }

    private String checkArg(String arg) {
        if (arg == null || "".equals(arg) || arg.charAt(0) == '-') {
            return "";
        }
        return arg;
    }

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

        int resultInt = 0;

        try {
            resultInt = Integer.parseInt(result.toString()) * scale;
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Parsing into number fail. " + e.getMessage());
        }

        return resultInt;
    }
}


