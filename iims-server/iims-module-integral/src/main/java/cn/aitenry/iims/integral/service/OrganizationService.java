package cn.aitenry.iims.integral.service;

import cn.aitenry.iims.common.model.entity.integral.Organization;
import cn.aitenry.iims.integral.model.vo.organization.OrganizationMenuVO;
import cn.aitenry.iims.integral.model.vo.organization.OrganizationVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 23:23
 * @Version: v1.0.0
 * @Description: TODO
 **/
public interface OrganizationService {

    List<OrganizationMenuVO> getTreeMenus(Long companyId);

    List<OrganizationVO> getAllMenuList();

    Organization getDepartmentByJobId(Long id);

    List<Organization> getOrganizationByIds(List<Long> ids);

    List<Organization> getDepartmentsByJobIds(ArrayList<Long> ids);
}
