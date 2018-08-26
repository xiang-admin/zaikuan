package com.pro.warehouse.mail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class EmailConfigTest {
    @Autowired
    MailService mailService;

    @Test
    public void sendAttachmentsMail() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        System.out.print(dateFormat.format(new Date()));

    }

}