package cn.aitenry.iims.integral.mapper;

import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.common.model.entity.integral.Organization;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.integral.model.vo.organization.OrganizationMenuVO;
import org.apache.ibatis.annotations.Mapper;

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

    List<OrganizationMenuVO> getCompanyList();

    List<OrganizationMenuVO> getAllMenuList();

    List<OrganizationMenuVO> getCompanyOrgStructure(Long companyId);

    Organization getDepartmentByJobId(Long id);

    List<Organization> getOrganizationByIds(List<Long> ids);

    List<Organization> getDepartmentByJobIds(ArrayList<Long> ids);

    void insert(Organization organization);

    @AutoFill(value = OperationType.UPDATE)
    void update(Organization organization);

    @AutoFill(value = OperationType.UPDATE)
    Boolean updateDeleted(DeletedStatus deletedStatus);
}
