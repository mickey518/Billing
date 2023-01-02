package org.mickey.billing.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mickey.billing.constant.PatternConstant;
import org.mickey.billing.mapper.IWechatPayMapper;
import org.mickey.billing.po.WechatPay;
import org.mickey.billing.service.IWechatPayService;
import org.mickey.billing.utils.GsonUtils;
import org.mickey.billing.vo.PayStatisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class WechatPayServiceImpl implements IWechatPayService {
    final static Logger log = LoggerFactory.getLogger(WechatPayServiceImpl.class);

    @Resource
    private IWechatPayMapper wechatPayMapper;

    @Override
    public List<WechatPay> selectList() {
        List<WechatPay> wechatPays = wechatPayMapper.selectList(null);
        wechatPays.sort(Comparator.comparing(WechatPay::getDate));
        return wechatPays;
    }

    @Override
    public int importPayment(InputStream inputStream) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(PatternConstant.YYYY_MM_DD_HH_MM);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        String tableHeader = "微信支付账单明细列表";
        List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);

        List<WechatPay> wechatPays = selectList();

        AtomicInteger result = new AtomicInteger();
        AtomicInteger index = new AtomicInteger();
        AtomicInteger tableHeaderCount = new AtomicInteger();
        lines.forEach(line -> {
            index.getAndIncrement();
            if (line.contains(tableHeader)) {
                tableHeaderCount.set(index.get() + 2);
            }
            if (index.get() >= tableHeaderCount.get() && tableHeaderCount.get() > 0) {
                String[] cells = line.split(",");
                try {
                    WechatPay build = WechatPay.builder().date(dateFormat.parse(cells[0])).type(cells[1]).target(cells[2]).product(cells[3].replace("\"", "").trim()).incomeOrOutlay(cells[4]).money(Double.parseDouble(cells[5].substring(1))).payWay(cells[6]).status(cells[7]).payNumber(cells[8].trim()).targetNumber(cells[9].replace("\t", "")).remark(cells[10].replace("\"", "").trim()).build();
                    // 订单号不存在的话，创建
                    if (wechatPays.stream().noneMatch(p -> build.getPayNumber().equals(p.getPayNumber()))) {
                        log.info("微信订单 [{}] 不存在，进行导入...", build.getPayNumber());
                        result.addAndGet(insert(build));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return result.get();
    }

    @Override
    public List<PayStatisticsDto> statisticsByDay() {
        List<WechatPay> wechatPays = selectList();

        wechatPays.forEach(wechatPay -> wechatPay.setDateString(DateFormatUtils.format(wechatPay.getDate(), PatternConstant.YYYY_MM_DD, TimeZone.getTimeZone("GMT+8"))));

        Map<String, Double> stringDoubleMap = wechatPays.stream().collect(Collectors.groupingBy(WechatPay::getDateString, Collectors.summingDouble(WechatPay::getMoney)));

        return stringDoubleMap.entrySet().stream().map(m -> {
            Date date;
            try {
                date = DateUtils.parseDate(m.getKey(), PatternConstant.YYYY_MM_DD);
                Calendar calendar = DateUtils.toCalendar(date);

                return PayStatisticsDto.builder().year(calendar.get(Calendar.YEAR)).month(calendar.get(Calendar.MONTH) + 1).day(calendar.get(Calendar.DAY_OF_MONTH)).money(m.getValue()).build();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }).sorted(Comparator.comparingInt(PayStatisticsDto::getYear)
                .thenComparingInt(PayStatisticsDto::getMonth)
                .thenComparingInt(PayStatisticsDto::getDay)
        ).toList();
    }

    @Override
    public List<PayStatisticsDto> statisticsByMonth() {
        List<WechatPay> wechatPays = selectList();

        wechatPays.forEach(wechatPay -> wechatPay.setDateString(DateFormatUtils.format(wechatPay.getDate(), PatternConstant.YYYY_MM, TimeZone.getTimeZone("GMT+8"))));

        Map<String, Double> stringDoubleMap = wechatPays.stream().collect(Collectors.groupingBy(WechatPay::getDateString, Collectors.summingDouble(WechatPay::getMoney)));

        return stringDoubleMap.entrySet().stream().map(m -> {
                    Date date;
                    try {
                        date = DateUtils.parseDate(m.getKey(), PatternConstant.YYYY_MM);
                        Calendar calendar = DateUtils.toCalendar(date);

                        return PayStatisticsDto.builder().year(calendar.get(Calendar.YEAR)).month(calendar.get(Calendar.MONTH) + 1).money(m.getValue()).build();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                }).sorted(Comparator.comparingInt(PayStatisticsDto::getYear).thenComparingInt(PayStatisticsDto::getMonth))
                .toList();
    }

    @Override
    public List<PayStatisticsDto> statisticsByYear() {
        List<WechatPay> wechatPays = selectList();

        wechatPays.forEach(wechatPay -> wechatPay.setDateString(DateFormatUtils.format(wechatPay.getDate(), PatternConstant.YYYY, TimeZone.getTimeZone("GMT+8"))));

        Map<String, Double> stringDoubleMap = wechatPays.stream().collect(Collectors.groupingBy(WechatPay::getDateString, Collectors.summingDouble(WechatPay::getMoney)));

        return stringDoubleMap.entrySet().stream().map(m -> {
                    Date date;
                    try {
                        date = DateUtils.parseDate(m.getKey(), PatternConstant.YYYY);
                        Calendar calendar = DateUtils.toCalendar(date);

                        return PayStatisticsDto.builder().year(calendar.get(Calendar.YEAR)).money(m.getValue()).build();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                }).sorted(Comparator.comparingInt(PayStatisticsDto::getYear))
                .toList();
    }

    private int insert(WechatPay wechatPay) {
        wechatPay.setFlag("WECHAT_PAY");
        wechatPay.setCreateBy("import");
        wechatPay.setCreateTime(new Date());
        wechatPay.setVersion(1L);
//        GsonUtils.print(wechatPay);
        return wechatPayMapper.insert(wechatPay);
//        return 0;
    }
}
