package ink.ckx.rabbit.task.autoconfigure;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import ink.ckx.rabbit.task.parser.ElasticJobConfParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverLists"}, matchIfMissing = false)
@EnableConfigurationProperties(JobZookeeperProperties.class)
public class JobParserAutoConfiguration {

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobZookeeperProperties) {

        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(jobZookeeperProperties.getServerLists(), jobZookeeperProperties.getNamespace());
        zkConfig.setBaseSleepTimeMilliseconds(jobZookeeperProperties.getBaseSleepTimeMilliseconds());
        zkConfig.setMaxSleepTimeMilliseconds(jobZookeeperProperties.getMaxSleepTimeMilliseconds());
        zkConfig.setConnectionTimeoutMilliseconds(jobZookeeperProperties.getConnectionTimeoutMilliseconds());
        zkConfig.setSessionTimeoutMilliseconds(jobZookeeperProperties.getSessionTimeoutMilliseconds());
        zkConfig.setMaxRetries(jobZookeeperProperties.getMaxRetries());
        zkConfig.setDigest(jobZookeeperProperties.getDigest());
        log.info("初始化 job 注册中心配置成功, zk address : {}, namespace : {}", jobZookeeperProperties.getServerLists(), jobZookeeperProperties.getNamespace());
        return new ZookeeperRegistryCenter(zkConfig);
    }

    @Bean
    public ElasticJobConfParser elasticJobConfParser(JobZookeeperProperties jobZookeeperProperties, ZookeeperRegistryCenter zookeeperRegistryCenter) {
        return new ElasticJobConfParser(jobZookeeperProperties, zookeeperRegistryCenter);
    }
}