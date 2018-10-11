package cherkasov.com;

import cherkasov.com.exceptions.IncorrectInputParameters;
import org.apache.commons.cli.*;

import java.util.logging.Level;

import static cherkasov.com.ProjectLogger.LOG;

/**
 * Processing the command line parameters
 */
public class ParserParameters {

    private final String version = "version: 1.0";

    public ParserParameters() {

    }

    /**
     * Parses the arguments from command line into a parameters.
     * @return      parameters
     * @param args
     */
    public Parameters parseArgs(String[] args) throws IncorrectInputParameters {
        // create Options object
        Options options = new Options();

        options.addOption("h", "help", false, "print help message");
        options.addOption("v", "version", false, "print the version information and exit");
        options.addOption("d", "debug", false, "output debugging information while working");

        options.addOption(Option.builder("n")
                .desc("number of active threads")
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
        String helpMessage = "utility.jar options";

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
            System.out.println(version);
            return null;
        }

        int numberOfThreads;
        long maxDownloadSpeed;
        String fileNameWithLinks;
        String outputFolder;

        //fills parameters if exist or prints the help otherwise
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
            LOG.log(Level.INFO, "Parsing of command line parameters was done");

            return new Parameters(numberOfThreads, maxDownloadSpeed, fileNameWithLinks, outputFolder, debug);
        } else {
            return null;
        }
    }

    /**
     * Processing the string into a number except negative numbers.
     * Recognize suffix k or K as a factor 1024 (kilobyte) and m or M as a factor 1024*1024 (megabyte)
     * @param arg   the string for processing
     * @return      a number correspondent to arg or 0L if the string does not contain a
     *             parsable number or it is negative
     */
    private long parseIntoNumber(String arg) throws IncorrectInputParameters {

        if (arg == null || arg.length() == 0 || arg.charAt(0) == '-') {
            return 0L;
        }

        StringBuilder resultString = new StringBuilder();

        int scale = 1;

        for (int i = 0; i < arg.length(); i++) {

            char c = arg.charAt(i);

            if (c >= '0' && c <= '9') {
                resultString.append(c);
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
            }
        }

        long resultInLong = 0;

        try {
            resultInLong = Long.parseLong(resultString.toString()) * scale;
        } catch (NumberFormatException e) {
            throw new IncorrectInputParameters("Parsing command line parameters into the number was fail.", e);
        }

        return resultInLong;
    }
}


