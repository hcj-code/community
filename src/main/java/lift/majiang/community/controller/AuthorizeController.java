package lift.majiang.community.controller;

import lift.majiang.community.dto.AccessTokenDto;
import lift.majiang.community.dto.GithubUser;
import lift.majiang.community.mapper.UserMapper;
import lift.majiang.community.model.User;
import lift.majiang.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller //用于把当前类对象存入spring容器中
public class AuthorizeController {
    @Autowired //用于类对象的注入
    private GithubProvider githubProvider;
    @Value("${github.client.id}") //用于基本类型的注入
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String RedirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback") //("/callback")来自于  github.redirect.uri = http://localhost:8887/callback
    public String callback(@RequestParam(name = "code") String code, //此处的code是GitHub同意授权，返回的code
                           @RequestParam(name = "state")String state,
                            HttpServletRequest request,
                           HttpServletResponse response){ //此处的state来自于index中请求code处的代码

        AccessTokenDto accessTokenDto = new AccessTokenDto();

        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(RedirectUri);
        accessTokenDto.setState(state);

        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        System.out.println(accessToken);

        GithubUser githubuser = githubProvider.getUser(accessToken);
        System.out.println(githubuser.getId());
        if(githubuser != null){
            User user = new User();
            String token = UUID.randomUUID().toString(); //生成唯一识别码（和之前GitHub里获得的不一样）
            user.setToken(token);
            user.setName(githubuser.getName());
            user.setAccountId(String.valueOf(githubuser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
            response.addCookie(new Cookie("token",token));
            //登录成功，写cookie和session
            //request.getSession().setAttribute("user",githubuser);
            return "redirect:index"; //返回：重定位到index.html
            //return "redirect:/";
        }else{
            //登录失败，重新登录
            //return "redirect:index";
            return "redirect:/";
        }

        //return "index";
    }
}
