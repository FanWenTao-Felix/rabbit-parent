package ink.ckx.test.esjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import ink.ckx.rabbit.task.annotation.ElasticJobConfig;
import org.springframework.stereotype.Component;

@Component
@ElasticJobConfig(
        name = "DemoJob",
        cron = "0/10 * * * * ?",
        description = "样例定时任务",
        overwrite = true,
        shardingTotalCount = 2
)
public class DemoJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.err.println("执行Demo job.");
    }
}