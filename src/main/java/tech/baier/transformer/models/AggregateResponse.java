package tech.baier.transformer.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder()
public class AggregateResponse {

    Long timestamp;
    List<BalanceChange> balanceChanges;
}
