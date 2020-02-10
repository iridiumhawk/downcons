package cherkasov.com

import cherkasov.com.ProjectLogger.LOG
import cherkasov.com.exceptions.IncorrectInputParameters
import org.apache.commons.cli.*
import java.util.logging.Level

/**
 * Processing the command line parameters
 */
class ParserParameters {
    private val version = "version: 1.0"
    /**
     * Parses the arguments from command line into a parameters.
     *
     * @param args
     * @return parameters
     */
    @Throws(IncorrectInputParameters::class)
    fun parseArgs(args: Array<String?>?): Parameters { // create Options object
        val options = Options()
        options.addOption("h", "help", false, "print help message")
        options.addOption("v", "version", false, "print the version information and exit")
        options.addOption("d", "debug", false, "output debugging information while working")
        options.addOption(Option.builder("n").desc("number of active threads").hasArg().argName("threads").build())
        options.addOption(Option.builder("l").desc("max download speed in Megabyte/second").hasArg().argName("speed").build())
        options.addOption(Option.builder("f").desc("file name with http links").hasArg().argName("file").build())
        options.addOption(Option.builder("o").desc("output folder for saving downloaded files").hasArg().argName("folder").build())
        // create the parser
        val parser: CommandLineParser = DefaultParser()
        val cmd: CommandLine
        // automatically generate the help statement
        val formatter = HelpFormatter()
        val helpMessage = "utility.jar options"
        // parse the command line arguments
        cmd = try {
            parser.parse(options, args)
        } catch (e: ParseException) {
            LOG.log(Level.WARNING, "Parsing failed.  Reason: " + e.message)
            formatter.printHelp(helpMessage, options)
            throw IncorrectInputParameters()
        }
        if (cmd.hasOption("h")) {
            formatter.printHelp(helpMessage, options)
            return Parameters.EMPTY
        }
        if (cmd.hasOption("v")) {
            println(version)
            return Parameters.EMPTY
        }
        val numberOfThreads: Int
        val maxDownloadSpeed: Long
        val fileNameWithLinks: String?
        val outputFolder: String?
        //fills parameters if exist or prints the help otherwise
        if (cmd.hasOption("n") && cmd.hasOption("l") && cmd.hasOption("f") && cmd.hasOption("o")) {
            numberOfThreads = cmd.getOptionValue("n").toInt()
            maxDownloadSpeed = parseIntoNumber(cmd.getOptionValue("l"))
            fileNameWithLinks = cmd.getOptionValue("f")
            outputFolder = cmd.getOptionValue("o")
        } else {
            formatter.printHelp(helpMessage, options)
            throw IncorrectInputParameters("Not all required options present")
        }
        val debug = cmd.hasOption("d")
        return if (numberOfThreads > 0 && maxDownloadSpeed > 0 && fileNameWithLinks != null && outputFolder != null && fileNameWithLinks.trim { it <= ' ' } != "" && outputFolder.trim { it <= ' ' } != "") {
            LOG.log(Level.INFO, "Parsing of command line parameters was done")
            Parameters(numberOfThreads, maxDownloadSpeed, fileNameWithLinks, outputFolder, debug)
        } else {
            throw IncorrectInputParameters()
        }
    }

    /**
     * Processing the string into a number except negative numbers.
     * Recognize suffix k or K as a factor 1024 (kilobyte) and m or M as a factor 1024*1024 (megabyte)
     *
     * @param arg the string for processing
     * @return a number correspondent to arg or 0L if the string does not contain a
     * parsable number or it is negative
     */
    @Throws(IncorrectInputParameters::class)
    private fun parseIntoNumber(arg: String?): Long {
        if (arg == null || arg.length == 0 || arg[0] == '-') {
            return 0L
        }
        val resultString = StringBuilder()
        var scale = 1
        for (i in 0 until arg.length) {
            val c = arg[i]
            if (c >= '0' && c <= '9') {
                resultString.append(c)
            }
            when (c) {
                'k', 'K' -> scale = 1024
                'm', 'M' -> scale = 1024 * 1024
            }
        }
        var resultInLong: Long = 0
        resultInLong = try {
            resultString.toString().toLong() * scale
        } catch (e: NumberFormatException) {
            throw IncorrectInputParameters("Parsing command line parameters into the number was fail.", e)
        }
        return resultInLong
    }
}