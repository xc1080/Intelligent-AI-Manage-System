package cn.aitenry.iims.archive.service.impl;

import cn.aitenry.iims.common.model.entity.integral.User;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.aitenry.iims.archive.enums.ArchivalLevelEnum;
import cn.aitenry.iims.archive.enums.ArchiveDeadlineEnum;
import cn.aitenry.iims.archive.mapper.ArchiveHashMapper;
import cn.aitenry.iims.archive.mapper.ArchiveMetadataMapper;
import cn.aitenry.iims.archive.mapper.ArchiveTypeMapper;
import cn.aitenry.iims.archive.model.dto.AddArchiveMetadataDTO;
import cn.aitenry.iims.archive.model.dto.ArchiveMenuPageQueryDTO;
import cn.aitenry.iims.archive.model.dto.EditArchiveMetadataDTO;
import cn.aitenry.iims.archive.model.entity.ArchiveBaseMetadata;
import cn.aitenry.iims.archive.model.entity.ArchiveMapper;
import cn.aitenry.iims.archive.model.entity.ArchiveMetadata;
import cn.aitenry.iims.archive.model.entity.ArchiveType;
import cn.aitenry.iims.archive.model.vo.ArchiveBaseMetadataVO;
import cn.aitenry.iims.archive.model.vo.ArchiveMenuVO;
import cn.aitenry.iims.archive.model.vo.ArchiveMetadataVO;
import cn.aitenry.iims.archive.service.ArchiveBaseModuleService;
import cn.aitenry.iims.common.model.entity.base.BaseUserInfo;
import cn.aitenry.iims.common.model.entity.integral.Organization;
import cn.aitenry.iims.common.model.entity.status.DeletedStatus;
import cn.aitenry.iims.common.result.PageResult;
import cn.aitenry.iims.integral.service.UserService;
import cn.aitenry.iims.integral.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: 档案基础模块层
 **/
@Service
@Slf4j
public class ArchiveBaseModuleServiceImpl implements ArchiveBaseModuleService {

    private final ArchiveMetadataMapper archiveMetadataMapper;
    private final ArchiveHashMapper archiveHashMapper;
    private final ArchiveTypeMapper archiveTypeMapper;
    private final UserService userService;
    private final OrganizationService organizationService;

    public ArchiveBaseModuleServiceImpl(ArchiveMetadataMapper archiveMetadataMapper, ArchiveHashMapper archiveHashMapper, ArchiveTypeMapper archiveTypeMapper, UserService userService, OrganizationService organizationService) {
        this.archiveMetadataMapper = archiveMetadataMapper;
        this.archiveHashMapper = archiveHashMapper;
        this.archiveTypeMapper = archiveTypeMapper;
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @Override
    public List<ArchiveMenuVO> getTreeMenus() {

        //查询当前用户的id
        long userId = StpUtil.getLoginIdAsLong();
        log.info("用户ID({}), 请求档案树列表", userId);
        User user = userService.getById(userId);
        Long organization = user.getOrganization();
        Organization departmentByJobId = organizationService.getDepartmentByJobId(organization);
        Long departmentId = departmentByJobId.getId();
        ArchiveMapper mapper = archiveHashMapper.getMapperById(departmentId);
        List<Long> organizationIds = JSONArray.parseArray(mapper.getOrganizationIds(), Long.class);
        List<Long> archiveTypeIds = JSONArray.parseArray(mapper.getArchiveTypeIds(), Long.class);
        List<Organization> organizations = organizationService.getOrganizationByIds(organizationIds);
        List<ArchiveMenuVO> allMenus = new ArrayList<>();
        List<ArchiveType> archiveTypes = archiveTypeMapper.getArchiveTypesByIds(archiveTypeIds);
        organizations.forEach(o -> {
            allMenus.add(ArchiveMenuVO.builder().id(o.getId()).parentId(o.getParentId())
                    .label(o.getName()).labelCode(o.getCode()).typeLabel(o.getName())
                    .type(o.getType()).build());
            if (Objects.equals(o.getType(), 1)) {
                for (ArchiveType archiveType : archiveTypes) {
                    String id = String.format("%s%s%s", o.getParentId(),
                            o.getId(),archiveType.getId());
                    allMenus.add(ArchiveMenuVO.builder().id(Long.valueOf(id)).parentId(o.getId())
                            .label(archiveType.getName()).detailComponent(archiveType.getDetailComponent())
                            .operateComponent(archiveType.getOperateComponent()).typeLabel(archiveType.getName())
                            .labelCode(archiveType.getCode()).build());
                }
            }
        });

        Map<Long, ArchiveMenuVO> menuMap = new HashMap<>(); // 用于快速查找菜单项

        // 将所有菜单放入Map中，便于后续快速定位
        for (ArchiveMenuVO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
        }

        // 从所有菜单中筛选出根节点（父ID为0）
        List<ArchiveMenuVO> rootList = new ArrayList<>();
        for (ArchiveMenuVO menu : allMenus) {
            if (menu.getParentId().equals(0L)) {
                rootList.add(menu);
                // 递归构建子菜单
                findChild(menu, menuMap);
            }
        }
        return rootList;
    }

    @Override
    public PageResult pageQueryBaseMetadata(ArchiveMenuPageQueryDTO archiveMenuPageQueryDto) {
        int page = archiveMenuPageQueryDto.getPage();
        int pageSize = archiveMenuPageQueryDto.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<ArchiveBaseMetadata> archiveBaseMetadata = archiveMetadataMapper.pageQuery(archiveMenuPageQueryDto);
        List<ArchiveBaseMetadataVO> archiveBaseMetadataVOS = archiveBaseMetadata.stream().map(metadata -> ArchiveBaseMetadataVO.builder()
                .id(metadata.getId())
                .archivalLevel(ArchivalLevelEnum.fromKey(metadata.getArchivalLevel()).getInfo())
                .archivalDeadline(ArchiveDeadlineEnum.fromKey(metadata.getArchivalDeadline()).getInfo())
                .archivalYear(metadata.getArchivalYear())
                .archivalCode(metadata.getArchivalCode())
                .archivalTitle(metadata.getArchivalTitle())
                .archivalDate(metadata.getArchivalDate())
                .archivalResponsible(JSONObject.parseObject(metadata.getArchivalResponsible(), BaseUserInfo.class))
                .build()).toList();

        return new PageResult(archiveBaseMetadata.getTotal(), archiveBaseMetadataVOS);
    }

    @Override
    public ArchiveMetadataVO getArchiveMetadataById(Long id) {
        return archiveMetadataMapper.getArchiveMetadataById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editArchiveMetadata(EditArchiveMetadataDTO editArchiveMetadataDto) {
        ArchiveMetadata metadata = new ArchiveMetadata();
        BeanUtils.copyProperties(editArchiveMetadataDto, metadata);
        archiveMetadataMapper.update(metadata);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addArchiveMetadata(AddArchiveMetadataDTO addArchiveMetadataDto) {
        ArchiveMetadata metadata = new ArchiveMetadata();
        BeanUtils.copyProperties(addArchiveMetadataDto, metadata);
        metadata.setIsDeleted(false);
        archiveMetadataMapper.insert(metadata);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delArchiveMetadata(List<Long> ids) {
        if (!ids.isEmpty()) {
            DeletedStatus deletedStatus = DeletedStatus.builder()
                    .isDeleted(true).ids(ids).build();
            archiveMetadataMapper.updateArchiveDeleted(deletedStatus);
        }
    }


    /**
     * 递归查找子菜单
     * @param menu 当前菜单项
     * @param menuMap 所有菜单的Map映射，用于快速定位
     */
    private void findChild(ArchiveMenuVO menu, Map<Long, ArchiveMenuVO> menuMap) {
        List<ArchiveMenuVO> children = new ArrayList<>();
        for (ArchiveMenuVO child : menuMap.values()) {
            if (child.getParentId().equals(menu.getId())) {
                children.add(child);
                // 继续查找子菜单的子菜单
                findChild(child, menuMap);
            }
        }
        menu.setChildren(children);
    }

}
