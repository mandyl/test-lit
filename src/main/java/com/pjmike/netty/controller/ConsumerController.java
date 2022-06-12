package com.pjmike.netty.controller;

import com.pjmike.netty.client.NettyClient;
import com.pjmike.netty.protocol.protobuf.MessageBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author pjmike
 * @create 2018-10-24 16:47
 */
@RestController
public class ConsumerController {
//    @Autowired
//    private NettyClient nettyClient;
//
//    @GetMapping("/send")
//    public String send() {
//        MessageBase.Message message = new MessageBase.Message()
//                .toBuilder().setCmd(MessageBase.Message.CommandType.NORMAL)
//                .setContent("hello netty")
//                .setRequestId(UUID.randomUUID().toString()).build();
//        nettyClient.sendMsg(message);
//        return "send ok";
//    }
    @GetMapping("/sleep")
    public String getTest(@RequestParam Long sleep) throws InterruptedException {
        System.out.println(sleep);
        String data = "sleep method over " + sleep;
        Thread.sleep(sleep);
        return data;
    }

    @GetMapping("/sleepdesc")
    public String getTestSleepDesc(@RequestParam Long cpuCnt,
        @RequestParam Long loopCount, @RequestParam Long mainsleep) throws InterruptedException {

        for (int i = 0; i<cpuCnt; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int x = 0; x < loopCount; x ++) {
                    }
                }
            }).start();
            String data = "getTestSleepDesc method over " + i;
            System.out.println(data);
        }
        Thread.sleep(mainsleep);
        return "ok";
    }

    @PostMapping("/cpubusy")
    public String postTest(@RequestParam Integer cpubusy) {
        for (int i = 0; i<cpubusy; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int x = 0;
                    for (;;) {
                        x++;
                    }
                }
            }).start();
            String data = "cpubusy method over " + i;
            System.out.println(data);
        }
        return "start....";
    }

    @PostMapping("/ctest")
    public String postTest(@RequestBody String body) {
        String data = "POST method over";
        System.out.println(body);
        return data;
    }
}
