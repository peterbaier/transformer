package tech.baier.transformer.models;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder(toBuilder = true)
public class ContextData {

    Long timestamp;
    Map<String, BalanceChange> balanceChanges;
}
