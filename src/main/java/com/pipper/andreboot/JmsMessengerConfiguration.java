package com.pipper.andreboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.pipper.andreboot.core.DefaultWorker;
import com.pipper.andreboot.core.job.MutableJobTask;
import com.pipper.andreboot.core.messenger.JmsMessenger;
import com.pipper.andreboot.core.task.JobTask;
import com.pipper.andreboot.jms.JmsMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Configuration
@EnableConfigurationProperties(PiperProperties.class)
public class JmsMessengerConfiguration {
    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private DefaultWorker worker;

    @Autowired
    private Coordinator coordinator;

    private final ObjectMapper json = new ObjectMapper();

    @Bean
    JmsMessenger jmsMessenger () {
        return new JmsMessenger();
    }

    @Bean
    JmsMessageConverter jmsMessageConverter (PiperProperties piperProperties) {
        return new JmsMessageConverter(piperProperties.getSerialization().getDateformat());
    }

    @Bean
    JmsTemplate jmsTemplate (PiperProperties piperProperties) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setMessageConverter(jmsMessageConverter(piperProperties));
        return jmsTemplate;
    }

    @Bean
    DefaultMessageListenerContainer workerMessageListener () {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory (connectionFactory);
        container.setDestinationName("tasks");
        MessageListener listener = (m) -> worker.handle(toTask(m));
        container.setMessageListener(listener);
        return container;
    }

    @Bean
    DefaultMessageListenerContainer completionsMessageListener () throws JMSException {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory (connectionFactory);
        container.setDestinationName("completions");
        MessageListener listener = (m) -> coordinator.complete(toTask(m));
        container.setMessageListener(listener);
        return container;
    }

    private JobTask toTask (Message aMessage) {
        try {
            Map<String,Object> raw = aMessage.getBody(Map.class);
            Map<String, Object> task = new HashMap<>();
            for(Entry<String,Object> entry : raw.entrySet()) {
                if(entry.getValue().toString().startsWith("list:")) {
                    try {
                        task.put(entry.getKey(), json.readValue((String)entry.getValue().toString().substring(5),ArrayList.class));
                    } catch (IOException e) {
                        throw Throwables.propagate(e);
                    }
                }
                else {
                    task.put(entry.getKey(), entry.getValue());
                }
            }
            return new MutableJobTask(task);
        } catch (JMSException e) {
            throw Throwables.propagate(e);
        }
    }

}
