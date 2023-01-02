package org.mickey.billing.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
public abstract class BasePay {

    private boolean isDeleted;
    private Long version;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    /**
     * 标记下是微信还是支付宝
     */
    private String flag;
}
