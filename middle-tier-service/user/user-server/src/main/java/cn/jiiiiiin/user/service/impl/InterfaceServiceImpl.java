package cn.jiiiiiin.user.service.impl;

import cn.jiiiiiin.user.entity.Interface;
import cn.jiiiiiin.user.exception.UserServiceException;
import cn.jiiiiiin.user.mapper.InterfaceMapper;
import cn.jiiiiiin.user.service.IInterfaceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 系统接口表 服务实现类
 * </p>
 *
 * @author jiiiiiin
 * @since 2018-12-09
 */
@Service
@AllArgsConstructor
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, Interface> implements IInterfaceService {

    private final InterfaceMapper interfaceMapper;

    /**
     * `接口名称+接口类型`必须唯一
     *
     * @param entity
     * @return
     */
    @Override
    public boolean saveOrUpdate(Interface entity) {
        if (entity.getId() != null) {
            // 更新
            val currentRecord = this.getById(entity);
            if (currentRecord.equals(entity)) {
                // 没有修改渠道、名称、地址
                return super.updateById(entity);
            }
        }

        val res = this.getOne(new QueryWrapper<Interface>().eq(Interface.CHANNEL, entity.getChannel()).eq(Interface.URL, entity.getUrl()).eq(Interface.METHOD, entity.getMethod()));
        if (res == null) {
            return super.saveOrUpdate(entity);
        } else {
            throw new UserServiceException("待新增或更新的接口已经存在接口地址和类型相同的一条记录，请检查");
        }
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        interfaceMapper.deleteRelationRecords(idList);
        return super.removeByIds(idList);
    }
}
