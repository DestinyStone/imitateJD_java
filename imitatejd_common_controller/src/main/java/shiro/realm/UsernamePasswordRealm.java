package shiro.realm;


import bean.UmsMember;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import service.UmsMemberService;

/**
 * @Auther: DestinyStone
 * @Date: 2020/10/9 14:16
 * @Description: 针对UsernamePasswordToken校验的Realm
 */

public class UsernamePasswordRealm extends AuthorizingRealm {

    @Reference
    private UmsMemberService umsMemberService;


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String permission = umsMemberService.selectMemberPermission(((UmsMember)principals.getPrimaryPrincipal()).getPermissionId());
        if (permission == null)
            return null;
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermission(permission);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        final String username = token.getPrincipal().toString();
        UmsMember umsMemberResult = umsMemberService.selectByUsername(username);
        if (umsMemberResult == null) {
            throw new UnknownAccountException("未知的用户");
        }
        return new SimpleAuthenticationInfo(umsMemberResult, umsMemberResult.getPassword(), this.getName());
    }
}
