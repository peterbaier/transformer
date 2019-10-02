package co.elliptic.baier.transformer.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class ContextData {

    Long timestamp;
    Map<String, BalanceChange> balanceChanges;
}
