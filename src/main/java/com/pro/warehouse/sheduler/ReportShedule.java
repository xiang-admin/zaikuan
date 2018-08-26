package com.pro.warehouse.sheduler;

import com.alibaba.fastjson.JSON;
import com.pro.warehouse.Service.ExcelService;
import com.pro.warehouse.Service.ReportService;
import com.pro.warehouse.constant.Operation;
import com.pro.warehouse.mail.MailService;
import com.pro.warehouse.pojo.DaliyCount;
import com.pro.warehouse.pojo.StockHUB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.Access;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Component
public class ReportShedule {

    @Autowired
    MailService mailService;
    @Autowired
    ReportService reportService;
    @Autowired
    ExcelService excelService;
    /**
     * 统计每天的入库数，出库数，库存数
     */
    @Scheduled(cron = "0 55 20 * * ?" )
    public void computeCount(){
        try {
            List<StockHUB> stockHUBS = reportService.generateStoreReoport(new Date());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String format = dateFormat.format(new Date());
            String content = "请点击下载报表："+"http://118.31.2.211:8880/report-makeStoreReport?endDate="+format+" 报表生成日期："+format;
            mailService.sendAttachmentsMail("1249505842@qq.com", "主题：带附件的邮件", content);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
