package co.elliptic.baier.transformer.utils;

import co.elliptic.baier.transformer.enums.LineType;
import co.elliptic.baier.transformer.models.BalanceChange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.sound.sampled.Line;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class BlockChainProcessor {

    private static final int TX_SENDER_ADDRESS = 1;
    private static final int TX_RECIPIENT_ADDRESS = 2;
    private static final int TX_VALUE = 4;

    private static final int SCALE = 16;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static Map<String, BalanceChange> addNewTransactions(Map<String, BalanceChange> balanceChanges, String[] transactionLine) {
        if (!transactionLine[TX_SENDER_ADDRESS].equals(transactionLine[TX_RECIPIENT_ADDRESS])) {
            BalanceChange sender = balanceChanges.get(transactionLine[TX_SENDER_ADDRESS]);
            BalanceChange recipient = balanceChanges.get(transactionLine[TX_RECIPIENT_ADDRESS]);

            if (sender != null) {
                sender.setUsdBalanceChange(sender.getUsdBalanceChange()
                        .subtract(new BigDecimal(transactionLine[TX_VALUE]).setScale(SCALE, ROUNDING_MODE)));
            } else {
                balanceChanges.put(transactionLine[TX_SENDER_ADDRESS], BalanceChange.builder()
                        .address(transactionLine[TX_SENDER_ADDRESS])
                        .usdBalanceChange(new BigDecimal(transactionLine[TX_VALUE]).setScale(SCALE, ROUNDING_MODE).negate())
                        .build());
            }

            if (recipient != null) {
                recipient.setUsdBalanceChange(recipient.getUsdBalanceChange()
                        .add(new BigDecimal(transactionLine[TX_VALUE]).setScale(SCALE, ROUNDING_MODE)));
            } else {
                balanceChanges.put(transactionLine[TX_RECIPIENT_ADDRESS], BalanceChange.builder()
                        .address(transactionLine[TX_RECIPIENT_ADDRESS])
                        .usdBalanceChange(new BigDecimal(transactionLine[TX_VALUE]).setScale(SCALE, ROUNDING_MODE))
                        .build());
            }
        }
        return balanceChanges;
    }

    public static List<BalanceChange> exchangeToUSD(Map<String, BalanceChange> balanceChanges, String rate) {
        return balanceChanges.values().stream()
                .map(balanceChange -> balanceChange.toBuilder()
                        .usdBalanceChange(balanceChange.getUsdBalanceChange()
                                .multiply(new BigDecimal(rate)).setScale(SCALE, ROUNDING_MODE))
                        .build())
                .collect(toList());
    }

    public static String[] getLineData(String line, LineType lineType) {
        String[] lineList = StringUtils.substringAfter(line, ":").split(",");
        if (lineList.length != lineType.getLineSize()) {
            throw new IllegalArgumentException("Line has incorrect number of data. Skipping line:" + line);
        }
        return lineList;
    }

    public static LineType getKey(String line) {
        try {
            return LineType.valueOf(StringUtils.substringBefore(line, ":"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown line action. Skipping line:" + line);
        }
    }
}
