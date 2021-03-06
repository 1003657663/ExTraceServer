package com.express.daoImpl;

import com.express.daoBase.BaseDao;
import com.express.model.CustomerEntity;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by violet on 2016/3/28.
 */
public class CustomerDao extends BaseDao<CustomerEntity, Integer> {

    public CustomerDao() {
        super(CustomerEntity.class);
    }

    //根据用户id拿到用户信息
    public CustomerEntity get(int id) {
        CustomerEntity customerEntity = super.get(id);
        return customerEntity;
    }

    //根据用户手机号拿到用户信息
    public List<CustomerEntity> getByTel(String tel) {
        return findBy("id", true, Restrictions.eq("telephone", tel));
    }

    //更新或者插入一条新的数据
    public void save(CustomerEntity customerEntity) {
        super.save(customerEntity);
    }

}
