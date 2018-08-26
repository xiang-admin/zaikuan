package com.pro.warehouse.dao;

import com.pro.warehouse.pojo.DaliyCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

@Repository
public interface DaliyCountReposity extends JpaRepository<DaliyCount, Long> {
}
