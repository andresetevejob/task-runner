package com.pipper.andreboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@ConfigurationProperties("piper.worker")
public class WorkerProperties extends HashMap<String, Object> {
}
