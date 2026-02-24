package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.model.entity.integral.Organization;
import cn.aitenry.iims.integral.model.vo.organization.OrganizationMenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 22:52
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface OrganizationMapper {

    @Select("SELECT * FROM iims_integral_organization WHERE type = 0 AND is_deleted = 0")
    List<OrganizationMenuVO> getCompanyList();

    @Select("SELECT * FROM iims_integral_organization WHERE is_deleted = 0")
    List<OrganizationMenuVO> getAllMenuList();

    List<OrganizationMenuVO> getCompanyOrgStructure(Long companyId);

    Organization getDepartmentByJobId(Long id);

    List<Organization> getOrganizationByIds(List<Long> ids);

    List<Organization> getDepartmentByJobIds(ArrayList<Long> ids);
}
