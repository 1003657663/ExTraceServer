package com.express.serviceImpl;

import com.express.daoImpl.*;
import com.express.info.ExpressInfo;
import com.express.info.PackageInfo;
import com.express.model.*;
import com.express.serviceInterface.IDomainService;
import org.hibernate.criterion.Restrictions;
import utils.Utils;

import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by violet on 2016/4/6.
 */
public class DomainService implements IDomainService {

    private OutletsDao outletsDao;
    private PackageHistoryDao packageHistoryDao;
    private RegionDao regionDao;
    private CityDao cityDao;
    private ProvinceDao provinceDao;
    private PackAndpackDao packAndpackDao;
    private ExpressAndPackageDao expressAndPackageDao;
    private PackageDao packageDao;
    private ExpressDao expressDao;
    private EmployeesDao employeesDao;
    private AddressDao addressDao;
    private CustomerDao customerDao;

    public OutletsDao getOutletsDao() {
        return outletsDao;
    }

    public void setOutletsDao(OutletsDao outletsDao) {
        this.outletsDao = outletsDao;
    }

    public PackageHistoryDao getPackageHistoryDao() {
        return packageHistoryDao;
    }

    public void setPackageHistoryDao(PackageHistoryDao packageHistoryDao) {
        this.packageHistoryDao = packageHistoryDao;
    }

    public RegionDao getRegionDao() {
        return regionDao;
    }

    public void setRegionDao(RegionDao regionDao) {
        this.regionDao = regionDao;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public void setCityDao(CityDao cityDao) {
        this.cityDao = cityDao;
    }

    public ProvinceDao getProvinceDao() {
        return provinceDao;
    }

    public void setProvinceDao(ProvinceDao provinceDao) {
        this.provinceDao = provinceDao;
    }

    public PackAndpackDao getPackAndpackDao() {
        return packAndpackDao;
    }

    public void setPackAndpackDao(PackAndpackDao packAndpackDao) {
        this.packAndpackDao = packAndpackDao;
    }

    public ExpressAndPackageDao getExpressAndPackageDao() {
        return expressAndPackageDao;
    }

    public void setExpressAndPackageDao(ExpressAndPackageDao expressAndPackageDao) {
        this.expressAndPackageDao = expressAndPackageDao;
    }

    public PackageDao getPackageDao() {
        return packageDao;
    }

    public void setPackageDao(PackageDao packageDao) {
        this.packageDao = packageDao;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public AddressDao getAddressDao() {
        return addressDao;
    }

    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    public EmployeesDao getEmployeesDao() {
        return employeesDao;
    }

    public void setEmployeesDao(EmployeesDao employeesDao) {
        this.employeesDao = employeesDao;
    }

    public ExpressDao getExpressDao() {
        return expressDao;
    }

    public void setExpressDao(ExpressDao expressDao) {
        this.expressDao = expressDao;
    }

    /////////////////////////////公共的接口（用户和工作人员都要用的）////////////////////////////

    @Override
    public Integer PackageLoadIntoPackage(List<String> packageIds) {
        //获得包裹id
        String packageId = packageIds.get(0);
        //遍历剩下的id存入关系表
        for (int i = 1; i < packageIds.size(); ++i) {
            //存入包裹快递表
            ExpressandpackageEntity expressandpackageEntity = new ExpressandpackageEntity();
            expressandpackageEntity.setTime(new Date());
            expressandpackageEntity.setPackageId(packageId);
            expressandpackageEntity.setExpressId(packageIds.get(i));
            expressAndPackageDao.save(expressandpackageEntity);

            //存入快递快递表
            PackandpackEntity packandpackEntity = new PackandpackEntity();
            packandpackEntity.setIsHistory(0);
            packandpackEntity.setPackageId(packageIds.get(i));
            packandpackEntity.setParentId(packageId);
            packAndpackDao.save(packandpackEntity);
        }
        return 1;
    }

    @Override
    public Integer ExpressLoadIntoPackage(List<String> expressIds) {
        //获得包裹id
        String packageId = expressIds.get(0);
        //遍历剩下的id存入关系表
        for (int i = 1; i < expressIds.size(); ++i) {
            //存入包裹快递表
            ExpressandpackageEntity expressandpackageEntity = new ExpressandpackageEntity();
            expressandpackageEntity.setTime(new Date());
            expressandpackageEntity.setPackageId(packageId);
            expressandpackageEntity.setExpressId(expressIds.get(i));
            expressAndPackageDao.save(expressandpackageEntity);
        }
        return 1;
    }

    @Override
    public PackageInfo getPackageInfo(String packageId) {
        //创建一个包裹的信息对象
        PackageInfo packageInfo = new PackageInfo();
        //获取包裹的对象实体
        PackageEntity packageEntity = packageDao.get(packageId);
        //获取包裹历史的对象实体
        List<PackagehistoryEntity> by = packageHistoryDao.findBy("time", true, Restrictions.eq("packageId", packageEntity.getId()));
        PackagehistoryEntity packagehistoryEntity = by.get(by.size() - 1);
        //获取出发和到达站点对象
        OutletsEntity fromOutletsEntity = outletsDao.get(packagehistoryEntity.getFromOutletsId());
        OutletsEntity toOutletsEntity = outletsDao.get(packagehistoryEntity.getToOutletsId());
        //获取用户对象

        EmployeesEntity employeesEntity = employeesDao.get(packageEntity.getEmployeesId());

        //填充数据
        packageInfo.setId(packageEntity.getId());
        packageInfo.setPackageFrom(fromOutletsEntity.getName());
        packageInfo.setPackageTo(toOutletsEntity.getName());
        packageInfo.setEmployeesName(employeesEntity.getName());
        packageInfo.setEmployeesID(packageEntity.getEmployeesId());
        packageInfo.setCloseTime(packageEntity.getTime().toString());

        return packageInfo;
    }

    @Override
    public String PrepareSendExpress(Integer customerId, Integer sendAddressId, Integer recvAddressId) {
        ExpressEntity expressEntity = new ExpressEntity();

        //获取id 校验是否存在
        String packageId = Utils.getUUid();
        while (true) {
            List<PackageEntity> by = packageDao.findBy("id", true, Restrictions.eq("id", packageId));
            if (by.size() == 0)
                break;
            else
                packageId = Utils.getUUid();
        }

        //填充实体信息
        expressEntity.setId(packageId);
        expressEntity.setSendAddressId(sendAddressId);
        expressEntity.setAccAddressId(recvAddressId);

        //外键约束，必须带用户id
        expressEntity.setCustomerId(customerId);

        expressDao.save(expressEntity);

        //返回实体对象
        return expressEntity.getId();
    }

    @Override
    public PackageInfo CreateAPackage(Integer fromID, Integer toID, Integer employeesID) {
        PackageEntity packageEntity = new PackageEntity();
        PackagehistoryEntity packagehistoryEntity = new PackagehistoryEntity();
        PackageInfo packageInfo = new PackageInfo();

        //获取id 校验是否存在
        String packageId = Utils.getUUid();
        while (true) {
            List<PackageEntity> by = packageDao.findBy("id", true, Restrictions.eq("id", packageId));
            if (by.size() == 0)
                break;
            else
                packageId = Utils.getUUid();
        }
        EmployeesEntity employeesEntity = employeesDao.get(employeesID);
        OutletsEntity fromOutletsEntity = outletsDao.get(fromID);
        OutletsEntity toOutletsEntity = outletsDao.get(toID);

        //填充实体信息
        packageEntity.setTime(new Date());
        packageEntity.setId(packageId);
        packageEntity.setEmployeesId(employeesID);

        packagehistoryEntity.setPackageId(packageId);
        packagehistoryEntity.setFromOutletsId(fromID);
        packagehistoryEntity.setToOutletsId(toID);
        packagehistoryEntity.setTime(new Date());

        packageInfo.setId(packageId);
        packageInfo.setCloseTime(new Date().toString());
        packageInfo.setPackageFrom(fromOutletsEntity.getName());
        packageInfo.setPackageTo(toOutletsEntity.getName());
        packageInfo.setEmployeesID(employeesID);
        packageInfo.setEmployeesName(employeesEntity.getName());

        packageDao.save(packageEntity);
        packageHistoryDao.save(packagehistoryEntity);

        return packageInfo;
    }

    @Override
    public Response LoadIntoPackage(String PackageId, String Id, Integer isPackage) {
        try {
            //如果不是包裹
            if (isPackage == 0) {
                ExpressandpackageEntity expressandpackageEntity = new ExpressandpackageEntity(Id, PackageId, new Date());
                expressAndPackageDao.save(expressandpackageEntity);
            } else if (isPackage == 1) {
                PackandpackEntity packandpackEntity = new PackandpackEntity();
                //如果是包裹需要存储父包裹表
                packandpackEntity.setParentId(PackageId);
                packandpackEntity.setPackageId(Id);
                //不是历史
                packandpackEntity.setIsHistory(0);
                packAndpackDao.save(packandpackEntity);
            }
            return Response.ok().header("PackandpackClass", "R_PackandpackInfo").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Override
    public List<ExpressEntity> searchExpressInPackageById(String PackageId) {
        List<ExpressEntity> lists = new ArrayList<>();
        //按照条件查找相应的包裹里所有快件
        List<ExpressandpackageEntity> all = expressAndPackageDao.findBy("packageId", true, Restrictions.eq("packageId", PackageId));
        for (int i = 0; i < all.size(); ++i) {
            String expressId = all.get(i).getExpressId();
            //保存快递
            lists.add(expressDao.get(expressId));
        }
        return lists;
    }

    @Override
    public ExpressInfo getExpressInfoById(String id) {
        //返回快递对象实体
        ExpressInfo expressInfo = new ExpressInfo();
        //获取快递信息
        ExpressEntity expressEntity = expressDao.get(id);
        //获取发件人信息
        CustomerEntity customerEntity = customerDao.get(expressEntity.getCustomerId());
        //发件人地址
        AddressEntity sendAddressEntity = addressDao.get(expressEntity.getSendAddressId());
        //收件人地址
        AddressEntity recvAddressEntity = addressDao.get(expressEntity.getAccAddressId());
        //收件人regin
        RegionEntity sendRegionEntity = regionDao.get(sendAddressEntity.getRegionId());
        CityEntity sendCityEntity = cityDao.get(sendRegionEntity.getCityid());
        ProvinceEntity sendProvinceEntity = provinceDao.get(sendCityEntity.getPid());
        //发件人regin
        RegionEntity recvRegionEntity = regionDao.get(sendAddressEntity.getRegionId());
        CityEntity recvCityEntity = cityDao.get(recvRegionEntity.getCityid());
        ProvinceEntity recvProvinceEntity = provinceDao.get(recvCityEntity.getPid());

        //塞数据。。。我日好长、、、
        expressInfo.setID(expressEntity.getId());
        expressInfo.setSname(sendAddressEntity.getName());
        expressInfo.setStel(sendAddressEntity.getTelephone());
        expressInfo.setSadd(sendProvinceEntity.getPname() + "-" + sendCityEntity.getCname() + "-" + sendRegionEntity.getArea());
        expressInfo.setSaddinfo(sendAddressEntity.getAddress());
        expressInfo.setRname(recvAddressEntity.getName());
        expressInfo.setRtel(recvAddressEntity.getTelephone());
        expressInfo.setRadd(recvProvinceEntity.getPname() + "-" + recvCityEntity.getCname() + "-" + recvRegionEntity.getArea());
        expressInfo.setRaddinfo(recvAddressEntity.getAddress());
        expressInfo.setGetTime(expressEntity.getGetTime().toString());
        expressInfo.setOutTime(expressEntity.getOutTime().toString());
        expressInfo.setWeight(expressEntity.getWeight());
        expressInfo.setTranFee(expressEntity.getTranFee());
        expressInfo.setInsuFee(expressEntity.getInsuFee());

        expressInfo.setAcc1(expressEntity.getAcc1());
        expressInfo.setAcc2(expressEntity.getAcc2());

        return expressInfo;
    }

    @Override
    public List<ExpressInfo> getExpressInfoByTel(String tel) {
        List<ExpressInfo> lists = new ArrayList<>();
        //用户信息
        List<CustomerEntity> customer = customerDao.findBy("telephone", true, Restrictions.eq("telephone", tel));
        //手机号相关的快递
        List<ExpressEntity> by = expressDao.findBy("customerId", true, Restrictions.eq("customerId", customer.get(0).getId()));

        for (int i = 0; i < by.size(); ++i) {
            ExpressInfo expressInfo = getExpressInfoById(by.get(i).getId());
            lists.add(expressInfo);
        }
        return lists;
    }

    @Override
    public List<ExpressInfo> getExpressInfoByCustomerId(Integer CustomerId) {
        List<ExpressInfo> lists = new ArrayList<>();
        List<ExpressEntity> by = expressDao.findBy("customerId", true, Restrictions.eq("customerId", CustomerId));

        for (int i = 0; i < by.size(); ++i) {
            ExpressInfo expressInfo = getExpressInfoById(by.get(i).getId());
            lists.add(expressInfo);
        }
        return lists;
    }

    @Override
    public PackageEntity findAPackageById(String PackageId) {
        //直接返回包裹对象实体
        return packageDao.get(PackageId);
    }

    @Override
    public List<PackageEntity> searchPackageInPackageById(String PackageId) {
        List<PackageEntity> lists = new ArrayList<>();
        //按照条件查找相应的包裹里所有包裹
        List<PackandpackEntity> by = packAndpackDao.findBy("parentId", true, Restrictions.eq("parentId", PackageId));
        for (int i = 0; i < by.size(); ++i) {
            String expressId = by.get(i).getPackageId();
            //拿出来所有的快递列表
            if (by.get(i).getIsHistory() == 0)
                lists.add(packageDao.get(expressId));
        }
        return lists;
    }

    @Override
    public Response saveExpress(ExpressEntity obj) {
        try {
            //保存快递并返回状态
            expressDao.save(obj);
            return Response.ok(obj).header("ExpressClass", "R_ExpressInfo").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Override
    public List<ExpressEntity> getWork(Integer employeeId, String starttime, Integer days) {
        //获取时间段
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        try {
            startDate = sdf.parse(starttime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endDate = (Date) startDate.clone();
        endDate.setTime(endDate.getTime() + days * 86400000);

        List<ExpressEntity> lists = new ArrayList<>();
        //查找符合条件的包裹
        List<PackageEntity> by = packageDao.findBy("id", true, Restrictions.eq("employeesId", employeeId), Restrictions.between("time", startDate, endDate));

        //递归查找包裹里的快递并存放到lists中
        for (int i = 0; i < by.size(); ++i) {
            findAllWork(lists, by.get(i).getId());
        }
        return lists;
    }

    //递归方法
    public void findAllWork(List<ExpressEntity> lists, String PackageId) {
        //获得包裹中的快递和包裹
        List<ExpressEntity> expressEntities = searchExpressInPackageById(PackageId);
        List<PackageEntity> packageEntities = searchPackageInPackageById(PackageId);
        if (expressEntities != null) {
            //快递直接加入工作量lists
            lists.addAll(expressEntities);
        }
        if (packageEntities != null) {
            //如果存在包裹则遍历包裹再进行一遍查询包裹中包裹和快递的操作
            for (int i = 0; i < packageEntities.size(); ++i)
                findAllWork(lists, packageEntities.get(i).getId());
        }
    }


    /////////////////////////////用户的接口////////////////////////////

    //通过用户id获得用户信息
    @Override
    public CustomerEntity getCustomerInfoById(int id) {
        return customerDao.get(id);
    }

    //通过用户手机号获得用户信息
    @Override
    public CustomerEntity getCustomerInfoByTel(String tel) {
        if (tel == null)
            return null;
        List<CustomerEntity> list = customerDao.getByTel(tel);
        if (list.size()!=0){
            return list.get(0);
        }
        return null;
    }

    //注册，并在注册过程中检查手机号是否注册过
    @Override
    public String registerByCus(CustomerEntity obj) {
        if (obj.getName()==null || obj.getTelephone()==null || obj.getPassword()==null)
            return "{\"registerstate\":\"null\"}";
        List<CustomerEntity> list = null;
        CustomerEntity customerEntity = new CustomerEntity();

        try {
            list = customerDao.getByTel(obj.getTelephone());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (list.size() == 0) {
            try {
                customerDao.save(obj);
                list = customerDao.getByTel(obj.getTelephone());
                customerEntity = list.get(0);
                return "{\"registerstate\":\"true\", \"id\":"+customerEntity.getId()+"}";
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"registerstate\":\"false\"}";
            }
        } else {
            return "{\"registerstate\":\"deny\"}";
        }
    }

    //更新用户信息
    @Override
    public String updateCustomerInfo(CustomerEntity obj) {
        if (obj.getName()==null || obj.getPassword()==null || obj.getTelephone()==null)
            return "{\"updateCustomerInfo\":\"null\"}";
        try {
            customerDao.save(obj);
            return "{\"updateCustomerInfo\":\"true\"}";
        } catch (Exception e) {
            return "{\"updateCustomerInfo\":\"false\"}";
        }
    }

    //根据用户id删除用户信息
    @Override
    public Response deleteCustomerInfo(int id) {
        customerDao.removeById(id);
        return Response.ok("Deleted").header("EntityClass", "D_CustomerInfo").build();
    }

    //用户登陆post方法
    @Override
    public String login(CustomerEntity obj) {
        CustomerEntity customerEntity = new CustomerEntity();
        if (obj.getTelephone()==null || obj.getPassword()==null)
            return "{\"loginstate\":\"null\"}";
        List<CustomerEntity> list = customerDao.getByTel(obj.getTelephone());
        if (list.size() != 0) {
            customerEntity = list.get(0);
            if (customerEntity.getPassword().equals(obj.getPassword())) {
                return "{\"id\":"+customerEntity.getId()+", \"name\":\"" + customerEntity.getName() + "\", \"loginstate\":\"true\"}";
            }
        }
        return "{\"loginstate\":\"false\"}";
    }

    //注销登陆
    @Override
    public void doLogOut(int cid) {

    }

    //修改手机号
    @Override
    public String changeTel(String telold, String telnew) {
        if (telold == null || telnew == null)
            return "{\"changetel\":\"null\"}";
        List<CustomerEntity> listold = customerDao.getByTel(telold);
        List<CustomerEntity> listnew = customerDao.getByTel(telnew);
        if (listnew.size()!=0){
            return "{\"changetel\":\"deny1\"}";   //修改的手机号已注册
        }
        if (listold.size()!=1){
            return "{\"changetel\":\"deny2\"}";   //未找到该用户信息
        }

        CustomerEntity customerEntity = listold.get(0);
        customerEntity.setTelephone(telnew);

        try {
            customerDao.save(customerEntity);
            return "{\"changetel\":\"true\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"changetel\":\"false\"}";
        }
    }


    //修改密码
    @Override
    public String changePwd(String tel, String pwdold, String pwdnew) {
        if (tel == null || pwdnew == null || pwdold == null)
            return "{\"changepwd\":\"null\"}";

        List<CustomerEntity> list = customerDao.getByTel(tel);
        if (list.size()!=1){
           return "{\"changepwd\":\"deny1\"}";
        }
        CustomerEntity customerEntity = list.get(0);
        if (customerEntity.getPassword().equals(pwdold)){
            customerEntity.setPassword(pwdnew);
            try {
                customerDao.save(customerEntity);
                return "{\"changepwd\":\"true\"}";
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"changepwd\":\"deny2\"}";
            }
        }
        return "{\"changepwd\":\"false\"}";
    }

    @Override
    public Response createExpress(String id, int cid) {
        return null;
    }

    /////////////////////////////工作人员的公共接口////////////////////////////

    //通过工作人员id查找工作人员信息
    @Override
    public EmployeesEntity getEmployeeInfoById(int id) {
        return employeesDao.get(id);
    }

    //更新或者是插入一条数据
    @Override
    public Response saveEmployeeInfo(EmployeesEntity obj) {
        try {
            employeesDao.save(obj);
            return Response.ok(obj).header("EntityClass", "R_EmployeeInfo").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    //删除员工信息
    @Override
    public Response deleteEmployee(int id) {
        employeesDao.removeById(id);
        return Response.ok("Deleted").header("EntityClass", "D_Employee").build();
    }

    //员工通过手机号和密码登陆
    @Override
    public boolean doLoginByEmployee(String tel, String pwd) {
        List<EmployeesEntity> list = employeesDao.getByTel(tel);
        EmployeesEntity employeesEntity = new EmployeesEntity();
        if (list.size() != 0) {
            employeesEntity = list.get(0);
            if (employeesEntity.getPassword().equals(pwd)) {
                return true;
            }
        }
        return false;
    }

    //员工注销登陆
    @Override
    public void doLogOutByEmployee(int id) {

    }

    @Override
    public Response savePackage(PackageEntity obj) {
        try {
            packageDao.save(obj);
            return Response.ok(obj).header("EntityClass", "R_PackageInfo").build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @Override
    public ExpressEntity getPackageById(String pid) {
        return expressDao.get(pid);
    }



    /////////////////////////////快递员的接口////////////////////////////


    /////////////////////////////分拣员的接口////////////////////////////


}
