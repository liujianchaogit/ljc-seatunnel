package com.ljc.seatunnel.datasource.client.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ThreadSafe {}
