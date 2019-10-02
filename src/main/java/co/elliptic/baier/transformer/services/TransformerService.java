package co.elliptic.baier.transformer.services;

import co.elliptic.baier.transformer.enums.LineType;
import co.elliptic.baier.transformer.models.AggregateResponse;
import co.elliptic.baier.transformer.models.ContextData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static co.elliptic.baier.transformer.utils.BlockChainProcessor.*;
import static co.elliptic.baier.transformer.utils.ResultFileWriter.*;

@Slf4j
@Service
public class TransformerService {

    private static File resultJson;
    private static final int TIMESTAMP = 2;
    private static final int EXCHANGE_RATE = 1;

    private static final BiFunction<ContextData, String[], ContextData> processTransaction = (context, lines) ->
            context.toBuilder()
                    .balanceChanges(addNewTransactions(context.getBalanceChanges(), lines))
                    .build();

    private static final BiFunction<ContextData, String[], ContextData> processBlock = (context, lines) ->
            context.toBuilder()
                    .timestamp(Long.parseLong(lines[TIMESTAMP]))
                    .build();

    private static final BiFunction<ContextData, String[], ContextData> processExchange = (context, lines) -> {
        writeBlockToFile(AggregateResponse.builder()
                .timestamp(context.getTimestamp())
                .balanceChanges(exchangeToUSD(context.getBalanceChanges(), lines[EXCHANGE_RATE]))
                .build(), resultJson);

        return ContextData.builder().balanceChanges(new HashMap<>()).build();
    };

    private static Map<LineType, BiFunction<ContextData, String[], ContextData>> functionsByKey = new HashMap<>();

    static {
        functionsByKey.put(LineType.tx, processTransaction);
        functionsByKey.put(LineType.bk, processBlock);
        functionsByKey.put(LineType.fx, processExchange);
    }

    public void process(String filePath) {
        if (!new File(filePath).exists()) {
            log.error("Could not find file at path:" + filePath);
            System.err.println("File does not exist. Please provide full path.");
            return;
        }

        log.info("Start processing...");
        resultJson = writeStartOfFile(StringUtils.substringAfterLast(filePath, "/").replace(".csv", ".json"));

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            ContextData context = ContextData.builder().balanceChanges(new HashMap<>()).build();

            while (br.ready()) {
                String line = br.readLine();
                try {
                    LineType functionKey = getKey(line);
                    context = functionsByKey.get(functionKey).apply(context, getLineData(line, functionKey));
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                }
            }

            writeEndOfFile(resultJson);
        } catch (IOException e) {
            log.error("Could not find file at path:" + filePath);
        }
    }
}
