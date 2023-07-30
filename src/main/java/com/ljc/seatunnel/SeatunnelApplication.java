package com.ljc.seatunnel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.ljc.seatunnel.dal.mapper")
@SpringBootApplication
public class SeatunnelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatunnelApplication.class, args);
    }

}
