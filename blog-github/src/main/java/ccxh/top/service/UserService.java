package ccxh.top.service;

import ccxh.top.blog.github.mapper.pojo.UserPojo;
import ccxh.top.pojo.Result;

public interface UserService {
    Result findUserByid(Integer id);
}
