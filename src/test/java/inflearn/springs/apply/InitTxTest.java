package inflearn.springs.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

/**
 * @PostConstruct 와 @Transactional 을 함께 사용하면 트랜잭션이 적용되지 않는다
 * 왜냐하면 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP 가 적용되기 때문이다. 따라서 초기화 시점에는 해당 메서드에서 트랜잭션을 획득할 수 있다.(순서가 꼬이는 것이다.)
 *
 * 트랜잭션 안에서 무언가를 수행해야 한다면 ApplicationReadyEvent 를 사용해서 @EventListener 를 사용해야 한다
 * 그리고 만약 일반적으로 초기화 해야한다면 @PostConstruct 만 사용하면 된다
 * */
@SpringBootTest
public class InitTxTest {

    @Autowired Hello hello;

    @Test
    void go(){
        // 초기화 코드는 스프링이 초기화 시점에 호출한다
    }

    @TestConfiguration
    static class InitTxTestConfig{
        @Bean
        Hello hello(){
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("hello init @PostConstruct tx Active ={}", isActive);
        }

        @EventListener(ApplicationReadyEvent.class) // 스프링 컨테이너가 다 떴다 (AOP, Bean, container 가 다 완성이 되었으면 그때 적용이 된다, after JVM is running)
        @Transactional
        public void initV2(){
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("hello init @PostConstruct tx Active ={}", isActive);
        }
    }
}
