package org.mickey.billing.service;

import org.mickey.billing.po.WechatPay;
import org.mickey.billing.vo.PayStatisticsDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IWechatPayService {

    List<WechatPay> selectList();

    /**
     * import the WeChat payment csv file to DB.
     * @param inputStream the WeChat payment csv file input stream
     * @return imported to DB result.
     */
    int importPayment(InputStream inputStream) throws IOException;

    List<PayStatisticsDto> statisticsByDay();

    List<PayStatisticsDto> statisticsByMonth();

    List<PayStatisticsDto> statisticsByYear();

}
