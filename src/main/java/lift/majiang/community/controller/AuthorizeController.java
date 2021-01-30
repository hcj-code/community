package lift.majiang.community.controller;

import lift.majiang.community.dto.AccessTokenDto;
import lift.majiang.community.dto.GithubUser;
import lift.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state")String state){

        AccessTokenDto accessTokenDto = new AccessTokenDto();

        accessTokenDto.setClient_id("a95b25ecc06a910e03e6");
        accessTokenDto.setClient_secret("2adf4f55706953d2d2cf16000e0837fabcd37932");
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri("http://localhost:8887/callback");
        accessTokenDto.setState(state);

        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        System.out.println(accessToken);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getId());

        return "index";
    }
}
