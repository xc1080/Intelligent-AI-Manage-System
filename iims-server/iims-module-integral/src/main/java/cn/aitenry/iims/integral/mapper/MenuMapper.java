package cn.aitenry.iims.integral.mapper;

import com.github.pagehelper.Page;
import cn.aitenry.iims.common.annotation.AutoFill;
import cn.aitenry.iims.common.enums.OperationType;
import cn.aitenry.iims.integral.model.dto.menu.MenuPageQueryDTO;
import cn.aitenry.iims.common.model.entity.integral.Menu;
import cn.aitenry.iims.integral.model.vo.user.UserMenuVO;
import cn.aitenry.iims.integral.model.vo.menu.MenuVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2023/01/22 00:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface MenuMapper {
    /**
     * 新增标签
     * @param menu Menu
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Menu menu);

    /**
     * 编辑标签
     * @param menu Menu
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Menu menu);

    /**
     * 标签分页查询
     * @param menuPageQueryDTO MenuPageQueryDTO
     * @return Page<MenuVO>
     */
    Page<MenuVO> pageQuery(MenuPageQueryDTO menuPageQueryDTO);

    /**
     * 根据id删除菜单
     * @param id 菜单ID
     */
    @Delete("delete from iims_integral_menu where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id查询标签
     * @param id 菜单ID
     * @return Menu
     */
    @Select("select * from iims_integral_menu where id = #{id}")
    Menu getById(Long id);

    /**
     * 查询所有菜单
     * @return List<MenuVO>
     */
    List<MenuVO> all();

    /**
     * 查询所有菜单
     * @return List<UserMenuVo>
     */
    @Select("SELECT * FROM iims_integral_menu WHERE `status` = 1 order by parent_id, order_num;")
    List<UserMenuVO> allMenu();
}
