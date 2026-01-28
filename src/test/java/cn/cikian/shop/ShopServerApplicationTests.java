package cn.cikian.shop;


import cn.cikian.shop.sys.entity.SysUser;
import cn.cikian.shop.sys.service.SysUserService;
import cn.cikian.shop.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class ShopServerApplicationTests {

    @Autowired
    private SysUserService userService;

    @Test
    public void test1() {
        SysUser cikian = userService.getByUsername("cikian");
        System.out.println(cikian);
    }

    @Test
    public void testCreateJwt() {
        String token = JwtUtil.createJWT("cikian");
        log.info("token: {}", token);
    }

    @Test
    public void testParseJwt() throws Exception {
        Claims claims = JwtUtil.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJiMjdkY2Y0YzU5MmE0NDE1YmQxOTI4NDk1YTc5N2YwZSIsInN1YiI6ImNpa2lhbiIsImlzcyI6ImNpa2lhbiIsImlhdCI6MTc2OTYxOTI2MSwiZXhwIjoxNzY5NjIyODYxfQ.ZXUd0_1ZIHNkTgi7G_Qy3Z-0GCx47HoSScGZeE2RL-A");
        System.out.println("*******************");
        System.out.println(claims);
    }

}
