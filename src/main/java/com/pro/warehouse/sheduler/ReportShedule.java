package com.pro.warehouse.sheduler;

import com.pro.warehouse.Service.ExcelService;
import com.pro.warehouse.Service.LogService;
import com.pro.warehouse.Service.ReportService;
import com.pro.warehouse.constant.MailReceiver;
import com.pro.warehouse.constant.Operation;
import com.pro.warehouse.mail.MailService;
import com.pro.warehouse.pojo.StockHUB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.Access;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Component
public class ReportShedule {
    Logger logger = LoggerFactory.getLogger(ReportShedule.class.getName());

    @Autowired
    MailService mailService;
    @Autowired
    ReportService reportService;
    @Autowired
    ExcelService excelService;
    @Autowired
    LogService logService;
    /**
     * 统计每天的入库数，出库数，库存数
     */
    @Scheduled(cron = "0 00 20 * * ?" )
    public void sendMail(){
        try {
            logger.debug("发送邮件");
            List<StockHUB> stockHUBS = reportService.generateStoreReoport(new Date());
            if(stockHUBS==null||stockHUBS.size()==0){
                stockHUBS.add(new StockHUB());
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            String format = dateFormat.format(new Date());
            String filePath = "files"+File.separator+System.currentTimeMillis()+".xlsx";
            excelService.ExportEcel(stockHUBS,"库存报表["+format+"]","库存报表",filePath,StockHUB.class);
            String subject = format+"的库存报表";
            String content = "系统自动发送库存报表，"+" 报表生成日期："+format+",邮件为系统发送，请勿回复！";
            //mailService.sendAttachmentsMail(MailReceiver.receiver, "主题：带附件的邮件", content,filePath);
            mailService.sendAttachmentsMail(MailReceiver.receiver, "主题：带附件的邮件", content,filePath);
            logService.saveOpLog("系统",Operation.SEND_EMAIL_REPORT.getOperation(),"成功",content);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
