package com.demo.configuration;

import com.demo.service.SchemaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.*;

/**
 * Created by ajinkya on 12/9/16.
 */
@Component
public class SchemaConfiguration
        implements ApplicationListener {
    Jedis jedisConnection = new Jedis("localhost");

    private Log log = LogFactory.getLog(SchemaConfiguration.class);

    @Autowired
    SchemaService _schemaService;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("Checking for schemas in the database");
        String schema__person = jedisConnection.get("SCHEMA__person");
        if (StringUtils.isBlank(schema__person)) {
            log.info("Creating Person Schema");
            String schemaAsString = getSchemaAsString("person_schema.json");
            String result = _schemaService.addNewSchema(schemaAsString);
            log.info("Result: " + result);
        }
        String schema__benefit = jedisConnection.get("SCHEMA__benefit");
        if (StringUtils.isBlank(schema__benefit)) {
            log.info("Creating Plan Schema");
            String schemaAsString = getSchemaAsString("plan_schema.json");
            String result = _schemaService.addNewSchema(schemaAsString);
            log.info("Result: " + result);
        }
        log.info("Application started. Schema check finished");
    }

    private String getSchemaAsString(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
