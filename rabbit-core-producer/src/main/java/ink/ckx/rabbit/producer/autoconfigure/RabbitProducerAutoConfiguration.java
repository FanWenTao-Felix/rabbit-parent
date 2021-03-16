package ink.ckx.rabbit.producer.autoconfigure;

import ink.ckx.rabbit.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenkaixin
 * @date 2021/03/16
 */
@EnableElasticJob
@Configuration
@ComponentScan({"ink.ckx.rabbit.producer.*"})
public class RabbitProducerAutoConfiguration {
}