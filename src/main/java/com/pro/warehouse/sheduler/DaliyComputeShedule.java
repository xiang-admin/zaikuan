package com.pro.warehouse.sheduler;

import com.alibaba.fastjson.JSON;
import com.pro.warehouse.Service.ApplyEnterService;
import com.pro.warehouse.Service.ApplyOutService;
import com.pro.warehouse.Service.EntrepotStatusService;
import com.pro.warehouse.Service.LogService;
import com.pro.warehouse.constant.Operation;
import com.pro.warehouse.dao.DaliyCountReposity;
import com.pro.warehouse.pojo.DaliyCount;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;


@Component
public class DaliyComputeShedule {
    public final static long SECOND = 1*1000;
    @Autowired
    private ApplyEnterService applyEnterService;
    @Autowired
    private ApplyOutService applyOutService;
    @Autowired
    private EntrepotStatusService entrepotStatusService;
    @Autowired
    private DaliyCountReposity daliyCountReposity;
    @Autowired
    private LogService logService;
    FastDateFormat fdf = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    /**
     * 统计每天的入库数，出库数，库存数
     */
    @Scheduled(cron = "0 55 23 * * ?" )
    public void computeCount(){
        System.out.println("定时任务demo1开始......");
        long begin = System.currentTimeMillis();
        int sizeOfIn = applyEnterService.getNumberOfTodayApplyEnter();
        int sizeOfOut = applyOutService.getNumberOfTodayApplyEnter();
        int sizeOfEntre = entrepotStatusService.getAllEntrepotCount();
        DaliyCount count = new DaliyCount();
        count.setSize(sizeOfIn);
        count.setType("入库数量");
        count.setComputeDate(new Date());
        daliyCountReposity.save(count);
        DaliyCount count1 = new DaliyCount();
        count1.setSize(sizeOfOut);
        count1.setType("出库数量");
        count1.setComputeDate(new Date());
        daliyCountReposity.save(count1);
        DaliyCount count2 = new DaliyCount();
        count2.setSize(sizeOfEntre);
        count2.setType("库存数量");
        count2.setComputeDate(new Date());
        daliyCountReposity.save(count2);

        System.out.println("数量分别未"+sizeOfEntre+" "+sizeOfIn+" " +sizeOfOut);
        long end = System.currentTimeMillis();
        System.out.println("定时任务demo1结束，共耗时：[" + (end-begin)+ "]毫秒");

        logService.saveOpLog("系统执行", Operation.AUTO_COMPUTE_NUMBER.getOperation(),"成功", JSON.toJSONString(new ArrayList<DaliyCount>(){{
            add(count);
            add(count1);
            add(count2);
        }}));
    }
}
