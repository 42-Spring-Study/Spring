package me.staek.log;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogTestController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/log")
    public String log() {
        String name = "Spring";


        /**
         * 호출 대상이 이나더라도 string concat 발생함.
         */
        log.trace("trace:" +  name);

        /**
         * 그래서 이렇게 검사를 하는 로직을 함께 사용했었지만
         */
        if (log.isTraceEnabled()) {
            log.trace("trace:" +  name);
        }
        /**
         * 지금은 아래처럼 format을 지정하는 함수를 사용한다.
         */
        log.trace("trace: {}", name);
        log.debug("debug: {}", name);
        log.info("info: {}", name);
        log.warn("warn: {}", name);
        log.error("error: {}", name);
        return "ok";
    }

}
