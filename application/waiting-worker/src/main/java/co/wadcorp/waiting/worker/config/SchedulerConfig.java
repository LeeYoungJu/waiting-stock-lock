package co.wadcorp.waiting.worker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

  private static final Integer THREAD_POOL_SIZE = 10;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(THREAD_POOL_SIZE);
    taskScheduler.setThreadNamePrefix("waiting-worker-task-");
    taskScheduler.initialize();

    taskRegistrar.setTaskScheduler(taskScheduler);
  }
}
