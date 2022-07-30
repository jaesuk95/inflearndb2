package inflearn.springs.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("aop clas={}", basicService.getClass());
        assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    /**
     * txTest run 하면..
     * (Proxy BasicService 를 내부에서 생성하며 tx(), nonTx() 를 등록한다. 여기서 tx() 는 transaction 이 등록되어 있으며 Proxy
     * 에서 transaction 을 정상적으로 등록하여 BasicService 단으로 보내주고, 반면 NonTx 는 transaction 이 등록되어 있지 않아 그냥 패스)
     * */
    @Test
    void txTest(){
        basicService.tx();
        basicService.non_tx();
    }

    @TestConfiguration
    static class TxApplyBasciaConfig {
        @Bean
        BasicService basicService(){
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService{

        @Transactional
        public void tx(){
            log.info("call TX");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();   // TransactionSynchronizationManager = Transaction Manager
            log.info("tx active={}", txActive);
        }

        public void non_tx(){
            log.info("call non TX");
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}

/**
 * AOP
 * 내가 원하는 곳에 적용
 * AOP ex.
 * (Proxy Controller) -> controller
 * controller -> (Proxy MemberService) -> MemberService
 * MemberService -> (Proxy MemberRepository) -> MemberRepository
 *
 * AOP 적용 안했을 때
 * controller -> memberService -> memberRepository
 * */