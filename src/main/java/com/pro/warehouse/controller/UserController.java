package com.pro.warehouse.controller;

import com.pro.warehouse.Service.IndexService;
import com.pro.warehouse.dao.*;
import com.pro.warehouse.mail.MailService;
import com.pro.warehouse.pojo.User;
import com.pro.warehouse.util.EncrypeUtil;
import com.pro.warehouse.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class.getName());
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IndexService indexService;
    @Autowired
    MailService mailService;


    private Integer pagesize = 3;//每页显示的条数


    // 通过@Resource注解引入JdbcTemplate对象
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CommonRepository<User> commonRepository;




    @RequestMapping(value = "/user-dologin", method = {RequestMethod.GET, RequestMethod.POST})
    public String UserLogin(User user, ModelMap modelMap, HttpServletRequest request) throws Exception {
        String page = "index";

        List<User> userList = userRepository.findUserByusername(user.getUsername());
        if (userList.size() == 0)//此用户不存在
        {
            modelMap.addAttribute("message", "用户名或者密码不正确");
            page = "login";
        } else {
            if (!userList.get(0).getPassword().equals(EncrypeUtil.shaEncode(user.getPassword()))) {
                modelMap.addAttribute("message", "用户名或者密码不正确");
                page = "login";
            } else {
                User loginUser = userList.get(0);
                request.getSession().setAttribute("user", loginUser);
            }
        }
        int enterSize = indexService.getYestdayApplyEnterCount();
        int outSize = indexService.getYestdayApplyOutCount();
        int entrepotSize = indexService.getEntrpotSize();
        modelMap.addAttribute("enterSize",enterSize);
        modelMap.addAttribute("outSize",outSize);
        modelMap.addAttribute("entrepotSize",entrepotSize);
        System.out.println("登陆用户：" + userList.get(0));
        return page;
    }

    @RequestMapping("/user-login")
    public String thymeleaftest(ModelMap map) {
        // map.addAttribute("host", "http://www.baidu.com");
        return "login";
    }


    @RequestMapping("/user-save")
    public String saveUser(User user) {
        try {
            user.setPassword(EncrypeUtil.shaEncode(user.getPassword()));
            user.setStatus(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        userRepository.save(user);
        return "redirect:/user-getAll?pagenum=1";
    }



    @RequestMapping("/user-getAll")
    public String getAlllUser(User user, ModelMap modelMap, int pagenum) {
        String page = "user_list";
        if (user != null) {
            StringBuffer sql = null;
            try {
                sql = commonRepository.getFiledValues(user, pagenum);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            sql.append( "1 = 1");
            int totalpage = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(User.class)).size();
            sql.append(" LIMIT " + (pagenum - 1) * pagesize + "," + pagesize);
            List<User> users = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(User.class));
            System.out.println("已确认的申请" + users);
            modelMap.addAttribute("users", users);
            modelMap.addAttribute("page", pagenum);
            modelMap.addAttribute("totalpage", PageUtil.getTotalPage(totalpage, pagesize));
        }
        return page;

    }

    @RequestMapping("/user-disabledUser")
    public String disabledUser(Long id, ModelMap modelMap, HttpSession session) {
        User user1 = (User)session.getAttribute("user");
        System.out.println("禁用账户"+id);
        if(id==user1.getId()){
            session.setAttribute("message","不能禁用自己");
            return "redirect:/user-getAll?pagenum=1";
        }
        User user = userRepository.findUserByid(id);
        if(null!=user){
            user.setStatus(1);
            userRepository.save(user);
            System.out.println("禁用账户"+user);
        }
        return "redirect:/user-getAll?pagenum=1";
    }

    @RequestMapping("/user-enableUser")
    public String enableUser(Long id, ModelMap modelMa,HttpSession session) {
        User user1 = (User)session.getAttribute("user");
        if(id==(user1.getId())){
            session.setAttribute("message","不能启用自己");
            return "redirect:/user-getAll?pagenum=1";
        }
        User user = userRepository.findUserByid(id);
        if(null!=user){
            user.setStatus(2);
            userRepository.save(user);
            System.out.println("启用账户"+user);
        }
        return "redirect:/user-getAll?pagenum=1";
    }

    public String updateUser(Long id, ModelMap modelMap) {

        return "redirect:user/getAll?pagenum=1";
    }

    @RequestMapping("/user-delete")
    public String deleteUser(Long id,HttpSession session) {
        User user1 = (User)session.getAttribute("user");
        if(id==(user1.getId())){
            session.setAttribute("message","不能删除自己");
            return "redirect:/user-getAll?pagenum=1";
        }
        session.setAttribute("message","删除了用户"+userRepository.findUserByid(id).getUsername());
        userRepository.deleteById(id);
        return "redirect:/user-getAll?pagenum=1";
    }


    @RequestMapping("/user-searchById")
    @ResponseBody
    public User searchUser(Long id) {
        User user = userRepository.findUserByid(id);

        return user;
    }

    @RequestMapping("/user-edit")
    public String userEdit(User user) throws Exception {
        user.setPassword(EncrypeUtil.shaEncode(user.getPassword()));
        System.out.println(user);
        userRepository.save(user);
        System.out.println("编辑用户"+user);
        return "redirect:/user-getAll?pagenum=1";
    }


}
