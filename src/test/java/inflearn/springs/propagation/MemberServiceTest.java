package inflearn.springs.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.rmi.UnexpectedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;


    /**
     * memberService        @Transactional:OFF
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON
     * */
    @Test
    void outerTxOff_success() {

        // GIVEN
        String username = "outerTxOff_success";

        // WHEN
        memberService.joinV1(username);

        // WHEN : 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }


    /**
     * memberService        @Transactional:OFF
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON EXCEPTION
     * */
    @Test
    void outerTxOff_fail() {

        // GIVEN
        String username = "로그예외_outerTxOff_fail";

        // WHEN
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
//        memberService.joinV1(username);

        // WHEN : 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());

    }


    /**
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:OFF
     * logRepository        @Transactional:OFF
     * */
    @Test
    void single_TX() {
        // GIVEN
        String username = "single_TX";

        // WHEN
        memberService.joinV1(username);

        // WHEN : 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }


    /**
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON
     * */
    @Test
    void outerTxOn_success() {
        // GIVEN
        String username = "outerTxOn_success";

        // WHEN
        memberService.joinV1(username);

        // WHEN : 모든 데이터가 정상 저장된다
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }


    /**
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON EXCEPTION
     * */
    @Test
    void outerTxOn_fail() {
        // GIVEN
        String username = "로그예외outerTxOn_success";

        // WHEN
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);
//        memberService.joinV1(username);

        // WHEN : 모든 데이터가 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }


    /**
     * memberService        @Transactional:ON
     * memberRepository     @Transactional:ON
     * logRepository        @Transactional:ON EXCEPTION
     * */
    @Test
    void recoverException_fail() {
        // GIVEN
        String username = "로그예외_recoverException_fail";

        // WHEN
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedException.class);

        // WHEN : 모든 데이터가 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
}