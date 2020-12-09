package shiro.realm;

import bean.UmsMember;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import service.UmsMemberService;
import shiro.token.JWTToken;
import utils.JwtUtil;

/**
 * @Auther: ASUS
 * @Date: 2020/10/10 13:28
 * @Description:
 */
public class JWTRealm extends AuthorizingRealm {

    @Reference
    private UmsMemberService umsMemberService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        return null;
//        String tokenStr = principals.getPrimaryPrincipal().toString();
//        String username = JwtUtil.getUsername(tokenStr);
//        boolean verify = JwtUtil.verify(tokenStr, username);
//        if (!verify) {
//            return null;
//        }
//
//        String permission = umsMemberService.selectMemberPermission(username);
//
//        if (permission == null)
//            return null;
//
//        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//        simpleAuthorizationInfo.addStringPermission(permission);
//        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)throws AuthenticationException{

        String tokenStr = token.getPrincipal().toString();
        String username = JwtUtil.getUsername(token.getPrincipal().toString());
        boolean verify = JwtUtil.verify(token.getPrincipal().toString(), JwtUtil.getUsername(token.getPrincipal().toString()));
        if (!verify) {
            throw  new AuthenticationException("无效的token");
        }
        UmsMember umsMember = umsMemberService.selectByUsername(username);
        if (umsMember == null) {
            throw new  UnknownAccountException("无效的用户名");
        }
        return new SimpleAuthenticationInfo(umsMember, tokenStr, this.getName());

    }
}
