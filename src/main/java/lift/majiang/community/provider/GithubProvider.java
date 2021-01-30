package lift.majiang.community.provider;

import com.alibaba.fastjson.JSON;
import lift.majiang.community.dto.AccessTokenDto;
import lift.majiang.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    /**
     * 根据OkHttp 的post请求代码改写的(像GitHub请求获取token)
     * @param accessTokenDto
     * @return
     */
    public String getAccessToken(AccessTokenDto accessTokenDto){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDto));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string =  response.body().string();
            String token = string.split("&")[0].split("=")[1];
            System.out.println(string);
            System.out.println(token);
            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据OkHttp 的post请求代码改写的（向GitHub请求获取用户信息）
     * @param accessToken
     * @return
     */
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //.url("https://api.github.com/user?access_token="+accessToken) 这种方式已经不支持了
                //需要改为.url("https://api.github.com/user") 和 .header("Authorization","token "+accessToken)
                .url("https://api.github.com/user")
                .header("Authorization","token "+accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string =  response.body().string();
            System.out.println(string);
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            //System.out.println(githubUser.getId());
            //System.out.println(githubUser.getLogin());

            return githubUser;
        } catch (IOException e) {
        }
        return null;
    }
}
