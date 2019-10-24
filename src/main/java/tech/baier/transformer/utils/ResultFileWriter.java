package tech.baier.transformer.utils;

import tech.baier.transformer.models.AggregateResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ResultFileWriter {

    public static File writeStartOfFile(String fileName) {
        File resultJson = new File(fileName);
        if (resultJson.exists()) {
            resultJson = new File(fileName
                    .replace(".json", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".json"));
        }

        try (FileWriter fr = new FileWriter(resultJson, true);
             BufferedWriter br = new BufferedWriter(fr)) {

            br.write("[");

        } catch (IOException e) {
            log.error("Beginning of file could not be written: " + resultJson.getAbsolutePath());
        }

        return resultJson;
    }

    public static void writeBlockToFile(AggregateResponse block, File resultJson) {
        try (FileWriter fr = new FileWriter(resultJson, true);
             BufferedWriter br = new BufferedWriter(fr)) {

            br.newLine();
            // if we need to stick with underscore_case on the result,
            // we can use Jackson FasterXML library with decorators
            Gson gson = new Gson();
            br.write(gson.toJson(block));
            br.write(",");

        } catch (IOException e) {
            log.error("Block could not be written to file: " + resultJson.getAbsolutePath(), e);
        }
    }

    public static void writeEndOfFile(File resultJson) {
        try (FileWriter fr = new FileWriter(resultJson, true);
             BufferedWriter bwriter = new BufferedWriter(fr)) {

            bwriter.newLine();
            bwriter.write("]");
            log.info("Stopped processing...");
            System.out.println("Your result can be found here: " + resultJson.getAbsoluteFile());
        } catch (IOException e) {
            log.error("End of file could not be written: " + resultJson.getAbsolutePath());
        }
    }
}
