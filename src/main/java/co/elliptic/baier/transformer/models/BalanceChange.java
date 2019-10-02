package co.elliptic.baier.transformer.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class BalanceChange {

    String address;
    BigDecimal usdBalanceChange;
}
