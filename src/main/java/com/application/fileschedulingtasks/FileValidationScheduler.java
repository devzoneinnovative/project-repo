package com.application.fileschedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FileValidationScheduler {


    private static Logger logger = LoggerFactory.getLogger(FileValidationScheduler.class);


    @Value("${remote_location}")
    private String remotePath;

    @Value("${user_profiles}")
    private String[] userProfiles;

    @Scheduled(fixedRate = 50000)
    public void fileExecutionSchedulerExecute() {
        //https://www.baeldung.com/java-read-line-at-number
        //https://stackoverflow.com/questions/686231/quickly-read-the-last-line-of-a-text-file
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        Path path = Paths.get(remotePath);

        // file exists and it is not a directory
        //!Files.isDirectory(path)
        List<Path> paths = getFileForProcessing(remotePath);
        if (Files.exists(path) && isFilenameValid(remotePath)) {
            readFile(paths.get(0));
            System.out.println("File exists!");
        }
        System.out.println("Java cron job expression:: " + strDate);
    }


    public boolean isFilenameValid(String file) {
        List<Path> paths = getFileForProcessing(remotePath);
        String fname = paths.get(0).getFileName().toString();
        String fnameDelimeted = fname.split("_", 2)[0];
        for (String user : userProfiles) {
            if (fnameDelimeted.equals(user)) {
                return true;
            }
        }
        return false;
    }


    public List<Path> getFileForProcessing(String remotePath) {
        List<Path> paths = new ArrayList<>();
        Path configFilePath = Paths.get(remotePath);


/*        List<Path> fileWithName = Files.walk(configFilePath)
                .filter(s -> s.toString().endsWith(".java"))
                .map(Path::getFileName).sorted().collect(Collectors.toList());*/

        List<Path> fileWithName = null;
        try {
            fileWithName = Files.walk(configFilePath)
                    .filter(s -> s.toString().endsWith(".txt"))
                    .sorted().collect(Collectors.toList());

            for (Path name : fileWithName) {
                paths.add(name);
                // printing the name of file in every sub folder
                System.out.println(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return paths;
    }


    public void readFile(Path filePathtest) {
        List<String> allLines = null;
        String sizeOfAFile = null;
        try {
            allLines = Files.readAllLines(filePathtest);
            sizeOfAFile = String.valueOf(allLines.size() - 3);
            String firstLine = allLines.stream().skip(0).findFirst().get();
            String secondLine = allLines.stream().skip(1).findFirst().get();
            String lastLine = allLines.stream().skip(allLines.size() - 1).findFirst().get();
            Pattern header = Pattern.compile("^HEADER");
            Pattern footer = Pattern.compile("^FOOTER");
            if (header.matcher(firstLine).find() && footer.matcher(lastLine).find() && lastLine.contains(sizeOfAFile)) {
                logger.info("FILE VALIDATION DONE SUCCESSFULLY !!! " + filePathtest.getFileName() + "  IS A VALID FILE !!! MOVE TO THE OTHER SERVICE");
            }
            System.out.println(firstLine);
            System.out.println(secondLine);
            System.out.println(lastLine);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

