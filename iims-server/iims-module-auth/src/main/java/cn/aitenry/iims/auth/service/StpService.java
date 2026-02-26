package cn.aitenry.iims.auth.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.aitenry.iims.integral.mapper.UserMapper;
import cn.aitenry.iims.integral.model.vo.role.RoleVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Component
@Slf4j
public class StpService implements StpInterface {

    @Resource
    private UserMapper userMapper;

    /**
     * 返回一个用户所拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object userId, String loginType) {
        String uid = (String) userId;
        Set<String> set = userMapper.searchUserPermissions(uid);
        return new ArrayList<>(set);
    }

    /**
     * 返回一个用户所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        String userId = (String) loginId;
        List<RoleVO> roleVOS = userMapper.searchUserRoles(userId);
        return roleVOS.stream().map(RoleVO::getRoleName).collect(Collectors.toList());
    }
}