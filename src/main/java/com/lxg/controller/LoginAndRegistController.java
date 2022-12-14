package com.lxg.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lxg.domain.Admin;
import com.lxg.domain.User;
import com.lxg.service.AdminService;
import com.lxg.service.UserService;
import com.lxg.utils.WebUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;


@Controller
public class LoginAndRegistController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/login")
    public String login(HttpSession session) {
        session.removeAttribute("flag1");
        return "user/login";
    }

    @RequestMapping("/regist")
    public String regist(HttpServletRequest req,HttpSession session) {

            session.setAttribute("flag1","true");

//        return "user/regist";
        return "user/login";
    }

    @RequestMapping("/loginSuccess")
    public String loginSuccess(HttpServletRequest req, HttpSession session) {

        session.removeAttribute("flag1");

        //  1、获取请求的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String radio = req.getParameter("radio");

        System.out.println(radio);

        if (radio != null && radio.equals("admin")) {
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username).eq("password", password);
            Admin loginUser = adminService.getOne(queryWrapper);
            // 如果等于null,说明登录 失败!
            if (loginUser == null) {
                // 把错误信息，和回显的表单项信息，保存到Request域中
                req.setAttribute("login_msg", "用户或密码错误！");
                req.setAttribute("username", username);
                //   跳回登录页面
                return "/user/login";
            } else {
                // 登录 成功
                //保存用户信息到Session域中
                req.getSession().setAttribute("user", loginUser);
                //跳到成功页面login_success.html
                return "user/login_Success";
            }
        } else if (radio != null && radio.equals("reader")) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", username).eq("password", password);
            User loginUser = userService.getOne(queryWrapper);
            // 如果等于null,说明登录 失败!
            if (loginUser == null) {
                // 把错误信息，和回显的表单项信息，保存到Request域中
                req.setAttribute("login_msg", "用户或密码错误！");
                req.setAttribute("username", username);
                //   跳回登录页面
                return "/user/login";
            } else {
                // 登录 成功
                //保存用户信息到Session域中
                req.getSession().setAttribute("user", loginUser);
                //跳到成功页面login_success.html
                return "user/login_Success";
            }
        }
        return "/user/login";
    }


    @RequestMapping("/registSuccess")
    public String registSuccess(HttpServletRequest req,HttpSession session) throws Exception {
        //获取Session域中的验证码
        String token = (String) req.getSession().getAttribute("verifyCode");
        //删除当前session中的这个验证码
        req.getSession().removeAttribute("verifyCode");


        System.out.println(token);

        //1、获取请求的参数
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String sno = req.getParameter("sno");
        String email = req.getParameter("email");
        String code = req.getParameter("code");

        //给参数注入值
        User user = WebUtils.copyParamToBean(req.getParameterMap(), new User());

        //2、检查 验证码是否正确
        if (token != null && token.equalsIgnoreCase(code)) {
            //3、检查 用户名和学号是否可用
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<User>();
            queryWrapper1.eq("username", username);
            User user1 = userService.getOne(queryWrapper1);

            QueryWrapper<User> queryWrapper2 = new QueryWrapper<User>();
            queryWrapper2.eq("sno", sno);
            User user2 = userService.getOne(queryWrapper2);

            QueryWrapper<User> queryWrapper3 = new QueryWrapper<User>();
            queryWrapper3.eq("email", email);
            User user3 = userService.getOne(queryWrapper3);

            if (user1 != null) {
                System.out.println("用户名[" + username + "]已存在!");
                // 把回显信息，保存到Request域中
                req.setAttribute("regist_msg", "用户名已存在！！");
                req.setAttribute("username", username);
                req.setAttribute("email", email);
                req.setAttribute("sno", sno);

                session.setAttribute("flag1","true");


                //跳回注册页面
                return "/user/login";

            } else if (user2 != null) {
                // 把回显信息，保存到Request域中
                req.setAttribute("regist_msg", "学号已存在！！");
                req.setAttribute("username", username);
                req.setAttribute("email", email);
                req.setAttribute("sno", sno);

                session.setAttribute("flag1","true");


            } else if (user3 != null) {
                // 把回显信息，保存到Request域中
                req.setAttribute("regist_msg","邮箱已注册！！");
                req.setAttribute("username", username);
                req.setAttribute("email", email);
                req.setAttribute("sno", sno);

                session.setAttribute("flag1","true");


            } else {
                //可用
                //调用Sservice保存到数据库
                userService.save(user);
                QueryWrapper<User> queryWrapper4 = new QueryWrapper<User>();
                queryWrapper4.eq("username", username);
                User loginUser = userService.getOne(queryWrapper4);
                req.getSession().setAttribute("user", loginUser);
                //跳到注册成功页面 regist_success.jsp
                return "user/regist_success";
            }
        } else {
            // 把回显信息，保存到Request域中
            req.setAttribute("regist_msg", "验证码错误！！");
            req.setAttribute("username", username);
            req.setAttribute("email", email);
            req.setAttribute("sno", sno);
            System.out.println("验证码[" + code + "]错误");


            session.setAttribute("flag1","true");


            return "/user/login";
        }

        session.setAttribute("flag1","true");


        return "/user/login";
    }
}


