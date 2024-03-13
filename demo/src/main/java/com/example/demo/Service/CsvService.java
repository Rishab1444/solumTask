package com.example.demo.Service;

import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.*;
import com.example.demo.Model.UserDetail;
import com.example.demo.Repository.UserDetailRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class CsvService {

    private static final int QUEUE_CAPACITY = 1000;
    private static final UserDetail SENTINEL = new UserDetail();
    private final Executor csvExecutor;

    @Value("${app.csv.folder}")
    private  String csvFolderPath;

    private UserDetailRepository userDetailRepository;
    @Autowired
    public CsvService(UserDetailRepository userDetailRepository,@Qualifier("csvExecutor") Executor csvExecutor){
       this.userDetailRepository = userDetailRepository;
       this.csvExecutor = csvExecutor;
   }

   @Async("csvExecutor")
    public CompletableFuture<Path> copyAndProcessCsvFile(Path sourceFilePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Ensure destination directory exists or create it
                Path destinationDirectory = Paths.get(csvFolderPath.trim());
                if (!Files.exists(destinationDirectory)) {
                    Files.createDirectories(destinationDirectory);
                }

                // Copy CSV file to destination directory
                Path destinationPath = destinationDirectory.resolve(sourceFilePath.getFileName());
                Files.copy(sourceFilePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Files copied to " + destinationPath);
                return destinationPath;



            } catch (IOException e) {
                throw new RuntimeException("Error copying or processing CSV file", e);
            }
        },csvExecutor);
    }
    @Async("csvExecutor")
    public void processCsvFile(Path filePath) {
        BlockingQueue<UserDetail> queue = new LinkedBlockingQueue<>();
        produce(queue, filePath);
        consume(queue);

    }

    @Async("csvExecutor")
    protected void produce(BlockingQueue<UserDetail> queue, Path filePath) {
        try (Reader reader = Files.newBufferedReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                UserDetail userDetail = createUserDetailFromRecord(csvRecord);
                queue.put(userDetail);
            }
            queue.put(SENTINEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Producer completed");
    }

    @Async("csvExecutor")
    protected void consume(BlockingQueue<UserDetail> queue) {
        System.out.println("Consumer Started");
        try {
            while (true) {
                UserDetail userDetail = queue.take();
                if (userDetail == SENTINEL) {
                    break;
                }
                saveUserDetail(userDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private UserDetail createUserDetailFromRecord(CSVRecord csvRecord) {
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(csvRecord.get("User Id"));
        userDetail.setFirstName(csvRecord.get("First Name"));
        userDetail.setLastName(csvRecord.get("Last Name"));
        userDetail.setEmail(csvRecord.get("Email"));
        userDetail.setPhone(csvRecord.get("Phone"));
        userDetail.setDateOfBirth(LocalDate.parse(csvRecord.get("Date of birth")));
        userDetail.setJobTitle(csvRecord.get("Job Title"));
        userDetail.setSex(csvRecord.get("Sex"));
        return userDetail;
    }

    @Transactional
    public void saveUserDetail(UserDetail userDetail) {
        userDetailRepository.save(userDetail);
    }

}


