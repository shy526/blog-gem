package ccxh.top.service.impl;

import ccxh.top.blog.github.mapper.UserMapper;
import ccxh.top.blog.github.mapper.pojo.UserPojo;
import ccxh.top.config.Service;
import ccxh.top.pojo.Result;
import ccxh.top.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author admin
 */
@Service
public class UserServiceImpl implements UserService {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    UserMapper userMapper;
    @Override
    public Result findUserByid(Integer id){
        UserPojo userPojo = userMapper.selectByPrimaryKey(id);
        return Result.ok(userPojo);
    }
}
