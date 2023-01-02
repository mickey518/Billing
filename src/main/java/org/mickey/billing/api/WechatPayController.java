package org.mickey.billing.api;

import org.mickey.billing.mapper.IWechatPayMapper;
import org.mickey.billing.po.WechatPay;
import org.mickey.billing.service.IWechatPayService;
import org.mickey.billing.vo.PayStatisticsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("pay/wechat")
public class WechatPayController {

    final static Logger log = LoggerFactory.getLogger(WechatPayController.class);

    @Resource
    private IWechatPayService wechatPayService;

    @GetMapping()
    public List<WechatPay> queryAll() {
        log.info("查询微信支付列表");
        return wechatPayService.selectList();
    }

    @PostMapping("import")
    public int importFile(@RequestParam MultipartFile file) {
        log.info("file.getName(): {}", file.getName());
        log.info("file.getOriginalFilename(): {}", file.getOriginalFilename());
        log.info("file.getContentType(): {}", file.getContentType());
        log.info("file.getSize(): {}", file.getSize());
        log.info("file.getResource(): {}", file.getResource());

        int result = 0;
        log.info("导入微信账单: {}", file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            result = wechatPayService.importPayment(inputStream);
        } catch (IOException ioException) {
            log.error(ioException.getMessage(), ioException);
        }
        log.info("导入完成; 共导入{}条记录", result);
        return result;
    }

    @GetMapping("total/day")
    public List<PayStatisticsDto> statisticsByDay() {
        return wechatPayService.statisticsByDay();
    }

    @GetMapping("total/month")
    public List<PayStatisticsDto> statisticsByMonth() {
        return wechatPayService.statisticsByMonth();
    }

    @GetMapping("total/year")
    public List<PayStatisticsDto> statisticsByYear() {
        return wechatPayService.statisticsByYear();
    }
}
