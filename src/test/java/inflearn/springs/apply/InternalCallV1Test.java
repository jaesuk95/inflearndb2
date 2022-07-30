package inflearn.springs.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 프록시 방식의 AOP 한계
 * @Transactional 를 사용하면 스프링은 Proxy (대리) 를 만들어서 AOP 를 적용한다.
 * 프록시를 사용하면 메서드 내부 호출에 프록시를 적용할 수 없다.
 *
 * 해결 방법은?
 * 1. 가장 단순한 방법은 내부 호출을 피하기 위해 'internal()' 메서드를 별도의 클래스로 분리하는 것이다
 * 2. 또 하나의 방법은 @Transactional 를 사용하지 않고 Manual 으로 자바 코드로 작성한다. eg. tx.start(), commit(); 근데 너무 어렵다
 * */
@Slf4j
@SpringBootTest
public class InternalCallV1Test {

    @Autowired CallService callService;

    @Test
    void printProxy(){
        log.info("callService class ={}",callService.getClass());
    }

    @Test
    void internalCall(){
        callService.internal();
    }

    @Test
    void externalCall(){
        callService.external();
    }

    @TestConfiguration
    static class InternalCallV1TestConfig{
        @Bean
        CallService callService(){
            return new CallService();
        }
    }

    @Slf4j
    static class CallService{

        public void external(){
            log.info("call external");
            printTxInfo();
            internal();     // <- internal transaction 이 안되는 이유는 자바 언어에서 메서드 앞에 별도의 참조가 없으면 'this' 라는 뜻으로 자기 자신의 인스턴스를 가리킨다.
        }

        @Transactional
        public void internal(){
            log.info("call internal");
            printTxInfo();
        }

        private void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
//            boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
//            log.info("tx readOnly={}",readOnly);
        }
    }
}
