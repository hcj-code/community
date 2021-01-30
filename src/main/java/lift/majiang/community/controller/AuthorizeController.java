package lift.majiang.community.controller;

import lift.majiang.community.dto.AccessTokenDto;
import lift.majiang.community.dto.GithubUser;
import lift.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String RedirectUri;
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code, //此处的code是GitHub同意授权，返回的code
                           @RequestParam(name = "state")String state){ //此处的state来自于index中请求code处的代码

        AccessTokenDto accessTokenDto = new AccessTokenDto();

        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(RedirectUri);
        accessTokenDto.setState(state);

        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        System.out.println(accessToken);
        GithubUser user = githubProvider.getUser(accessToken);
        System.out.println(user.getId());

        return "index";
    }
}
