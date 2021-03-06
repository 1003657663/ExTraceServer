package com.express.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by violet on 2016/4/6.
 */
@Entity
@Table(name = "package", schema = "", catalog = "express")
@org.hibernate.annotations.Proxy(lazy = false)
@XmlRootElement(name = "Package")
public class PackageEntity implements Serializable {
    private String id;
    private int employeesId;
    private Date time;

    public PackageEntity() {
    }

    public PackageEntity(String id, int employeesId, Date time) {
        this.id = id;
        this.employeesId = employeesId;
        this.time = time;
    }

    @Id
    @Column(name = "ID", nullable = false, insertable = true, updatable = true, length = 24)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "EmployeesID", nullable = false, insertable = true, updatable = true)
    public int getEmployeesId() {
        return employeesId;
    }

    public void setEmployeesId(int employeesId) {
        this.employeesId = employeesId;
    }

    @Basic
    @Column(name = "time", nullable = true, insertable = true, updatable = true)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageEntity that = (PackageEntity) o;

        if (employeesId != that.employeesId) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + employeesId;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
