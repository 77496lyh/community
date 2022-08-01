package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {


    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void text(){
        mailClient.sendMail("2911664304@qq.com","TEST","hello");
    }

    @Test
    public void textEmail(){
        Context context = new Context();
        context.setVariable("username","sunday");

        String process = templateEngine.process("/mail/activation", context);
        System.out.println(process);

        mailClient.sendMail("2911664304@qq.com","牛客",process);
    }
}
