package cherkasov.com;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static cherkasov.com.Main.LOG;

//parsing input parameters
public class ParserParameters {

    //default values
    private int numberOfThreads = 1;
    private int maxDownloadSpeed = 1000000; //bytes in second
    private String fileNameWithLinks = "links.txt";
    private String outputFolder = "download";

    //command line keys
    private final String numberOfThreadsKey = "-n";
    private final String maxDownloadSpeedKey = "-l";
    private final String fileNameWithLinksKey = "-f";
    private final String outputFolderKey = "-o";

    private Map<String, String> parameters = new HashMap<>();

    {
        parameters.put(numberOfThreadsKey, null);
        parameters.put(maxDownloadSpeedKey, null);
        parameters.put(fileNameWithLinksKey, null);
        parameters.put(outputFolderKey, null);
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

        parseParam();

        LOG.log(Level.INFO, "parsing args was done");

    }

    private void parseParam() {
        if (parameters.get(fileNameWithLinksKey) != null) {
            this.fileNameWithLinks = parameters.get(fileNameWithLinksKey);
        } else {
            throw new RuntimeException("NO file with links exist!");
            // exit            System.exit(-1);
        }

        if (parameters.get(numberOfThreadsKey) != null) {
            this.numberOfThreads = Integer.parseInt(parameters.get(numberOfThreadsKey));
        }

        if (parameters.get(maxDownloadSpeedKey) != null) {
            this.maxDownloadSpeed = Integer.parseInt(parameters.get(maxDownloadSpeedKey));
        }


        if (parameters.get(outputFolderKey) != null) {
            this.outputFolder = parameters.get(outputFolderKey);
        }

        LOG.log(Level.INFO, "parsing args was done");
    }

    private void parseArgs(String[] args) {

        List<String> argsList = Arrays.asList(args);

        //todo change getting params
/*        for (String input: args             ) {
            switch (input){
                case "-r": break;
                default:
            }
        }*/
//        System.out.println(argsList);

//todo check array index outbound
        try {

//todo make simpler
            int index = argsList.indexOf(numberOfThreadsKey) + 1;
            if (checkParameterOnCorrect(argsList.get(index))) {
                parameters.put(numberOfThreadsKey, argsList.get(index));
            }
            index = argsList.indexOf(maxDownloadSpeedKey) + 1;
            if (checkParameterOnCorrect(argsList.get(index))) {
                parameters.put(maxDownloadSpeedKey, argsList.get(index));
            }
            index = argsList.indexOf(fileNameWithLinksKey) + 1;
            if (checkParameterOnCorrect(argsList.get(index))) {
                parameters.put(fileNameWithLinksKey, argsList.get(index));
            }
            index = argsList.indexOf(outputFolderKey) + 1;
            if (checkParameterOnCorrect(argsList.get(index))) {
                parameters.put(outputFolderKey, argsList.get(index));
            }

        } catch (ArrayIndexOutOfBoundsException e){
            LOG.log(Level.WARNING,"index out of bounds");
        }
    }

private boolean checkParameterOnCorrect(String param){
    //todo change to Enum
 boolean result = param.equals(numberOfThreadsKey) || param.equals(maxDownloadSpeedKey) || param.equals(fileNameWithLinksKey)
    || param.equals(outputFolderKey) || param.equals("") ;
    return !result;
}

}
