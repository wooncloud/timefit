package timefit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 작업 실행 설정
 * Async 어노테이션이 붙은 메서드를 별도 스레드에서 실행하기 위한 설정입니다.
 * [목적]
 * 이메일 발송, 외부 API 호출 등 시간이 걸리는 작업을 요청 스레드와 분리하여 API 응답 속도를 개선합니다.
 * [중요 제약사항]
 * 1. @Async는 같은 클래스 내부에서 호출 시 작동하지 않음 (프록시 미적용)
 *    → 반드시 다른 빈에서 호출해야 함
 * 2. 비동기 메서드에서 발생한 예외는 호출자에게 전파되지 않음
 *    → 내부에서 try-catch로 처리하거나 로깅 필요
 * 3. 트랜잭션은 비동기 스레드로 전파되지 않음
 *    → 비동기 메서드 내부에서 새로운 트랜잭션 시작 필요
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 비동기 작업을 실행할 스레드 풀(Executor) 설정
     * [스레드 풀 동작 방식]
     * 1. 작업 요청이 들어오면 코어 스레드(CorePoolSize)에서 처리
     * 2. 모든 코어 스레드가 사용 중이면 큐(QueueCapacity)에 대기
     * 3. 큐가 가득 차면 최대 스레드(MaxPoolSize)까지 추가 생성
     * 4. 그래도 처리 못하면 RejectedExecutionException 발생
     * [튜닝]
     * - 이메일 발송만 사용: 현재 설정(2/5/100) 기본 스레드 , 최대 , 대기 큐 사이즈
     * - 외부 API 호출 추가할 경우, MaxPoolSize 10~20으로 증가 할것을 권장 받음.
     */
    @Override
    public Executor getAsyncExecutor() {
        // ThreadPoolTaskExecutor는 스레드 풀을 관리하며, 설정된 개수만큼의 스레드를 유지합니다.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 스레드 풀 크기 설정
        executor.setCorePoolSize(2);        // 기본 유지 스레드 수
        executor.setMaxPoolSize(5);         // 최대 스레드 수
        executor.setQueueCapacity(100);     // 대기 큐 크기

        // 스레드 이름 설정 (로그 추적용)
        executor.setThreadNamePrefix("async-email-"); // log에 스레드 식별용 으로 붙이는 prefix

        // 종료 정책
        executor.setWaitForTasksToCompleteOnShutdown(true);   // application 종료 시 실행 중인 작업이 완료될 때까지 대기
        executor.setAwaitTerminationSeconds(60);              // 최대 60초까지 대기 후 강제 종료

        executor.initialize();

        log.info("비동기 실행기 초기화 완료 - 코어 풀 크기: {}, 최대 풀 크기: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());

        return executor;
    }
}