package com.lsj.study.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lishangj
 */
@RestController
public class OauthClientDemoController {

    @RequestMapping(path = "/hello")
    public String demo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return "hello";
    }
}
