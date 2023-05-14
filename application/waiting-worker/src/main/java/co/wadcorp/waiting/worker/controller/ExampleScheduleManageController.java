package co.wadcorp.waiting.worker.controller;

import co.wadcorp.waiting.worker.controller.dto.StopSchedulerRequest;
import co.wadcorp.waiting.worker.task.ScheduledTask;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ExampleScheduleManageController {

  private final ScheduledAnnotationBeanPostProcessor postProcessor;
  private final Map<String, ScheduledTask> scheduledTaskMap;

  /**
   * 스케줄러 종료 API
   */
  @PostMapping("/scheduler/stop")
  public void stopScheduler(@RequestBody StopSchedulerRequest request) {
    log.info("scheduler - stop");

    ScheduledTask exampleScheduledTask = scheduledTaskMap.get(request.getBean());
    postProcessor.postProcessBeforeDestruction(exampleScheduledTask, request.getBeanName());
  }

  /**
   * 스케줄러 시작 API
   */
  @PostMapping("/scheduler/start")
  public void startScheduler(@RequestBody StopSchedulerRequest request) {
    log.info("scheduler - start");

    ScheduledTask exampleScheduledTask = scheduledTaskMap.get(request.getBean());
    postProcessor.postProcessAfterInitialization(exampleScheduledTask, request.getBeanName());
  }
}
