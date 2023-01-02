package org.mickey.billing.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PayStatisticsDto {

    private Integer year;
    private Integer month;
    private Integer day;

    private Double money;
}
