package org.mickey.billing.po;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class WechatPay extends BasePay {
    private Long id;
    /**
     * 交易时间
     */
    private Date date;
    /**
     * 交易类型
     */
    private String type;
    /**
     * 交易对方
     */
    private String target;
    /**
     * 商品
     */
    private String product;
    /**
     * 收/支
     */
    private String incomeOrOutlay;
    /**
     * 金额(元)
     */
    private Double money;
    /**
     * 支付方式
     */
    private String payWay;
    /**
     * 当前状态
     */
    private String status;
    /**
     * 交易单号
     */
    private String payNumber;
    /**
     * 商户单号
     */
    private String targetNumber;
    /**
     * 备注
     */
    private String remark;
    /**
     * 消费类型
     */
    private String module;

    @TableField(exist=false)
    private String dateString;
}
